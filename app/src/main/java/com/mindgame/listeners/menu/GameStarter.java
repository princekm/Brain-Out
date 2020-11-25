package com.mindgame.listeners.menu;

import android.view.View;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

public class GameStarter implements View.OnClickListener {
    private GameScreenActivity activity;

    public GameStarter(GameScreenActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v)
    {

        if(SettingsManager.isSoundOn(activity))
        {
            SoundManager.playButtonSound(activity);
        }
        activity.delegateStartGame();
    }
}
