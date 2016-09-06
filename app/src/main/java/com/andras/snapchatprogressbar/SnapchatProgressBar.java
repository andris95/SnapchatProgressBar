package com.andras.snapchatprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by andras on 06.09.16.
 */
public class SnapchatProgressBar extends View {
    //Sizes (with defaults)
    private int layoutHeight = 0;
    private int layoutWidth = 0;
    private float outerBarLength = 360 * 0.75f;
    private float innerBarLength = 180;
    private float stoppedOuterBarLength = 360 * 0.75f;
    private float stoppedInnerBarLength = 180;
    private float outerBarWidth = 10;
    private float innerBarWidth = 5;

    //Padding (with defaults)
    private int paddingTop = 0;
    private int paddingBottom = 0;
    private int paddingLeft = 0;
    private int paddingRight = 0;

    //Colors (with defaults)
    private int barColor = Color.WHITE;

    //Paints
    private Paint outerBarPaint = new Paint();
    private Paint innerBarPaint = new Paint();

    //Rectangles
    private RectF innerCircleBounds = new RectF();
    private RectF outerCircleBounds = new RectF();

    //Animation
    //The amount of pixels to move the bar by on each draw
    private float spinSpeed = 8f;
    //The number of milliseconds to wait in between each draw
    private int delayMillis = 1000/25;
    private float outerProgress = 0;
    private float innerProgress = 360;
    boolean isSpinning = false;

    public SnapchatProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.SnapchatProgressBar));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightMode != MeasureSpec.UNSPECIFIED && widthMode != MeasureSpec.UNSPECIFIED) {
            if (widthWithoutPadding > heightWithoutPadding) {
                size = heightWithoutPadding;
            } else {
                size = widthWithoutPadding;
            }
        } else {
            size = Math.max(heightWithoutPadding, widthWithoutPadding);
        }

        Log.d("MEASURE", "padding: " + this.getPaddingBottom() + " " + this.getPaddingTop());
        setMeasuredDimension(
                size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        Log.d("BAR", "onSizeChanged");
        Log.d("MEASURE", "padding: " + this.getPaddingBottom() + " " + this.getPaddingTop());
        layoutWidth = newWidth;
        layoutHeight = newHeight;
        setupBounds();
        setupPaints();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw the bar
        if (isSpinning) {
            /**outer*/
            canvas.drawArc(outerCircleBounds, outerProgress, outerBarLength, false, outerBarPaint);
            /**inner*/
            canvas.drawArc(innerCircleBounds, innerProgress, innerBarLength, false, innerBarPaint);
        } else {
            /**outer*/
            canvas.drawArc(outerCircleBounds, outerProgress, stoppedOuterBarLength, false, outerBarPaint);
            /**inner*/
            canvas.drawArc(innerCircleBounds, innerProgress, stoppedInnerBarLength, false, innerBarPaint);
        }
        if (isSpinning) {
            scheduleRedraw();
        }
    }

    private void setupBounds() {
        // Width should equal to Height, find the min value to setup the circle
        int minValue = Math.min(layoutWidth, layoutHeight);

        // Calc the Offset if needed
        int xOffset = layoutWidth - minValue;
        int yOffset = layoutHeight - minValue;

        // Add the offset
        paddingTop = this.getPaddingTop() + (yOffset / 2);
        paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        paddingRight = this.getPaddingRight() + (xOffset / 2);

        int width = getWidth();
        int height = getHeight();
        Log.d("BAR", "width: " + width + " height: " + height +
                " paddingTop: " + paddingTop + " paddingBottom: " + paddingBottom + " paddingLeft: " + paddingLeft);

        outerCircleBounds = new RectF(
                paddingLeft + outerBarWidth,
                paddingTop + outerBarWidth,
                width - paddingRight - outerBarWidth,
                height - paddingBottom - outerBarWidth);
        innerCircleBounds = new RectF(
                paddingLeft + outerBarWidth + (1.5f * outerBarWidth),
                paddingTop + outerBarWidth + (1.5f * outerBarWidth),
                width - paddingRight - outerBarWidth - (1.5f * outerBarWidth),
                height - paddingBottom - outerBarWidth - (1.5f * outerBarWidth));
    }

    private void setupPaints() {
        outerBarPaint.setColor(barColor);
        outerBarPaint.setAntiAlias(true);
        outerBarPaint.setStyle(Paint.Style.STROKE);
        outerBarPaint.setStrokeWidth(outerBarWidth);

        innerBarPaint.setColor(barColor);
        innerBarPaint.setAntiAlias(true);
        innerBarPaint.setStyle(Paint.Style.STROKE);
        innerBarPaint.setStrokeWidth(innerBarWidth);
    }

    private void parseAttributes(TypedArray a) {
        Log.d("BAR", "parseAttributes");
        spinSpeed = (int) a.getDimension(R.styleable.SnapchatProgressBar_pwSpinSpeed, spinSpeed);
        delayMillis = a.getInteger(R.styleable.SnapchatProgressBar_pwDelayMillis, delayMillis);
        if (delayMillis < 0) { delayMillis = 10; }

        barColor = a.getColor(R.styleable.SnapchatProgressBar_pwBarColor, barColor);
        outerBarWidth = a.getDimension(R.styleable.SnapchatProgressBar_pwOuterBarWidth, outerBarWidth);
        innerBarWidth = a.getDimension(R.styleable.SnapchatProgressBar_pwInnerBarWidth, innerBarWidth);

        a.recycle();
    }

    private void scheduleRedraw() {
        outerProgress += spinSpeed;
        if (outerProgress > 360) {
            outerProgress = 0;
        }
        innerProgress -= spinSpeed;
        if (innerProgress < 0) {
            innerProgress = 360;
        }
        postInvalidateDelayed(delayMillis);
    }
    /**
     *   Check if the wheel is currently spinning
     */
    public boolean isSpinning() {
        return isSpinning;
    }

    /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        outerProgress = 0;
        invalidate();
    }

    /**
     * Turn off startSpinning mode
     */
    public void stopSpinning() {
        isSpinning = false;
        outerProgress = 0;
        innerProgress = 360;
        postInvalidate();
    }


    /**
     * Puts the view on spin mode
     */
    public void startSpinning() {
        isSpinning = true;
        postInvalidate();
    }

    /**
     * Increment the outerProgress by 1 (of 360)
     */
    public void incrementProgress() {
        incrementProgress(1);
    }

    public void incrementProgress(int amount) {
        isSpinning = false;
        outerProgress += amount;
        if (outerProgress > 360)
            outerProgress %= 360;
        postInvalidate();
    }

    public void setOuterBarWidth(int outerBarWidth) {
        this.outerBarWidth = outerBarWidth;

        if ( this.outerBarPaint != null ) {
            this.outerBarPaint.setStrokeWidth( this.outerBarWidth);
        }
    }

    public void setInnerBarWidth(int innerBarWidth) {
        this.innerBarWidth = innerBarWidth;
        if (this.innerBarPaint != null ) {
            this.innerBarPaint.setStrokeWidth( this.innerBarWidth);
        }
    }

    public void setPadding(int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;
        this.paddingBottom = paddingBottom;
        this.paddingTop = paddingTop;
        requestLayout();
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        requestLayout();
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }


    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }


    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
        if (this.outerBarPaint != null) {
            this.outerBarPaint.setColor(this.barColor);
        }
        if (this.innerBarPaint != null) {
            this.innerBarPaint.setColor(this.barColor);
        }
    }

    public float getSpinSpeed() {
        return spinSpeed;
    }

    public void setSpinSpeed(float spinSpeed) {
        this.spinSpeed = spinSpeed;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }
}
