package com.traindelaythegame.api.v1.post;

import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class EndGame extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/endGame";
    }

    @Override
    public void handle(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
