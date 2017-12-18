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
        /**Drawable���Ѿ�ʵ��������ӿڣ�����ֻ��Ҫ����
         * ��һ�������� ˢ�½ӿ�*/
        mRippleDrawable2.setCallback(this);

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
        mRippleDrawable2.draw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) 
    {
        super.onSizeChanged(w, h, oldw, oldh);
       /**���һ������������ Drawable ���ƺ�ˢ�µ�����
        * ��������һ���Ļ��� Drawable�᲻ͣ�Ļ���ԭʼ�����Ļ��������൱��û��ˢ�£�
        * ��Ϊ�Ǵ����Ͻǿ�ʼ���ģ����ԣ����ϱ߾�Ϊ0�����±߾�Ϊ�ؼ��Ŀ��*/
        mRippleDrawable2.setBounds(0,0,getWidth(),getHeight());
    }

    //�ڶ�������֤Drawable�Ƿ����
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
