package com.mindgame.activities;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.plattysoft.leonids.ParticleSystem;

public class ParticleEffect {
    private Activity activity;
    private View destView;

    public ParticleEffect(Activity activity,View destView) {
        this.activity = activity;
       this. destView=destView;
    }
    public void showFireWorks()
    {
        ParticleSystem ps = new ParticleSystem(activity, 100, R.drawable.star_pink, 2000);
        ps.setScaleRange(0.7f, 1.3f);
        ps.setSpeedRange(0.1f, 0.25f);
        ps.setRotationSpeedRange(90, 180);
        ps.setFadeOut(200, new AccelerateInterpolator());
        ps.oneShot(destView, 70);

        ParticleSystem ps2 = new ParticleSystem(activity, 100, R.drawable.star_white, 2000);
        ps2.setScaleRange(0.7f, 1.3f);
        ps2.setSpeedRange(0.1f, 0.25f);
        ps.setRotationSpeedRange(90, 180);
        ps2.setFadeOut(200, new AccelerateInterpolator());
        ps2.oneShot(destView, 70);
    }
}
