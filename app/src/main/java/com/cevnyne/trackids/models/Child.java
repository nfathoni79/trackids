package com.cevnyne.trackids.models;

public class Child {

    private String name;
    private Position position;

    public Child() {}

    public Child(String name) {
        this.name = name;
        this.position = new Position();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
