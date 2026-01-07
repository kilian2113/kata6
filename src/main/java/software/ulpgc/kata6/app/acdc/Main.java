package software.ulpgc.kata6.app.acdc;

import software.ulpgc.kata6.app.HistogramChart;
import software.ulpgc.kata6.app.MovieDeserializer;
import software.ulpgc.kata6.app.MovieWebService;
import software.ulpgc.kata6.app.RemoteStore;

public class Main {
    public static void main(String[] args) {
        RemoteStore store = new RemoteStore(MovieDeserializer::fromTsv);
        HistogramChart.with(store).savePNG();
        MovieWebService.with(store).start();
    }
}
