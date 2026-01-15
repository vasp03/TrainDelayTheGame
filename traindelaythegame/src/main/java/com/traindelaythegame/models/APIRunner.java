package com.traindelaythegame.models;

import java.util.ArrayList;
import io.javalin.Javalin;

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
