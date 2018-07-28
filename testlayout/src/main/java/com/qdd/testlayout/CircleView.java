package com.qdd.testlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final float PADDING = 50;
    private static final float RADIUS = 200;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int)(PADDING+RADIUS)*2;

        int target_width = resolveSize(size,widthMeasureSpec);
        int target_height = resolveSize(size,heightMeasureSpec);

        setMeasuredDimension(target_width,target_height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.RED);

        canvas.drawCircle(PADDING + RADIUS ,PADDING + RADIUS ,RADIUS,paint);
    }
}
