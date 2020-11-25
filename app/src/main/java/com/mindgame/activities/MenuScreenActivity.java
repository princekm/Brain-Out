package com.mindgame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.mindgame.listeners.menu.ApplicationQuitter;
import com.mindgame.listeners.menu.ScreenOpener;
import com.mindgame.managers.SettingsManager;

public class MenuScreenActivity extends AppCompatActivity {

    private Button playButton;
    private Button settingsButton;
    private Button quitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        init();
        instantiateButtons();
        setButtonActions();
        loadAnimation();
        loadSettingsIfNotCreated();
    }

    private void init()
    {
        View rootView = getWindow(). getDecorView(). getRootView();
        rootView.getBackground().setDither(true);
    }
    private void loadSettingsIfNotCreated()
    {
        SettingsManager.createDefaultIfNotExists(getApplicationContext());
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnimation();
    }


    private void instantiateButtons()
    {
        playButton = findViewById(R.id.playButton);
        settingsButton = findViewById(R.id.settingsButton);
        quitButton = findViewById(R.id.quitButton);


    }
    private void loadAnimation()
    {
        loadAnimation(playButton);
        loadAnimation(settingsButton);
        loadAnimation(quitButton);
    }
    private void loadAnimation(Button button)
    {
        Animation buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        button.startAnimation(buttonAnimation);
    }
    private void setButtonActions()
    {
        playButton.setOnClickListener(new ScreenOpener(this,PlayerModeScreenActivity.class,false));
        settingsButton.setOnClickListener(new ScreenOpener(this,SettingsScreenActivity.class,false));
        quitButton.setOnClickListener(new ApplicationQuitter(this));
    }

}