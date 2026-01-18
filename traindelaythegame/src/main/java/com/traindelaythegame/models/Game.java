package com.traindelaythegame.models;

public class Game {
    private int id;
    private boolean isActive;
    private String gameMap;
    private String gameName;

    public Game(int id, boolean isActive, String gameMap, String gameName) {
        this.id = id;
        this.isActive = isActive;
        this.gameMap = gameMap;
        this.gameName = gameName;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getGameMap() {
        return gameMap;
    }

    public String getGameName() {
        return gameName;
    }
}
