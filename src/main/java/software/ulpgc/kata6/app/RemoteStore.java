package software.ulpgc.kata6.app;

import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class RemoteStore implements Store {
    private static final String url = "https://datasets.imdbws.com/title.basics.tsv.gz";
    private final Function<String, Movie> deserializer;

    public RemoteStore(Function<String, Movie> deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Stream<Movie> movies() {
        try {
            return moviesIn(new URL(url));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> moviesIn(URL url) throws IOException {
        return moviesIn(url.openConnection());
    }

    private Stream<Movie> moviesIn(URLConnection urlConnection) throws IOException {
        return moviesIn(unzip(urlConnection.getInputStream()));
    }

    private Stream<Movie> moviesIn(InputStream inputStream) throws IOException {
        return moviesIn(toReader(inputStream))
                .onClose(()->close(inputStream));
    }

    private void close(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> moviesIn(BufferedReader reader) throws IOException {
        return reader.lines().skip(1).map(deserializer);
    }

    private BufferedReader toReader(InputStream inputStream) throws IOException {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private GZIPInputStream unzip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(new BufferedInputStream(inputStream));
    }
}
