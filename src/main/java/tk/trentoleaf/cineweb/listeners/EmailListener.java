package tk.trentoleaf.cineweb.listeners;

import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.email.EmailSender;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
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
