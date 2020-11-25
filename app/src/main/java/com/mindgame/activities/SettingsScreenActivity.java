package com.mindgame.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.mindgame.connection.MultiPlayerHandler;
import com.mindgame.listeners.settings.NameSetter;
import com.mindgame.listeners.settings.SoundSetter;
import com.mindgame.listeners.settings.VibrationSetter;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;
import com.mindgame.managers.VibrationManager;

public class SettingsScreenActivity extends AppCompatActivity {

    private EditText nameEdit;
    private Button okButton;
    private Button soundToggleButton;
    private Button vibrateToggleButton;
    private boolean soundToggle;
    private boolean vibrationToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        loadSettingsIfNotCreated();
        instantiateViews();
        setPlayerName();
        setToggleSettings();
        setViewActions();
    }
    private void loadSettingsIfNotCreated()
    {
        SettingsManager.createDefaultIfNotExists(getApplicationContext());
    }
    private void setToggleSettings()
    {
        boolean vibrateValue=SettingsManager.isVibrationOn(this);
        String vibrateOnOff = vibrateValue?getResources().getString(R.string.vibrationDescOn):getResources().getString(R.string.vibrationDescOff);
        vibrateToggleButton.setText(vibrateOnOff);

        boolean soundValue=SettingsManager.isSoundOn(this);
        String soundOnOff = soundValue?getResources().getString(R.string.soundDescOn):getResources().getString(R.string.soundDescOff);
        soundToggleButton.setText(soundOnOff);

    }
    private void setViewActions()
    {
        vibrateToggleButton.setOnClickListener(new VibrationSetter(this));
        okButton.setOnClickListener(new NameSetter(this));
        soundToggleButton.setOnClickListener(new SoundSetter(this));
    }
    private void setPlayerName()
    {
        nameEdit.setText(SettingsManager.getPlayerName(getApplicationContext()));
    }
    public void saveName()
    {
        String playerName=nameEdit.getText().toString();
        if(playerName.length()>0)
        {
            if(SettingsManager.isSoundOn(this))
                SoundManager.playButtonSound(this);
          //  Toast.makeText(this, "Player name is set:"+playerName, Toast.LENGTH_SHORT).show();
            Log.d("nplayer",playerName);
            SettingsManager.savePlayerName(this,playerName);
            MultiPlayerHandler.getInstance(this).getPlayer().setName(playerName);
        }
    }
    public void saveSoundState()
    {
        boolean value = soundToggle;
        if(value)
            SoundManager.playButtonSound(this);
        SettingsManager.saveSound(this,value);
        String onOff = value?getResources().getString(R.string.soundDescOn):getResources().getString(R.string.soundDescOff);
        soundToggleButton.setText(onOff);
        setSoundToggle(!soundToggle);

    }
    public void saveVibrationState()
    {
        boolean value = vibrationToggle;
        if(value)
            VibrationManager.playVibration(this);
        SettingsManager.saveVibration(this,value);
        String onOff = value?getResources().getString(R.string.vibrationDescOn):getResources().getString(R.string.vibrationDescOff);
        vibrateToggleButton.setText(onOff);
        setVibrationToggle(!vibrationToggle);

    }
    private void instantiateViews()
    {
        nameEdit=findViewById(R.id.nameView);
        okButton=findViewById(R.id.okButton);
        vibrateToggleButton=findViewById(R.id.vibrateToggle);
        soundToggleButton=findViewById(R.id.soundToggle);
    }
    public void setSoundToggle(boolean soundToggle) {
        this.soundToggle = soundToggle;
    }


    public void setVibrationToggle(boolean vibrationToggle) {
        this.vibrationToggle = vibrationToggle;
    }


}