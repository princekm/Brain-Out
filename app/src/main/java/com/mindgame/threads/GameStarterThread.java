package com.mindgame.threads;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.activities.R;

public class GameStarterThread extends TimerThread {


    public GameStarterThread(GameScreenActivity activity, long elapsedMillisecs) {
        super(activity, elapsedMillisecs);
    }


    @Override
    protected void onStart() {
        String memorizeText = activity.getResources().getString(R.string.memorizeText);
        activity.showInfo(memorizeText, GameScreenActivity.MessageType.GENERAL);
    }

    @Override
    protected void onTick() {

    }

    @Override
    protected void onFinish() {
        activity.showInfiniteBrain(View.INVISIBLE);
        activity.hideInfo();
        activity.startGame();


    }
}
