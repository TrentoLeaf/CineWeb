package tk.trentoleaf.cineweb.listeners;

import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.email.EmailSender;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * WebListener that checks the correct configuration of SendGrid. If the needed Environment Variables are absent
 * or the provided credentials are wrong, print a warning.
 */
@WebListener
public class EmailListener implements ServletContextListener {
    private final Logger logger = Logger.getLogger(EmailListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            // create an Email Sender
            EmailSender.instance();

        } catch (SendGridException e) {
            System.out.println("################################################################################");
            System.out.println("#                                                                              #");
            System.out.println("#  Cannot configure SendGrid -> missing or wrong credentials                   #");
            System.out.println("#  Please, set the Environment variable SENDGRID_USERNAME & SENDGRID_PASSWORD  #");
            System.out.println("#  No email can be sent during this run...                                     #");
            System.out.println("#                                                                              #");
            System.out.println("################################################################################");

            System.err.println("################################################################################");
            System.err.println("#                                                                              #");
            System.err.println("#  Cannot configure SendGrid -> missing or wrong credentials                   #");
            System.err.println("#  Please, set the Environment variable SENDGRID_USERNAME & SENDGRID_PASSWORD  #");
            System.err.println("#  No email can be sent during this run...                                     #");
            System.err.println("#                                                                              #");
            System.err.println("################################################################################");

            // log the exception for debug purposes
            logger.severe("SendGridException -> " + e.toString());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
