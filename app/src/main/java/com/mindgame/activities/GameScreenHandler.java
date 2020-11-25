package com.mindgame.activities;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mindgame.connection.Packetizer;
import com.mindgame.connection.packets.PlayerInfoPacket;
import com.mindgame.connection.packets.ResultInfoPacket;
import com.mindgame.connection.packets.WordsInfoPacket;

public class GameScreenHandler extends Handler {
    GameScreenActivity gameScreenActivity;

    public GameScreenHandler(GameScreenActivity gameScreenActivity) {
        this.gameScreenActivity = gameScreenActivity;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {

        if(gameScreenActivity.isActivityVisible()) {
            Bundle bundle=msg.getData();

            switch (msg.what) {
                case Packetizer.WORDS_INFO: {
                    WordsInfoPacket wordsInfoPacket = (WordsInfoPacket) bundle.getSerializable(Packetizer.KEY_MEMORIZE_WORDS);
                    gameScreenActivity.handleWordsPacket(wordsInfoPacket);
                }
                break;
                case Packetizer.PLAYER_RESULT: {
                    ResultInfoPacket resultInfoPacket = (ResultInfoPacket) bundle.getSerializable(Packetizer.KEY_PLAYER_RESULT);
                    gameScreenActivity.handleResultInfoPacket(resultInfoPacket);
                }
                break;
                case Packetizer.LEAVE_GROUP: {
                    PlayerInfoPacket playerInfoPacket = (PlayerInfoPacket) bundle.getSerializable(Packetizer.KEY_LEAVE_GROUP);
                    if(gameScreenActivity.isActivityVisible())
                    {
                        gameScreenActivity.handleLeaveGroup(playerInfoPacket);
                    }                }
                break;

            }
        }
    }
}
