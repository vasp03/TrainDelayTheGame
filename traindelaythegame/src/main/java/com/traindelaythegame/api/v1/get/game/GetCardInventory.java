package com.traindelaythegame.api.v1.get.game;

import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class GetCardInventory extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/inventory";
    }

    @Override
    public void handle(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
