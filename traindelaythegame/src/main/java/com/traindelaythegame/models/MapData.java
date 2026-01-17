package com.traindelaythegame.models;

public class MapData {
    private String name;
    private int id;

    public MapData(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
