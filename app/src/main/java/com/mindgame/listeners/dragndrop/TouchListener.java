package com.mindgame.listeners.dragndrop;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.VibrationManager;

public final class TouchListener implements View.OnTouchListener {
    private GameScreenActivity activity;

    public TouchListener(GameScreenActivity activity) {
        this.activity = activity;
    }

    public boolean onTouch(View dragView, MotionEvent motionEvent) {
        if(activity.getGameState()== GameScreenActivity.GameState.SHUFFLED)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        dragView);
                dragView.startDrag(data, shadowBuilder, dragView, 0);
                if(SettingsManager.isVibrationOn(activity))
                {
                    VibrationManager.playVibration(activity);

                }

                activity.setDragStartedIndex(dragView);
                return true;
            }
            else
                return false;
        }
        else {
            return false;
        }
    }
}