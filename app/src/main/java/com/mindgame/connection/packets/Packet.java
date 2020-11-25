package com.mindgame.connection.packets;

import android.os.Parcel;
import android.os.Parcelable;

import com.mindgame.connection.Packetizer;

import java.io.Serializable;

public class Packet implements Serializable {
    private static final long serialVersionUID =Packetizer.PACKET_S_ID;
    private int packetID;
    public Packet(int  packetID) {
        this.packetID = packetID;
    }
    public int getPacketID() {
        return packetID;
    }
}
