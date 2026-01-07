package software.ulpgc.kata6.app;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;

public class Desktop extends JFrame {

    private Desktop() {
        this.setLocationRelativeTo(null);
        this.setTitle("Histogram display");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static Desktop create() {
        return new Desktop();
    }

    public Desktop display(JFreeChart chart) {
        this.getContentPane().add(displayOf(chart));
        this.revalidate();
        return this;
    }

    private Component displayOf(JFreeChart chart) {
        return new ChartPanel(chart);
    }
}
