package com.mindgame.listeners.menu;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mindgame.activities.LevelScreenActivity;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

public class LevelAnouncer implements View.OnClickListener {
    LevelScreenActivity levelScreenActivity;

    public LevelAnouncer(LevelScreenActivity levelScreenActivity) {
        this.levelScreenActivity = levelScreenActivity;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button)v;
        String buttonText=button.getText().toString();
        String level="";
        if(buttonText.compareToIgnoreCase(LevelScreenActivity.EASY)==0)
        {
            level=LevelScreenActivity.EASY;
        }
        else if(buttonText.compareToIgnoreCase(LevelScreenActivity.HARD)==0)
        {
            level=LevelScreenActivity.HARD;
        }
        else if(buttonText.compareToIgnoreCase(LevelScreenActivity.MEDIUM)==0)
        {
            level=LevelScreenActivity.MEDIUM;
        }
        if(SettingsManager.isSoundOn(levelScreenActivity))
            SoundManager.playButtonSound(levelScreenActivity);

        levelScreenActivity.announceLevel(level);
    }
}
