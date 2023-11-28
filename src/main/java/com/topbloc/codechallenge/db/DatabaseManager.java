package com.topbloc.codechallenge.db;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseManager {
    private static final String jdbcPrefix = "jdbc:sqlite:";
    private static final String dbName = "challenge.db";
    private static String connectionString;
    private static Connection conn;
    private static JSONParser parser = new JSONParser();

    static {
        File dbFile = new File(dbName);
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
    }

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            System.out.println("Connection to SQLite has been established.");
            conn = connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Schema function to reset the database if needed - do not change
    public static void resetDatabase() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
        connect();
        applySchema();
        seedDatabase();
    }

    // Schema function to reset the database if needed - do not change
    private static void applySchema() {
        String itemsSql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String inventorySql = "CREATE TABLE IF NOT EXISTS inventory (\n"
                + "id integer PRIMARY KEY,\n"
                + "item integer NOT NULL UNIQUE references items(id) ON DELETE CASCADE,\n"
                + "stock integer NOT NULL,\n"
                + "capacity integer NOT NULL\n"
                + ");";
        String distributorSql = "CREATE TABLE IF NOT EXISTS distributors (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String distributorPricesSql = "CREATE TABLE IF NOT EXISTS distributor_prices (\n"
                + "id integer PRIMARY KEY,\n"
                + "distributor integer NOT NULL references distributors(id) ON DELETE CASCADE,\n"
                + "item integer NOT NULL references items(id) ON DELETE CASCADE,\n"
                + "cost float NOT NULL\n" +
                ");";

        try {
            System.out.println("Applying schema");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Schema applied");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Schema function to reset the database if needed - do not change
    private static void seedDatabase() {
        String itemsSql = "INSERT INTO items (id, name) VALUES (1, 'Licorice'), (2, 'Good & Plenty'),\n"
                + "(3, 'Smarties'), (4, 'Tootsie Rolls'), (5, 'Necco Wafers'), (6, 'Wax Cola Bottles'), (7, 'Circus Peanuts'), (8, 'Candy Corn'),\n"
                + "(9, 'Twix'), (10, 'Snickers'), (11, 'M&Ms'), (12, 'Skittles'), (13, 'Starburst'), (14, 'Butterfinger'), (15, 'Peach Rings'), (16, 'Gummy Bears'), (17, 'Sour Patch Kids')";
        String inventorySql = "INSERT INTO inventory (item, stock, capacity) VALUES\n"
                + "(1, 22, 25), (2, 4, 20), (3, 15, 25), (4, 30, 50), (5, 14, 15), (6, 8, 10), (7, 10, 10), (8, 30, 40), (9, 17, 70), (10, 43, 65),\n"
                +
                "(11, 32, 55), (12, 25, 45), (13, 8, 45), (14, 10, 60), (15, 20, 30), (16, 15, 35), (17, 14, 60)";
        String distributorSql = "INSERT INTO distributors (id, name) VALUES (1, 'Candy Corp'), (2, 'The Sweet Suite'), (3, 'Dentists Hate Us')";
        String distributorPricesSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES \n" +
                "(1, 1, 0.81), (1, 2, 0.46), (1, 3, 0.89), (1, 4, 0.45), (2, 2, 0.18), (2, 3, 0.54), (2, 4, 0.67), (2, 5, 0.25), (2, 6, 0.35), (2, 7, 0.23), (2, 8, 0.41), (2, 9, 0.54),\n"
                +
                "(2, 10, 0.25), (2, 11, 0.52), (2, 12, 0.07), (2, 13, 0.77), (2, 14, 0.93), (2, 15, 0.11), (2, 16, 0.42), (3, 10, 0.47), (3, 11, 0.84), (3, 12, 0.15), (3, 13, 0.07), (3, 14, 0.97),\n"
                +
                "(3, 15, 0.39), (3, 16, 0.91), (3, 17, 0.85)";

        try {
            System.out.println("Seeding database");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Database seeded");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper methods to convert ResultSet to JSON - change if desired, but should
    // not be required
    private static JSONArray convertResultSetToJson(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<String> colNames = IntStream.range(0, columns)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            jsonArray.add(convertRowToJson(rs, colNames));
        }
        return jsonArray;
    }

    private static JSONObject convertRowToJson(ResultSet rs, List<String> colNames) throws SQLException {
        JSONObject obj = new JSONObject();
        for (String colName : colNames) {
            obj.put(colName, rs.getObject(colName));
        }
        return obj;
    }

    // Controller functions - add your routes here. getItems is provided as an
    // example
    public static JSONArray getItems() {
        String sql = "SELECT * FROM items";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * All items in inventory, including the item name, ID, amount in stock, and
     * total capacity.
     */
    public static JSONArray getInventory() {
        String sql = "SELECT item.id, item.name, inventory.stock, inventory.capacity FROM items AS item INNER JOIN inventory ON item.id = inventory.item";

        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * Gets all items in inventory that are currently out of stock, including the
     * item name, ID, amount in stock, and total capacity
     */
    public static JSONArray getOutOfStock() {
        String sql = "SELECT item.id, item.name, inventory.stock, inventory.capacity FROM items AS item INNER JOIN inventory ON item.id = inventory.item WHERE inventory.stock = 0";

        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * All items in your inventory that are currently overstocked (Assuming
     * overstocked is where stock > 35), including the
     * item name, ID, amount in stock, and total capacity
     */
    public static JSONArray getOverStock() {
        String sql = "SELECT item.id, item.name, inventory.stock, inventory.capacity " +
                "FROM items AS item " +
                "INNER JOIN inventory ON item.id = inventory.item " +
                "WHERE inventory.stock > 35";

        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * Gets all items in inventory that are currently low on stock (<35%),
     * including the item name, ID, amount in stock, and total capacity
     */
    public static JSONArray getUnderStock() {
        String sql = "SELECT item.id, item.name, inventory.stock, inventory.capacity " +
                "FROM items AS item " +
                "INNER JOIN inventory ON item.id = inventory.item " +
                "WHERE inventory.stock < 35";

        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * A dynamic route that, when given an ID, returns the item name, ID, amount
     * in stock, and total capacity of that item
     */
    public static JSONArray getItemById(String itemId) {
        Integer id = Integer.parseInt(itemId);
        String sql = "SELECT item.id, item.name, inventory.stock, inventory.capacity " +
                "FROM items AS item " +
                "INNER JOIN inventory ON item.id = inventory.item " +
                "WHERE item.id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            return convertResultSetToJson(set);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * Gets all distributors, including the id and name
     */
    public static JSONArray getDistributors() {
        String sql = "SELECT * FROM distributors";
        try {

            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * A dynamic route that, given a distributors ID, returns the items distributed
     * by a given distributor, including the item name, ID, and cost
     */
    public static JSONArray getDistributorItemsByDistributorId(String itemId) {
        Integer id = Integer.parseInt(itemId);
        String sql = "SELECT items.id AS item_id, items.name AS item_name, distributor_prices.cost AS distributor_cost FROM distributor_prices "
                +
                "INNER JOIN items ON distributor_prices.item = items.id " +
                "WHERE distributor_prices.distributor = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            return convertResultSetToJson(set);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /*
     * A dynamic route that, given an item ID, returns all offerings from all
     * distributors for that item, including the distributor name, ID, and cost
     */
    public static JSONArray getDistributorItemsByItemId(String itemId) {
        // Parse data
        Integer id = Integer.parseInt(itemId);

        // Execute SQL
        String sql = "SELECT distributors.id AS distributor_id, distributors.name AS distributor_name, distributor_prices.cost "
                + "FROM distributor_prices " +
                "INNER JOIN items ON distributor_prices.item = items.id " +
                "INNER JOIN distributors ON distributor_prices.distributor = distributors.id " +
                "WHERE distributor_prices.item = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            return convertResultSetToJson(set);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /* Helper that returns the last index from specified table. */
    private static int getLastIndex(String tableName) throws SQLException {
        String sql = "SELECT MAX(id) FROM " + tableName;

        try (PreparedStatement statement = conn.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                // Return -1 if no results
                return -1;
            }
        }
    }

    /* Helper to save item to database. */
    public static Integer executeCreateItem(String name) throws SQLException {
        // Increment index
        int lastId = getLastIndex("items");
        int nextId = lastId + 1;

        // Execute SQL
        String sql = "INSERT INTO items (id, name) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, nextId);
            statement.setString(2, name);
            int rowsAdded = statement.executeUpdate();

            // Return index of item inserted
            return (rowsAdded > 0) ? nextId : null;
        }

    }

    /* Add a new item to items table */
    public static Integer createItem(String itemData) throws ParseException {
        try {
            // Extract data from params
            JSONObject data = (JSONObject) parser.parse(itemData);
            String name = (String) data.get("name");
            Integer itemId = executeCreateItem(name);

            if (itemId != null) {
                return itemId;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Helper to save inventory to database. */
    public static Integer executeSaveInventory(Integer item, Integer stock, Integer capacity) throws SQLException {
        // Increment index
        int lastId = getLastIndex("inventory");
        int nextId = lastId + 1;

        // Execute SQL
        String sql = "INSERT INTO inventory (id, item, stock, capacity) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, nextId);
            statement.setInt(2, item);
            statement.setInt(3, stock);
            statement.setInt(4, capacity);

            int rowsAdded = statement.executeUpdate();

            return (rowsAdded > 0) ? nextId : null;
        }

    }

    /* Add a new item to inventory table */
    public static Integer createInventory(String inventoryData) throws ParseException {
        try {
            // Extract + cast inventoryData
            JSONObject data = (JSONObject) parser.parse(inventoryData);
            Integer item = ((Long) data.get("item")).intValue();
            Integer stock = ((Long) data.get("stock")).intValue();
            Integer capacity = ((Long) data.get("capacity")).intValue();

            Integer inventoryId = executeSaveInventory(item, stock, capacity);

            if (inventoryId != null) {
                return inventoryId;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Helper to modify an existing item in inventory. */
    public static Integer executeUpdateInventory(Integer inventoryId, Integer stock, Integer capacity)
            throws SQLException {
        String sql = "UPDATE inventory SET stock = ?, capacity = ? WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, inventoryId);
            statement.setInt(2, stock);
            statement.setInt(3, capacity);

            int updatedCount = statement.executeUpdate();

            return (updatedCount > 0) ? inventoryId : null;

        }

    }

    /* Modify an existing item in inventory */
    public static Integer modifyInventory(String inventoryId, String inventoryData) throws ParseException {
        try {
            // Extract data from inventoryData
            Integer id = Integer.parseInt(inventoryId);
            JSONObject data = (JSONObject) parser.parse(inventoryData);

            // Cast int values if keys exist in inventoryData
            Integer updatedInventoryId = executeUpdateInventory(
                    id,
                    data.containsKey("stock") ? ((Long) data.get("stock")).intValue() : null,
                    data.containsKey("capacity") ? ((Long) data.get("capacity")).intValue() : null);

            return updatedInventoryId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Helper to save distributor to database. */
    public static Integer saveDistributor(String name) throws SQLException {
        // Increment index
        int lastId = getLastIndex("distributors");
        int nextId = lastId + 1;

        String sql = "INSERT INTO distributors (id, name) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, nextId);
            statement.setString(2, name);

            int rowsAdded = statement.executeUpdate();

            return (rowsAdded > 0) ? nextId : null;
        }

    }

    /* Adds a distributor */
    public static Integer createDistributor(String distributorData) throws ParseException {
        try {
            JSONObject data = (JSONObject) parser.parse(distributorData);
            String name = (String) data.get("name");

            Integer distributorId = saveDistributor(name);

            return (distributorId != null) ? distributorId : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Helper to save distributor item to database. */
    public static Integer executeSaveDistributorItem(Integer distributor, Integer item, float cost)
            throws SQLException {
        // Increment index
        int lastId = getLastIndex("distributor_prices");
        int nextId = lastId + 1;

        // Execute sql
        String sql = "INSERT INTO distributor_prices (id, distributor, item, cost) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, nextId);
            statement.setInt(2, distributor);
            statement.setInt(3, item);
            statement.setFloat(4, cost);

            int rowsAdded = statement.executeUpdate();

            // Return item id if row added
            return (rowsAdded > 0) ? nextId : null;
        }

    }

    /* Add items to a distributor's catalog (including the cost) */
    public static Integer createDistributorItem(String distributorItemData) throws ParseException {
        try {
            // Extract + cast data from distributorItemData
            JSONObject data = (JSONObject) parser.parse(distributorItemData);
            Integer distributor = ((Long) data.get("distributor")).intValue();
            Integer item = ((Long) data.get("item")).intValue();
            float cost = ((Number) data.get("cost")).floatValue();

            Integer distributorId = executeSaveDistributorItem(distributor, item, cost);

            return (distributorId != null) ? distributorId : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Helper to modify the price of an item in a distributor's catalog. */
    private static Integer executeUpdateDistributorItem(Integer inventoryId, float cost) throws SQLException {
        String sql = "UPDATE distributor_prices SET cost = ? WHERE id = ?";

        // Execute sql
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setFloat(1, cost);
            statement.setInt(2, inventoryId);

            int updatedCount = statement.executeUpdate();

            // Return updatedCount if update successful
            return (updatedCount > 0) ? inventoryId : null;

        }

    }

    /* Modify the price of an item in a distributor's catalog */
    public static Integer modifyDistributorItem(String distributorPriceId, String distributorPriceData)
            throws ParseException {
        try {
            Integer id = Integer.parseInt(distributorPriceId);
            JSONObject data = (JSONObject) parser.parse(distributorPriceData);
            float cost = (float) data.get("cost");

            Integer distributorItemId = executeUpdateDistributorItem(id, cost);

            return (distributorItemId != null) ? distributorItemId : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the cheapest price for restocking an item at a given quantity from all
     * distributors
     */
    public static JSONArray getCheapestPrice(String quantityAmount, String itemId) throws SQLException, ParseException {
        // Parse data
        Integer quantity = Integer.parseInt(quantityAmount);
        Integer id = Integer.parseInt(itemId);

        // Execute sql
        String sql = "SELECT item.id AS item_id, item.name AS item_name, MIN(distributor_prices.cost * ?) AS cheapest_price "
                +
                "FROM items AS item " +
                "INNER JOIN distributor_prices ON item.id = distributor_prices.item " +
                "INNER JOIN distributors ON distributor_prices.distributor = distributors.id " +
                "WHERE item.id = ? " +
                "GROUP BY item.id, item.name";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, id);

            ResultSet set = statement.executeQuery();
            return convertResultSetToJson(set);

        }
    }

    /* Delete an existing item from your inventory */
    public static Integer deleteItem(String itemId) throws SQLException, ParseException {
        Integer id = Integer.parseInt(itemId);
        String sql = "DELETE FROM inventory WHERE inventory.item = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);

            int deletedCount = statement.executeUpdate();

            return (deletedCount > 0) ? id : null;

        }
    }

    /* Delete an existing distributor given their ID */
    public static Integer deleteDistributor(String distributorId) throws SQLException, ParseException {
        Integer id = Integer.parseInt(distributorId);
        String sql = "DELETE FROM distributors WHERE id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);

            int deletedCount = statement.executeUpdate();

            return (deletedCount > 0) ? id : null;

        }
    }
}
