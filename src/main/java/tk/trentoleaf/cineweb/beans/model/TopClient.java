package tk.trentoleaf.cineweb.beans.model;

/**
 * This class represent a TopClient, one of the top 10 clients (for spent money).
 */
public class TopClient {

    private int uid;
    private String firstName;
    private String secondName;
    private int tickets;
    private double spent;

    /**
     * Construct a new TopClient.
     *
     * @param uid        ID of the User.
     * @param firstName  First Name.
     * @param secondName Second Name.
     * @param tickets    Number of bought tickets.
     * @param spent      Total spent money.
     */
    public TopClient(int uid, String firstName, String secondName, int tickets, double spent) {
        this.uid = uid;
        this.firstName = firstName;
        this.secondName = secondName;
        this.tickets = tickets;
        this.spent = spent;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopClient client = (TopClient) o;

        if (uid != client.uid) return false;
        if (tickets != client.tickets) return false;
        if (Double.compare(client.spent, spent) != 0) return false;
        if (!firstName.equals(client.firstName)) return false;
        return secondName.equals(client.secondName);
    }

}
