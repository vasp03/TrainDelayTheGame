package com.traindelaythegame.api.v1.get.HTML;

import com.traindelaythegame.helpers.HTMLServe;
import com.traindelaythegame.models.APIEndpoint;

public class MapGenerator extends APIEndpoint {
    @Override
    public String path() {
        return "/";
    }

    @Override
    public void handle(io.javalin.http.Context ctx) throws UnsupportedOperationException {
        String landingPage = HTMLServe.getPage("Map");

        if (landingPage == null) {
            ctx.status(500).result("Error loading landing page");
            return;
        }

        ctx.contentType("text/html").result(landingPage);
    }

}
