package com.example.testgilde;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class BigView extends View implements GestureDetector.OnGestureListener,View.OnTouchListener {

    private final Rect mRect;
    private final BitmapFactory.Options mOptions;
    private final GestureDetector mGestruDetetor;
    private final Scroller mSroller;
    private int mImageWidth;
    private int mImageHeight;
    private BitmapRegionDecoder mDecoder;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;
    private Bitmap mBitmap;

    public BigView(Context context) {
        this(context,null);
    }

    public BigView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRect = new Rect();
        mOptions = new BitmapFactory.Options();
        //手势识别
        mGestruDetetor = new GestureDetector(context,this);
        //滚动类
        mSroller = new Scroller(context);

        setOnTouchListener(this);
    }

    public void setImage(InputStream is)
    {
        // 获取图片宽和高，注意：不能将整个图片加载进内存
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        //开启复用
        mOptions.inMutable = true;
        //设置格式
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inJustDecodeBounds = false;

        //区域解码器
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is,false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        //确定加载图片的区域
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;
        //计算缩放因子
        mScale = mViewWidth/(float)mImageWidth;
        mRect.bottom = (int)(mViewHeight/mScale);
    }

    //第4步，画出具体的内容


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //解码器是不是为空
        if (mDecoder == null)
        {
            return;
        }

        //真正内存复用  //复用的bitmap内存必须和即将解码的bitmap尺寸一样
        mOptions.inBitmap = mBitmap;

        mBitmap = mDecoder.decodeRegion(mRect,mOptions);

        //得到一个矩阵进行缩放，相当于得到view的大小

        Matrix matrix = new Matrix();
        matrix.setScale(mScale,mScale);
        canvas.drawBitmap(mBitmap,matrix,null);
    }

    //
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        if (!mSroller.isFinished())
        {
            mSroller.forceFinished(true);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
        mRect.offset(0,(int)distanceY);

        if (mRect.bottom > mImageHeight)
        {
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight - (int)(mViewHeight/mScale);
        }

        if (mRect.top < 0)
        {
            mRect.top = 0;
            mRect.bottom = (int)(mViewHeight/mScale);
        }

        invalidate();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
        mSroller.fling(0,mRect.top,0,(int)-velocityY,0,0,0,mImageHeight - (int)(mViewHeight/mScale));
        return false;
    }

    @Override
    public void computeScroll() {
//        super.computeScroll();
        if (mSroller.isFinished())
        {
            return;
        }
        if (mSroller.computeScrollOffset())
        {
            mRect.top = mSroller.getCurrY();
            mRect.bottom = mRect.top + (int)(mViewHeight/mScale);
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestruDetetor.onTouchEvent(motionEvent);
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }


}
