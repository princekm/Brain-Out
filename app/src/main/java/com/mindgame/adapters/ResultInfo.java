package com.mindgame.adapters;

import com.mindgame.connection.packets.Player;

public class ResultInfo {
    private boolean result;
    private int elapsedTime;
    private Player player;

    public ResultInfo(boolean result, int elapsedTime, Player player) {
        this.result = result;
        this.elapsedTime = elapsedTime;
        this.player = player;
    }

    public boolean isResult() {
        return result;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public  void set(boolean result,int elapsedTime)
    {
        this.result=result;
        this.elapsedTime=elapsedTime;
    }
    public Player getPlayer() {
        return player;
    }
}
