package tk.trentoleaf.cineweb.email;


import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import tk.trentoleaf.cineweb.email.pdf.FilmTicketData;
import tk.trentoleaf.cineweb.email.pdf.FullFillPDF;
import tk.trentoleaf.cineweb.model.User;
import tk.trentoleaf.cineweb.rest.utils.Utils;

import java.io.File;
import java.io.IOException;
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

    public static EmailSender instance() throws SendGridException {
        if (sender == null) {
            sender = new EmailSender();
        }
        return sender;
    }

    private EmailSender() throws SendGridException {
        final String username = System.getenv("SENDGRID_USERNAME");
        final String password = System.getenv("SENDGRID_PASSWORD");

        if (username == null || password == null) {
            throw new SendGridException(new RuntimeException("Please set the system variables SENDGRID_USERNAME & SENDGRID_PASSWORD"));
        }

        this.sendgrid = new SendGrid(username, password);
    }

    // send an email to confirm the registration
    public void sendRegistrationEmail(URI uri, User user, String verificationCode) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?c=" + verificationCode;
        final String urlLogo = Utils.uriToRoot(uri)+"/img/logo.png";

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Conferma registrazione");
        email.setText(EmailUtils.registrationText(user.getFirstName(), user.getSecondName(), url));
        email.setHtml("<html lang=\"it\">\n" +
                "<head>\n" +
                "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n" +
                "\t<title>CineWeb</title>\n" +
                "</head>\n" +
                "<body topmargin=\"0\" leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" style=\"-webkit-font-smoothing: antialiased;width:100% !important;background:#e4e4e4;-webkit-text-size-adjust:none;\">\n" +
                "\n" +
                "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#212121\"><tr><td width=\"100%\">\n" +
                "\n" +
                "\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"table\"><tr><td width=\"600\" class=\"cell\">\n" +
                "\n" +
                "\t\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"table\"><tr><td width=\"200\" class=\"logocell\"><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"20\" class=\"hide\"/><br class=\"hide\"/><img src=\""+urlLogo+" width=\"70\" height=\"70\" alt=\"Campaign Monitor\" style=\"-ms-interpolation-mode:bicubic;\"/><br/><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"10\" class=\"hide\"/><br class=\"hide\"/></td>\n" +
                "\t\t\t<td width=\"200\" align=\"center\" style=\"color:#ffea00;font-size:26px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;\">\n" +
                "\t\t\t\t<span><strong>CineWeb</strong></span>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td align=\"right\" width=\"200\" class=\"hide\" style=\"color:#ffea00;font-size:12px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;\"><span>HOME </span><strong><span style=\"text-transform:uppercase;\"/></strong> <span>TROVACI </span></td>\n" +
                "\t\t</tr></table><table width=\"600\" cellpadding=\"25\" cellspacing=\"0\" border=\"0\" class=\"promotable\"><tr><td bgcolor=\"#ffffff\" width=\"600\" class=\"promocell\">\n" +
                "\n" +
                "\t\t<multiline label=\"Main feature intro\"><p style=\"font-size: 16px;line-height: 26px;font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;margin-top: 0;margin-bottom: 0;padding-top: 0;padding-bottom: 14px;font-weight: normal\">Gentile "+user.getSecondName()+",<br/>Riceve questa email in quanto ha richiesto di iscriversi al nostro sito, La ringraziamo per la sua richiesta e nel farlo le proponiamo una selezione aggiornata dei nostri film in programmazione.<br/>Rinnoavandole i nostri ringraziamenti per la Sua registrazione le ricordiamo che il nostro team è a sua disposizione per qualsiasi dubbio o necessita.<br/>Cordiali Saluti,<br/>Il team di TrentoLeaf+</p></multiline></td>\n" +
                "\t</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/><table width=\"600\" cellpadding=\"25\" cellspacing=\"0\" border=\"0\" class=\"promotable\"><tr><td bgcolor=\"#ffffff\" width=\"600\" class=\"promocell\">\n" +
                "\n" +
                "\t\t<multiline label=\"Main feature intro\"><p style=\"font-size: 16px;line-height: 26px;font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;margin-top: 0;margin-bottom: 0;padding-top: 0;padding-bottom: 14px;font-weight: normal\">Riepilogo dati inseriti:<br/>Firstname: "+user.getFirstName()+",<br/>Secondname:"+user.getSecondName()+"<br/>Email:"+user.getEmail()+"<br/>Clicca il link qui sotto per finalizzare la tua iscrizione<br/>"+url+"</p></multiline></td>\n" +
                "\t</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/><layout label=\"Gallery highlights\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#ffea00\" nowrap=\"nowrap\"><img border=\"0\" src=\"images/spacer.gif\" width=\"5\" height=\"1\"/></td>\n" +
                "\t\t<td width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "\n" +
                "\t\t\t<table width=\"100%\" cellpadding=\"20\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#ffffff\" class=\"contentblock\">\n" +
                "\n" +
                "\t\t\t\t<h4 class=\"secondary\" style=\"color: #444 !important;font-size: 16px;line-height: 24px;font-family: &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif;margin-top: 0;margin-bottom: 10px;padding-top: 0;padding-bottom: 0;font-weight: normal\"><strong><singleline label=\"Gallery title\">Prossimamente nelle nostre migliori sale:</singleline></strong></h4>\n" +
                "\t\t\t\t<multiline label=\"Gallery description\"></multiline></td>\n" +
                "\t\t\t</tr></table></td>\n" +
                "\t</tr></table><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td bgcolor=\"#ffea00\" nowrap=\"nowrap\"><img border=\"0\" src=\"images/spacer.gif\" width=\"5\" height=\"1\"/></td>\n" +
                "\t\t<td bgcolor=\"#ffffff\" nowrap=\"nowrap\"><img border=\"0\" src=\"images/spacer.gif\" width=\"5\" height=\"1\"/></td>\n" +
                "\t\t<td width=\"100%\" bgcolor=\"#ffffff\">\n" +
                "\n" +
                "\t\t\t<table cellpadding=\"5\" cellspacing=\"0\" border=\"0\"><tr><td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 1\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 2\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 3\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 4\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 5\"/></td>\n" +
                "\t\t\t</tr><tr><td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 6\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 7\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 8\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 9\"/></td>\n" +
                "\t\t\t\t<td><img border=\"0\" src=\"images/gallery.png\" width=\"107\" height=\"107\" editable=\"true\" class=\"galleryimage\" label=\"Image 10\"/></td>\n" +
                "\t\t\t</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"5\"/><br/></td>\n" +
                "\t</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"15\" class=\"divider\"/><br/></layout></td>\n" +
                "\t</tr></table><img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"25\" class=\"divider\"/><br/><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" bgcolor=\"#f2f2f2\"><tr><td>\n" +
                "\n" +
                "\t<img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"30\"/><br/><table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"table\"><tr><td width=\"600\" nowrap=\"nowrap\" bgcolor=\"#f2f2f2\" class=\"cell\">\n" +
                "\n" +
                "\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"table\"><tr><td width=\"380\" valign=\"top\" class=\"footershow\">\n" +
                "\n" +
                "\t\t<img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"8\"/><br/><p style=\"color:#a6a6a6;font-size:12px;font-family:Helvetica,Arial,sans-serif;margin-top:0;margin-bottom:15px;padding-top:0;padding-bottom:0;line-height:18px;\" class=\"reminder\">Stai vedendo questo perché sei un mitico cliente del <a href=\"https://http://cineweb.herokuapp.com/\" style=\"color:#a6a6a6;text-decoration:underline;\">nostro sito</a> al quale ti sei iscritto.</p>\n" +
                "\t\t<p style=\"color:#c9c9c9;font-size:12px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;\"><preferences style=\"color:#3ca7dd;text-decoration:none;\"><strong>Modifica la tua iscrizione</strong></preferences>  |  <unsubscribe style=\"color:#3ca7dd;text-decoration:none;\"><strong>Disiscriviti</strong></unsubscribe></p>\n" +
                "\n" +
                "\n" +
                "\t</td>\n" +
                "\t\t<td align=\"right\" width=\"220\" style=\"color:#a6a6a6;font-size:12px;font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;text-shadow: 0 1px 0 #ffffff;\" valign=\"top\" class=\"hide\">\n" +
                "\n" +
                "\t\t\t<img border=\"0\" src=\"images/spacer.gif\" width=\"1\" height=\"10\"/><br/><p style=\"color:#b3b3b3;font-size:11px;line-height:15px;font-family:Helvetica,Arial,sans-serif;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;font-weight:bold;\">CineWeb</p><p style=\"color:#b3b3b3;font-size:11px;line-height:15px;font-family:Helvetica,Arial,sans-serif;margin-top:0;margin-bottom:0;padding-top:0;padding-bottom:0;font-weight:normal;\">Made by TrentoLeaf+</p></td>\n" +
                "\t</tr></table></td>\n" +
                "</tr></table></td>\n" +
                "</tr></table></td>\n" +
                "</tr></table></body>\n" +
                "</html>\n");
        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }

    // send a password recover
    public void sendRecoverPasswordEmail(URI uri, User user, String code) throws SendGridException {

        // create url
        final String url = Utils.uriToRoot(uri) + "/#?r=" + code;

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Recupero password");
        email.setText("TESTO... " + url);

        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }

    //send a pdf with the ticket
    public void sendTicketPDFEmail(URI uri, User user, FilmTicketData data) throws SendGridException, IOException {

        // create email
        SendGrid.Email email = new SendGrid.Email();
        email.setFrom(FROM);
        email.addTo(user.getEmail());
        email.setSubject(WE + " - Ticket acquistato");
        email.setText("Ecco a lei il Ticket in allegato in formato PDF!");
        //TODO: optionally add a cute html text

        email.addAttachment("Ticket", new FullFillPDF(data));

        // try to send, log failures
        try {
            sendgrid.send(email);
        } catch (SendGridException e) {
            logger.severe(e.toString());
            throw e;
        }
    }
}
