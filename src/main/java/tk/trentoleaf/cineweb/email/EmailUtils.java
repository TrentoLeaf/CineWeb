package tk.trentoleaf.cineweb.email;

/**
 * Created by stefano on 30/05/15.
 */
public class EmailUtils {

    public static String registrationText (String firstName, String secondName, String url){
        StringBuilder sb =  new StringBuilder();

                sb.append("Gentile " + firstName + " " + secondName + ",\n " +
                "La Sua richiesta di iscrizione è stata inoltrata con successo." +
                "Per completare la Sua iscrizione prema questo link:\n\n" + url + "\n\n"
                );

        return sb.toString();
    }
}
