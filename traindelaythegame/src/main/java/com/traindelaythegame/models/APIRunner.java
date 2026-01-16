package com.traindelaythegame.models;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.javalin.Javalin;
import io.javalin.http.util.NaiveRateLimit;

public class APIRunner {
    private Javalin app;
    private ArrayList<APIEndpoint> getEndpoints = new ArrayList<>();
    private ArrayList<APIEndpoint> postEndpoints = new ArrayList<>();

    public APIRunner() {
        app = Javalin.create();
    }

    public void registerEndpoints(ArrayList<APIEndpoint> getEndpoints, ArrayList<APIEndpoint> postEndpoints) {
        this.getEndpoints.addAll(getEndpoints);
        this.postEndpoints.addAll(postEndpoints);
    }

    public APIRunner start() {
        for (APIEndpoint endPoint : getEndpoints) {
            app.get(endPoint.path(), ctx -> {
                try {
                    NaiveRateLimit.requestPerTimeUnit(ctx, 10, TimeUnit.SECONDS);

                    endPoint.handle(ctx);
                } catch (UnsupportedOperationException e) {
                    ctx.status(501).result("Not Implemented: " + endPoint.getClass().getSimpleName());
                }
            });
        }

        getEndpoints.clear();

        for (APIEndpoint endPoint : postEndpoints) {
            app.post(endPoint.path(), ctx -> {
                try {
                    NaiveRateLimit.requestPerTimeUnit(ctx, 1, TimeUnit.SECONDS);

                    endPoint.handle(ctx);
                } catch (UnsupportedOperationException e) {
                    ctx.status(501).result("Not Implemented: " + endPoint.getClass().getSimpleName());
                }
            });
        }

        postEndpoints.clear();

        app.start(5000);
        return this;
    }

    public APIRunner stop() {
        app.stop();
        return this;
    }
}
