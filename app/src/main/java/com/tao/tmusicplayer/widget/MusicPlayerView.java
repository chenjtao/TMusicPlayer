package com.tao.tmusicplayer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tao.tmusicplayer.R;

public class MusicPlayerView extends View {
    private static final long ROTATE_DELAY = 5;//旋转动作时间
    private int mRotateDegrees;//旋转的角度
    private Handler mRotate;
    private int mWidth;
    private int mHeight;
    private float mCenterX;
    private float mCenterY;
    private RectF rectF;
    private Bitmap mBitmapCover;
    private float mCoverScale;
    private BitmapShader mShader;
    private Paint paint;
    private boolean isRotating;
    private final Runnable mRunnableRotate = new Runnable() {
        @Override
        public void run() {
            if (isRotating) {
                updateCoverRotate();
                mRotate.postDelayed(mRunnableRotate, ROTATE_DELAY);
            }
        }
    };

    public MusicPlayerView(Context context) {
        super(context);
        init(context, null);
    }

    public MusicPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MusicPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 更新封面角度,重新绘制图片
     */

    private void updateCoverRotate() {
        mRotateDegrees += 1;
        mRotateDegrees = mRotateDegrees % 360;
        postInvalidate();
    }

    /**
     * 判读是否在旋转
     * @return
     */
    public boolean isRotating() {
        return isRotating;
    }

    /**
     * 开始旋转图片
     */
    public void start(){
        isRotating=true;
        mRotate.removeCallbacksAndMessages(null);
        mRotate.postDelayed(mRunnableRotate,ROTATE_DELAY);
        postInvalidate();
    }

    /**
     * 停止图片旋转
     */
    public void stop(){
        isRotating = false;
        postInvalidate();
    }

    /**
     * 通过本地图片设置封面图
     */
    public void setCoverDrawable(int coverDrawable) {
        Drawable drawable = getContext().getResources().getDrawable(coverDrawable);
        mBitmapCover = drawableToBitmap(drawable);
        createShader();
        postInvalidate();
    }
    /**
     * 网络图片加载使用Picasso图片加载工具
     *
     * @param imageUrl
     */
    public void setCoverURL(String imageUrl) {
        Picasso.with(getContext()).load(imageUrl).into(target);
    }

    /**
     * 初始化View资源
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.play_view);
        Drawable mDrawableCover = a.getDrawable(R.styleable.play_view_cover);
        if (mDrawableCover != null) {
            mBitmapCover = drawableToBitmap(mDrawableCover);
        }
        a.recycle();
        mRotateDegrees = 0;
        //通过handler更新图片角度
        mRotate = new Handler();
        rectF = new RectF();
    }

    /**
     * 测量宽高,设置中心点中心点位置,创建阴影
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int minSide = Math.min(mWidth, mHeight);    //取宽高最小值设置图片宽高
        mWidth = minSide;
        mHeight = minSide;
        setMeasuredDimension(mWidth, mHeight);      //重新设置宽高
        //中心点位置
        mCenterX = mWidth / 2f;
        mCenterY = mHeight / 2f;
        //设置图片显示位置
        rectF.set(20.0f, 20.0f, mWidth - 20.0f, mHeight - 20.0f);
        createShader();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShader == null) {
            return;
        }
        //画封面图片 判读图片的中心距离xy,算出边角大小,然后画圆
        float radius = mCenterX <= mCenterY ? mCenterX - 75.0f : mCenterY - 75.0f;
        canvas.rotate(mRotateDegrees, mCenterX, mCenterY);
        canvas.drawCircle(mCenterX, mCenterY, radius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
        }
        return super.onTouchEvent(event);
    }



    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mBitmapCover = bitmap;
            createShader();
            postInvalidate();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    private int mCoverColor = Color.YELLOW;

    private void createShader() {
        if (mWidth == 0) {
            return;
        }
        if (mBitmapCover == null) {    //如果封面为为创建默认颜色
            mBitmapCover = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mBitmapCover.eraseColor(mCoverColor);
        }
        mCoverScale = ((float) mWidth) / (float) mBitmapCover.getWidth();
        //创建缩放后的bitmap
        mBitmapCover = Bitmap.createScaledBitmap(mBitmapCover,
                (int) (mBitmapCover.getWidth() * mCoverScale), (int) (mBitmapCover.getHeight() * mCoverScale), true);
        mShader = new BitmapShader(mBitmapCover, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(mShader);
    }

    /**
     * 将drawable转换为位图 为BitmapShader准备
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}