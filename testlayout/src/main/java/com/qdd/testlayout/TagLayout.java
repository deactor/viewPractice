package com.qdd.testlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class TagLayout extends ViewGroup {
    private static final String TAG = "qddt";
    private static final int TAG_MARGIN = 15;

    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {
        int left = getPaddingStart();
        int top = getPaddingTop();
        int childWidth = 0;
        int childHeight = 0;
        int maxHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();

            //如果此时右边界超出父view，则换行
            if (left + childWidth + getPaddingEnd() > getWidth()) {
                left = getPaddingStart();
                top += maxHeight + TAG_MARGIN;
                maxHeight = 0;
            }
            maxHeight = Math.max(maxHeight, childHeight);

            child.layout(left, top, left + childWidth, top + childHeight);
            left += childWidth + TAG_MARGIN;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthUsed = 0;
        int heightUsed = 0;

        //计算得到的viewGroup的宽度
        int totalWidth = 0;
        //计算得到的viewGroup的高度
        int totalHeight = 0;
        int widthMeasureSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        //记录一行的最大高度
        int maxHight = 0;
        //记录maxHight的上一次值，换行时当前测得的maxHight不准确，使用oldMaxHight来设置当前行最大高度
        int oldMaxHight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);

            oldMaxHight = maxHight;
            maxHight = Math.max(maxHight, child.getMeasuredHeight());

            //如果宽度超过父view，换行重测
            //测量时在measureChildWithMargins里父view宽度会去除padding，所以这里加上padding才能达到父view宽度。
            if ((MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED && (widthUsed + child.getMeasuredWidth() + getPaddingStart() + getPaddingEnd() >= widthMeasureSpecSize)) ||
                    (child.getMeasuredState() & MEASURED_STATE_TOO_SMALL) != 0) {
                totalWidth = Math.max(totalWidth, widthUsed - TAG_MARGIN + getPaddingEnd() + getPaddingStart());
                widthUsed = 0;

                heightUsed += oldMaxHight + TAG_MARGIN;

                measureChildWithMargins(child, widthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                maxHight = child.getMeasuredHeight();
            }

            widthUsed += child.getMeasuredWidth() + TAG_MARGIN;
        }

        totalWidth = Math.max(totalWidth, widthUsed - TAG_MARGIN + getPaddingEnd() + getPaddingStart());
        totalHeight = heightUsed + maxHight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resolveSizeAndState(totalWidth, widthMeasureSpec, 0), resolveSizeAndState(totalHeight, heightMeasureSpec, 0));
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
