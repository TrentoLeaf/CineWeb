package tk.trentoleaf.cineweb.email;

public class EmailUtils {

    public static String registrationText(String firstName, String secondName, String url) {
        StringBuilder sb = new StringBuilder();

        sb.append("Gentile " + firstName + " " + secondName + ",\n ")
                .append("La Sua richiesta di iscrizione è stata inoltrata con successo.")
                .append("Per completare la Sua iscrizione prema questo link:\n\n")
                .append(url)
                .append("\n\n");

        return sb.toString();
    }
}
