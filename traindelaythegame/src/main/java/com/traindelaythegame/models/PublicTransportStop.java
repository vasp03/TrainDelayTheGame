package com.traindelaythegame.models;

public class PublicTransportStop {
    private String name;
    private String location;
    private String type;

    public PublicTransportStop(String name, String location, String type) {
        this.name = name;
        this.location = location;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }
}
