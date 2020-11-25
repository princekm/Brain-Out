package com.mindgame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.mindgame.listeners.menu.ScreenOpener;
import com.mindgame.managers.AnimationManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayerModeScreenActivity extends AppCompatActivity {

    public final static String PLAYER_MODE="player";
    public final static String SINGLE_PLAYER="SINGLE PLAYER";
    public final static String MULTI_PLAYER="MULTI PLAYER";
    private Button singlePlayerButton;
    private Button multiPlayerButton;
    private AnimationManager animationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_mode_screen);
        instantiateViews();
        animationManager = new AnimationManager(this);
        setButtonActions();
        loadAnimation();
    }

    private void openGameMenuScreen()
    {
        Intent i = new Intent(this, MenuScreenActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed() {
        openGameMenuScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnimation();
    }



    private void instantiateViews()
    {
        singlePlayerButton =(Button)findViewById(R.id.singlePlayerButton);
        multiPlayerButton =(Button)findViewById(R.id.multiPlayerButton);
    }

    private void loadAnimation()
    {
        animationManager.loadSlideDownAnimation(singlePlayerButton);
        animationManager.loadSlideDownAnimation(multiPlayerButton);
    }
    private void setButtonActions()
    {
        ScreenOpener singlePlayerButtonOpener =new ScreenOpener(this,LevelScreenActivity.class,false);
        singlePlayerButtonOpener.addMessage(PLAYER_MODE,SINGLE_PLAYER);
        singlePlayerButton.setOnClickListener(singlePlayerButtonOpener);
        ScreenOpener multiPlayerButtonOpener =new ScreenOpener(this,PlayerListScreenActivity.class,false);
        multiPlayerButtonOpener.addMessage(PLAYER_MODE,MULTI_PLAYER);
        multiPlayerButton.setOnClickListener(multiPlayerButtonOpener);
    }





}