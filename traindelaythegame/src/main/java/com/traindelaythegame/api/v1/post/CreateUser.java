package com.traindelaythegame.api.v1.post;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class CreateUser extends APIEndpoint {
    private final TrainDelayTheGame app;

    public CreateUser(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/createUser";
    }

    @Override
    public void handle(Context ctx) {
        String username = ctx.formParam("username");

        int userId = app.getDatabase().createPlayer(username);

        ctx.status(201);
        ctx.result(userId + "");
    }
}
