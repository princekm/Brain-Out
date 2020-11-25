package com.mindgame.connection.packets;

import com.mindgame.connection.Packetizer;

import java.util.ArrayList;

public class PlayersInfoPacket extends GroupInfoPacket {
    private static final long serialVersionUID =Packetizer.PLAYERS_INFO_S_ID;

    public void setPlayersInfos(ArrayList<Player> playersInfos) {
        this.playersInfos = playersInfos;
    }
    private ArrayList<Player> playersInfos;
    public PlayersInfoPacket(int packetID,  String groupName,String groupMac) {
        super(packetID, groupName,groupMac);
        playersInfos=new ArrayList<Player>();
    }
    public void addPlayer(String playerName,String playerMac)
    {
        playersInfos.add(new Player(playerName,playerMac));
    }
    public ArrayList<Player> getPlayersInfo() {
        return playersInfos;
    }

}
