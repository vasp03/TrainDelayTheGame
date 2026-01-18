package com.traindelaythegame.api.v1.get;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.helpers.Database;
import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.models.MapData;

import io.javalin.http.Context;

public class GetAllMaps extends APIEndpoint {
    private TrainDelayTheGame app;

    public GetAllMaps(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/gamemaps";
    }

    @Override
    public void handle(Context ctx) throws UnsupportedOperationException {
        Database db = app.getDatabase();

        MapData[] allMaps = db.getAllGameMaps();

        JsonArray mapsJson = new JsonArray();
        for (MapData map : allMaps) {
            JsonObject mapObj = new JsonObject();
            mapObj.addProperty("id", map.getId());
            mapObj.addProperty("name", map.getName());
            mapsJson.add(mapObj);
        }

        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(mapsJson.toString());
    }
}
