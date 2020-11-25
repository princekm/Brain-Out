package com.mindgame.activities;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mindgame.connection.Packetizer;
import com.mindgame.connection.packets.LevelInfoPacket;
import com.mindgame.connection.packets.PlayerInfoPacket;

public class LevelScreenHandler extends Handler {

    private LevelScreenActivity levelScreenActivity;

    public LevelScreenHandler(LevelScreenActivity levelScreenActivity) {
        this.levelScreenActivity = levelScreenActivity;
    }


    @Override
    public void handleMessage(@NonNull Message msg) {
        if(levelScreenActivity.isActivityVisible())
        {
            Bundle bundle=msg.getData();
            switch (msg.what)
            {

                case Packetizer.LEAVE_GROUP: {
                    PlayerInfoPacket playerInfoPacket = (PlayerInfoPacket) bundle.getSerializable(Packetizer.KEY_LEAVE_GROUP);
                    if(levelScreenActivity.isActivityVisible())
                    {
                        levelScreenActivity.handleLeaveGroup(playerInfoPacket);
                    }
                }
                break;

                case Packetizer.LEVEL_INFO:
                    {
                    LevelInfoPacket levelInfoPacket = (LevelInfoPacket) bundle.getSerializable(Packetizer.KEY_LEVEL_INFO);
                    levelScreenActivity.handleLevelSelected(levelInfoPacket);
                }
                break;
            }

        }
    }
}
