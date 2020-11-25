package com.mindgame.threads;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;

public abstract class TimerThread implements Runnable {
    protected GameScreenActivity activity;
    protected long elapsedMillisecs;
    public static final long DELAY_MILLI_SECS=1000;
    private boolean started;
    public TimerThread(GameScreenActivity activity, long elapsedMillisecs) {
        this.activity = activity;
        this.elapsedMillisecs = elapsedMillisecs;
        this.started=false;
    }

    @Override
    public void run() {

        if(!started)
        {
            started = true;
            onStart();
        }
        if(elapsedMillisecs!=0)
        {
            onTick();
            activity.getTimerHandler().postDelayed(this, DELAY_MILLI_SECS);
            elapsedMillisecs -= DELAY_MILLI_SECS;
        }
        else
        {
            onFinish();
        }

    }
    protected abstract void onStart();
    protected abstract void onTick();
    protected abstract void onFinish();
}

