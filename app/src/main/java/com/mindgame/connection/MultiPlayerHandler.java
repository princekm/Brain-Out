package com.mindgame.connection;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mindgame.activities.LevelScreenActivity;
import com.mindgame.adapters.ResultInfo;
import com.mindgame.connection.packets.GroupInfoPacket;
import com.mindgame.connection.packets.LevelInfoPacket;
import com.mindgame.connection.packets.Packet;
import com.mindgame.connection.packets.Player;
import com.mindgame.managers.SettingsManager;
import com.mindgame.threads.GroupAdvertiser;
import com.mindgame.threads.PacketReceiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.mindgame.activities.LevelScreenActivity.EASY;
import static com.mindgame.activities.LevelScreenActivity.HARD;
import static com.mindgame.activities.LevelScreenActivity.MEDIUM;

public class MultiPlayerHandler {


    private static final String DEBUG_STRING = "multiplayerhandler";



    public enum GroupState {REQUESTED, JOINED, LOCKED, NO_ACTION};
    private WifiManager.MulticastLock multicastLock;
    private Player player;
    private Player wordSetter;
    private Player group;
    private Packetizer packetizer;
    private ArrayList<Player> groups;
    private Context context;
    private GroupAdvertiser advertiserThread;
    private GroupState groupState;
    private MulticastSender multicastSender;
    private MulticastReceiver multicastReceiver;
    private ArrayList<Player> players;
    private HashMap<Player, String> levelSelected;
    private Handler playerListScreenHandler;
    private Handler gameScreenHandler;
    private Handler levelScreenHandler;
    boolean receiverStarted;
    private static MultiPlayerHandler multiPlayerHandler = null;

    public ArrayList<ResultInfo> getPointsTable() {
        return pointsTable;
    }

