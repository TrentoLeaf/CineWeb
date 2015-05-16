package tk.trentoleaf.cineweb.db;

import org.postgresql.util.PSQLException;
import tk.trentoleaf.cineweb.model.Role;
import tk.trentoleaf.cineweb.model.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class DB {
    private final Logger logger = Logger.getLogger(DB.class.getSimpleName());

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
        createTableRoles();
        createTableUsers();
        createTablePasswordResets();

        // test
        try {
            createUser(new User(Role.ADMIN, "teo@teo.com", "teo", "Matteo", "Zeni"));
            createUser(new User(Role.CLIENT, "davide@pippo.com", "dada", "Davide", "Pedranz"));
        } catch (Exception e) {
            //
        }

        // TODO: remove
        System.out.println("TRUE: " + authenticate("teo@teo.com", "teo"));
        System.out.println("FALSE: " + authenticate("davide@pippo.com", "teo"));
        System.out.println("FALSE: " + authenticate("teo@teo.com", "teosafd"));
        System.out.println("FALSE: " + authenticate("sdfsd", "teosafd"));
    }

    // close the connection
    public void close() throws SQLException {
        connection.close();
    }

    // create table roles & insert roles (if not exists)
    private void createTableRoles() throws SQLException {

        // create table roles
        Statement createStm = connection.createStatement();
        try {
            createStm.execute("CREATE TABLE IF NOT EXISTS roles (" +
                    "roleid CHAR(8) PRIMARY KEY," +
                    "description VARCHAR(200));");
        } finally {
            if (createStm != null) {
                createStm.close();
            }
        }

        // insert roles
        final List<Role> roles = Role.getRoles();
        final String insertRole = "INSERT INTO roles (roleid, description) VALUES (?, ?);";
        for (Role r : roles) {
            PreparedStatement insertStm = connection.prepareStatement(insertRole);
            try {
                insertStm.setString(1, r.getRoleID());
                insertStm.setString(2, r.getDescription());
                insertStm.execute();
            } catch (PSQLException e) {
                logger.warning("Role " + r.getRoleID() + " already exists... SKIP");
            } finally {
                if (insertStm != null) {
                    insertStm.close();
                }
            }
        }
    }

    // create table user
    private void createTableUsers() throws SQLException {
        Statement users = connection.createStatement();
        try {
            users.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "uid SERIAL PRIMARY KEY," +
                    "roleid CHAR(8) REFERENCES roles(roleid)," +
                    "email VARCHAR(256) UNIQUE NOT NULL," +
                    "pass CHAR(60) NOT NULL," +
                    "first_name VARCHAR(100)," +
                    "second_name VARCHAR(100)," +
                    "credit MONEY DEFAULT 0);");
            users.execute("CREATE INDEX ON users (email);");
        } finally {
            if (users != null) {
                users.close();
            }
        }
    }

    // create table password resets
    private void createTablePasswordResets() throws SQLException {
        Statement resets = connection.createStatement();
        try {
            resets.execute("CREATE TABLE IF NOT EXISTS resets (" +
                    "code CHAR(60) PRIMARY KEY," +
                    "uid INTEGER REFERENCES users(uid)," +
                    "exipiration TIMESTAMP);");
        } finally {
            if (resets != null) {
                resets.close();
            }
        }
    }

    // create user
    public void createUser(User user) throws SQLException {
        final String query = "INSERT INTO users (uid, roleid, email, pass, first_name, second_name)" +
                "VALUES (DEFAULT, ?, ?, crypt(?, gen_salt('bf')), ?, ?)";
        PreparedStatement create = connection.prepareStatement(query);
        try {
            create.setString(1, user.getRole().getRoleID());
            create.setString(2, user.getEmail());
            create.setString(3, user.getPassword());
            create.setString(4, user.getFirstName());
            create.setString(5, user.getSecondName());
            create.executeUpdate();
        } finally {
            if (create != null) {
                create.close();
            }
        }
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
