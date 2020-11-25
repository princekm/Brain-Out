package com.mindgame.listeners.settings;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mindgame.activities.SettingsScreenActivity;

public class NameSetter implements View.OnClickListener{
    private SettingsScreenActivity activity;

    public NameSetter(SettingsScreenActivity activity) {
        this.activity = activity;
    }



    @Override
    public void onClick(View v) {
        activity.saveName();
    }
}
