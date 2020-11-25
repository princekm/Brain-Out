package com.mindgame.listeners.menu;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;

public class WordAdder implements View.OnClickListener {
    private GameScreenActivity gameScreenActivity;

    public WordAdder(GameScreenActivity gameScreenActivity) {
        this.gameScreenActivity = gameScreenActivity;
    }

    @Override
    public void onClick(View v) {
        gameScreenActivity.addWord();
    }
}
