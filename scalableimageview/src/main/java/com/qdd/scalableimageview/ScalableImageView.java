package com.qdd.scalableimageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.widget.OverScroller;
import android.view.MotionEvent;
import android.view.View;

public class ScalableImageView extends View implements GestureDetector.OnDoubleTapListener, GestureDetector
        .OnGestureListener {
    private static final String TAG = "qddt";
    //默认放大倍数
    private static final float DEFAULT_SCALE = 4f;

    private Bitmap mBmp;
    private Paint mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mImageWidth = Utils.dp2px(250);
    private float mImageHight;
    private float mScale = 1f;

    private boolean mIsScaled;
    private GestureDetector mGestureDetector;
    //手指在X轴上的移动距离
    private float mGesture_dx;
    //手指在Y轴上的移动距离
    private float mGesture_dy;

    //图片移动位置X
    private float mImg_OffsetX;
    //图片移动位置Y
    private float mImg_OffsetY;

    //temp test start
    private float OffsetX;
    private float OffsetY;
    //temp test end

    //双击点X坐标
    private float mDouPoint_X;
    //双击点Y坐标
    private float mDouPoint_Y;

    //控制放大的动画
    private ObjectAnimator mScaleAnimator;
    //控制缩放动画完成度
    private float fraction;

    //控制Fling，OverScroller与Scroller有什么区别？
    private OverScroller mScroller;

    public ScalableImageView(Context context) {
        this(context, null);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mBmp = Utils.getAvatar(context.getResources(), (int) mImageWidth);
        mImageHight = mBmp.getHeight();
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(this);

        mScaleAnimator = ObjectAnimator.ofFloat(this, "fraction", 1f);
        mScaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                    mGesture_dx = 0;
                    mGesture_dy = 0;
                    OffsetX = OffsetY = 0;
            }
        });

        mScroller = new OverScroller(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //触摸事件交由GestureDetector来处理
        return mGestureDetector.onTouchEvent(event);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mImg_OffsetX = (getWidth() - mImageWidth) / 2;
        mImg_OffsetY = (getHeight() - mImageHight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mScale = 1f + (DEFAULT_SCALE - 1f) * fraction;
        canvas.drawColor(Color.BLUE);
        //跟随手指移动图片
        canvas.translate(mGesture_dx * fraction, mGesture_dy * fraction);
        //始终以canvas的中心点为缩放中心
        canvas.scale(mScale, mScale, getWidth()/2, getHeight()/2);
        //将双击点移动到中心
        canvas.translate(OffsetX,OffsetY);
        //将图片移动到中心
        canvas.translate(mImg_OffsetX,mImg_OffsetY);
        canvas.drawBitmap(mBmp, 0, 0, mBmpPaint);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }


    public float getFraction() {
        return fraction;
    }

    public void setFraction(float fraction) {
        this.fraction = fraction;
        invalidate();
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        mDouPoint_X = motionEvent.getX();
        mDouPoint_Y = motionEvent.getY();
        Log.d(TAG,"mDouPoint_X = " + mDouPoint_X + " ; mDouPoint_Y = " + mDouPoint_Y);

        OffsetX = getWidth()/2 - mDouPoint_X;
        OffsetY = getHeight()/2 - mDouPoint_Y;

        if (mIsScaled) {
            mScaleAnimator.reverse();
        } else {
            mScaleAnimator.start();
        }
        mIsScaled = !mIsScaled;
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        //onDown如果不返回true，其他后续事件序列（onDoubleTap等）都不会被接收到。
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
        //原始未缩放的图片不允许滚动操作
        if (!mIsScaled) {
            return false;
        }
        //增量距离，每次scroll的距离为起点-终点。
        mGesture_dx -= dx;
        //gesture_dx需要在- (mImageWidth*mScale - getWidth())/ 2 和(mImageWidth*mScale - getWidth())/ 2之间
        mGesture_dx = Math.min(mGesture_dx, (mImageWidth * mScale - getWidth()) / 2);
        mGesture_dx = Math.max(mGesture_dx, -(mImageWidth * mScale - getWidth()) / 2);
        mGesture_dy -= dy;
        //gesture_dy同上
        mGesture_dy = Math.min(mGesture_dy, (mImageHight * mScale - getHeight()) / 2);
        mGesture_dy = Math.max(mGesture_dy, -(mImageHight * mScale - getHeight()) / 2);
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    Runnable scrollerAnimator = new Runnable() {
        @Override
        public void run() {
            if (mScroller == null) {
                return;
            }
            //这个computeScrollOffset()是怎么计算的，getCurrX和getCurrY得到的是什么？
            if (mScroller.computeScrollOffset()) {
                mGesture_dx = mScroller.getCurrX();
                mGesture_dy = mScroller.getCurrY();
                invalidate();

                postOnAnimation(scrollerAnimator);
            }
        }
    };


    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
        //原始未缩放的图片不允许Fling操作
        if (!mIsScaled) {
            return false;
        }
        mScroller.fling((int) mGesture_dx, (int) mGesture_dy, (int) vx, (int) vy, (int) (getWidth() - mImageWidth *
                mScale) / 2, (int) (mImageWidth * mScale - getWidth()) / 2, (int) (getHeight() - mImageHight * mScale)
                / 2, (int) (mImageHight * mScale - getHeight()) / 2, 100, 100);
        //为什么要使用postOnAnimation?
        postOnAnimation(scrollerAnimator);
        return true;
    }
}
