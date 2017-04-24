package com.soulmatexd.cvandroid.SlidingMain;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by SoulMateXD on 2017/4/23.
 */

public class SlidingMain extends ScrollView{

    private LinearLayout mWrapper;
    private ViewGroup photoLayout;
    private ViewGroup albumLayout;

    private SlidingScrollListener listener;

    //屏幕高度
    private int mScreenHeight;
    private int halfScreenHeight;
    //onMeasure是否被调用过了
    private boolean once;

    public SlidingMain(Context context) {
        this(context, null);
    }

    public SlidingMain(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMain(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    public void setSlidingScrollListener(SlidingScrollListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once){
            mWrapper = (LinearLayout) getChildAt(0);
            photoLayout = (ViewGroup) mWrapper.getChildAt(0);
            albumLayout = (ViewGroup) mWrapper.getChildAt(1);

            photoLayout.getLayoutParams().height = albumLayout.getLayoutParams().height = mScreenHeight;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed){
            this.scrollTo(0, halfScreenHeight);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:

                int scaleY = getScrollY();
                if (scaleY >= (halfScreenHeight + halfScreenHeight/4)){
                    this.smoothScrollTo(0, mScreenHeight);
                    listener.onDownPage();
                }else if (scaleY <= (halfScreenHeight - halfScreenHeight/4)){
                    this.smoothScrollTo(0, 0);
                    listener.onUpPage();
                }else {
                    this.smoothScrollTo(0, halfScreenHeight);
                }
                return  false;
        }
        return super.onTouchEvent(ev);
    }

    public void showTakePhoto(){
        this.smoothScrollTo(0, 0);
    }

    public void showAlbum(){
        this.smoothScrollTo(0, mScreenHeight);
    }

    public void initSliding(){
        this.scrollTo(0, halfScreenHeight);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenHeight = metrics.heightPixels;
        halfScreenHeight = mScreenHeight/2;

        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
    }
}
