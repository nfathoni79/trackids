package com.cevnyne.trackids.models;

import java.util.HashMap;
import java.util.Map;

public class Parent {

    private String name;
    private Map<String, Boolean> children;

    public Parent() {}

    public Parent(String name) {
        this.name = name;
        this.children = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getChildren() {
        return children;
    }
}
