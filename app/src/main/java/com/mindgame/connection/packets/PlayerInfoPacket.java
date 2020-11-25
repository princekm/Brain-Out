package com.mindgame.connection.packets;

import com.mindgame.connection.Packetizer;

public class PlayerInfoPacket extends GroupInfoPacket{
    private static final long serialVersionUID =Packetizer.PLAYER_INFO_S_ID;
    private Player player;
    public PlayerInfoPacket(int packetID,String playerName,String playerMac,  String groupName,String groupMac) {
        super(packetID, groupName, groupMac);
        player = new Player(playerName,playerMac);
    }
    public String getPlayerName() {
        return player.getName();
    }
    public String getPlayerMac() {
        return player.getMacAddress();
    }
    public Player getPlayer() {
        return player;
    }


}
