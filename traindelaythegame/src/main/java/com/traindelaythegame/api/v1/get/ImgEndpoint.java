package com.traindelaythegame.api.v1.get;

import java.io.InputStream;

import com.traindelaythegame.models.APIEndpoint;

import io.javalin.http.Context;

public class ImgEndpoint extends APIEndpoint {
    @Override
    public String path() {
        return "/img/{imageName}";
    }

    @Override
    public void handle(Context ctx) throws UnsupportedOperationException {
        String imageName = ctx.pathParam("imageName");

        InputStream rs = this.getClass().getResourceAsStream("/img/" + imageName);

        if (rs == null) {
            ctx.status(404);
            ctx.result("Image not found");
            return;
        }

        ctx.contentType("image/png");
        ctx.result(rs);
    }
}
