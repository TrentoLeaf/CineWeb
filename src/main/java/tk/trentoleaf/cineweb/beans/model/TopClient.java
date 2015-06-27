package tk.trentoleaf.cineweb.beans.model;

public class TopClient {

    private int uid;
    private String firstName;
    private String secondName;
    private int tickets;
    private double spent;

    public TopClient() {
    }

    public TopClient(int uid, String firstName, String secondName, int tickets, double spent) {
        this.uid = uid;
        this.firstName = firstName;
        this.secondName = secondName;
        this.tickets = tickets;
        this.spent = spent;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
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
