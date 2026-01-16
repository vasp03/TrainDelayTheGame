package com.traindelaythegame;

import java.util.ArrayList;

import com.traindelaythegame.api.v1.get.game.*;
import com.traindelaythegame.api.v1.get.html.*;
import com.traindelaythegame.api.v1.get.other.*;
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
        endpointsGet.add(new GetActiveCurses());
        endpointsGet.add(new GetActiveQuestion());
        endpointsGet.add(new GetAllGames());
        endpointsGet.add(new GetCardInventory());
        endpointsGet.add(new GetGameSettings());
        endpointsGet.add(new GetPlayerTime());
        endpointsGet.add(new GetQuestion());

        endpointsGet.add(new GetAskQuestion());
        endpointsGet.add(new GetEndPage());
        endpointsGet.add(new GetGameLoadingGameSettings());
        endpointsGet.add(new GetGameLoadingPage());
        endpointsGet.add(new GetHiderMainPage());
        endpointsGet.add(new GetLanding());
        endpointsGet.add(new GetSeekerMainPage());

        endpointsGet.add(new ImgEndpoint());
        endpointsGet.add(new BusTrainStops(this));

        ArrayList<APIEndpoint> endpointsPost = new ArrayList<>();
        endpointsPost.add(new AnswerQuestion());
        endpointsPost.add(new CreateGame());
        endpointsPost.add(new EndGame());
        endpointsPost.add(new JoinGame());
        endpointsPost.add(new Send());
        endpointsPost.add(new SetSettings());

        this.apiRunner.registerEndpoints(endpointsGet, endpointsPost);
    }

    public Database getDatabase() {
        return this.database;
    }
}
