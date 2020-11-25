package com.mindgame.managers;

import android.app.Activity;
import android.media.MediaPlayer;
import com.mindgame.activities.R;

public class SoundManager {
    public static void playButtonSound(Activity activity)
    {

        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.click);
        play(mp);
    }
    public static void playWinSound(Activity activity)
    {
        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.point);
        play(mp);

    }
    public static void playAddSound(Activity activity)
    {
        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.click);
        play(mp);

    }
    public static void playLoseSound(Activity activity)
    {
        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.lose);
        play(mp);

    }
    public static void playTickSound(Activity activity)
    {
        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.tick);
        mp.setVolume(0.01f,0.01f);
        play(mp);

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
