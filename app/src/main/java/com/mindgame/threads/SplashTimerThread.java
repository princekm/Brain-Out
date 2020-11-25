package com.mindgame.threads;


import android.app.Activity;
import android.content.Intent;

import com.mindgame.activities.MenuScreenActivity;

public class SplashTimerThread implements Runnable {

    private  Activity activity;
    public SplashTimerThread(Activity activity)
    {
      this.activity=activity;

    }

    @Override
    public void run() {
        Intent i = new Intent(activity, MenuScreenActivity.class);
        activity.startActivity(i);
        activity.finish();
    }
}
