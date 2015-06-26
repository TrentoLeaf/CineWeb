package tk.trentoleaf.cineweb.db;

import tk.trentoleaf.cineweb.beans.model.Price;
import tk.trentoleaf.cineweb.exceptions.db.DBException;
import tk.trentoleaf.cineweb.exceptions.db.EntryNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the prices storage. It uses the connections provided by the DB class.
 *
 * @see DB
 */
public class PricesDB {

    // get a DB instance
    protected DB db = DB.instance();

    // instance -> singleton pattern
    private static PricesDB instance;

    // instance -> singleton pattern
    public static PricesDB instance() {
        if (instance == null) {
            instance = new PricesDB();
        }
        return instance;
    }

    // force to use the singleton
    private PricesDB() {
    }

    // load default prices
    public void loadDefaultPrices() {
        try {
            for (Price p : Price.DEFAULT_PRICES) {
                createPrice(p);
            }
        } catch (DBException e) {
            // do nothing
        }
    }

    // create price
    public void createPrice(Price price) throws DBException {
        final String query = "INSERT INTO prices (type, price) VALUES (?, ?)";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, price.getType());
            stm.setDouble(2, price.getPrice());
            stm.execute();
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get price by type
    public Price getPrice(String type) throws DBException, EntryNotFoundException {
        final String query = "SELECT price FROM prices WHERE type = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, type);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return new Price(type, rs.getDouble("price"));
            }

            // no price
            throw new EntryNotFoundException();

        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // get prices
    public List<Price> getPrices() throws DBException {
        List<Price> prices = new ArrayList<>();

        try (Connection connection = db.getConnection(); Statement stm = connection.createStatement()) {
            ResultSet rs = stm.executeQuery("SELECT type, price FROM prices;");

            while (rs.next()) {
                Price price = new Price();
                price.setType(rs.getString("type"));
                price.setPrice(rs.getDouble("price"));
                prices.add(price);
            }

        } catch (SQLException e) {
            throw DBException.factory(e);
        }

        return prices;
    }

    // edit price
    public void updatePrice(Price price) throws DBException, EntryNotFoundException {
        final String query = "UPDATE prices SET price = ? WHERE type = ?;";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setDouble(1, price.getPrice());
            stm.setString(2, price.getType());

            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

    // delete price
    public void deletePrice(Price price) throws DBException, EntryNotFoundException {
        deletePrice(price.getType());
    }

    // delete price
    public void deletePrice(String type) throws DBException, EntryNotFoundException {
        final String query = "DELETE FROM prices WHERE type = ?";

        try (Connection connection = db.getConnection(); PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setString(1, type);
            int rows = stm.executeUpdate();
            if (rows != 1) {
                throw new EntryNotFoundException();
            }
        } catch (SQLException e) {
            throw DBException.factory(e);
        }
    }

}