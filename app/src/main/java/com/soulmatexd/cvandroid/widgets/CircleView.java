package com.soulmatexd.cvandroid.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by xushuzhan on 2017/5/11.
 */

public class CircleView extends ImageView {
    private static final String TAG = "CircleView";
//    int mColor = Color.parseColor("#FF5F6D");
    int mColor = Color.parseColor("#FDCB3D");
    Paint mPaint;
    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    public void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }
    void setColor(int color){
        mColor = color;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST){
            width = 20;
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
            height = 20;
        }
        Log.d(TAG, "onMeasure: "+width+">"+height);
        setMeasuredDimension(20,20);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        canvas.drawCircle(10,10,5,mPaint);
        Log.d(TAG, "onDraw: "+getMeasuredWidth()+">"+getMeasuredHeight());
    }
}
