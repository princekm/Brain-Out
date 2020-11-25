package com.mindgame.connection.packets;

import com.mindgame.connection.Packetizer;

public class GroupInfoPacket extends Packet {
    private static final long serialVersionUID =Packetizer.GROUP_INFO_S_ID;
    private Player group;
    public GroupInfoPacket(int packetID, String groupName, String groupMac) {
        super(packetID);
        group = new Player(groupName,groupMac);
    }
    public Player getGroup()
    {
        return group;
    }
    public String getMacAddress() {
        return group.getMacAddress();
    }

    public String getGroupName() {
        return group.getName();
    }


}
