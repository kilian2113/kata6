package software.ulpgc.kata6.app;

import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabaseStore implements Store {
    private final Connection connection;

    public DatabaseStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Stream<Movie> movies() {
        try {
            return moviesIn(resultSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> moviesIn(ResultSet rs) {
        return Stream.generate(()->nextMovieIn(rs))
                .onClose(()->close(rs))
                .takeWhile(Objects::nonNull);
    }

    private Movie nextMovieIn(ResultSet rs) {
        try {
            return rs.next() ? readMovieIn(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Movie readMovieIn(ResultSet rs) throws SQLException {
        return new Movie(rs.getString(1),
                rs.getInt(2),
                rs.getInt(3)
        );
    }

    private void close(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet resultSet() throws SQLException {
        return connection.createStatement().executeQuery("SELECT * FROM movies");
    }
}
