package com.mindgame.listeners.settings;

import android.view.View;

import com.mindgame.activities.SettingsScreenActivity;

public class SoundSetter implements View.OnClickListener {
    private SettingsScreenActivity activity;

    public SoundSetter(SettingsScreenActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        activity.saveSoundState();
    }
}
