package com.mindgame.activities;

import java.util.ArrayList;
import java.util.Random;

public class WordGenerator {
    private ArrayList<String> dictionary;
    private  static WordGenerator wordGenerator=null;
    public ArrayList<String> getDictionary() {
        return dictionary;
    }

    public static synchronized WordGenerator getInstance()
    {
        if(wordGenerator==null)
        {
            wordGenerator= new WordGenerator();
        }
        return wordGenerator;
    }
    private WordGenerator()
    {
        dictionary = new ArrayList<>();

    }
    public String getRandomWord()
    {
        String word="";
        Random random = new Random();
        int index= random.nextInt(dictionary.size());
        word=dictionary.get(index);
        dictionary.remove(index);
        return word.toUpperCase();
    }
    public void restoreWords(ArrayList<String > words)
    {
        dictionary.addAll(words);
    }
}
