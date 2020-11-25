package com.mindgame.listeners.settings;

import android.view.View;

import com.mindgame.activities.SettingsScreenActivity;

public class VibrationSetter implements View.OnClickListener {

    private SettingsScreenActivity activity;

    public VibrationSetter(SettingsScreenActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
activity.saveVibrationState();
    }
}
