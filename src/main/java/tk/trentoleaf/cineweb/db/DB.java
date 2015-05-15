package tk.trentoleaf.cineweb.db;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class DB {

    // the connection to the database
    private Connection connection;

    // return a connection object reading the environment variables
    private static Connection getConnection() throws URISyntaxException, SQLException {
        final URI dbUri = new URI(System.getenv("DATABASE_URL"));

        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }

    // constructor
    public DB() throws ClassNotFoundException {
        // load PostgresSQL drivers
        Class.forName("org.postgresql.Driver");
    }

    // open the connection
    public void open() throws SQLException, URISyntaxException {

        // saves the connection object
        connection = getConnection();

        // run some SQL
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS test (id integer)");
        stmt.executeUpdate("INSERT INTO test VALUES (1)");
    }

    // close the connection
    public void close() throws SQLException {
        connection.close();
    }
}
