package com.mindgame.listeners.menu;

import android.view.View;

import com.mindgame.activities.PlayerListScreenActivity;

public class GroupJoiner implements View.OnClickListener {
    private PlayerListScreenActivity playerListScreenActivity;

    public GroupJoiner(PlayerListScreenActivity playerListScreenActivity) {
        this.playerListScreenActivity = playerListScreenActivity;
    }

    @Override
    public void onClick(View v) {
//        playerListScreenActivity.joinRoom();
    }
}
