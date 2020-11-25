package com.mindgame.threads;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;

public class ShowLevelThread extends TimerThread {
    public ShowLevelThread(GameScreenActivity activity, long elapsedMillisecs) {
        super(activity, elapsedMillisecs);
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onTick() {
        if(activity.isActivityVisible()) {
            activity.showInfoOnTable(elapsedMillisecs);
        }
    }

    @Override
    protected void onFinish() {
        if(activity.isActivityVisible()) {
            activity.showPointsTable(false);
            activity.clearLevelSelected();
            activity.clearPointsTable();
            activity.openLevelScreen();
//            activity.openL
        }
    }
}
