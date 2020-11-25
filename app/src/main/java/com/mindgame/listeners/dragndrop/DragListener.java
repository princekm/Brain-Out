package com.mindgame.listeners.dragndrop;

import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;

import com.mindgame.activities.GameScreenActivity;
import com.mindgame.activities.R;
import com.mindgame.managers.SettingsManager;
import com.mindgame.managers.VibrationManager;

public class DragListener implements View.OnDragListener {
    private GameScreenActivity activity;
    private Drawable enterShape;
    private Drawable normalShape;
    public DragListener(GameScreenActivity activity) {
        this.activity = activity;
        enterShape = activity.getResources().getDrawable(R.drawable.shape_drop_target);
        normalShape = activity.getResources().getDrawable(R.drawable.text_default);
    }

    @Override
    public boolean onDrag(View dropView, DragEvent event) {
        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                dropView.setBackground(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                dropView.setBackground(normalShape);

                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                    View dragView = (View) event.getLocalState();
                    if(dragView!=null) {
                        if (dragView != dropView)
                        {
                            activity.interChangePositions(dragView, dropView);
                        }
                        dropView.setBackground(normalShape);
                    }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
               // dropView.setBackground(normalShape);
            default:
                break;
        }
        return true;
    }
}

