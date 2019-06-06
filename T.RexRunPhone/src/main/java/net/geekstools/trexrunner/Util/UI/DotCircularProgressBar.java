package net.geekstools.trexrunner.Util.UI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;

import net.geekstools.trexrunner.R;

public class DotCircularProgressBar extends View {

    //Normal dot radius
    private int dotRadius = 10;

    //Expanded Dot Radius
    private int bounceDotRadius = 13;

    //to get identified in which position dot has to expand its radius
    private int dotPosition = 1;

    //specify how many dots you need in a progressbar
    private int dotAmount = 10;

    //specify the circle radius
    private int circleRadius = 50;


    public DotCircularProgressBar(Context context) {
        super(context);
    }

    public DotCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DotCircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Animation called when attaching to the window, i.e to your screen
        startAnimation();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //take the point to the center of the screen
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

        Paint progressPaint = new Paint();
        progressPaint.setColor(getContext().getColor(R.color.default_color_light));

        //call create dot method
        createDotInCircle(canvas, progressPaint);
    }

    private void createDotInCircle(Canvas canvas, Paint progressPaint) {
        //angle for each dot angle = (360/number of dots) i.e  (360/10)
        int angle = 36;

        for (int i = 1; i <= dotAmount; i++) {

            if (i == dotPosition) {
                // angle should be in radians  i.e formula (angle *(Math.PI / 180))
                float x = (float) (circleRadius * (Math.cos((angle * i) * (Math.PI / 180))));
                float y = (float) (circleRadius * (Math.sin((angle * i) * (Math.PI / 180))));

                canvas.drawCircle(x, y, bounceDotRadius, progressPaint);

            } else {
                // angle should be in radians  i.e formula (angle *(Math.PI / 180))
                float x = (float) (circleRadius * (Math.cos((angle * i) * (Math.PI / 180))));
                float y = (float) (circleRadius * (Math.sin((angle * i) * (Math.PI / 180))));

                canvas.drawCircle(x, y, dotRadius, progressPaint);

            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;

        //Dynamically setting width and height to progressbar 100 is circle radius, dotRadius * 3 to cover the width and height of Progressbar
        width = 100 + (dotRadius * 3);
        height = 100 + (dotRadius * 3);

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    private void startAnimation() {
        BounceAnimation bounceAnimation = new BounceAnimation();
        bounceAnimation.setDuration(333);
        bounceAnimation.setRepeatCount(Animation.INFINITE);
        bounceAnimation.setInterpolator(new BounceInterpolator());
        bounceAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                dotPosition++;
                //when dotPosition == dotAmount , then start again applying animation from 0th positon , i.e  dotPosition = 0;
                if (dotPosition > dotAmount) {
                    dotPosition = 1;
                }


            }
        });
        startAnimation(bounceAnimation);
    }


    private class BounceAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation transformation) {
            super.applyTransformation(interpolatedTime, transformation);
            //call invalidate to redraw your view againg.
            invalidate();
        }
    }
}