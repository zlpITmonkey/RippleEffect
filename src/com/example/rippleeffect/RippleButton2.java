package com.example.rippleeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by didik on 2017/2/13.
 */

public class RippleButton2 extends Button {

    private RippleDrawable2 mRippleDrawable2;

    public RippleButton2(Context context) {
        this(context,null);
    }

    public RippleButton2(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleButton2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippleDrawable2 = new RippleDrawable2();
        /**Drawable中已经实现了这个接口，我们只需要调用
         * 第一步：设置 刷新接口*/
        mRippleDrawable2.setCallback(this);

        /**
         * 如果设置背景,则只有{@link RippleButton#onTouchEvent(MotionEvent)} 方法需要重写,
         * 其他的都被{@link android.view.View#setBackgroundDrawable(Drawable)}实现了
         */
//        setBackgroundDrawable(mRippleDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) 
    {
        // 绘制自己的 Drawable
        mRippleDrawable2.draw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
    {
        super.onSizeChanged(w, h, oldw, oldh);
       /**最后一步：重新设置 Drawable 绘制和刷新的区域
        * 不设置这一步的话， Drawable会不停的绘制原始坐标点的绘制区域（相当于没有刷新）
        * 因为是从左上角开始画的，所以，左上边距为0，右下边距为控件的宽高*/
        mRippleDrawable2.setBounds(0,0,getWidth(),getHeight());
    }

    //第二步：验证Drawable是否存在
    @Override
    protected boolean verifyDrawable(Drawable who) 
    {
        return mRippleDrawable2 == who || super.verifyDrawable(who);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
        mRippleDrawable2.onTouch(event);
        return true;
    }
}
