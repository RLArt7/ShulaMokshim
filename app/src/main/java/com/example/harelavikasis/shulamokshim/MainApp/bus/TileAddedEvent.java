package com.example.harelavikasis.shulamokshim.MainApp.bus;

/**
 * Created by harelavikasis on 10/01/2017.
 */

public class TileAddedEvent {
    private int x;
    private int y;

    public TileAddedEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
