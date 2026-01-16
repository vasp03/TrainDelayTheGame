package com.traindelaythegame.helpers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Database {
    Gson gson = new Gson();

    // TODO: Add interrupts and queue to handle database operations asynchronously

    private Connection connection = null;

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("Failed to connect to database!");
        }

        createAllTables();

        addAllStopsToDatabase();
    }

    private void createAllTables() {
        // createPlayerTable();
        // createActiveQuestionTable();
        // createActiveCursesTable();
        // createCardInventoryTable();
        // createGameTable();
        // createPlayerHideTimeHistoryTable();
        // creatGameMapTable();
        // createMapPOATable();
        createPublicTransportStopsTable();
    }

    private void createPlayerTable() {
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerTable'");
    }

    private void createActiveQuestionTable() {
        throw new UnsupportedOperationException("Unimplemented method 'createActiveQuestionTable'");
    }

    private void createActiveCursesTable() {
        throw new UnsupportedOperationException("Unimplemented method 'createActiveCursesTable'");
    }

    private void createCardInventoryTable() {
        throw new UnsupportedOperationException("Unimplemented method 'createCardInventoryTable'");
    }

    private void createGameTable() {
        throw new UnsupportedOperationException("Unimplemented method 'createGameTable'");
    }

    private void createPlayerHideTimeHistoryTable() {
        throw new UnsupportedOperationException("Unimplemented method 'createPlayerHideTimeHistoryTable'");
    }

    private void creatGameMapTable() {
        throw new UnsupportedOperationException("Unimplemented method 'creatGameMapTable'");
    }

    private void createPublicTransportStopsTable() {
        String sql = "DROP TABLE IF EXISTS PublicTransportStops;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create PublicTransportStops table!");
        }

        sql = "CREATE TABLE IF NOT EXISTS PublicTransportStops (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	name TEXT NOT NULL,\n"
                + "	longitude REAL NOT NULL,\n"
                + "	latitude REAL NOT NULL,\n"
                + "	type TEXT NOT NULL\n"
                + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create PublicTransportStops table!");
        }
    }

    private void createMapPOATable() {
        throw new UnsupportedOperationException("Unimplemented method 'createMapPOATable'");
    }

    // TODO: On startup load stops data to be able to filter out stops that is
    // outside play area

    public void addAllStopsToDatabase() {
        String path = "other/stops.json";
        String sql = "INSERT OR REPLACE INTO PublicTransportStops(name, longitude, latitude, type) VALUES(?, ?, ?, ?)";

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);

        if (inputStream == null) {
            System.out.println("Stops data not found");
            return;
        }

        JsonObject jsonObject = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);
        JsonArray stopsArray = jsonObject.getAsJsonArray("stops");

        final int batchSize = 500;
        int count = 0;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (JsonElement jsonElement : stopsArray) {
                    JsonObject stopObject = jsonElement.getAsJsonObject();

                    String name = stopObject.get("name").getAsString();
                    String cords = stopObject.get("location").getAsString();
                    String type = stopObject.get("transportMode").getAsString();

                    String longitude = cords.split(",")[0].trim();
                    String latitude = cords.split(",")[1].trim();

                    pstmt.setString(1, name);
                    pstmt.setDouble(2, Double.parseDouble(longitude));
                    pstmt.setDouble(3, Double.parseDouble(latitude));
                    pstmt.setString(4, type);
                    pstmt.addBatch();

                    count++;
                    if (count % batchSize == 0) {
                        pstmt.executeBatch();
                        System.out.print(count + " ");
                    }
                }
                pstmt.executeBatch();
                System.out.println("Total stops inserted: " + count);
            }

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to prepare statement for inserting stops");

            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public JsonArray getStopsInBoundsAsJson(double minLat, double maxLat, double minLon,
            double maxLon) {
        List<JsonObject> stops = new ArrayList<>();
        String query = "SELECT * FROM PublicTransportStops WHERE "
                + "latitude BETWEEN ? AND ? AND "
                + "longitude BETWEEN ? AND ?;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, minLat);
            stmt.setDouble(2, maxLat);
            stmt.setDouble(3, minLon);
            stmt.setDouble(4, maxLon);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObject stop = new JsonObject();
                stop.addProperty("name", rs.getString("name"));
                stop.addProperty("longitude", rs.getDouble("longitude"));
                stop.addProperty("latitude", rs.getDouble("latitude"));
                stop.addProperty("type", rs.getString("type"));

                stops.add(stop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gson.toJsonTree(stops).getAsJsonArray();
    }
}
