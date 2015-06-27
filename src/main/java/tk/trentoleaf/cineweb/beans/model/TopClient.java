package tk.trentoleaf.cineweb.beans.model;

public class TopClient {

    public int uid;
    public int tickets;
    public double spent;

    public TopClient() {
    }

    public TopClient(int uid, int tickets, double spent) {
        this.uid = uid;
        this.tickets = tickets;
        this.spent = spent;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
        return Double.compare(client.spent, spent) == 0;
    }
}
