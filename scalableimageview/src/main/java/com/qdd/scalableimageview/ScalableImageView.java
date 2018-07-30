package com.qdd.scalableimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScalableImageView extends View {

    private Bitmap bmp;
    private Paint bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float imageWidth = Utils.dp2px(250);

    public ScalableImageView(Context context) {
        this(context,null);
    }

    public ScalableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        bmp = Utils.getAvatar(context.getResources(),(int)imageWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
        canvas.drawBitmap(bmp,(getWidth() - bmp.getWidth())/2,(getHeight() - bmp.getHeight())/2,bmpPaint);
    }
}
