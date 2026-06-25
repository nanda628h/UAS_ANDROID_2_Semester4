package com.example.remainderjadwal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class TutorialOverlayView extends View {

    private Paint overlayPaint;
    private Paint holePaint;
    private float holeX, holeY, holeRadius;
    private boolean ready = false;

    private ImageView mascotView;
    private AnimationDrawable mouthAnimation;

    public TutorialOverlayView(Context context) {
        super(context);
        init();
    }

    public TutorialOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.parseColor("#CC000000"));

        holePaint = new Paint();
        holePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setHole(float x, float y, float radius) {
        this.holeX = x;
        this.holeY = y;
        this.holeRadius = radius;
        this.ready = true;
        invalidate();
    }

    public ImageView createMascotView(Context context) {
        mascotView = new ImageView(context);
        mascotView.setImageResource(R.drawable.mascot_mouth_closed);
        return mascotView;
    }

    public void startTalking() {
        if (mascotView == null) return;

        mouthAnimation = new AnimationDrawable();
        mouthAnimation.addFrame(
                getContext().getResources().getDrawable(R.drawable.mascot_mouth_open, null), 200);
        mouthAnimation.addFrame(
                getContext().getResources().getDrawable(R.drawable.mascot_mouth_closed, null), 200);
        mouthAnimation.setOneShot(false);

        mascotView.setImageDrawable(mouthAnimation);
        mascotView.post(() -> mouthAnimation.start());
    }

    public void stopTalking() {
        if (mouthAnimation != null) {
            mouthAnimation.stop();
        }
        if (mascotView != null) {
            mascotView.setImageResource(R.drawable.mascot_mouth_closed);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!ready) return;

        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
        canvas.drawCircle(holeX, holeY, holeRadius, holePaint);
    }
}