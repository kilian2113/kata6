package software.ulpgc.kata6.app.abba;

import software.ulpgc.kata6.app.Desktop;
import software.ulpgc.kata6.app.MovieDeserializer;
import software.ulpgc.kata6.app.RemoteStore;

public class Main {
    public static void main(String[] args) {
        Desktop.with(new RemoteStore(MovieDeserializer::fromTsv))
                .display()
                .setVisible(true);
    }
}
