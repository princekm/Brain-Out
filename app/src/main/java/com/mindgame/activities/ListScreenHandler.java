package com.mindgame.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mindgame.connection.Packetizer;
import com.mindgame.connection.packets.GroupInfoPacket;
import com.mindgame.connection.packets.PlayerInfoPacket;
import com.mindgame.connection.packets.PlayersInfoPacket;

public class ListScreenHandler extends Handler {

    private PlayerListScreenActivity playerListScreenActivity;

    public ListScreenHandler(PlayerListScreenActivity playerListScreenActivity) {
        this.playerListScreenActivity = playerListScreenActivity;
    }


    @Override
    public void handleMessage(@NonNull Message msg) {
        if(playerListScreenActivity.isActivityVisible())
        {
            Bundle bundle=msg.getData();
            switch (msg.what)
            {
                case Packetizer.ADVERTISE_GROUP: {
                    GroupInfoPacket groupInfoPacket = (GroupInfoPacket) bundle.getSerializable(Packetizer.KEY_ADVERTIZE_GROUP);
                    playerListScreenActivity.handleGroupAdvertiseResult(groupInfoPacket);
                }
                    break;
                case Packetizer.LEAVE_GROUP: {
                    PlayerInfoPacket playerInfoPacket = (PlayerInfoPacket) bundle.getSerializable(Packetizer.KEY_LEAVE_GROUP);
                    if(playerListScreenActivity.isActivityVisible())
                        playerListScreenActivity.handleLeaveGroupResult(playerInfoPacket);
                }
                    break;
                case Packetizer.JOIN_GROUP: {
                    PlayerInfoPacket playerInfoPacket = (PlayerInfoPacket) bundle.getSerializable(Packetizer.KEY_JOIN_GROUP);
                    playerListScreenActivity.handleJoinGroupResult(playerInfoPacket);
                }
                break;
                case Packetizer.JOINED_GROUP: {
                    PlayerInfoPacket playerInfoPacket = (PlayerInfoPacket) bundle.getSerializable(Packetizer.KEY_JOINED_GROUP);
                    playerListScreenActivity.handleJoinedGroupResult(playerInfoPacket);
                }
                break;
                case Packetizer.PLAYERS_INFO: {
                    PlayersInfoPacket playersInfoPacket = (PlayersInfoPacket) bundle.getSerializable(Packetizer.KEY_PLAYERS_INFO);
                    playerListScreenActivity.handlePlayersInfoResult(playersInfoPacket);
                }
                break;
                case Packetizer.GROUP_ATTENDANCE: {
                    GroupInfoPacket groupInfoPacket = (GroupInfoPacket) bundle.getSerializable(Packetizer.KEY_GROUP_ATTENDANCE);
                    playerListScreenActivity.handleLockGroupResult(groupInfoPacket);
                }
                break;
            }

        }
    }
}
