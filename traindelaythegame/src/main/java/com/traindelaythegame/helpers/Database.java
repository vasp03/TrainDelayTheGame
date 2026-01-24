package com.traindelaythegame.helpers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.traindelaythegame.models.Cords;
import com.traindelaythegame.models.MapData;

public class Database {
    Gson gson = new Gson();

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
        creatGameMapTable();
        createMapPolygonElement();
        createPublicTransportStopsTable();
    }

    private void creatGameMapTable() {
        String sql = "CREATE TABLE IF NOT EXISTS GameMap (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	name TEXT NOT NULL UNIQUE\n"
                + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create GameMap table!");
        }
    }

    private void createMapPolygonElement() {
        String sql = "CREATE TABLE IF NOT EXISTS MapPolygonElement (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	gameMapId INTEGER NOT NULL,\n"
                + "	lat REAL NOT NULL,\n"
                + "	lon REAL NOT NULL\n"
                + ");";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create MapPOA table!");
        }
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

    /**
     * Add all public transport stops from the stops.json file to the database
     */
    private void addAllStopsToDatabase() {
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

    /**
     * Get all public transport stops within the given latitude and longitude bounds
     * 
     * @param minLat
     * @param maxLat
     * @param minLon
     * @param maxLon
     * @return JsonArray of stops
     */
    public JsonArray getStopsInBoundsAsJson(double minLat, double maxLat, double minLon,
            double maxLon, String exclusion) {
        List<JsonObject> stops = new ArrayList<>();
        String query = "SELECT * FROM PublicTransportStops WHERE "
                + "latitude BETWEEN ? AND ? AND "
                + "longitude BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, minLat);
            stmt.setDouble(2, maxLat);
            stmt.setDouble(3, minLon);
            stmt.setDouble(4, maxLon);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String rowType = rs.getString("type");

                if (exclusion != null && !exclusion.isEmpty()) {
                    String[] excludeTypes = exclusion.split(",");
                    String[] haveTypes = (rowType == null) ? new String[0] : rowType.split(",");

                    Set<String> excludeSet = new HashSet<>();
                    for (String t : excludeTypes) {
                        if (t != null && !t.trim().isEmpty()) {
                            excludeSet.add(t.trim().toLowerCase());
                        }
                    }

                    Set<String> haveSet = new HashSet<>();
                    for (String t : haveTypes) {
                        if (t != null && !t.trim().isEmpty()) {
                            haveSet.add(t.trim().toLowerCase());
                        }
                    }

                    // Only exclude the stop if ALL of its types are within the exclusion set.
                    // Example: stop types = {train, bus}, exclusion = {train} -> keep the stop.
                    boolean shouldExclude = false;
                    if (!haveSet.isEmpty() && !excludeSet.isEmpty()) {
                        if (excludeSet.containsAll(haveSet)) {
                            shouldExclude = true;
                        }
                    }

                    if (shouldExclude) {
                        continue; // skip stops that have only excluded types
                    }
                }

                JsonObject stop = new JsonObject();
                stop.addProperty("name", rs.getString("name"));
                stop.addProperty("longitude", rs.getDouble("longitude"));
                stop.addProperty("latitude", rs.getDouble("latitude"));
                stop.addProperty("type", rowType);

                stops.add(stop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gson.toJsonTree(stops).getAsJsonArray();
    }

    /**
     * Add a game map with polygon points to the database
     * 
     * @param name
     * @param polygonPoints
     */
    public void addGameMap(String name, Cords[] polygonPoints) throws SQLException {
        String sql = "INSERT INTO GameMap(name) VALUES(?)";
        int gameMapId = -1;

        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.executeUpdate();

        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            gameMapId = rs.getInt(1);
        }

        if (gameMapId != -1) {
            sql = "INSERT INTO MapPolygonElement(gameMapId, lat, lon) VALUES(?, ?, ?)";

            pstmt = connection.prepareStatement(sql);
            connection.setAutoCommit(false);

            for (Cords point : polygonPoints) {
                pstmt.setInt(1, gameMapId);
                pstmt.setDouble(2, point.getLat());
                pstmt.setDouble(3, point.getLon());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    /**
     * Get the polygon points of a game map by name
     * 
     * @param name
     * @return
     */
    public Cords[] getGameMap(String id) {
        String getMapSql = "SELECT * FROM GameMap WHERE id = ?";
        int gameMapId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(getMapSql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                gameMapId = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get game map: " + id);
            return null;
        }

        // Sorted by id to maintain order
        String getPolygonCordsSql = "SELECT * FROM MapPolygonElement WHERE gameMapId = ? ORDER BY id ASC";
        Cords[] polygonPoints = null;

        try (PreparedStatement pstmt = connection.prepareStatement(getPolygonCordsSql)) {
            pstmt.setInt(1, gameMapId);
            ResultSet rs = pstmt.executeQuery();

            List<Cords> pointsList = new ArrayList<>();

            while (rs.next()) {
                double lat = rs.getDouble("lat");
                double lon = rs.getDouble("lon");

                pointsList.add(new Cords(lat, lon));
            }

            polygonPoints = pointsList.toArray(new Cords[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get polygon points for game map: " + id);
            return null;
        }

        return polygonPoints;
    }

    /**
     * Remove a game map by id
     * 
     * @param id
     */
    public void removeGameMap(String name) {
        String deleteGameMap = "DELETE FROM GameMap WHERE name = ?";
        String deletePolygonPoints = "DELETE FROM MapPolygonElement WHERE gameMapId = (SELECT id FROM GameMap WHERE name = ?)";

        try (PreparedStatement pstmt1 = connection.prepareStatement(deletePolygonPoints);
                PreparedStatement pstmt2 = connection.prepareStatement(deleteGameMap)) {
            pstmt1.setString(1, name);
            pstmt1.executeUpdate();

            pstmt2.setString(1, name);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to remove game map with name: " + name);
        }
    }

    /**
     * Get all game maps from the database
     * 
     * @return MapData[]
     */
    public MapData[] getAllGameMaps() {
        String sql = "SELECT * FROM GameMap";
        ArrayList<MapData> gameMaps = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int gameMapId = rs.getInt("id");

                gameMaps.add(new MapData(name, gameMapId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to get all game maps");
        }

        return gameMaps.toArray(new MapData[0]);
    }
}
