package com.traindelaythegame.api.v1.post;

import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class JoinGame extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/join";
    }

    @Override
    public void handle(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
