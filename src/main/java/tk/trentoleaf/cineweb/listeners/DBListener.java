package tk.trentoleaf.cineweb.listeners;

import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.db.UsersDB;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebListener
public class DBListener implements ServletContextListener {
    private final Logger logger = Logger.getLogger(DBListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // create a TodoDB connection
        //TodoDB todoDB = TodoDB.instance();
        final DB db = DB.instance();

        try {
            // open the connection
            // todoDB.open();
            db.open();

            // create first user
            // todoDB.createAdminUser();
            UsersDB.instance().createAdminUser();

        } catch (RuntimeException | SQLException | URISyntaxException e) {
            logger.severe("Cannot open the connection to the database -> " + e.toString());
            logger.warning("Try to set the Environment variable DATABASE_URL to 'postgres://user:password@localhost:5432/db'");
            throw new RuntimeException(e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        // retrieve the database
        //final TodoDB todoDB = TodoDB.instance();
        final DB db = DB.instance();

        // close the connection
        try {
            // todoDB.close();
            db.close();
        } catch (NullPointerException | SQLException e) {
            logger.severe("Cannot close the connection to the database");
            throw new RuntimeException(e);
        }
    }
}
