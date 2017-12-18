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

public class RippleButton extends Button {

    private RippleDrawable mRippleDrawable;

    public RippleButton(Context context) {
        this(context,null);
    }

    public RippleButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRippleDrawable = new RippleDrawable();
        /**Drawable���Ѿ�ʵ��������ӿڣ�����ֻ��Ҫ����
         * ��һ�������� ˢ�½ӿ�*/
        mRippleDrawable.setCallback(this);

        /**
         * ������ñ���,��ֻ��{@link RippleButton#onTouchEvent(MotionEvent)} ������Ҫ��д,
         * �����Ķ���{@link android.view.View#setBackgroundDrawable(Drawable)}ʵ����
         */
//        setBackgroundDrawable(mRippleDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) 
    {
        // �����Լ��� Drawable
        mRippleDrawable.draw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
    {
        super.onSizeChanged(w, h, oldw, oldh);
       /**���һ������������ Drawable ���ƺ�ˢ�µ�����
        * ��������һ���Ļ��� Drawable�᲻ͣ�Ļ���ԭʼ�����Ļ��������൱��û��ˢ�£�
        * ��Ϊ�Ǵ����Ͻǿ�ʼ���ģ����ԣ����ϱ߾�Ϊ0�����±߾�Ϊ�ؼ��Ŀ��*/
        mRippleDrawable.setBounds(0,0,getWidth(),getHeight());
    }

    //�ڶ�������֤Drawable�Ƿ����
    @Override
    protected boolean verifyDrawable(Drawable who) 
    {
        return mRippleDrawable == who || super.verifyDrawable(who);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
        mRippleDrawable.onTouch(event);
        return true;
    }
}
