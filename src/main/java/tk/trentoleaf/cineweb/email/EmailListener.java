package tk.trentoleaf.cineweb.email;

import com.sendgrid.SendGridException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

public class EmailListener implements ServletContextListener {
    private final Logger logger = Logger.getLogger(EmailListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            // create an Email Sender
            EmailSender.instance();

        } catch (SendGridException e) {
            logger.severe("Cannot configure SendGrid -> " + e.toString());
            logger.warning("Try to set the Environment variable SENDGRID_USERNAME & SENDGRID_PASSWORD");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // do nothing
    }
}
