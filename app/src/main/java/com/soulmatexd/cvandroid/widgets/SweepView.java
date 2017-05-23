package com.soulmatexd.cvandroid.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by xushuzhan on 2017/5/8.
 */

public class SweepView extends RelativeLayout {
    private static final String TAG = "SweepView";
    Context context;
    int width = 0;
    int height = 0;
    int j = 0;//CircleImageView的下标

    public SweepView(Context context) {
        this(context, null);
    }

    public SweepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SweepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }


    public void addCircle() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(CENTER_HORIZONTAL);


        final CircleView circleView = new CircleView(context);
        circleView.setLayoutParams(new ViewGroup.LayoutParams(25, 25));
        circleView.setVisibility(INVISIBLE);
        addView(circleView, params);


        circleView.post(new Runnable() {
            @Override
            public void run() {

                int[] randomArray = {0, 1};
                int point1x = 0;
                int point1y = 0;
                int point2x = 0;
                int point2y = 0;
                if (randomArray[new Random().nextInt(2)] == 0) {
                    point1x = new Random().nextInt((width / 2 - dp2px(context, 50)));
                } else {
                    point1x = new Random().nextInt((width / 2 - dp2px(context, 50))) + (width / 2 + dp2px(context, 50));
                }
                if (randomArray[new Random().nextInt(2)] == 0) {
                    point2x = new Random().nextInt((width / 2 - dp2px(context, 50)));
                } else {
                    point2x = new Random().nextInt((width / 2 - dp2px(context, 50))) + (width / 2 + dp2px(context, 50));
                }
                point1y = new Random().nextInt(height / 2 - dp2px(context, 50)) + (height / 2 + dp2px(context, 50));
                point2y = -new Random().nextInt(point1y) + point1y;
                int endX = new Random().nextInt(dp2px(context, 100)) + (width / 2 - dp2px(context, 100));
                int endY = -new Random().nextInt(point2y) + point2y;
                ValueAnimator translateAnimator = ValueAnimator.ofObject(new HeartEvaluator(new PointF(point1x, point1y), new PointF(point2x, point2y)),
                        new PointF(new Random().nextInt(getMeasuredWidth()), getMeasuredHeight() - getMeasuredHeight()/9),
                        new PointF(endX, endY));
                translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        PointF pointF = (PointF) animation.getAnimatedValue();
                        circleView.setX(pointF.x);
                        circleView.setY(pointF.y);
                    }
                });
                translateAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        removeView(circleView);
                    }
                });

                TimeInterpolator[] timeInterpolator = {new LinearInterpolator(), new AccelerateDecelerateInterpolator(), new DecelerateInterpolator(), new AccelerateInterpolator()};
                translateAnimator.setInterpolator(timeInterpolator[new Random().nextInt(timeInterpolator.length)]);
                ObjectAnimator translateAlphaAnimator = ObjectAnimator.ofFloat(circleView, View.ALPHA, 1f, 0f);
                translateAlphaAnimator.setInterpolator(new DecelerateInterpolator());
                AnimatorSet translateAnimatorSet = new AnimatorSet();
                translateAnimatorSet.playTogether(translateAnimator, translateAlphaAnimator);
                translateAnimatorSet.setDuration(1000);

                //入场动画效果
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(circleView, View.SCALE_X, 0.5f, 1f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(circleView, View.SCALE_Y, 0.5f, 1f);
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(circleView, View.ALPHA, 0.5f, 1f);
                AnimatorSet enterAnimatorSet = new AnimatorSet();
                enterAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
                enterAnimatorSet.setDuration(500);
                enterAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        circleView.setVisibility(VISIBLE);
                    }
                });
                //总体动画调度
                AnimatorSet allAnimator = new AnimatorSet();
                allAnimator.playTogether(enterAnimatorSet, translateAnimatorSet);
                allAnimator.start();
            }
        });
    }

    public void startSweep(@NonNull final View view, final int duration) {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 10000; i++) {
                        if (!Thread.interrupted()) {
                            Thread.sleep(20);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addCircle();
                                }
                            });
                        } else {
                            break;
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        view.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams lp0 = (LayoutParams) getLayoutParams();
                lp0.setMargins(0, 50 - getMeasuredHeight(), 0, 0);
                setLayoutParams(lp0);


                AnimatorSet sweepAnimators = new AnimatorSet();
                ObjectAnimator objectAnimator0 = ObjectAnimator.ofFloat(SweepView.this, "alpha", 0, 1f).setDuration(500);
                ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(SweepView.this, "translationY", view.getMeasuredHeight()-50).setDuration(duration);
                objectAnimator1.setInterpolator(new LinearInterpolator());
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(SweepView.this, "alpha", 1, 0f).setDuration(500);
                sweepAnimators.playSequentially(objectAnimator0, objectAnimator1,objectAnimator2);
                sweepAnimators.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Log.d(TAG, "onAnimationEnd: ");
                        t.interrupt();

                    }
                });
                sweepAnimators.start();
            }
        });

    }

    //估值器
    private class HeartEvaluator implements TypeEvaluator<PointF> {

        //贝塞尔曲线参考点1
        PointF f1;
        //贝塞尔曲线参考点2
        PointF f2;

        public HeartEvaluator(PointF f1, PointF f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float leftTime = 1f - fraction;

            PointF newPointF = new PointF();
            newPointF.x = startValue.x * leftTime * leftTime * leftTime
                    + f1.x * 3 * leftTime * leftTime * fraction
                    + f2.x * 3 * leftTime * fraction * fraction
                    + endValue.x * fraction * fraction * fraction;
            newPointF.y = startValue.y * leftTime * leftTime * leftTime
                    + f1.y * 3 * leftTime * leftTime * fraction
                    + f2.y * 3 * leftTime * fraction * fraction
                    + endValue.y * fraction * fraction * fraction;
            return newPointF;
        }
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FDCB3D"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(0, getMeasuredHeight() - getMeasuredHeight()/9, getMeasuredWidth(), getMeasuredHeight(), paint);
    }

}