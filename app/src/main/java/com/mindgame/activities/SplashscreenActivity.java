    package com.mindgame.activities;

    import androidx.appcompat.app.AppCompatActivity;

    import android.graphics.drawable.Drawable;
    import android.os.Bundle;
    import android.os.Handler;
    import android.util.Log;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.mindgame.connection.MultiPlayerHandler;
    import com.mindgame.managers.AnimationManager;
    import com.mindgame.threads.SplashTimerThread;
    import com.mindgame.threads.WordReaderThread;
    import com.race604.drawable.wave.WaveDrawable;

    /**
     * An example full-screen activity that shows and hides the system UI (i.e.
     * status bar and navigation/system bar) with user interaction.
     */
    public class SplashscreenActivity extends AppCompatActivity {

        private Handler handler;
        private ImageView imageView;
        private SplashTimerThread splashTimerThread;
        private WordReaderThread wordReaderThread;
        private AnimationManager animationManager;
        private WaveDrawable progressBarDrawable;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);
          //
            init();
            instantiateViews();
           // startTitleAnimation();
           // startIconAnimation();
            loadWords();
            Log.d("activitythread","runnning");


        }
        public void updateProgress(int progress)
        {
            progressBarDrawable.setLevel((progress/WordReaderThread.TOTAL_WORDS)*100);
            Log.d("progress",""+progress);
        }
        private void instantiateViews()
        {
            imageView= (ImageView) findViewById(R.id.icon_view);
            progressBarDrawable = new WaveDrawable(this, R.mipmap.ic_launcher_foreground);
            progressBarDrawable.setWaveAmplitude(10);
            progressBarDrawable.setWaveLength(5);
            progressBarDrawable.setWaveSpeed(100);
            progressBarDrawable.setIndeterminate(true);
            imageView = findViewById(R.id.icon_view);
            imageView.setImageDrawable(progressBarDrawable);
            progressBarDrawable.start();



        }
        private void init()
        {
            handler = new Handler();
            animationManager =new AnimationManager(this);


        }
        private void loadWords()
        {
            WordGenerator wordGenerator = WordGenerator.getInstance();
            wordReaderThread = new WordReaderThread(wordGenerator.getDictionary(),this);
            wordReaderThread.start();

        }
        public void loadMenuScreenActivity(){
          //  progressBarDrawable.stop();
            splashTimerThread = new SplashTimerThread(this);
            int delayMilliSecs = getResources().getInteger(R.integer.splash_delay_millisecs);
            handler.postDelayed(splashTimerThread,delayMilliSecs);
        }
        private void startTitleAnimation()
        {
            TextView titleView = (TextView) findViewById(R.id.splashscreen_title);
            animationManager.loadFadeInAnimation(titleView);
        }
        private void startIconAnimation()
        {
            ImageView iconView = (ImageView) findViewById(R.id.icon_view);
            animationManager.loadRightToLeftAnimation(iconView);
        }





    }