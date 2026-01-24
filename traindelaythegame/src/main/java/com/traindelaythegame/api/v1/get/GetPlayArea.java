package com.traindelaythegame.api.v1.get;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.helpers.Security;
import com.traindelaythegame.models.Cords;

import io.javalin.http.Context;

public class GetPlayArea extends APIEndpoint {
    private TrainDelayTheGame app;

    public GetPlayArea(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/playarea";
    }

    @Override
    public void handle(Context ctx) throws UnsupportedOperationException {
        // TODO: Add method to get game based on the player requesting the playarea and
        // get map name from game.

        String map = ctx.queryParam("map");

        if (map == null || map.isEmpty()) {
            ctx.status(400);
            ctx.json("Missing 'map' path parameter.");
            return;
        }

        if (!Security.isValidApiRequest(map)) {
            ctx.status(400);
            ctx.json("Invalid 'map' parameter.");
            return;
        }

        Cords[] cords = app.getDatabase().getGameMap(map);

        if (cords == null) {
            ctx.status(404);
            ctx.json("Game map not found: " + map);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Cords cord : cords) {
            sb.append(cord.getLat()).append(",").append(cord.getLon()).append(";");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Remove trailing semicolon
        }

        ctx.status(200);
        ctx.result(sb.toString());

    }
}
