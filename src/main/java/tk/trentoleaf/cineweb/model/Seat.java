package tk.trentoleaf.cineweb.model;

import java.io.Serializable;
import java.util.List;

public class Seat implements Serializable {

    private int id;
    private int rows;
    private int columns;

    private List<Place> places;

}
