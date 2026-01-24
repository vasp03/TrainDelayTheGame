package com.traindelaythegame.api.v1.delete;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.helpers.Security;

import io.javalin.http.Context;

public class RemoveGameMap extends APIEndpoint {
    private TrainDelayTheGame app;

    public RemoveGameMap(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/gamemap";
    }

    @Override
    public void handle(Context ctx) throws UnsupportedOperationException {
        String name = ctx.queryParam("name");

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

        app.getDatabase().removeGameMap(name);
        ctx.status(200);
        ctx.json("Game map removed successfully.");
    }

}
