package com.traindelaythegame.api.v1.post;

import java.time.LocalDateTime;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class CreateGame extends APIEndpoint {
    private final TrainDelayTheGame app;

    public CreateGame(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/createGame";
    }

    @Override
    public void handle(Context ctx) {
        LocalDateTime now = LocalDateTime.now();
        String gameName = "Game " + String.format("%02d:%02d", now.getHour(), now.getMinute());
        int id = app.getDatabase().createGame();
        ctx.status(201);
        ctx.result(id+"");
    }
}
