package com.mindgame.managers;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;


public class VibrationManager {
    private static final long DURATION = 50;
    public static void playVibration(Activity activity)
    {
        Vibrator v = (Vibrator)activity. getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(DURATION, VibrationEffect.EFFECT_TICK));
        } else {
            //deprecated in API 26
            v.vibrate(DURATION);
        }
    }
    private static void play(MediaPlayer mp)
    {
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

}
