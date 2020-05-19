package com.example.hfilproject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

public class ForegroundRelativeLayout extends RelativeLayout {

    private Drawable foregroundSelector;
    private Rect rectPadding;
    private boolean useBackgroundPadding = false;

    public ForegroundRelativeLayout(Context context) {
        super(context);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundRelativeLayout,
                defStyle, 0);

        final Drawable d = a.getDrawable(R.styleable.ForegroundRelativeLayout_foreground);
        if (d != null) {
            setForeground(d);
        }

        a.recycle();

        if (this.getBackground() instanceof NinePatchDrawable) {
            final NinePatchDrawable npd = (NinePatchDrawable) this.getBackground();
            rectPadding = new Rect();
            if (npd.getPadding(rectPadding)) {
                useBackgroundPadding = true;
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (foregroundSelector != null && foregroundSelector.isStateful()) {
            foregroundSelector.setState(getDrawableState());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (foregroundSelector != null) {
            if (useBackgroundPadding) {
                foregroundSelector.setBounds(rectPadding.left, rectPadding.top, w - rectPadding.right, h - rectPadding.bottom);
            } else {
                foregroundSelector.setBounds(0, 0, w, h);
            }
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        if (foregroundSelector != null) {
            foregroundSelector.draw(canvas);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (who == foregroundSelector);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foregroundSelector != null) foregroundSelector.jumpToCurrentState();
    }

    public void setForeground(Drawable drawable) {
        if (foregroundSelector != drawable) {
            if (foregroundSelector != null) {
                foregroundSelector.setCallback(null);
                unscheduleDrawable(foregroundSelector);
            }

            foregroundSelector = drawable;

            if (drawable != null) {
                setWillNotDraw(false);
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (foregroundSelector != null) {
            foregroundSelector.setHotspot(x, y);
        }
    }
}