package com.traindelaythegame.api.v1.get.game;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class GetAllGames extends APIEndpoint {
    private final TrainDelayTheGame app;

    public GetAllGames(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/getAllGames";
    }

    @Override
    public void handle(Context ctx) {
        ctx.json(app.getDatabase().getAllGames());
    }
}
