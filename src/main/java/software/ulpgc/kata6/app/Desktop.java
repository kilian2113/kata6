package software.ulpgc.kata6.app;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;
import software.ulpgc.kata6.architecture.task.HistogramBuilder;
import software.ulpgc.kata6.architecture.viewmodel.Histogram;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class Desktop extends JFrame {
    private final Store store;

    private Desktop(Store store) {
        this.store = store;
        this.setLocationRelativeTo(null);
        this.setTitle("Histogram display");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static Desktop with(Store store) {
        return new Desktop(store);
    }

    public Desktop display() {
        display(histogramOf(movies()));
        return this;
    }

    private void display(Histogram histogram) {
        this.getContentPane().add(displayOf(histogram));
        this.revalidate();
    }

    private Component displayOf(Histogram histogram) {
        return new ChartPanel(decorate(charOf(histogram)));
    }

    private JFreeChart decorate(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        decoratePlot((XYPlot) chart.getPlot());
        return chart;
    }

    private void decoratePlot(XYPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainCrosshairPaint(Color.LIGHT_GRAY);
        plot.setRangeCrosshairPaint(Color.LIGHT_GRAY);
        decorateRenderer((XYBarRenderer) plot.getRenderer());
    }

    private void decorateRenderer(XYBarRenderer renderer) {
        renderer.setSeriesPaint(0, new Color(70, 130, 180, 180));
        renderer.setMargin(0.1);
    }

    private JFreeChart charOf(Histogram histogram) {
        return ChartFactory.createHistogram(
                histogram.title(),
                histogram.x(),
                "Cuenta",
                datasetOf(histogram)
        );
    }

    private XYSeriesCollection datasetOf(Histogram histogram) {
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(seriesOf(histogram));
        return collection;
    }

    private XYSeries seriesOf(Histogram histogram) {
        XYSeries series = new XYSeries(histogram.legend());
        for(int bin : histogram)
            series.add(bin, histogram.count(bin));
        return series;
    }

    private Histogram histogramOf(Stream<Movie> movies) {
        return HistogramBuilder.with(movies)
                .title("Películas por año")
                .x("Año")
                .legend("Nº películas")
                .build(Movie::year);
    }

    private Stream<Movie> movies() {
        return store.movies()
                .filter(m->m.year()>=1900)
                .filter(m->m.year()<=2025);
    }

}
