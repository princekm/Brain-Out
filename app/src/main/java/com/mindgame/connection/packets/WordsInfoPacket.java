package com.mindgame.connection.packets;

import com.mindgame.connection.Packetizer;

import java.util.ArrayList;

public class WordsInfoPacket extends PlayerInfoPacket {
    private static final long serialVersionUID =Packetizer.WORDS_INFO_S_ID;

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    private ArrayList<String> words;
    public WordsInfoPacket(int packetID,String playerName,String playerMac, String groupName,String groupMac) {
        super(packetID, playerName,playerMac, groupName,groupMac);
        words = new ArrayList<>();
    }
}
