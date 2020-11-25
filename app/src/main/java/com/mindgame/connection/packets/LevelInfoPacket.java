package com.mindgame.connection.packets;

import com.mindgame.connection.Packetizer;

public class LevelInfoPacket extends PlayerInfoPacket {
    private static final long serialVersionUID = Packetizer.LEVEL_INFO_S_ID;
    private String level;
    public LevelInfoPacket(int packetID,String level,String playerName,String playerMac, String groupName, String groupMac)
    {
        super(packetID, playerName, playerMac,groupName,groupMac);
        this.level=level;
    }
    public String getLevel() {
        return level;
    }

}
