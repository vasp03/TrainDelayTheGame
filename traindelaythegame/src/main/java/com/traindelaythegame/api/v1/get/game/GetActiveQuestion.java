package com.traindelaythegame.api.v1.get.game;

import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class GetActiveQuestion extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/questions";
    }

    @Override
    public void handle(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
