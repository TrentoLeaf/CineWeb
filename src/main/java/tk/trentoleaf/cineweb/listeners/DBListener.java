package tk.trentoleaf.cineweb.listeners;

import tk.trentoleaf.cineweb.db.DB;
import tk.trentoleaf.cineweb.db.PricesDB;
import tk.trentoleaf.cineweb.db.UsersDB;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * WebListener that opens a pool of connections to the database at the application start.
 * Closes the connection at the application shutdown.
 */
@WebListener
public class DBListener implements ServletContextListener {
    private final Logger logger = Logger.getLogger(DBListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // get an instance of the database
        final DB db = DB.instance();

        try {
            // open the connections pool
            db.open();

            // create the first admin user if not exists
            UsersDB.instance().createAdminUser();

            // load default prices
            PricesDB.instance().loadDefaultPrices();

        } catch (RuntimeException | SQLException | URISyntaxException e) {
            logger.severe("Cannot open the connection to the database -> " + e.toString());
            logger.warning("Try to set the Environment variable DATABASE_URL to 'postgres://user:password@localhost:5432/db'");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        // get the database instance
        final DB db = DB.instance();

        try {
            // close the connections
            db.close();

        } catch (NullPointerException | SQLException e) {
            logger.severe("Cannot close the connection to the database");
            throw new RuntimeException(e);
        }
    }
}
