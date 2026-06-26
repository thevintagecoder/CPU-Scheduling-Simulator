package com.cpuscheduler.app.engine;

/** A Learn-tab theory card for one policy. */
public final class TheoryCard {
    public final Algorithm key;
    public final String name;
    public final String tag;
    public final int accent;       // ARGB
    public final String description;
    public final String pro;
    public final String con;

    public TheoryCard(Algorithm key, String name, String tag, int accent,
                      String description, String pro, String con) {
        this.key = key;
        this.name = name;
        this.tag = tag;
        this.accent = accent;
        this.description = description;
        this.pro = pro;
        this.con = con;
    }
}
