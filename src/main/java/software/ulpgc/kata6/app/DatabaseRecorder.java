package software.ulpgc.kata6.app;

import software.ulpgc.kata6.architecture.io.Recorder;
import software.ulpgc.kata6.architecture.model.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

public class DatabaseRecorder implements Recorder {
    private final Connection connection;
    private final PreparedStatement preparedStatement;
    private int count = 0;

    public DatabaseRecorder(Connection connection) throws SQLException {
        this.connection = connection;
        createTableIfNotExists();
        this.preparedStatement = connection.prepareStatement("INSERT INTO movies (title, year, duration, gender) VALUES (?, ?, ?, ?)");
    }

    @Override
    public void record(Stream<Movie> movies) {
        try {
            movies.forEach(this::record);
            flushBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void flushBatch() throws SQLException {
        preparedStatement.executeBatch();
        connection.commit();
    }

    private void record(Movie movie) {
        try {
            insert(movie);
            flushBatchIfRequired();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void insert(Movie movie) throws SQLException {
        preparedStatement.setString(1, movie.title());
        preparedStatement.setInt(2, movie.year());
        preparedStatement.setInt(3, movie.duration());
        preparedStatement.setString(4, movie.gender());
        preparedStatement.addBatch();
    }
    private void flushBatchIfRequired() throws SQLException {
        if(++count % 10000 == 0) preparedStatement.executeBatch();
    }

    private void createTableIfNotExists() throws SQLException {
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS movies (title TEXT, year INTEGER, duration INTEGER, gender TEXT);)");
    }
}
