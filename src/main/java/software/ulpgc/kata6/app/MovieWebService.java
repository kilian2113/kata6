package software.ulpgc.kata6.app;

import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.io.WebService;
import software.ulpgc.kata6.architecture.model.Movie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Stream;

public class MovieWebService implements WebService {
    private final Store store;

    private MovieWebService(Store store) {
        this.store = store;
    }

    public static MovieWebService with(Store store) {
        return new MovieWebService(store);
    }

    @Override
    public void start() {
        Javalin app = Javalin.create();
        app.get("/", this::welcome);
        app.get("/movies/histogram", this::histogramOfMovies);
        app.get("/movies", this::allMovies);
        app.get("/movies/{gender}", this::allMoviesByGender);
        app.start(8080);
    }

    private void allMoviesByGender(Context context) {
        String gender = context.pathParam("gender");
        movies(context, gender);
    }

    private void allMovies(Context context) {
        movies(context, null);
    }

    private void movies(Context context, String gender) {
        String year = context.queryParam("year");
        String from = context.queryParam("from");
        String to = context.queryParam("to");

        if(year == null) context.json(moviesByRange(moviesByGender(movies(), gender), from, to).toList());
        else context.json(moviesByYear(moviesByGender(movies(), gender), year).toList());
    }

    private Stream<Movie> moviesByYear(Stream<Movie> movies, String year) {
        return movies.filter(m->m.year()==Integer.parseInt(year));
    }

    private Stream<Movie> moviesByRange(Stream<Movie> movies,  String from, String to) {
        if(from == null && to == null) return movies;
        if(to == null) return movies.filter(m->m.year()>=Integer.parseInt(from));
        return movies.filter(m->m.year()>=Integer.parseInt(from) && m.year()<=Integer.parseInt(to));
    }

    private Stream<Movie> moviesByGender(Stream<Movie> movies, String gender) {
        return gender == null ? movies : movies.filter(m->m.gender().equalsIgnoreCase(gender));
    }

    private Stream<Movie> movies() {
        return store.movies().limit(100000);
    }

    private void welcome(Context context) {
        context.result("Welcome to the Movie Web Service!");
    }

    private void histogramOfMovies(Context context) {
        File file = new File("histogram.png");

        try {
            InputStream histogramStream = new FileInputStream(file);
            context.contentType("image/png");
            context.result(histogramStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
