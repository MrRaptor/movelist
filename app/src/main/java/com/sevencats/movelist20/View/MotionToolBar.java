package com.sevencats.movelist20.View;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.constraint.motion.MotionLayout;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

import com.sevencats.movelist20.MainActivity;

public class MotionToolBar extends MotionLayout implements AppBarLayout.OnOffsetChangedListener {

    public MotionToolBar(Context context) {
        super(context);
    }

    public MotionToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MotionToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float progress = -verticalOffset / (float)appBarLayout.getTotalScrollRange();
        setProgress(progress);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AppBarLayout a = (AppBarLayout)getParent();
        a.addOnOffsetChangedListener(this);
    }


}
