package com.traindelaythegame.api.v1.get.html;

import com.traindelaythegame.helpers.HTMLServe;
import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class PlayAreaCreator extends APIEndpoint {
    @Override
    public String path() {
        return "/api/v1/playareacreator";
    }

    @Override
    public void handle(Context ctx) throws UnsupportedOperationException {
        String landingPage = HTMLServe.getPage("PlayAreaCreator");

        if (landingPage == null) {
            ctx.status(500).result("Error loading landing page");
            return;
        }

        ctx.contentType("text/html").result(landingPage);
    }
}
