package com.mindgame.threads;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.activities.R;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

public class MemoryTimerThread extends TimerThread{

    private int count;
    public MemoryTimerThread(GameScreenActivity activity, long durationMillisecs) {
        super(activity,durationMillisecs);
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onTick() {

        if(activity.getLoadedWordCount()<activity.getTotalWordCount())
        {
            activity.addNextWord();
        }
        else if(activity.getTotalWordCount()==count)
        {
            if(SettingsManager.isSoundOn(activity))
                SoundManager.playTickSound(activity);

            activity.showTimeInfo(View.VISIBLE);
            activity.updateTimeInfo(""+elapsedMillisecs/TimerThread.DELAY_MILLI_SECS,GameScreenActivity.TextColor.GREEN);
        }
        else {
            activity.updateTimeInfo("" + elapsedMillisecs / TimerThread.DELAY_MILLI_SECS, GameScreenActivity.TextColor.GREEN);
            if(SettingsManager.isSoundOn(activity))
                SoundManager.playTickSound(activity);

        }
        if(elapsedMillisecs==TimerThread.DELAY_MILLI_SECS)
        {
            String rearrangeText= activity.getResources().getString(R.string.rearrangeText);
            activity.showInfo(rearrangeText, GameScreenActivity.MessageType.GENERAL);
        }
        count++;



    }

    @Override
    protected void onFinish() {
        activity.onFinishMemoryTimer();
    }
}
