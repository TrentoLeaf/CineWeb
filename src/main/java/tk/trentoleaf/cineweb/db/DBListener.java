package tk.trentoleaf.cineweb.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DBListener implements ServletContextListener {
    private final Logger logger = Logger.getLogger(DBListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            // create a DB connection
            DB db = DB.instance();

            // open the connection
            db.open();

        } catch (RuntimeException | SQLException | URISyntaxException e) {
            logger.severe("Cannot open the connection to the database -> " + e.toString());
            logger.warning("Try to set the Environment variable DATABASE_URL to 'postgres://user:password@localhost:5432/db'");
            throw new RuntimeException(e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        // retrieve the database
        final DB db = DB.instance();

        // close the connection
        try {
            db.close();
        } catch (NullPointerException | SQLException e) {
            logger.severe("Cannot close the connection to the database");
            throw new RuntimeException(e);
        }
    }
}
