package com.mindgame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mindgame.connection.MultiPlayerHandler;
import com.mindgame.connection.packets.LevelInfoPacket;
import com.mindgame.connection.packets.Player;
import com.mindgame.connection.packets.PlayerInfoPacket;
import com.mindgame.listeners.menu.LevelAnouncer;
import com.mindgame.listeners.menu.LevelOpener;
import com.mindgame.managers.AnimationManager;

public class LevelScreenActivity extends AppCompatActivity {

    private String player;


    private LinearLayout linearLayout;
    private LevelAnouncer levelAnouncer;
    private LevelOpener levelOpener;

    private boolean activityVisible;
    public static final String LEVEL = "level";
    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private AnimationManager animationManager;
    private MultiPlayerHandler multiPlayerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_screen);
        loadMode();
        init();
        instantiateViews();
        loadAnimation();
        setPlayerModeText();
        setButtonActions();
    }

    @Override
    public void onBackPressed() {
        if (player.equals(PlayerModeScreenActivity.MULTI_PLAYER)) {
            if(multiPlayerHandler.isAdvertising())
                multiPlayerHandler.setGroupAdvertisement(false,true);
            else if(multiPlayerHandler.hasJoinedGroup())
                multiPlayerHandler.sendLeaveGroupPacket();
            openPlayerListScreen();
        } else
            super.onBackPressed();
    }

    public void showSnackInfo(String text) {
        Snackbar snackbar = Snackbar.make(linearLayout, text, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorOrange));

        snackbar.show();
    }

    public void setActivityVisible(boolean activityVisible) {
        this.activityVisible = activityVisible;
    }

    public boolean isActivityVisible() {
        return activityVisible;
    }

    private void init() {
        animationManager = new AnimationManager(this);
        if (player.equals(PlayerModeScreenActivity.MULTI_PLAYER)) {
            levelAnouncer = new LevelAnouncer(this);
            multiPlayerHandler = MultiPlayerHandler.getInstance(this);
            multiPlayerHandler.setGameScreenHandler(null);
            multiPlayerHandler.setLevelScreenHandler(new LevelScreenHandler(this));
        } else {
            levelOpener = new LevelOpener(this);
        }
    }

    private void instantiateViews() {
        linearLayout = findViewById(R.id.levelParentItem);
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);

    }

    @Override
    protected void onPause() {
        super.onPause();
        setActivityVisible(false);
    }

    private void loadMode() {
        player = getIntent().getExtras().getString(PlayerModeScreenActivity.PLAYER_MODE);
    }

    private void loadAnimation() {
        animationManager.loadSlideDownAnimation(easyButton);
        animationManager.loadSlideDownAnimation(mediumButton);
        animationManager.loadSlideDownAnimation(hardButton);
    }

    private void setPlayerModeText() {
        TextView textView = findViewById(R.id.levelMenuTitle);
        textView.setText(player);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnimation();
        showSnackInfo("Select level");
        setActivityVisible(true);
    }

    public void announceLevel(String level) {
        multiPlayerHandler.sendLevelInfo(level);
    }

    private void openPlayerListScreen() {
        Intent i = new Intent(this, PlayerListScreenActivity.class);
        i.putExtra(PlayerModeScreenActivity.PLAYER_MODE, player);
        startActivity(i);
        finish();
    }

    public void handleLevelSelected(LevelInfoPacket levelInfoPacket) {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        if (groupState == MultiPlayerHandler.GroupState.LOCKED) {
            Player group = levelInfoPacket.getGroup();
            Player player = levelInfoPacket.getPlayer();
            if (group.equals(multiPlayerHandler.getGroup())) {
                multiPlayerHandler.addLevelInfo(levelInfoPacket);
                String playerName = player.getName();
                if (player.equals(multiPlayerHandler.getPlayer()))
                    playerName = "You";
                showSnackInfo(playerName + " selected " + levelInfoPacket.getLevel());
                if (multiPlayerHandler.isLevelDecided()) {
                    String level = multiPlayerHandler.getPolledLevel();
                    openGameLevel(level);

                }
            }
        }
    }

    public void openGameLevel(String level) {
        Intent i = new Intent(this, GameScreenActivity.class);
        i.putExtra(LEVEL, level);
        i.putExtra(PlayerModeScreenActivity.PLAYER_MODE, player);
        startActivity(i);
    }

    public void handleLeaveGroup(PlayerInfoPacket playerInfoPacket) {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        Player group = playerInfoPacket.getGroup();
        Player player = playerInfoPacket.getPlayer();
        if (groupState == MultiPlayerHandler.GroupState.LOCKED) {
            if (group.equals(multiPlayerHandler.getGroup())) {
                if (player.equals(multiPlayerHandler.getPlayer())) {
                    multiPlayerHandler.clearPlayers();
                    multiPlayerHandler.clearGroup();
                    multiPlayerHandler.clearLevelSelected();
                    multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                    openPlayerListScreen();
                } else {
                    multiPlayerHandler.removePlayer(player);
                    multiPlayerHandler.removeLevelSelected(player);
                    if (multiPlayerHandler.getPlayers().size() == 1) {
                        multiPlayerHandler.clearPlayers();
                        multiPlayerHandler.clearGroup();
                        multiPlayerHandler.clearLevelSelected();
                        multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                        openPlayerListScreen();
                    }

                }
            }
        }
    }

    private void setButtonActions() {

        if (player.equals(PlayerModeScreenActivity.SINGLE_PLAYER)) {
            easyButton.setOnClickListener(levelOpener);
            mediumButton.setOnClickListener(levelOpener);
            hardButton.setOnClickListener(levelOpener);
        } else {
            easyButton.setOnClickListener(new LevelAnouncer(this));
            mediumButton.setOnClickListener(levelAnouncer);
            hardButton.setOnClickListener(levelAnouncer);

        }
    }
}