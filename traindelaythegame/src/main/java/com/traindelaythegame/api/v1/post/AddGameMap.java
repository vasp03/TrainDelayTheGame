package com.traindelaythegame.api.v1.post;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.helpers.Database;
import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.helpers.Security;
import com.traindelaythegame.models.Cords;

import io.javalin.http.Context;

public class AddGameMap extends APIEndpoint {
    private TrainDelayTheGame app;

    public AddGameMap(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/gamemap";
    }

    @Override
    public void handle(Context ctx) throws UnsupportedOperationException {
        String name = ctx.queryParam("name");
        String polygonPointsJson = ctx.queryParam("polygonPoints"); // Formatted: 23.1231,42.1231;24.1231,43.1231;...

        if (name == null || name.isEmpty()) {
            ctx.status(400);
            ctx.json("Missing 'name' form parameter.");
            return;
        }

        if (!Security.isValidApiRequest(name)) {
            ctx.status(400);
            ctx.json("Invalid 'name' parameter.");
            return;
        }

        if (polygonPointsJson == null || polygonPointsJson.isEmpty()) {
            ctx.status(400);
            ctx.json("Missing 'polygonPoints' form parameter.");
            return;
        }

        if (!Security.isValidNumericList(polygonPointsJson)) {
            ctx.status(400);
            ctx.json("Invalid 'polygonPoints' parameter.");
            return;
        }

        Database db = app.getDatabase();
        String[] pointPairs = polygonPointsJson.split(";");
        Cords[] polygonPoints = new Cords[pointPairs.length];

        for (int i = 0; i < pointPairs.length; i++) {
            String[] coords = pointPairs[i].split(",");
            try {
                double x = Double.parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                polygonPoints[i] = new Cords(x, y);
            } catch (Exception e) {
                ctx.status(400);
                ctx.json("Invalid coordinate pair: " + pointPairs[i]);
                return;
            }
        }

        try {
            db.addGameMap(name, polygonPoints);
        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Failed to add game map: " + e.getMessage());
            return;
        }

        ctx.status(200);
    }
}
