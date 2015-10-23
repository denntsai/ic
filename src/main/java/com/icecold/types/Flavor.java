package com.icecold.types;

public class Flavor {
    private String name;

    public Flavor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("Flavor[name='%s']", name);
    }
}
