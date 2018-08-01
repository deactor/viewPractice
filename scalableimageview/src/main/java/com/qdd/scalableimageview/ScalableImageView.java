package com.qdd.scalableimageview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

public class ScalableImageView extends View implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {
    private static final String TAG = "qddt";
    private static final float DEFAULT_SCALE = 4f;

    private Bitmap bmp;
    private Paint bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float imageWidth = Utils.dp2px(250);
    private float imageHight;
    private float mScale = 1f;

    private boolean isScale;
    private GestureDetector gestureDetector;
    private float gesture_dx;
    private float gesture_dy;

    private OverScroller mScroller;

    public ScalableImageView(Context context) {
        this(context, null);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        bmp = Utils.getAvatar(context.getResources(), (int) imageWidth);
        imageHight = bmp.getHeight();
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setOnDoubleTapListener(this);

        mAnimator = ObjectAnimator.ofFloat(this, "fraction", 1f);

        mScroller = new OverScroller(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gestureDetector.onTouchEvent(event);

    }

    private float offectX;
    private float offectY;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        offectX = (getWidth() - bmp.getWidth()) / 2;
        offectY = (getHeight() - bmp.getHeight()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mScale = 1f + (DEFAULT_SCALE - 1f) * fraction;;
        canvas.drawColor(Color.BLUE);
        canvas.translate(gesture_dx,gesture_dy);
        canvas.scale(mScale, mScale, getWidth() / 2, getHeight() / 2);
        canvas.drawBitmap(bmp, offectX, offectY, bmpPaint);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    private float fraction;
    private ObjectAnimator mAnimator;

    public float getFraction() {
        return fraction;
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
        invalidate();
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        isScale = !isScale;
        if (isScale) {
            mAnimator.start();
        } else {
            mAnimator.reverse();
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {
        if(!isScale){
            return true;
        }
        gesture_dx -= dx;
        //gesture_dx需要在- (imageWidth*mScale - getWidth())/ 2 和(imageWidth*mScale - getWidth())/ 2之间
        gesture_dx = Math.min(gesture_dx,(imageWidth*mScale - getWidth())/ 2);
        gesture_dx = Math.max(gesture_dx,- (imageWidth*mScale - getWidth())/ 2);
        gesture_dy -= dy;
        //gesture_dy同上
        gesture_dy = Math.min(gesture_dy,(imageHight*mScale - getHeight())/ 2);
        gesture_dy = Math.max(gesture_dy,- (imageHight*mScale - getHeight())/ 2);
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    Runnable scrollerAnimator = new Runnable() {
        @Override
        public void run() {
            if(mScroller == null){
                return;
            }
            if(mScroller.computeScrollOffset()){
                gesture_dx = mScroller.getCurrX();
                gesture_dy = mScroller.getCurrY();
                invalidate();

                postOnAnimation(scrollerAnimator);
            }
        }
    };

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
        mScroller.fling((int)gesture_dx,(int)gesture_dy,(int)vx,(int)vy,(int)(getWidth() - imageWidth*mScale) / 2,(int)(imageWidth*mScale - getWidth()) / 2,(int)(getHeight() - imageHight*mScale) / 2,(int)(imageHight*mScale - getHeight()) / 2);
        postOnAnimation(scrollerAnimator);
        return false;
    }
}
