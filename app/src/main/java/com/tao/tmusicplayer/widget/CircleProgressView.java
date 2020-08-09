package com.tao.tmusicplayer.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {

    private int max = 100;

    private int progress = 0;

    // 画圆所在的距形区域
    private final RectF mRectF;

    private final Paint mPaint;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRectF = new RectF();
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mCircleLineStrokeWidth = 16;
        int width = this.getWidth();
        int height = this.getHeight();

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        mPaint.setColor(Color.rgb(0xff, 0x00, 0x00));
        canvas.drawArc(mRectF, -90, ((float) progress / max) * 360, false, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec != heightMeasureSpec) {
            int min = Math.min(widthMeasureSpec, heightMeasureSpec);
            widthMeasureSpec = min;
            heightMeasureSpec = min;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int maxProgress) {
        this.max = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}
