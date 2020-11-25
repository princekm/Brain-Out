package com.mindgame.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingsManager {
    private static final String PREFERENCES="GamePreferences";
    private static final String VIBRATE="vibrate";
    private static final String SOUND="sound";
    private static final String PLAYER_NAME="player";
    private static final String DEFAULT_PLAYER_NAME="player";
    private static  SharedPreferences.Editor editor=null;

    private static SharedPreferences sharedPreferences=null;
    public static String getPlayerName(Context context)
    {
        sharedPreferences=getSharedPreferences(context);
        return sharedPreferences.getString(PLAYER_NAME,"");
    }
    public static void savePlayerName(Context activity,String value)
    {
        editor.putString(PLAYER_NAME,value);
        editor.apply();

    }
    public   static boolean isVibrationOn(Context activity)
    {
        return getSharedPreferences(activity).getBoolean(VIBRATE,true);
    }
    public   static boolean isSoundOn(Context activity)
    {

        return getSharedPreferences(activity).getBoolean(SOUND,true);
    }
    public static void saveSound(Context activity,boolean value)
    {

        editor.putBoolean(SOUND,value).commit();
    }
    public static void saveVibration(Context activity,boolean value)
    {

        editor.putBoolean(VIBRATE,value).commit();
    }
    public static void createDefaultIfNotExists(Context context)
    {
        if(getPlayerName(context).isEmpty())
        {
            savePlayerName(context,DEFAULT_PLAYER_NAME);
            saveSound(context,true);
            saveVibration(context,true);
        }
    }
    private static SharedPreferences getSharedPreferences(Context context)
    {
        if(sharedPreferences==null)
        {
            sharedPreferences=context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            editor=sharedPreferences.edit();
        }
        return sharedPreferences;
    }
}
