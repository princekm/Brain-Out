package com.mindgame.listeners.menu;

import android.view.View;
import android.widget.Button;

import com.mindgame.activities.LevelScreenActivity;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

public class LevelOpener implements View.OnClickListener {
    private LevelScreenActivity levelScreenActivity;

    public LevelOpener(LevelScreenActivity levelScreenActivity) {
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

        levelScreenActivity.openGameLevel(level);
    }
}
