package com.traindelaythegame;

import java.util.ArrayList;

import com.traindelaythegame.api.v1.delete.RemoveGameMap;
import com.traindelaythegame.api.v1.get.BusTrainStops;
import com.traindelaythegame.api.v1.get.GetAllMaps;
import com.traindelaythegame.api.v1.get.GetPlayArea;
import com.traindelaythegame.api.v1.get.ImgEndpoint;
import com.traindelaythegame.api.v1.get.HTML.MapGenerator;
import com.traindelaythegame.api.v1.get.HTML.MapPlay;
import com.traindelaythegame.api.v1.post.*;
import com.traindelaythegame.helpers.Database;
import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.models.APIRunner;

public class TrainDelayTheGame {
    private final APIRunner apiRunner;
    private Database database;

    public TrainDelayTheGame() {
        this.apiRunner = new APIRunner();
        this.database = new Database();

        registerEndpoints();
        this.apiRunner.start();
    }

    public void registerEndpoints() {
        ArrayList<APIEndpoint> endpointsGet = new ArrayList<>();
        endpointsGet.add(new ImgEndpoint());
        endpointsGet.add(new MapGenerator());
        endpointsGet.add(new MapPlay());

        endpointsGet.add(new BusTrainStops(this));
        endpointsGet.add(new GetPlayArea(this));
        endpointsGet.add(new GetAllMaps(this));

        ArrayList<APIEndpoint> endpointsPost = new ArrayList<>();
        endpointsPost.add(new AddGameMap(this));

        ArrayList<APIEndpoint> endpointsDelete = new ArrayList<>();
        endpointsDelete.add(new RemoveGameMap(this));

        this.apiRunner.registerEndpoints(endpointsGet, endpointsPost, endpointsDelete);
    }

    public Database getDatabase() {
        return this.database;
    }
}
