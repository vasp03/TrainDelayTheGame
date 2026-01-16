package com.traindelaythegame.api.v1.get.html;

import com.traindelaythegame.helpers.HTMLServe;
import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class GetIngameMap extends APIEndpoint {

    @Override
    public String path() {
        return "/api/v1/map";
    }

    @Override
    public void handle(Context ctx) {
        String landingPage = HTMLServe.getPage("Map");

        if (landingPage == null) {
            ctx.status(500).result("Error loading map page");
            return;
        }

        ctx.contentType("text/html").result(landingPage);
    }

}
