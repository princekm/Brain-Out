package com.mindgame.connection.packets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mindgame.connection.Packetizer;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = Packetizer.PLAYER_S_ID;

    private String name;
    private String macAddress;

    public Player(String playerName, String macAddress) {
        this.name = playerName;
        this.macAddress = macAddress;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        boolean equals=false;
        if(obj instanceof Player)
        {
            Player another=(Player)obj;
            if(macAddress.compareTo(another.macAddress)==0)
                equals=true;
        }
        return equals;
    }

    @NonNull
    @Override
    public String toString() {
        return "[ name:"+name+""+", address:"+macAddress+"]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }


}
