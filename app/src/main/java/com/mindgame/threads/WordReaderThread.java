package com.mindgame.threads;


import android.util.Log;

import com.mindgame.activities.SplashscreenActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class WordReaderThread extends Thread{
    private ArrayList<String> words;
    private SplashscreenActivity splashscreenActivity;
    public static  final  int TOTAL_WORDS=370099;

    public WordReaderThread(ArrayList<String> words,SplashscreenActivity splashscreenActivity) {
        this.words = words;
        this.splashscreenActivity=splashscreenActivity;
    }

    @Override
    public void run() {
        try {
            long currentTime=0;
            long endTime=0;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(splashscreenActivity.getAssets().open("words/words.txt"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String mLine=null;
                 currentTime=System.currentTimeMillis();

                while ((mLine = reader.readLine()) != null)
                {
                    //process line
                    words.add(mLine);

                    if(words.size()%50==0) {
                        splashscreenActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                splashscreenActivity.updateProgress(words.size());
                            }
                        });
                    }
                }
            } catch (Exception e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                        endTime=System.currentTimeMillis();

                        Log.d("milliseconds",""+(endTime-currentTime));
                        Log.d("milliseconds",""+words.size());
                        splashscreenActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                splashscreenActivity.loadMenuScreenActivity();

                            }
                        });

                    } catch (Exception e) {
                        //log the exception
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
