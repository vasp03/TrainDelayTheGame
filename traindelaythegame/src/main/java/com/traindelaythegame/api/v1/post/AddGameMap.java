package com.traindelaythegame.api.v1.post;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.helpers.Database;
import com.traindelaythegame.models.APIEndpoint;
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

        if (polygonPointsJson == null || polygonPointsJson.isEmpty()) {
            ctx.status(400);
            ctx.json("Missing 'polygonPoints' form parameter.");
            return;
        }

        Database db = app.getDatabase();
        String[] pointPairs = polygonPointsJson.split(";");
        Cords[] polygonPoints = new Cords[pointPairs.length];

        for (int i = 0; i < pointPairs.length; i++) {
            String[] coords = pointPairs[i].split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            polygonPoints[i] = new Cords(x, y);
        }

        db.addGameMap(name, polygonPoints);

        ctx.status(200);
    }
}
