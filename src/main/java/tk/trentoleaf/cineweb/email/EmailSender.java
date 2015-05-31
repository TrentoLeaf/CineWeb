package tk.trentoleaf.cineweb.email;


import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.rest.utils.Utils;

import java.net.URI;
import java.util.logging.Logger;

public class EmailSender {
    private static final Logger logger = Logger.getLogger(EmailSender.class.getSimpleName());

    // constants
    public static final String WE = "Cineweb";
    public static final String FROM = "cineweb@trentoleaf.tk";

    private SendGrid sendgrid;

    // singleton
    private static EmailSender sender;

    public static EmailSender instance() {
        if (sender == null) {
            sender = new EmailSender();
        }
        return sender;
    }

    private EmailSender() {
        final String username = System.getenv("SENDGRID_USERNAME");
        final String password = System.getenv("SENDGRID_PASSWORD");

        if (username == null || password == null) {
            throw new RuntimeException("Please set the system variables SENDGRID_USERNAME & SENDGRID_PASSWORD");
        }

        this.sendgrid = new SendGrid(username, password);
    }

    // send an email to confirm the registration
    public void sendRegistrationEmail(URI uri, User user, String verificationCode) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?c=" + verificationCode;

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Conferma registrazione");
        email.setText(EmailUtils.registrationText(user.getFirstName(), user.getSecondName(), url));
        email.setHtml(
                "<html lang=\"it\">"+
                "<head>"+
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"+
                "<title>CineWeb</title>"+
                "</head>"+
                "<body topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" style=\"-webkit-font-smoothing: antialiased;width:100% !important;background:#e4e4e4;-webkit-text-size-adjust:none;\">"+

                "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#212121\"><tr><td width=\"100%\">"+

                "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"table\"><tr><td width=\"600\" class=\"cell\">"+

                "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"table\"><tr><td width=\"200\" class=\"logocell\"><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"20\" class=\"hide\"/><br class=\"hide\"/><img src=\"logo.png\" width=\"70\" height=\"70\" alt=\"Campaign Monitor\" style=\"-ms-interpolation-mode:bicubic;\"/><br/><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"10\" class=\"hide\"/><br class=\"hide\"/></td>"+
                "<td width=\"200\" align=\"center\" style=\"color:#ffea00;font-size:26px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;\">"+
                "<span><strong>CineWeb</strong></span>"+
                "</td>"+
                "<td align=\"right\" width=\"200\" class=\"hide\" style=\"color:#ffea00;font-size:12px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;\"><span>HOME </span><strong><span style=\"text-transform:uppercase;\"/></strong> <span>TROVACI </span></td>"+
                "</tr></table><table width=\"600\" cellpadding=\"25\" cellspacing=\"0\" border=\"0\" class=\"promotable\"><tr><td bgcolor=\"#ffffff\" width=\"600\" class=\"promocell\">"+

                "<multiline label=\"Main feature intro\"><p style=\"font-size: 16px;line-height: 26px;font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;margin-top: 0;margin-bottom: 0;padding-top: 0;padding-bottom: 14px;font-weight: normal\">Ecco qui il tuo biglietto, direttamente da CineWeb un designer di altissimo lineeaggio lo ha disegnato a posto per te dopo essere stato frustato esattamente 13 volte.</p></multiline></td>"+
                "</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/><img border=\"0\" src=\"download.jpg\" label=\"Hero image\" editable=\"true\" width=\"600\" height=\"253\" id=\"screenshot\"/><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/><layout label=\"Gallery highlights\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#ffea00\" nowrap=\"nowrap\"><img border=\"0\" src=\"images/spacer.gif\" width=\"5\" height=\"1\"/></td>"+
                "<td width=\"100%\" bgcolor=\"#ffffff\">"+

                "<table width=\"100%\" cellpadding=\"20\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#ffffff\" class=\"contentblock\">"+

                "<h4 class=\"secondary\" style=\"color: #444 !important;font-size: 16px;line-height: 24px;font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;margin-top: 0;margin-bottom: 10px;padding-top: 0;padding-bottom: 0;font-weight: normal\"><strong><singleline label=\"Gallery title\">Title of gallery summary</singleline></strong></h4>"+
                "<multiline label=\"Gallery description\"><p style=\"color: #888;font-size: 13px;line-height: 19px;font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;margin-top: 0;margin-bottom: 12px;padding-top: 0;padding-bottom: 0;font-weight: normal\">Description of this month's gallery entries</p></multiline></td>"+
                        "</tr></table></td>"+
                "</tr></table><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#ffea00\" nowrap=\"nowrap\"><img border=\"0\" src=\"images/spacer.gif\" width=\"5\" height=\"1\"/></td>"+
                "<td bgcolor=\"#ffffff\" nowrap=\"nowrap\"><img border=\"0\" src=\"images/spacer.gif\" width=\"5\" height=\"1\"/></td>"+
                "<td width=\"100%\" bgcolor=\"#ffffff\">"+

                "<table cellpadding=\"5\" cellspacing=\"0\" border=\"0\"><tr><td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 1\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 2\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 3\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 4\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 5\"/></td>"+
                "</tr><tr><td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 6\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 7\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 8\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 9\"/></td>"+
                "<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 10\"/></td>"+
                "</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"5\"/><br/></td>"+
                "</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/></layout></td>"+
                "</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"25\" class=\"divider\"/><br/><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#f2f2f2\"><tr><td>"+

                "<img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"30\"/><br/><table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"table\"><tr><td width=\"600\" nowrap=\"nowrap\" bgcolor=\"#f2f2f2\" class=\"cell\">"+

                "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"table\"><tr><td width=\"380\" valign=\"top\" class=\"footershow\">"+

                "<img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"8\"/><br/><p style=\"color:#a6a6a6;font-size:12px;font-family:Helvetica,Arial,sans-serif;margin-top:0;margin-bottom:15px;padding-top:0;padding-bottom:0;line-height:18px;\" class=\"reminder\">Stai vedendo questo perché sei un mitico cliente del <a href=\"http://www.abcwidgets.com/\" style=\"color:#a6a6a6;text-decoration:underline;\">nostro sito</a> al quale ti sei iscritto.</p>"+
                "<p style=\"color:#c9c9c9;font-size:12px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;\"><preferences style=\"color:#3ca7dd;text-decoration:none;\"><strong>Modifica la tua iscrizione</strong></preferences>  |  <unsubscribe style=\"color:#3ca7dd;text-decoration:none;\"><strong>Disiscriviti</strong></unsubscribe></p>"+


                "</td>"+
                "<td align=\"right\" width=\"220\" style=\"color:#a6a6a6;font-size:12px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;text-shadow: 0 1px 0 #ffffff;\" valign=\"top\" class=\"hide\">"+

                "<img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"10\"/><br/><p style=\"color:#b3b3b3;font-size:11px;line-height:15px;font-family:Helvetica,Arial,sans-serif;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;font-weight:bold;\">CineWeb</p><p style=\"color:#b3b3b3;font-size:11px;line-height:15px;font-family:Helvetica,Arial,sans-serif;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;font-weight:normal;\">Made by TrentoLeaf+</p></td>"+
                "</tr></table></td>"+
                "</tr></table></td>"+
                "</tr></table></td>"+
                "</tr></table></body>"+
                "</html>"
        );
        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }
}
