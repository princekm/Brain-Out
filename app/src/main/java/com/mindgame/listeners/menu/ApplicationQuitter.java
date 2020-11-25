package com.mindgame.listeners.menu;

import android.app.Activity;
import android.view.View;

import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

public class ApplicationQuitter implements View.OnClickListener{
    private Activity activity;

    public ApplicationQuitter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        if(SettingsManager.isSoundOn(activity))
        {
            SoundManager.playButtonSound(activity);
        }
        activity.finishAffinity();
    }
}
