package com.traindelaythegame;

import java.util.ArrayList;

import com.traindelaythegame.api.v1.get.game.*;
import com.traindelaythegame.api.v1.get.html.*;
import com.traindelaythegame.api.v1.post.*;

import com.traindelaythegame.models.APIEndpoint;
import com.traindelaythegame.models.APIRunner;

public class TrainDelayTheGame {
    private final APIRunner apiRunner;

    public TrainDelayTheGame() {
        this.apiRunner = new APIRunner();

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
        endpointsGet.add(new GetIngameMap());
        endpointsGet.add(new GetPlayerTime());
        endpointsGet.add(new GetQuestion());

        endpointsGet.add(new GetAskQuestion());
        endpointsGet.add(new GetEndPage());
        endpointsGet.add(new GetGameLoadingGameSettings());
        endpointsGet.add(new GetGameLoadingPage());
        endpointsGet.add(new GetHiderMainPage());
        endpointsGet.add(new GetLanding());
        endpointsGet.add(new GetSeekerMainPage());

        ArrayList<APIEndpoint> endpointsPost = new ArrayList<>();
        endpointsPost.add(new AnswerQuestion());
        endpointsPost.add(new CreateGame());
        endpointsPost.add(new EndGame());
        endpointsPost.add(new JoinGame());
        endpointsPost.add(new Send());
        endpointsPost.add(new SetSettings());

        this.apiRunner.registerEndpoints(endpointsGet, endpointsPost);
    }
}
