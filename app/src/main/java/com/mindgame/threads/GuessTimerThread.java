package com.mindgame.threads;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

public class GuessTimerThread extends TimerThread {

    public GuessTimerThread(GameScreenActivity activity, long elapsedMillisecs) {
        super(activity, elapsedMillisecs);

    }

    @Override
    protected void onStart() {

        activity.showTimeInfo(View.VISIBLE);
    }

    @Override
    protected void onTick() {
        if(SettingsManager.isSoundOn(activity))
            SoundManager.playTickSound(activity);
        activity.updateTimeInfo(""+elapsedMillisecs/TimerThread.DELAY_MILLI_SECS,GameScreenActivity.TextColor.RED);
    }

    @Override
    protected void onFinish() {
        activity.showTimeInfo(View.INVISIBLE);
        if(activity.getGameState()== GameScreenActivity.GameState.SHUFFLED)
        activity.performValidate(true);

    }


}
