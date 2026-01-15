package com.traindelaythegame.api.v1.get.game;

import com.traindelaythegame.models.APIEndpoint;

public class GetPlayerTime extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/playtime";
    }

    @Override
    public void handle(io.javalin.http.Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
