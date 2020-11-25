package com.mindgame.listeners.menu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class ScreenOpener implements View.OnClickListener {
    private Activity activity;
    private Class nextActivityClass;
    private boolean finishCurrentActivity;
    private Map<String,String> messages;

    public ScreenOpener(Activity activity, Class nextActivityClass, boolean finishCurrentActivity) {
        this.activity = activity;
        this.nextActivityClass = nextActivityClass;
        this.finishCurrentActivity = finishCurrentActivity;
        this.messages=new HashMap<String, String>() ;
    }

    public void addMessage(String key,String value)
    {
        messages.put(key,value);
    }
    @Override
    public void onClick(View v) {
        if(SettingsManager.isSoundOn(activity))
            SoundManager.playButtonSound(activity);
        Intent i = new Intent(activity, nextActivityClass);
        for(Map.Entry<String,String> entry:messages.entrySet())
        {
            i.putExtra(entry.getKey(),entry.getValue());
        }
        activity.startActivity(i);
        if(finishCurrentActivity)
            activity.finish();

    }

}
