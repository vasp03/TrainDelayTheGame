package com.traindelaythegame.api.v1.get;

import com.google.gson.JsonArray;
import com.traindelaythegame.TrainDelayTheGame;
import com.traindelaythegame.models.APIEndpoint;

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

        double minLon = (minLongitude != null) ? Double.parseDouble(minLongitude) : -180.0;
        double maxLon = (maxLongitude != null) ? Double.parseDouble(maxLongitude) : 180.0;
        double minLat = (minLatitude != null) ? Double.parseDouble(minLatitude) : -90.0;
        double maxLat = (maxLatitude != null) ? Double.parseDouble(maxLatitude) : 90.0;

        JsonArray rs = app.getDatabase().getStopsInBoundsAsJson(minLat, maxLat, minLon, maxLon);

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
