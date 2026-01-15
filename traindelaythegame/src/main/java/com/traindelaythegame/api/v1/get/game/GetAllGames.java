package com.traindelaythegame.api.v1.get.game;

import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class GetAllGames extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/games";
    }

    @Override
    public void handle(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
