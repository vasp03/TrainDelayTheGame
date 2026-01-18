package com.traindelaythegame.api.v1.post;

import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class JoinGame extends APIEndpoint {
    private final TrainDelayTheGame app;

    public JoinGame(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/join";
    }

    @Override
    public void handle(Context ctx) {
        String gameIdParam = ctx.formParam("gameId");
        String userIdParam = ctx.formParam("userId");

        if (gameIdParam == null || userIdParam == null) {
            ctx.status(400).result("Missing gameId or userId parameter");
            return;
        }

        int gameId = -1;
        int userId = -1;
        try {
            gameId = Integer.parseInt(gameIdParam);
            userId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid gameId or userId parameter");
            return;
        }

        if (gameId == -1 || userId == -1) {
            ctx.status(400).result("Missing gameId or userId parameter");
            return;
        }

        int playerId = app.getDatabase().addPlayerToGame(gameId, userId);
        if (playerId == -1) {
            ctx.status(404).result("Game not found");
            return;
        }
        ctx.status(200).result("Player joined with ID: " + playerId);
    }
}
