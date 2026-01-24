package com.traindelaythegame.api.v1.get;

import com.google.gson.JsonArray;
import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.helpers.Security;

public class BusTrainStops extends APIEndpoint {
    private TrainDelayTheGame app = null;

    public BusTrainStops(TrainDelayTheGame app) {
        this.app = app;
    }

    @Override
    public String path() {
        return "/api/v1/stops";
    }

    @Override
    public void handle(io.javalin.http.Context ctx) throws UnsupportedOperationException {
        String minLongitude = ctx.queryParam("minLongitude");
        String maxLongitude = ctx.queryParam("maxLongitude");
        String minLatitude = ctx.queryParam("minLatitude");
        String maxLatitude = ctx.queryParam("maxLatitude");
        String exclude = ctx.queryParam("exclude"); // list of types to exclude (bus, train), comma separated

        double minLon = -180.0;
        double maxLon = 180.0;
        double minLat = -90.0;
        double maxLat = 90.0;

        try {
            if (minLongitude != null && !minLongitude.isEmpty()) minLon = Double.parseDouble(minLongitude);
            if (maxLongitude != null && !maxLongitude.isEmpty()) maxLon = Double.parseDouble(maxLongitude);
            if (minLatitude != null && !minLatitude.isEmpty()) minLat = Double.parseDouble(minLatitude);
            if (maxLatitude != null && !maxLatitude.isEmpty()) maxLat = Double.parseDouble(maxLatitude);
        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.result("Invalid numeric query parameter.");
            return;
        }

        if (!Security.isValidTypeList(exclude)) {
            ctx.status(400);
            ctx.result("Invalid 'exclude' parameter.");
            return;
        }

        JsonArray rs = app.getDatabase().getStopsInBoundsAsJson(minLat, maxLat, minLon, maxLon, exclude);
        if (rs == null) {
            ctx.status(404);
            ctx.result("Stops data not found");
            return;
        }

        ctx.contentType("application/json");
        ctx.status(200);
        ctx.result(rs.toString());
    }
}
