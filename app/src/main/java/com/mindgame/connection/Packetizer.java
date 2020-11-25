package com.mindgame.connection;

import com.mindgame.connection.packets.GroupInfoPacket;
import com.mindgame.connection.packets.LevelInfoPacket;
import com.mindgame.connection.packets.Player;
import com.mindgame.connection.packets.PlayerInfoPacket;
import com.mindgame.connection.packets.PlayersInfoPacket;
import com.mindgame.connection.packets.ResultInfoPacket;
import com.mindgame.connection.packets.WordsInfoPacket;

import java.util.ArrayList;

public class Packetizer {

    private static Packetizer packetizer=null;
    public static final long PACKET_S_ID=1L;
    public static final long GROUP_INFO_S_ID=3L;
    public static final long PLAYER_S_ID=4L;
    public static final long PLAYER_INFO_S_ID=5L;
    public static final long PLAYERS_INFO_S_ID=6L;
    public static final long RESULT_INFO_S_ID=7L;
    public static final long TIMER_INFO_S_ID=8L;
    public static final long WORDS_INFO_S_ID=9L;
    public static final long LEVEL_INFO_S_ID=10L;

    public static final int   ADVERTISE_GROUP=0;
    public static final int   JOIN_GROUP=1;
    public static final int   JOINED_GROUP=2;
    public static final int   GROUP_ATTENDANCE=3;
    public static final int   PLAYER_ATTENDANCE=4;
    public static final int   PLAYERS_INFO=5;
    public static final int   WORDS_INFO =6;
    public static final int   PLAYER_RESULT=7;
    public static final int   LEAVE_GROUP=8;
    public static final int   HEALTH_INFO=9;
    public static final int   UNKNOWN=10;
    public static final int   LEVEL_INFO=11;

    public static final String KEY_ADVERTIZE_GROUP =""+ ADVERTISE_GROUP;
    public static final String KEY_LEAVE_GROUP =""+ LEAVE_GROUP;
    public static final String KEY_JOIN_GROUP =""+ JOIN_GROUP;
    public static final String KEY_JOINED_GROUP =""+ JOINED_GROUP;
    public static final String KEY_PLAYERS_INFO =""+ PLAYERS_INFO;
    public static final String KEY_GROUP_ATTENDANCE =""+ GROUP_ATTENDANCE;
    public static final String KEY_LEVEL_INFO =""+ LEVEL_INFO;
    public static final String KEY_MEMORIZE_WORDS =""+ WORDS_INFO;
    public static final String KEY_PLAYER_RESULT =""+ PLAYER_RESULT;
    public static final String KEY_UNKNOWN_MSG =""+ UNKNOWN;

    public static Packetizer getInstance()
    {
        if(packetizer==null)
        {
            packetizer = new Packetizer();
        }
        return packetizer;
    }
    public GroupInfoPacket createAdvertiseGroupPacket(String groupName, String groupMac)
    {
        return new GroupInfoPacket(ADVERTISE_GROUP,groupName,groupMac);
    }
    private PlayerInfoPacket createPlayerInfoPacket(int packetID,String playerName, String playerMac,String groupName, String groupMac)
    {
        PlayerInfoPacket playerInfoPacket = new PlayerInfoPacket(packetID,playerName,playerMac,groupName,groupMac);
        return playerInfoPacket;
    }
    public PlayerInfoPacket createJoinRequestPacket(String playerName, String playerMac,String groupName, String groupMac )
    {
        return createPlayerInfoPacket(JOIN_GROUP,playerName,playerMac,groupName,groupMac);
    }
    public PlayerInfoPacket createJoinedRequestPacket(String playerName, String playerMac,String groupName, String groupMac)
    {
        return createPlayerInfoPacket(JOINED_GROUP,playerName,playerMac,groupName,groupMac);
    }
    public GroupInfoPacket createGroupAttendancePacket(String groupName, String groupMac)
    {
        return new GroupInfoPacket(GROUP_ATTENDANCE,groupName,groupMac);
    }

    public PlayersInfoPacket createPlayersInfoPacket(String groupName, String groupMac, ArrayList<Player> playersInfo)
    {
        PlayersInfoPacket playersInfoPacket = new PlayersInfoPacket(PLAYERS_INFO,groupName,groupMac);
        playersInfoPacket.setPlayersInfos(playersInfo);
        return playersInfoPacket;
    }
    public WordsInfoPacket createWordsInfoPacket(String playerName, String playerMac,String groupName, String groupMac, ArrayList<String> words)
    {
        return createWordsInfoPacket(WORDS_INFO,playerName,playerMac,groupName,groupMac,words);

    }

    private WordsInfoPacket createWordsInfoPacket(int packetID,String playerName, String playerMac,String groupName, String groupMac,ArrayList<String> words)
    {
        WordsInfoPacket wordsInfoPacket = new WordsInfoPacket(packetID,playerName,playerMac,groupName,groupMac);
        wordsInfoPacket.setWords(words);
        return wordsInfoPacket;
    }

    public ResultInfoPacket createResultInfoPacket(int elapsedTime,String playerName, String playerMac,String groupName, String groupMac)
    {
        ResultInfoPacket resultInfoPacket = new ResultInfoPacket(PLAYER_RESULT,playerName,playerMac,groupName,groupMac,elapsedTime);
        return resultInfoPacket;
    }
    public PlayerInfoPacket createLevelInfoPacket(String level,String playerName, String playerMac,String groupName, String groupMac)
    {
        LevelInfoPacket levelInfoPacket = new LevelInfoPacket(LEVEL_INFO,level,playerName,playerMac,groupName,groupMac);
        return levelInfoPacket;
    }
    public PlayerInfoPacket createRemoveGroup(String playerName, String playerMac,String groupName, String groupMac)
    {
        return createPlayerInfoPacket(LEAVE_GROUP,playerName,playerMac,groupName,groupMac);
    }


}

