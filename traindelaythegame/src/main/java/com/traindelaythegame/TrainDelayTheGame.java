package com.traindelaythegame;

import java.util.ArrayList;

import com.traindelaythegame.api.v1.delete.RemoveGameMap;
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
        endpointsGet.add(new GetAllGames(this));
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
        endpointsGet.add(new PlayAreaCreator());

        endpointsGet.add(new ImgEndpoint());
        endpointsGet.add(new BusTrainStops(this));
        endpointsGet.add(new GetPlayArea(this));
        endpointsGet.add(new GetAllMaps(this));

        ArrayList<APIEndpoint> endpointsPost = new ArrayList<>();
        endpointsPost.add(new AnswerQuestion());
        endpointsPost.add(new CreateGame(this));
        endpointsPost.add(new EndGame());
        endpointsPost.add(new JoinGame(this));
        endpointsPost.add(new Send());
        endpointsPost.add(new SetSettings());
        endpointsPost.add(new AddGameMap(this));
        endpointsPost.add(new CreateUser(this));

        ArrayList<APIEndpoint> endpointsDelete = new ArrayList<>();
        endpointsDelete.add(new RemoveGameMap(this));

        this.apiRunner.registerEndpoints(endpointsGet, endpointsPost, endpointsDelete);
    }

    public Database getDatabase() {
        return this.database;
    }
}