    private MultiPlayerHandler(Context context) {
        receiverStarted = false;
        advertiserThread = null;
        this.context = context;
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            multicastLock = wm.createMulticastLock(MultiPlayerHandler.class.getSimpleName());
            setGroupState(GroupState.NO_ACTION);
            ConnectionHelper connectionHelper = ConnectionHelper.getInstance();
            player = new Player(SettingsManager.getPlayerName(context), connectionHelper.getMacAddr());
            group = new Player(null, null);
            players = new ArrayList<>();
            groups = new ArrayList<>();
            levelSelected = new HashMap<>();
            pointsTable = new ArrayList<>();
            packetizer = Packetizer.getInstance();
            multicastReceiver = connectionHelper.getMulticastReceiver();
            multicastSender = connectionHelper.getMulticastSender();
        } catch (UnknownHostException e) {
            Log.e(DEBUG_STRING, "constructor init failed");
        }
    }

    public static synchronized MultiPlayerHandler getInstance(Context context) {
        if (multiPlayerHandler == null) {
            multiPlayerHandler = new MultiPlayerHandler(context);
        }
        return multiPlayerHandler;
    }

    public ResultInfo getPlayerResult(Player player) {
        ResultInfo resultInfo = null;
        for (ResultInfo entry : pointsTable) {
            if (entry.getPlayer().equals(player)) {
                resultInfo = entry;
                break;
            }
        }
        return resultInfo;
    }

    public  boolean isGroupOwner()
    {
        boolean owner=false;
        if(group.getMacAddress()==null)
            owner=false;
        else
            owner=group.equals(player);
        return owner;
    }
    public void updatePlayerResult(Player player, Integer seconds, boolean won) {
        getPlayerResult(player).set(won, seconds);
    }

    public void addPlayerResult(Player player, Integer seconds, boolean won) {
        pointsTable.add(new ResultInfo(won, seconds, player));
    }

    public void clearPointsTable() {
        pointsTable.clear();
    }

    private ArrayList<ResultInfo> pointsTable;

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Player> getGroups() {
        return groups;
    }


    public Player getPlayer() {
        return player;
    }

    public MulticastReceiver getMulticastReceiver() {
        return multicastReceiver;
    }


    public void removePlayer(Player player) {
        for (Player playerInfo : players) {
            if (playerInfo.equals(player)) {
                players.remove(playerInfo);
                break;
            }
        }
    }

    public void removeLevelSelected(Player player) {
        for (Player levelSelector : levelSelected.keySet()) {
            if (levelSelector.equals(player)) {
                levelSelected.remove(levelSelector);
            }
        }
    }

    public void removeAvailableGroup(Player group) {
        for (Player groupInfo : groups) {
            if (groupInfo.equals(group)) {
                groups.remove(groupInfo);
                break;
            }
        }
    }

    public Player getGroup() {
        return group;
    }

    public GroupState getGroupState() {
        return groupState;
    }


    public void clearGroups() {
        groups.clear();
    }

    public void clearGroup() {
        group.setMacAddress(null);
        group.setName(null);
    }

    public void clearPlayers() {
        players.clear();
    }

    public boolean isPlayerAdded(Player player) {
        boolean isAdded = false;
        for (Player playerInfo : players) {
            if (playerInfo.equals(player)) {
                isAdded = true;
                break;
            }
        }
        return isAdded;
    }

    public boolean isGroupAdded(Player group) {
        boolean isAdded = false;
        for (Player groupInfo : groups) {
            if (groupInfo.equals(group)) {
                isAdded = true;
                break;
            }
        }
        return isAdded;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addGroup(Player group) {
        groups.add(group);
    }

    public void setGroupState(GroupState groupState) {
        this.groupState = groupState;
    }


    public void setPlayerListScreenHandler(Handler playerListScreenHandler) {
        this.playerListScreenHandler = playerListScreenHandler;
    }

    public void setGameScreenHandler(Handler gameScreenHandler) {
        this.gameScreenHandler = gameScreenHandler;
    }

    public void setLevelScreenHandler(Handler levelScreenHandler) {
        this.levelScreenHandler = levelScreenHandler;
    }

    public boolean acquireMulticastLock(boolean lock) {
        if (lock && !multicastLock.isHeld()) {
            multicastLock.acquire();
        } else
            multicastLock.release();

        return multicastLock.isHeld();


    }


    public void sendWords(ArrayList<String> wordArray) {
            sendPacket(packetizer.createWordsInfoPacket(player.getName(), player.getMacAddress(), group.getName(), group.getMacAddress(), wordArray));
    }

    public void sendGroupInfo() {
            sendPacket(packetizer.createPlayersInfoPacket(group.getName(), group.getMacAddress(), players));
    }


    public boolean isAdvertising()
    {
        boolean advertising=false;
        if(advertiserThread!=null)
        {
            advertising=!advertiserThread.isPaused();
        }
        return advertising;
    }
    public void setGroupAdvertisement(boolean value,boolean sendLeavePacket) {
        advertiserThread.setPaused(!value,sendLeavePacket);
    }

    public void sendGroupAdvertisement() {
        Log.d("advertising",player.toString());
        GroupInfoPacket groupInfoPacket= packetizer.createAdvertiseGroupPacket(player.getName(), player.getMacAddress());
        sendPacket(groupInfoPacket);
    }

    public void advertiseGroup() {
        setGroupState(GroupState.REQUESTED);
        if (advertiserThread == null) {
            advertiserThread = new GroupAdvertiser(multiPlayerHandler);
            advertiserThread.start();
        }
    }

    public void addLevelInfo(LevelInfoPacket levelInfoPacket) {
        for (Player player : players) {
            Player levelPlayer = new Player(levelInfoPacket.getPlayerName(), levelInfoPacket.getPlayerMac());
            if (player.equals(levelPlayer)) {
                levelSelected.put(player, levelInfoPacket.getLevel());
                break;
            }
        }
    }

    public void sendLevelInfo(String level) {
        sendPacket(packetizer.createLevelInfoPacket(level, player.getName(), player.getMacAddress(), group.getName(), group.getMacAddress()));
    }

    public void sendPacket(Packet packet) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(packet);
            byte[] data = outputStream.toByteArray();
            sendPacket(data);
        } catch (IOException e) {
            Log.e(DEBUG_STRING, e.getMessage());
        }
    }

    public void sendLeaveGroupPacket() {
        sendPacket(packetizer.createRemoveGroup(player.getName(), player.getMacAddress(), group.getName(), group.getMacAddress()));
    }

    public void sendJoinedGroupPacket(Player group) {
        sendPacket(packetizer.createJoinedRequestPacket(player.getName(), player.getMacAddress(), group.getName(), group.getMacAddress()));
    }

    public void sendLockGroupPacket() {
        sendPacket(packetizer.createGroupAttendancePacket(group.getName(), group.getMacAddress()));

    }

    public Player getWordSetter() {
        return wordSetter;
    }

    public Player getNextPlayer() {
        Player nextPlayer = null;
        if (wordSetter != null) {
            int index = players.indexOf(wordSetter);
            if (index < players.size() - 1) {
                nextPlayer = players.get(index + 1);
            } else {
                if (players.size() > 0)
                    nextPlayer = players.get(0);
            }
        }
        return nextPlayer;
    }

    public void setCurrentPlayer() {
        if (wordSetter == null) {
            if (players.size() > 0)
                wordSetter = players.get(0);
        } else {
            int index = players.indexOf(wordSetter);
            if (index < players.size() - 1) {
                wordSetter = players.get(index + 1);
            } else {
                if (players.size() > 0)
                    wordSetter = players.get(0);
            }
        }
    }

    public void sendResult(int seconds) {
        sendPacket(packetizer.createResultInfoPacket(seconds, player.getName(), player.getMacAddress(), group.getName(), group.getMacAddress()));
    }

    public boolean hasJoinedGroup()
    {
        boolean joined=false;
        if(group==null)
            joined=false;
        else if(group.getName()==null)
            joined=false;
        else
            joined=true;
        return joined;
    }
    public void sendJoinGroupPacket(Player destGroup) {
        if (groupState == GroupState.NO_ACTION) {
            group.setName(destGroup.getName());
            group.setMacAddress(destGroup.getMacAddress());
            setGroupState(GroupState.REQUESTED);
            sendPacket(packetizer.createJoinRequestPacket(player.getName(), player.getMacAddress(), destGroup.getName(), destGroup.getMacAddress()));
        }
    }

    private void sendPacket(byte bytes[]) {
        Runnable senderThread = new Runnable() {
            @Override
            public void run() {
                try {
                    multicastSender.send(bytes);
                } catch (IOException e) {
                    Log.e(DEBUG_STRING, e.getMessage());
                }
            }
        };
        new Thread(senderThread).start();
    }

    public void setReceiverActive() {
        if (!receiverStarted) {
            receiverStarted = true;
            Thread receiverThread = new PacketReceiver(this);
            receiverThread.start();
        }
    }

    public boolean isLevelDecided() {
        return levelSelected.size() == players.size();

    }

    public void clearLevelSelected() {
        levelSelected.clear();
    }

    public String getPolledLevel() {
        int easyCount=0;
        int mediumCount=0;
        int hardCount=0;
        for (String selectedLevel : levelSelected.values()) {
            if(selectedLevel.compareToIgnoreCase(EASY)==0)
                easyCount++;
            else if(selectedLevel.compareToIgnoreCase(MEDIUM)==0)
            {
                mediumCount++;
            }
            else if(selectedLevel.compareToIgnoreCase(HARD)==0)
            {
                hardCount++;
            }
        }

        String resultLevel=EASY;

        if((easyCount==mediumCount)&&(easyCount==hardCount))
        {
         resultLevel=EASY;//all chose different
        }
        else if((easyCount==mediumCount)&&(hardCount>easyCount))
        {
            resultLevel=HARD;//hard poll
        }
        else if((easyCount==hardCount)&&(mediumCount>easyCount))
        {
            resultLevel=MEDIUM;//medium poll
        }
        else if((mediumCount==hardCount)&&(easyCount>mediumCount))
        {
            resultLevel=EASY;//easy poll
        }
        else if((easyCount!=mediumCount)&&(mediumCount!=hardCount)&&(easyCount!=hardCount))
        {
            if(easyCount>mediumCount)
            {
               if(easyCount>hardCount)
               {
                   resultLevel=EASY;
               }
               else
               {
                   resultLevel=HARD;
               }
            }
            else if(mediumCount>easyCount)
            {
                if(mediumCount>hardCount)
                {
                    resultLevel=MEDIUM;
                }
                else
                {
                    resultLevel=HARD;
                }

            }

        }
        else
        {
            resultLevel=EASY;//two equal poll and the other one less
        }

        return resultLevel;
    }

    public void handlePacket(byte bytes[]) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream is = new ObjectInputStream(in);
            Packet packet = (Packet) is.readObject();

            Bundle bundle = new Bundle();
            String key = Packetizer.KEY_UNKNOWN_MSG;
            switch (packet.getPacketID()) {
                case Packetizer.ADVERTISE_GROUP: {
                    key = Packetizer.KEY_ADVERTIZE_GROUP;
                }
                break;
                case Packetizer.LEAVE_GROUP: {
                    key = Packetizer.KEY_LEAVE_GROUP;
                }
                break;
                case Packetizer.JOIN_GROUP: {
                    key = Packetizer.KEY_JOIN_GROUP;
                }
                break;
                case Packetizer.JOINED_GROUP: {
                    key = Packetizer.KEY_JOINED_GROUP;
                }
                break;
                case Packetizer.PLAYERS_INFO: {
                    key = Packetizer.KEY_PLAYERS_INFO;
                }
                break;
                case Packetizer.GROUP_ATTENDANCE: {
                    key = Packetizer.KEY_GROUP_ATTENDANCE;
                }
                break;
                case Packetizer.LEVEL_INFO: {
                    key = Packetizer.KEY_LEVEL_INFO;
                }
                break;
                case Packetizer.WORDS_INFO: {
                    key = Packetizer.KEY_MEMORIZE_WORDS;
                }
                break;
                case Packetizer.PLAYER_RESULT: {
                    key = Packetizer.KEY_PLAYER_RESULT;
                }
                break;

            }


            bundle.putSerializable(key, packet);

            if (gameScreenHandler != null) {
                Message message = gameScreenHandler.obtainMessage(packet.getPacketID());
                message.setData(bundle);
                Message m = new Message();
                m.copyFrom(message);
                gameScreenHandler.sendMessage(m);
            }

            if (playerListScreenHandler != null) {
                Message message = playerListScreenHandler.obtainMessage(packet.getPacketID());
                message.setData(bundle);
                Message m = new Message();
                m.copyFrom(message);
                playerListScreenHandler.sendMessage(m);
            }
            if (levelScreenHandler != null) {
                Message message = levelScreenHandler.obtainMessage(packet.getPacketID());
                message.setData(bundle);
                Message m = new Message();
                m.copyFrom(message);
                levelScreenHandler.sendMessage(m);
            }

        } catch (Exception e) {
            Log.e(DEBUG_STRING, "error in handling packet");
        }
    }
}
