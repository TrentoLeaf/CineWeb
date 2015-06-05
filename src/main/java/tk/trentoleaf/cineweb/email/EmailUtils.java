package tk.trentoleaf.cineweb.email;

public class EmailUtils {

    public static String registrationText(String firstName, String secondName, String url) {
        StringBuilder sb = new StringBuilder();

        sb.append("Gentile " + firstName + " " + secondName + ",\n ")
                .append("La Sua richiesta di iscrizione è stata inoltrata con successo.")
                .append("Per completare la Sua iscrizione clicchi su questo link:\n\n")
                .append(url)
                .append("\n\n");

        return sb.toString();
    }


    public static String resetPasswordText(String url) {
        StringBuilder sb =new StringBuilder();

        sb.append("Come da sua richiesta le è stata mandata la possibilità di ripristinare la sua password.\n" +
                "Per completare questa operazione clicchi sul link sottostante.\n\n" + url + "\n\n ");

        return sb.toString();
    }
}