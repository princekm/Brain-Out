package com.mindgame.connection.packets;

import com.mindgame.connection.Packetizer;

public class ResultInfoPacket extends PlayerInfoPacket {
    private static final long serialVersionUID =Packetizer.RESULT_INFO_S_ID;
    private int elapsedTime;
    private boolean won;

    public ResultInfoPacket(int packetID, String playerName, String playerMac, String groupName, String groupMac, int elapsedTime) {
        super(packetID, playerName, playerMac, groupName, groupMac);
        setElapsedTime(elapsedTime);
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
        setWon((elapsedTime>0?true:false));
    }

    public boolean isWon() {
        return won;
    }

    private void setWon(boolean won) {
        this.won = won;
    }
}
