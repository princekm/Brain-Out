package com.mindgame.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.android.material.snackbar.Snackbar;
import com.mindgame.adapters.PlayerListAdapter;
import com.mindgame.connection.MultiPlayerHandler;
import com.mindgame.connection.packets.Player;
import com.mindgame.connection.packets.PlayerInfoPacket;
import com.mindgame.connection.packets.ResultInfoPacket;
import com.mindgame.connection.packets.WordsInfoPacket;
import com.mindgame.listeners.dragndrop.DragListener;
import com.mindgame.listeners.menu.GameStarter;
import com.mindgame.listeners.dragndrop.TouchListener;
import com.mindgame.listeners.menu.LevelOpener;
import com.mindgame.listeners.menu.WordAdder;
import com.mindgame.managers.AnimationManager;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.SoundManager;
import com.mindgame.threads.GameStarterThread;
import com.mindgame.threads.GuessTimerThread;
import com.mindgame.threads.MemoryTimerThread;
import com.mindgame.threads.ShowLevelThread;
import com.mindgame.threads.TimerThread;
import com.race604.drawable.wave.WaveDrawable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class GameScreenActivity extends AppCompatActivity {

    private String player;
    private String timeInfo;
    private String level;
    private final int LEVEL_SHOW_DELAY = 5000;
    private final int EASY_COUNT=5;
    private final int MEDIUM_COUNT=10;
    private final int HARD_COUNT=15;
    private final String CUSTOM_FONT ="fonts/orbitron.ttf";
    public enum MessageType{GENERAL,SUCCESS,FAILURE};
    public enum TextColor{GREEN, RED,WHITE};
    private LinearLayout linearLayout;
    private int dragStartedIndex=-1;
    private ArrayList<String> wordArray;
    private CircleProgressBar timeTextView;
    private TextView levelTextView;
    private TextView infoView;
    private ImageView imageView;
    private Button multiFuncButton;
    private Button addWordButton;
    private LinearLayout editPad;
    private EditText editText;
    private WaveDrawable progressBarDrawable;
    private  ParticleEffect particleEffect;
    private Handler timerHandler;
    private MemoryTimerThread memoryTimerThread;
    private GuessTimerThread guessTimerThread;
    private ShowLevelThread showLevelThread;
    private long memorizeTime;
    private long guessTime;
    private int loadedWordCount;
    private boolean playToggle;
    private int textCount;
    private ListView playerListView;
    private TextView resultTextView;
    private LinearLayout pointsTable;
    private PlayerListAdapter playerListAdapter;

    private AnimationManager animationManager;



    private boolean visible;
    private MultiPlayerHandler multiPlayerHandler;

    public enum GameState{NOT_STARTED,STARTED,SHUFFLED,FINISHED};


    private GameState gameState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        loadModeAndLevel();
        instantiateViews();
        init();
        setButtonActions();
        setLevelText();
        createTextViews();
    }

    private void openPlayerListScreen()
    {
        Intent i = new Intent(this, PlayerListScreenActivity.class);
        i.putExtra(PlayerModeScreenActivity.PLAYER_MODE,player);
        startActivity(i);
        finish();
    }
    public void handleLeaveGroup(PlayerInfoPacket playerInfoPacket)
    {
        MultiPlayerHandler.GroupState groupState= multiPlayerHandler.getGroupState();

        Player group = playerInfoPacket.getGroup();
        Player player = playerInfoPacket.getPlayer();

        if(groupState == MultiPlayerHandler.GroupState.LOCKED)
        {
            if(group.equals(multiPlayerHandler.getGroup()))
            {
                if(player.equals(multiPlayerHandler.getPlayer()))
                {
                    multiPlayerHandler.clearPlayers();
                    multiPlayerHandler.clearGroup();
                    multiPlayerHandler.clearLevelSelected();
                    multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                    openPlayerListScreen();
                }
                else
                {
                    multiPlayerHandler.removePlayer(player);
                    multiPlayerHandler.removeLevelSelected(player);
                    if(multiPlayerHandler.getPlayers().size()==1)
                    {
                        multiPlayerHandler.clearPlayers();
                        multiPlayerHandler.clearGroup();
                        multiPlayerHandler.clearLevelSelected();
                        multiPlayerHandler.setGroupState(MultiPlayerHandler.GroupState.NO_ACTION);
                        openPlayerListScreen();
                    }

                }
            }
        }
    }

    public void clearPointsTable()
    {
        multiPlayerHandler.clearPointsTable();

    }
    public void clearLevelSelected()
    {
        multiPlayerHandler.clearLevelSelected();

    }
    public void openLevelScreen()
    {
        Intent i = new Intent(this, LevelScreenActivity.class);
        i.putExtra(PlayerModeScreenActivity.PLAYER_MODE,player);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed() {
        if(player.equals(PlayerModeScreenActivity.MULTI_PLAYER))
        {
            if (pointsTable.getVisibility() == View.VISIBLE)
            {
                showPointsTable(false);
                timerHandler.removeCallbacks(showLevelThread);
                multiPlayerHandler.clearPointsTable();
                multiPlayerHandler.clearLevelSelected();
                openLevelScreen();
            }
        }
        else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivityVisible(true);
        if(player.equals(PlayerModeScreenActivity.MULTI_PLAYER)) {
            if (!multiPlayerHandler.getWordSetter().equals(multiPlayerHandler.getPlayer())) {
                showInfo("Waiting for Words", MessageType.GENERAL);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        setActivityVisible(false);
    }

    public boolean isActivityVisible() {
        return visible;
    }
    public void showSnackInfo(String text)
    {
        Snackbar snackbar = Snackbar.make(linearLayout, text, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorOrange));
        snackbar.show();
    }
    private void setActivityVisible(boolean visible) {
        this.visible = visible;
    }
    private void toggleMultiFunctionButton()
    {
            playToggle=!playToggle;
            if(playToggle)
                multiFuncButton.setText("Play");
            else
                multiFuncButton.setText("Set");
    }
    private void init()
    {
        gameState = GameState.NOT_STARTED;
        loadedWordCount=0;
        playToggle=true;
        wordArray = new ArrayList<>();
        timerHandler = new Handler();
        particleEffect = new ParticleEffect(this,linearLayout);
        animationManager= new AnimationManager(this);
        if(player.equals(PlayerModeScreenActivity.MULTI_PLAYER)) {

            playerListAdapter =new PlayerListAdapter(this);
            playerListView.setAdapter(playerListAdapter);
            multiPlayerHandler = MultiPlayerHandler.getInstance(this);
            showSnackInfo("Setting multihandler");
            multiPlayerHandler.setGameScreenHandler(new GameScreenHandler(this));
            multiPlayerHandler.setCurrentPlayer();
            if(!multiPlayerHandler.getWordSetter().equals(multiPlayerHandler.getPlayer()))
            {
                showSnackInfo(multiPlayerHandler.getWordSetter().getName() + " will set the words");
                multiFuncButton.setVisibility(View.INVISIBLE);
            }
            else
            {
                showSnackInfo("Set the words");
                toggleMultiFunctionButton();
            }
        }


    }
    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getLoadedWordCount() {
        return loadedWordCount;
    }

    public int getTotalWordCount()
    {
        return wordArray.size();
    }
    public void addNextWord()
    {
        TextView textView = (TextView) linearLayout.getChildAt(loadedWordCount);
        textView.setText(wordArray.get(loadedWordCount));
        textView.setVisibility(View.VISIBLE);
        animationManager.loadFadeInAnimation(textView);
        loadedWordCount++;

    }
    private void showStartButton(boolean value)
    {
        if(value)
        {
            multiFuncButton.setVisibility(View.VISIBLE);
            animationManager.loadSlideDownAnimation(multiFuncButton);
        }
        else
            animationManager.loadFadeOutAnimation(multiFuncButton);

    }

    private void sendWords()
    {
        multiPlayerHandler.sendWords(wordArray);
    }
    private void kickGame()
    {
        loadedWordCount = 0;
        for (int index = 0; index < linearLayout.getChildCount(); ++index) {
            View view = linearLayout.getChildAt(index);
            view.setVisibility(View.INVISIBLE);
        }
        showStartButton(false);
        showInfiniteBrain(View.VISIBLE);
        progressBarDrawable.setLevel(0);
        progressBarDrawable.setIndeterminate(true);
        timerHandler.postDelayed(new GameStarterThread(this, 3000), 1000);

    }
    public void delegateStartGame()
    {
        if(player.equals(PlayerModeScreenActivity.SINGLE_PLAYER)) {
            kickGame();
        }
        else
        {
            String buttonText = multiFuncButton.getText().toString();
            if(buttonText.compareToIgnoreCase("Play")==0)
            {
                sendWords();
                showInfo("Waiting for Results",MessageType.GENERAL);
                multiFuncButton.setVisibility(View.INVISIBLE);
            }
            else if(buttonText.compareToIgnoreCase("Set")==0)
            {
                showEditPad(true);
                multiFuncButton.setVisibility(View.INVISIBLE);
            }
        }
    }
    public Handler getTimerHandler() {
        return timerHandler;
    }
    public void updateTimeInfo(String text,TextColor textColor)
    {
        if(!text.isEmpty()) {
            timeTextView.setProgress(Integer.valueOf(text));
            timeInfo = "" + (guessTime/1000 - Integer.valueOf(text));
        }
        int color=0;
        switch (textColor)
        {
            case GREEN:
                color=  getResources().getColor(R.color.colorGreen);
                break;
            case RED:
                color=  getResources().getColor(R.color.colorRed);
                break;
            case WHITE:
                color=  getResources().getColor(R.color.colorWhite);
                break;
        }
      //  timeTextView.setTextColor(color);
    }

    public void showTimeInfo(int visibility)
    {
        if(visibility == View.VISIBLE) {
            timeTextView.setVisibility(View.VISIBLE);
            animationManager.loadFadeInAnimation(timeTextView);
        }
        else if(visibility == View.INVISIBLE)
            animationManager.loadFadeOutAnimation(timeTextView);
    }
    private void instantiateViews()
    {
        linearLayout = findViewById(R.id.textpad);
        resultTextView= findViewById(R.id.closeInfo);
        pointsTable = findViewById(R.id.pointsTable);
        playerListView = findViewById(R.id.pointsList);
        editPad = findViewById(R.id.editPad);
        editText = findViewById(R.id.wordEdit);
        addWordButton = findViewById(R.id.addWordButton);
        multiFuncButton = findViewById(R.id.multiFuncButton);
        timeTextView = findViewById(R.id.timeTextView);
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher_foreground);
        progressBarDrawable = new WaveDrawable(drawable);
        progressBarDrawable.setWaveAmplitude(10);
        progressBarDrawable.setWaveLength(5);
        progressBarDrawable.setWaveSpeed(100);
        progressBarDrawable.setIndeterminate(true);
        imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(progressBarDrawable);
        levelTextView = findViewById(R.id.levelTextView);
        infoView = findViewById(R.id.infoView);

    }
    public void setResultInfo(String text)
    {
        resultTextView.setText(text);
    }
    private void showEditPad(boolean show)
    {
        if(show)
        {
            editPad.setVisibility(View.VISIBLE);
        }
        else
        {
            editPad.setVisibility(View.INVISIBLE);
        }
    }
    public void addWord()
    {
        String word=editText.getText().toString().toUpperCase();
        if(!word.trim().isEmpty()) {
            if(wordArray.size()<textCount)
            {
                if(wordArray.indexOf(word)==-1)
                {
                    wordArray.add(word);
                    if(SettingsManager.isSoundOn(this))
                    {
                        SoundManager.playAddSound(this);
                    }
                    showSnackInfo(word + " added");
                    addNextWord();
                    editText.setText("");
                    if (wordArray.size() == textCount)
                    {
                        toggleMultiFunctionButton();
                        hideKeyboard();
                        showEditPad(false);
                        multiFuncButton.setVisibility(View.VISIBLE);
                    }
                }
                else
                    showSnackInfo(word + " already added");
            }

        }
        else
        {
            showSnackInfo("Type a word");
        }
    }
    public void hideInfo()
    {
        animationManager.loadFadeOutAnimation(infoView);

    }
    public void showInfiniteBrain(int visible)
    {
        if(visible == View.VISIBLE) {
            imageView.setVisibility(View.VISIBLE);
            animationManager.loadFadeInAnimation(imageView);
        }
        else if(visible == View.INVISIBLE)
            animationManager.loadFadeOutAnimation(imageView);
    }
    public void showInfo(String infoString,MessageType messageType)
    {
        animationManager.loadWobble(infoView);
        int color=0;
        switch (messageType)
        {
            case GENERAL:
              color=  getResources().getColor(R.color.colorOrange);
                break;
            case FAILURE:
                color=  getResources().getColor(R.color.colorRed);
                break;
            case SUCCESS:
                color=  getResources().getColor(R.color.colorGreen);

                break;
        }
//        infoView.setTypeface();
        infoView.setTextColor(color);
        infoView.setText(infoString);
        infoView.bringToFront();

    }

    private void createTextViews()
    {
        if(level.equals(LevelScreenActivity.EASY))
        {
            textCount=EASY_COUNT;
            memorizeTime=10000;
            guessTime=10000;
        }
        else if(level.equals(LevelScreenActivity.MEDIUM))
        {
            textCount=MEDIUM_COUNT;
            memorizeTime=20000;
            guessTime=30000;

        }
        else if(level.equals(LevelScreenActivity.HARD))
        {
            textCount=HARD_COUNT;
            memorizeTime=30000;
            guessTime=60000;
        }
        if(textCount>0) {
            int padding = getResources().getInteger(R.integer.textview_padding);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(50,10,50,10);
            TouchListener touchListener = new TouchListener(this);
            DragListener dragListener = new DragListener(this);
            for(int index=0;index<textCount;++index)
            {
                TextView textView = new TextView(this);
                Typeface face = Typeface.createFromAsset(getAssets(), CUSTOM_FONT);
                textView.setTypeface(face);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(padding,padding,padding,padding);
                textView.setVisibility(View.INVISIBLE);
                if(player.equals(PlayerModeScreenActivity.SINGLE_PLAYER) || (multiPlayerHandler.getPlayer()!=multiPlayerHandler.getWordSetter()))
                {
                    textView.setOnTouchListener(touchListener);
                    textView.setOnDragListener(dragListener);
                }
                textView.setTextSize(13);
                textView.setTextColor(getResources().getColor(R.color.colorWhite));
                textView.setBackground(getResources().getDrawable(R.drawable.text_default));
                linearLayout.addView(textView);
            }
        }
    }
    public void handleResultInfoPacket(ResultInfoPacket resultInfoPacket)
    {
        MultiPlayerHandler.GroupState groupState = multiPlayerHandler.getGroupState();
        if(groupState == MultiPlayerHandler.GroupState.LOCKED)
        {
            Player group = new Player(resultInfoPacket.getGroupName(),resultInfoPacket.getMacAddress());
            Player player = new Player(resultInfoPacket.getPlayerName(),resultInfoPacket.getPlayerMac());
            if(group.equals(multiPlayerHandler.getGroup()))
            {
                if(!multiPlayerHandler.getWordSetter().equals(player))
                {
                    if (multiPlayerHandler.getPlayerResult(player) == null)

                        multiPlayerHandler.addPlayerResult(player, resultInfoPacket.getElapsedTime(), resultInfoPacket.isWon());
                    else
                        multiPlayerHandler.updatePlayerResult(player, resultInfoPacket.getElapsedTime(), resultInfoPacket.isWon());
                }
                String playerName= player.getName();
                if(player.equals(multiPlayerHandler.getPlayer()))
                    playerName="You";

                if(resultInfoPacket.isWon())
                {
                    showSnackInfo(playerName + " did it in "+resultInfoPacket.getElapsedTime()+" secs");
                }
                if(pointsTable.getVisibility()==View.INVISIBLE)
                {
                    if (multiPlayerHandler.getPointsTable().size() == (multiPlayerHandler.getPlayers().size() - 1)) {
                        multiPlayerHandler.clearLevelSelected();
                        showPointsTable(true);
                        delegateShowLevelScreen();
                        hideInfo();
                    }
                }
            }
        }
    }
    public  void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void showInfoOnTable(long elapsedMillisecs)
    {
        Player nextPlayer=multiPlayerHandler.getNextPlayer();
        if(nextPlayer!=null) {
            String playerInfo =nextPlayer.getName() + " will set words next in ";
            setResultInfo(playerInfo+elapsedMillisecs / TimerThread.DELAY_MILLI_SECS + " secs ... ");
        }

    }
    public void showPointsTable(boolean value)
    {
        if(value) {
            pointsTable.setVisibility(View.VISIBLE);
            playerListAdapter.addAll(multiPlayerHandler.getPointsTable());
            playerListAdapter.notifyDataSetChanged();

        }
        else
        {
            pointsTable.setVisibility(View.INVISIBLE);
            playerListAdapter.clear();
        }
    }
    public void handleWordsPacket(WordsInfoPacket wordsInfoPacket)
    {
        MultiPlayerHandler.GroupState groupState= multiPlayerHandler.getGroupState();
        Player group = new Player(wordsInfoPacket.getGroupName(),wordsInfoPacket.getMacAddress());
        Player player = new Player(wordsInfoPacket.getPlayerName(),wordsInfoPacket.getPlayerMac());
        if(groupState == MultiPlayerHandler.GroupState.LOCKED)
        {
            if(multiPlayerHandler.getGroup().equals(group) && !player.equals(multiPlayerHandler.getPlayer()))
            {
                showSnackInfo("Words received");
                wordArray=wordsInfoPacket.getWords();
                kickGame();
            }
        }
    }
    private void setLevelText()
    {
        levelTextView.setText(level);
    }
    private void loadModeAndLevel()
    {
        player = getIntent().getExtras().getString(PlayerModeScreenActivity.PLAYER_MODE);
        level = getIntent().getExtras().getString(LevelScreenActivity.LEVEL);
    }


    private void setButtonActions() {
        multiFuncButton.setOnClickListener(new GameStarter(this));
        addWordButton.setOnClickListener(new WordAdder(this));

    }



    public void onFinishMemoryTimer()
    {
        shuffleWords();
        delegateValidate();
        hideInfo();
        showTimeInfo(View.INVISIBLE);

    }

    public void shuffleWords()
    {
        int childCount = linearLayout.getChildCount();

        for (int index = 0; index < childCount; ++index) {
            TextView textView= (TextView) linearLayout.getChildAt(index);
            textView.setText("");
        }
        Random random = new Random();
        ArrayList<String> wordCopy = new ArrayList<>();
        ArrayList<String> shuffledArray = new ArrayList<>();
        wordCopy.addAll(wordArray);

        for(int i=0;i<childCount;++i)
        {
            String tempString= wordArray.get(i);

            if(wordCopy.contains(tempString))
            {
                if(wordCopy.size()==1)
                {
                    shuffledArray.add(tempString);
                    break;
                }
                else
                    wordCopy.remove(tempString);

            }
            int randomIndex=random.nextInt(wordCopy.size());
            String randomString=wordCopy.get(randomIndex);
            Log.d("randomstring",randomString);
            wordCopy.remove(randomString);
            if(!shuffledArray.contains(tempString))
                wordCopy.add(tempString);
            shuffledArray.add(randomString);
        }
        String lastInShuffledString=shuffledArray.get(childCount-1);
        Log.d("laststring",lastInShuffledString);
        if(lastInShuffledString== wordArray.get(childCount-1))
        {
            int randomIndex=random.nextInt(childCount-1);
            Collections.swap(shuffledArray,randomIndex,childCount-1);
//            Log.d("randomindex",""+randomIndex);
//            String temp = shuffledArray.get(randomIndex);
//            Log.d("selectedstring",""+temp);
//            shuffledArray.add(randomIndex,lastInShuffledString);
//            shuffledArray.add(childCount-1,temp);
//            shuffledArray.

        }
        fillTextViews(shuffledArray);
        gameState = GameState.SHUFFLED;

    }

    private void fillTextViews(ArrayList<String> list)
    {
        int childCount = linearLayout.getChildCount();
        for (int index = 0; index < childCount; ++index) {
            TextView textView = (TextView) linearLayout.getChildAt(index);
            textView.setText(list.get(index));
        }

    }
    private void delegateShowLevelScreen()
    {
        showLevelThread=new ShowLevelThread(this,LEVEL_SHOW_DELAY);
        timerHandler.postDelayed(showLevelThread,TimerThread.DELAY_MILLI_SECS);

    }
    public void startGame()
    {
        if(gameState != GameState.STARTED) {
            setGameState(GameState.STARTED);
            progressBarDrawable.setIndeterminate(false);
            memoryTimerThread = new MemoryTimerThread(this, memorizeTime);
            timeTextView.setMax((int) (memorizeTime / TimerThread.DELAY_MILLI_SECS));

            timerHandler.postDelayed(memoryTimerThread, TimerThread.DELAY_MILLI_SECS);
            if (player.equals(PlayerModeScreenActivity.SINGLE_PLAYER)) {
                int childCount = linearLayout.getChildCount();
                {
                    wordArray.clear();
                    for (int index = 0; index < childCount; ++index) {
//                        WordGenerator generator = new WordGenerator();
                        String word = getRandomWord();
                        wordArray.add(word);
                        Log.d("normalword",word);
                    }
                }
            }
        }

    }
    private String getRandomWord()
    {
        WordGenerator wordGenerator=WordGenerator.getInstance();
        return wordGenerator.getRandomWord();
    }
    public void delegateValidate()
    {
        guessTimerThread=new GuessTimerThread(this,guessTime);
        timeTextView.setMax((int)(guessTime/TimerThread.DELAY_MILLI_SECS));
        timerHandler.postDelayed(guessTimerThread, TimerThread.DELAY_MILLI_SECS);

    }
    public void performValidate(boolean forced)
    {
        int childCount = linearLayout.getChildCount();
        {
            boolean ok=true;
            for (int index = 0; index < childCount; ++index)
            {
                TextView textView = (TextView) linearLayout.getChildAt(index);
                String textViewText = textView.getText().toString();
                boolean isMatching=textViewText.equals(wordArray.get(index));
                if(!isMatching) {
                    ok = false;
                    break;
                }
            }
            if(ok)
            {
                particleEffect.showFireWorks();
                if(SettingsManager.isSoundOn(this))
                {
                    SoundManager.playWinSound(this);
                }
                showInfo("You did it in\n"+timeInfo+" secs !",MessageType.SUCCESS);
                setGameState(GameState.FINISHED);
                getTimerHandler().removeCallbacks(guessTimerThread);
                if(player.equals(PlayerModeScreenActivity.SINGLE_PLAYER)) {
                    WordGenerator.getInstance().restoreWords(wordArray);
                    showStartButton(true);
                }
                else
                {
                    multiPlayerHandler.sendResult(Integer.valueOf(timeInfo));

                }
                updateTimeInfo("",TextColor.GREEN);
                showTimeInfo(View.INVISIBLE);
            }
            else if(forced)
            {
                showInfo("Time up !!!",MessageType.FAILURE);
                if(SettingsManager.isSoundOn(this))
                {
                    SoundManager.playLoseSound(this);
                }
                setGameState(GameState.FINISHED);
                if(player.equals(PlayerModeScreenActivity.SINGLE_PLAYER)) {
                    WordGenerator.getInstance().restoreWords(wordArray);
                    showStartButton(true);
                }
                else
                {
                    multiPlayerHandler.sendResult(-1);
                }
                updateTimeInfo("",TextColor.GREEN);
            }
        }
    }

    public void setDragStartedIndex(View view)
    {
        dragStartedIndex = linearLayout.indexOfChild(view);
    }
    public void interChangePositions(View dragView,View dropView)
    {
        int dropIndex = linearLayout.indexOfChild(dropView);
       Drawable normalShape = getResources().getDrawable(R.drawable.text_default);

        if(dropIndex!=dragStartedIndex)
        {
            linearLayout.removeView(dragView);
            linearLayout.addView(dragView,dropIndex);
            linearLayout.removeView(dropView);
            linearLayout.addView(dropView,dragStartedIndex);
            dragView.setBackground(normalShape);
            performValidate(false);
        }
    }


}