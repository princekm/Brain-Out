package com.mindgame.activities;

import android.content.Intent;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.mindgame.adapters.DeviceListAdapter;
import com.mindgame.connection.MultiPlayerHandler;
import com.mindgame.connection.packets.GroupInfoPacket;
import com.mindgame.connection.packets.Player;
import com.mindgame.connection.packets.PlayerInfoPacket;
import com.mindgame.connection.packets.PlayersInfoPacket;
import com.mindgame.listeners.menu.GameLocker;
import com.mindgame.listeners.menu.GroupCreator;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;
import com.trncic.library.DottedProgressBar;


public class PlayerListScreenActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_CODE_SWITCH_ON_FINE_LOCATION = 2;
    private RelativeLayout relativeLayout;
    private TextView textView;
    private TextView listHeaderView;
    private Button createButton;
    private Button startButton;
    private ListView deviceListView;
    private DeviceListAdapter adapter;

    private boolean createToggle;
    private MultiPlayerHandler multiPlayerHandler;
    private ListScreenHandler listScreenHandler;
    private String player;
    private String level;
    private boolean visible;

    private DottedProgressBar dilatingDotsProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list_screen);
        instantiateViews();
        init();
        setupButtonActions();
        loadModeAndLevel();

    }

    private void loadModeAndLevel() {
        player = getIntent().getExtras().getString(PlayerModeScreenActivity.PLAYER_MODE);
        level = getIntent().getExtras().getString(LevelScreenActivity.LEVEL);
    }


    @Override
    public void onBackPressed() {
        if (player.equals(PlayerModeScreenActivity.MULTI_PLAYER)) {
//            multiPlayerHandler.acquireMulticastLock(false);
            if (multiPlayerHandler.isAdvertising())
            {
                multiPlayerHandler.setGroupAdvertisement(false,true);
            }
            multiPlayerHandler.clearGroups();
            multiPlayerHandler.clearPlayers();
            multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
            openPlayerModeScreen();
        } else
            super.onBackPressed();
    }

    public boolean isActivityVisible() {
        return visible;
    }

    private void setActivityVisible(boolean visible) {
        this.visible = visible;
    }

    private void init() {
        createToggle = true;
        listScreenHandler = new ListScreenHandler(this);
        multiPlayerHandler = MultiPlayerHandler.getInstance(getApplicationContext());
        multiPlayerHandler.setPlayerListScreenHandler(listScreenHandler);
        multiPlayerHandler.setLevelScreenHandler(null);
        multiPlayerHandler.setGameScreenHandler(null);
        multiPlayerHandler.setReceiverActive();
        adapter = new DeviceListAdapter(this);
        deviceListView.setAdapter(adapter);
        deviceListView.setOnItemClickListener(adapter);

//        if(player.equals(PlayerModeScreenActivity.MULTI_PLAYER)) {
//            multiPlayerHandler.acquireMulticastLock(true);
//        }

    }

    private void instantiateViews() {
        dilatingDotsProgressBar = (DottedProgressBar) findViewById(R.id.progress);

        relativeLayout = findViewById(R.id.parentItem);
        textView = findViewById(R.id.infoTextView);
        listHeaderView = findViewById(R.id.listHeaderView);
        startButton = findViewById(R.id.startButton);
        createButton = findViewById(R.id.createButton);
        deviceListView = (ListView) findViewById(R.id.deviceList);
        deviceListView.setAdapter(adapter);


    }

    private void setupButtonActions() {
        createButton.setOnClickListener(new GroupCreator(this));
        startButton.setOnClickListener(new GameLocker(this));
    }

    public void showSnackInfo(String text) {
        Snackbar snackbar = Snackbar.make(relativeLayout, text, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorOrange));
        snackbar.show();
    }

    public void setCreateButtonText(boolean create) {
        if (create)
            createButton.setText("CREATE");
        else
            createButton.setText("LEAVE");
    }

    public void joinRoom(Player group) {
        if(!multiPlayerHandler.hasJoinedGroup())
            multiPlayerHandler.sendJoinGroupPacket(group);

    }

    private void clearGroups() {
        multiPlayerHandler.clearGroups();
        multiPlayerHandler.clearGroup();
        adapter.clear();

    }

    private void clearPlayers() {
        multiPlayerHandler.clearPlayers();
        adapter.clear();

    }

    public void createRoom() {
        if (createToggle) {
            clearGroups();
            clearPlayers();
            multiPlayerHandler.advertiseGroup();
            multiPlayerHandler.setGroupAdvertisement(true,true);

        } else
            {

                if(multiPlayerHandler.isAdvertising())
                     multiPlayerHandler.setGroupAdvertisement(false,true);
                else
                    multiPlayerHandler.sendLeaveGroupPacket();
        }
        if(SettingsManager.isSoundOn(this))
            SoundManager.playButtonSound(this);

        createButton.setVisibility(View.INVISIBLE);
        dilatingDotsProgressBar.startProgress();
        dilatingDotsProgressBar.setVisibility(View.VISIBLE);

        toggleCreateButton();

    }

    private void toggleCreateButton() {
        createToggle = !createToggle;
        setCreateButtonText(createToggle);

    }

    private boolean isWifiOn() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean value = false;
        if (wifiManager != null) {
            value = wifiManager.isWifiEnabled();
        }
        return value;
    }

    public void showText(String text) {

        textView.setText(text);
    }

    public void showHeaderText(String text) {

        listHeaderView.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivityVisible(true);

    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        setActivityVisible(false);
    }

    public void handleGroupAdvertiseResult(GroupInfoPacket groupInfoPacket) {
        Player group = groupInfoPacket.getGroup();
        Player player = multiPlayerHandler.getPlayer();
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();

        if (group.equals(player) && groupState == MultiPlayerHandler.GroupState.REQUESTED) {
            showHeaderText("Players");
            showText("Created Group: \n " + groupInfoPacket.getGroupName() + "  [ " + groupInfoPacket.getMacAddress() + " ]");
            multiPlayerHandler.getGroup().setMacAddress(groupInfoPacket.getMacAddress());
            multiPlayerHandler.getGroup().setName(groupInfoPacket.getGroupName());
            multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.JOINED);
            multiPlayerHandler.clearGroups();
            createButton.setVisibility(View.VISIBLE);

            dilatingDotsProgressBar.stopProgress();
            dilatingDotsProgressBar.setVisibility(View.INVISIBLE);

            if (!multiPlayerHandler.isPlayerAdded(multiPlayerHandler.getPlayer())) {
                multiPlayerHandler.addPlayer(multiPlayerHandler.getPlayer());
                adapter.clear();
                adapter.addAll(multiPlayerHandler.getPlayers());
                adapter.notifyDataSetChanged();
            }

        } else if (groupState == MultiPlayerHandler.GroupState.NO_ACTION) {
            if (!multiPlayerHandler.isGroupAdded(group)) {
                adapter.clear();
                multiPlayerHandler.addGroup(group);
                adapter.addAll(multiPlayerHandler.getGroups());
                adapter.notifyDataSetChanged();
            }
            showText("Groups are available");
        }


    }

    public void handleLeaveGroupResult(PlayerInfoPacket playerInfoPacket) {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        Player removalGroup = playerInfoPacket.getGroup();


        if (groupState == MultiPlayerHandler.GroupState.JOINED) {

            Player currentGroup = multiPlayerHandler.getGroup();
            Player player = playerInfoPacket.getPlayer();
            if (currentGroup.equals(removalGroup)) {
                if (multiPlayerHandler.getGroup().equals(multiPlayerHandler.getPlayer())) {

                    if (player.equals(multiPlayerHandler.getPlayer())) {
                        multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                        showHeaderText("Groups");
                        startButton.setVisibility(View.INVISIBLE);
                        showText("Deleted Group: \n " + playerInfoPacket.getGroupName() + "  [ " + playerInfoPacket.getMacAddress() + " ]");
                        multiPlayerHandler.clearGroup();
                        Log.d("removing","group");
                        multiPlayerHandler.clearPlayers();
                        adapter.clear();
                        createButton.setVisibility(View.VISIBLE);
                        dilatingDotsProgressBar.stopProgress();
                        dilatingDotsProgressBar.setVisibility(View.INVISIBLE);


                    } else {
                        showText("Left Group: \n " + playerInfoPacket.getGroupName() + "  [ " + playerInfoPacket.getMacAddress() + " ]");
                        Log.d("removing","1:"+player.toString());
                        multiPlayerHandler.removePlayer(player);
                        adapter.clear();
                        adapter.addAll(multiPlayerHandler.getPlayers());
                        for(Player player1:multiPlayerHandler.getPlayers())
                        {
                            Log.d("player",player1.toString());
                        }
                        adapter.notifyDataSetChanged();
                        createButton.setVisibility(View.VISIBLE);
                        dilatingDotsProgressBar.stopProgress();
                        dilatingDotsProgressBar.setVisibility(View.INVISIBLE);


                        multiPlayerHandler.sendGroupInfo();
                        if (multiPlayerHandler.getPlayers().size() == 1) {
                            startButton.setVisibility(View.INVISIBLE);
                        }

                    }


                } else {
                    showText("Left Group: \n " + playerInfoPacket.getGroupName() + "  [ " + playerInfoPacket.getMacAddress() + " ]");
                    if (!removalGroup.equals(multiPlayerHandler.getPlayer())) {
                        showHeaderText("Groups");
                        if (player.equals(removalGroup)) {
                            //group owner revokes
                            Log.d("removing","2:"+player.toString());

                            toggleCreateButton();
                            startButton.setVisibility(View.INVISIBLE);
                            multiPlayerHandler.clearPlayers();
                            adapter.clear();
                            multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                            multiPlayerHandler.sendGroupInfo();
                            multiPlayerHandler.clearGroup();
                            multiPlayerHandler.clearGroups();
                            createButton.setVisibility(View.VISIBLE);
                            dilatingDotsProgressBar.stopProgress();
                            dilatingDotsProgressBar.setVisibility(View.INVISIBLE);




                        } else if (multiPlayerHandler.getPlayer().equals(player)) {
                            //requested player and received player same
                            Log.d("removing","3:"+player.toString());

                            startButton.setVisibility(View.INVISIBLE);
                            multiPlayerHandler.clearPlayers();
                            adapter.clear();
                            multiPlayerHandler.sendGroupInfo();
                            multiPlayerHandler.clearGroup();
                            multiPlayerHandler.clearGroups();
                            multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                            createButton.setVisibility(View.VISIBLE);
                            dilatingDotsProgressBar.stopProgress();
                            dilatingDotsProgressBar.setVisibility(View.INVISIBLE);



                        } else {
                            Log.d("removing","4:"+player.toString());

                            multiPlayerHandler.removePlayer(player);
                            adapter.clear();
                            adapter.addAll(multiPlayerHandler.getPlayers());
                            adapter.notifyDataSetChanged();
                            multiPlayerHandler.sendGroupInfo();
                            createButton.setVisibility(View.VISIBLE);
                            dilatingDotsProgressBar.stopProgress();
                            dilatingDotsProgressBar.setVisibility(View.INVISIBLE);




                        }
                    } else {
                        Log.d("removing","5:"+player.toString());
                        multiPlayerHandler.removePlayer(player);
                        adapter.clear();
                        adapter.addAll(multiPlayerHandler.getPlayers());
                        adapter.notifyDataSetChanged();
                        multiPlayerHandler.sendGroupInfo();
                        createButton.setVisibility(View.VISIBLE);
                        dilatingDotsProgressBar.stopProgress();
                        dilatingDotsProgressBar.setVisibility(View.INVISIBLE);




                    }
                }


            } else {

                if(currentGroup.getName().isEmpty())
                clearGroups();
                createButton.setVisibility(View.VISIBLE);
                dilatingDotsProgressBar.stopProgress();
                dilatingDotsProgressBar.setVisibility(View.INVISIBLE);



            }

        } else if (multiPlayerHandler.isGroupAdded(removalGroup)) {
            multiPlayerHandler.removeAvailableGroup(removalGroup);
            adapter.clear();
            showText("");
            adapter.addAll(multiPlayerHandler.getGroups());
            adapter.notifyDataSetChanged();
            multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
        } else
            {

        }


    }

    public void handleJoinGroupResult(PlayerInfoPacket playerInfoPacket) {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        Player player = playerInfoPacket.getPlayer();
        Player group = playerInfoPacket.getGroup();
        if (groupState == MultiPlayerHandler.GroupState.JOINED) {
            if (group.equals(multiPlayerHandler.getGroup())) {
                showHeaderText("Players");
                multiPlayerHandler.clearGroups();
                if (!multiPlayerHandler.isPlayerAdded(player))
                    multiPlayerHandler.addPlayer(player);
                adapter.clear();
                adapter.addAll(multiPlayerHandler.getPlayers());
                adapter.notifyDataSetChanged();
                if (group.equals(multiPlayerHandler.getPlayer()))
                    startButton.setVisibility(View.VISIBLE);
                showText("Player Joined: \n " + player.getName() + "  [ " + player.getMacAddress() + " ]");
                multiPlayerHandler.sendJoinedGroupPacket(multiPlayerHandler.getGroup());
                multiPlayerHandler.sendGroupInfo();
                Log.d("joining","players");

                for(Player player1:multiPlayerHandler.getPlayers())
                {
                    Log.d("joining",player1.toString());
                }


            }
        }

    }

    public void handlePlayersInfoResult(PlayersInfoPacket playerInfoPacket) {
        if (multiPlayerHandler.getGroupState() == MultiPlayerHandler.GroupState.JOINED) {
            Player group = playerInfoPacket.getGroup();
            Log.d("groupnull",""+group);
            if (group.equals(multiPlayerHandler.getGroup())) {
                if(playerInfoPacket.getPlayersInfo().size()>0) {
                    multiPlayerHandler.clearPlayers();
                    for (Player player : playerInfoPacket.getPlayersInfo()) {
                        {
                            multiPlayerHandler.addPlayer(player);
                            Log.d("PlayersInfo", player.toString());
                        }
                    }
                    adapter.clear();
                    adapter.addAll(multiPlayerHandler.getPlayers());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void handleLockGroupResult(GroupInfoPacket groupInfoPacket) {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        if (groupState == MultiPlayerHandler.GroupState.JOINED) {
            Player group = groupInfoPacket.getGroup();
            if (group.equals(multiPlayerHandler.getGroup())) {
                multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.LOCKED);
                openGameScreen();
            }
        }
    }

    public void lockGame() {
      //  openGameScreen();
        multiPlayerHandler.setGroupAdvertisement(false,false);
        multiPlayerHandler.sendLockGroupPacket();
    }

    private void openPlayerModeScreen() {
        Intent i = new Intent(this, PlayerModeScreenActivity.class);
        startActivity(i);
        finish();
    }

    private void openGameScreen() {
        Intent i = new Intent(this, LevelScreenActivity.class);
        i.putExtra(PlayerModeScreenActivity.PLAYER_MODE, player);
        i.putExtra(LevelScreenActivity.LEVEL, level);
        startActivity(i);
    }

    public void handleJoinedGroupResult(PlayerInfoPacket playerInfoPacket) {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        Player group = playerInfoPacket.getGroup();
        Player player = playerInfoPacket.getPlayer();
        if (groupState == MultiPlayerHandler.GroupState.REQUESTED) {
            if (group.equals(multiPlayerHandler.getGroup())) {
                multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.JOINED);
                showHeaderText("Players");
//                if (!multiPlayerHandler.isPlayerAdded(group))
//                    multiPlayerHandler.addPlayer(group);
//                if (!multiPlayerHandler.isPlayerAdded(multiPlayerHandler.getPlayer()))
//                    multiPlayerHandler.addPlayer(multiPlayerHandler.getPlayer());
//                adapter.clear();
//                adapter.addAll(multiPlayerHandler.getPlayers());
//                adapter.notifyDataSetChanged();
                toggleCreateButton();
//                multiPlayerHandler.sendGroupInfo();
                showText("Group Joined: \n " + group.getName() + "  [ " + group.getMacAddress() + " ]");
            }
        }

    }


}