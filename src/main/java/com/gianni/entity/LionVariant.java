package com.gianni.entity;

public enum LionVariant {
    FEMALE(0), MALE(1), WHITE(2);

    private final int id;
    LionVariant(int id) { this.id = id; }
    public int id() { return id; }

    public static LionVariant byId(int id) {
        for (LionVariant v : values()) if (v.id == id) return v;
        return FEMALE;
    }
}
