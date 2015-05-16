package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.model.User;

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

    // open the connection & initialize the database
    public void open() throws SQLException, URISyntaxException {

        // saves the connection object
        connection = getConnection();

        // initialize the database
        createTableUser();

        // test
        try {
            createUser(new User("teo@teo.com", "teo", "Matteo", "Zeni"));
            createUser(new User("davide@pippo.com", "dada", "Davide", "Pedranz"));
        } catch (Exception e) {
            //
        }

        System.out.println("TRUE: " + authenticate("teo@teo.com", "teo"));
        System.out.println("FALSE: " + authenticate("davide@pippo.com", "teo"));
        System.out.println("FALSE: " + authenticate("teo@teo.com", "teosafd"));
        System.out.println("FALSE: " + authenticate("sdfsd", "teosafd"));
    }

    // close the connection
    public void close() throws SQLException {
        connection.close();
    }

    // create table user
    private void createTableUser() throws SQLException {
        Statement users = connection.createStatement();
        Statement resets = connection.createStatement();
        try {
            users.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "uid SERIAL PRIMARY KEY," +
                    "email VARCHAR(256) UNIQUE NOT NULL," +
                    "pass CHAR(60) NOT NULL," +
                    "first_name VARCHAR(100)," +
                    "second_name VARCHAR(100)," +
                    "credit MONEY DEFAULT 0);");
            users.execute("CREATE INDEX ON users (email);");
            resets.execute("CREATE TABLE IF NOT EXISTS resets (" +
                    "code CHAR(60) PRIMARY KEY," +
                    "uid INTEGER REFERENCES users(uid)," +
                    "exipiration TIMESTAMP);");
        } finally {
            if (users != null) {
                users.close();
            }
            if (resets != null) {
                resets.close();
            }
        }
    }

    // create user
    public boolean createUser(User user) throws SQLException {
        final String query = "INSERT INTO users (uid, email, pass, first_name, second_name)" +
                "VALUES (DEFAULT, ?, crypt(?, gen_salt('bf')), ?, ?)";
        PreparedStatement create = connection.prepareStatement(query);
        int rows;
        try {
            create.setString(1, user.getEmail());
            create.setString(2, user.getPassword());
            create.setString(3, user.getFirstName());
            create.setString(4, user.getSecondName());
            rows = create.executeUpdate();
        } finally {
            if (create != null) {
                create.close();
            }
        }
        return rows == 1;
    }

    // authenticate a user
    public boolean authenticate(String email, String password) throws SQLException {
        final String query = "SELECT COUNT(email) FROM users " +
                "WHERE email = ? AND pass = crypt(?, pass);";
        PreparedStatement select = connection.prepareStatement(query);
        boolean ok;
        try {
            select.setString(1, email);
            select.setString(2, password);
            ResultSet rs = select.executeQuery();
            rs.next();
            ok = (rs.getInt(1) == 1);
        } finally {
            if (select != null) {
                select.close();
            }
        }
        return ok;
    }
}
