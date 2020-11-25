package com.mindgame.managers;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.mindgame.activities.R;

public class AnimationManager {

    private Activity activity;

    public AnimationManager(Activity activity) {
        this.activity = activity;
    }

    public void loadSlideDownAnimation(View view)
    {
        Animation slideDownAnimation = AnimationUtils.loadAnimation(activity, R.anim.slide_down);
        view.startAnimation(slideDownAnimation);
    }
    public void loadFadeInAnimation(View view)
    {
        Animation fadImAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        view.startAnimation(fadImAnimation);
    }
    public void loadFadeOutAnimation(View view)
    {
        Animation fadImAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        view.startAnimation(fadImAnimation);
    }
    public void loadRightToLeftAnimation(View view)
    {
        Animation fadImAnimation = AnimationUtils.loadAnimation(activity, R.anim.right_to_left);
        view.startAnimation(fadImAnimation);
    }
    public void loadWobble(View view)
    {
        Animation wobbleAnimation = AnimationUtils.loadAnimation(activity, R.anim.wobble);
        view.startAnimation(wobbleAnimation);
    }
}
