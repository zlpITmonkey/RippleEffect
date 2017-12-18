package com.example.rippleeffect;

import android.R.integer;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by didik on 2017/2/8.
 */

public class RippleDrawable extends Drawable {

    private int mAlpha = 255;/*透明度范围 0 ~ 255*/
    private int mRippleColor = 0;
    private Paint mPaint = new Paint(
            Paint.ANTI_ALIAS_FLAG /*抗锯齿*/
    );
    //当前背景达到最大的透明度
    private int Max_Bg_Alpha=182;
    //当前圆达到最大的透明度
    private int Max_Circle_Alpha=255;
    
    private float mRipplePointX = 0;
    private float mRipplePointY = 0;
    private float mRippleRadius=0;
    private float mEnterprogress;
    //当前背景和圆的透明度
    private int mBgAlpha,mCircleAlpha;
    
    private Boolean isTouchUp;
    private Boolean isEnterFinsh;

    public RippleDrawable() {
        // 开启抗锯齿,让曲线边缘更圆滑
        mPaint.setAntiAlias(true);
        // 开启防抖动
        mPaint.setDither(true);
        //设置颜色
        setRippleColor(0x5ca3a1a1);
    }
    
 
/**---------------------------------------绘制界面的准备工作-------------------------------------------------------*/
    /**当颜色或者透明度改变的时候调用
     *（因为颜色本身就带有透明度，而Drawable也有一个透明度，叠加之后的透明度会影响颜色的变化
     *所以我们要计算真实的透明，然后把真正呈现在控件上的颜色返回
     *如果颜色或者透明度改变了，就会影响到体现在控件上的呈现颜色，所以当颜色或者透明度改变时就要调用
     *这个方法就是为了得到真实的颜色）*/
      private void onColorOrAlphaChange() 
      {
          mPaint.setColor(mRippleColor);
          //如果drawable的不为不透明状态
          if (mAlpha != 255) {
              // 得到颜色本身的透明度
              // 以下两种方式,1 是从画笔中提取透明度; 2 是从颜色中提取颜色的透明度; 任取一种即可
              int alpha = mPaint.getAlpha();
              // alpha = Color.alpha(mRippleColor);

              // 得到叠加后的实际透明度
              int realAlpha = (int) (alpha * (mAlpha / 255f));
              // 设置到画笔中去
              mPaint.setAlpha(realAlpha);
          }
          // 颜色改变时,也应该刷新自己
          invalidateSelf();
      }
      
      private int getMaxCircleAlpha(int preAlpha,int bgAlpha)
      {
    	  int dAlpha=preAlpha-bgAlpha;
		return (int) ((dAlpha*255f)/(255f-bgAlpha));	  
      }
      
    @Override
    public void draw(Canvas canvas) 
    {
    	int preAlpha=mPaint.getAlpha();
    	int bgAlpha=(int) (preAlpha*(mBgAlpha/255f));
    	int maxCircleAlpha=getMaxCircleAlpha(preAlpha, bgAlpha);
    	int mcircleAlpha=(int) (maxCircleAlpha*(mCircleAlpha/255f));
    	
    	mPaint.setAlpha(bgAlpha);
    	canvas.drawColor(mPaint.getColor());
    	mPaint.setAlpha(mcircleAlpha);
        canvas.drawCircle(mRipplePointX, mRipplePointY, mRippleRadius, mPaint);     
        
        mPaint.setAlpha(preAlpha);
    }

    //设置颜色的方法
    public void setRippleColor(int color) 
    {
        mRippleColor = color;
      // 颜色改变时调用
        onColorOrAlphaChange();
    }
    
    //设置 Drawable 的透明度
    @Override
    public void setAlpha(int alpha) 
    {
        mAlpha = alpha;
     // 透明度改改变时也要调用
        onColorOrAlphaChange();
    }

     //设置颜色滤镜
    @Override
    public void setColorFilter(ColorFilter colorFilter) 
    {
        // 设置画笔的颜色为传入的颜色滤镜
        mPaint.setColorFilter(colorFilter);
        // 刷新自己
        invalidateSelf();
    }

    //获取drawable的透明度状态
    @Override
    public int getOpacity() 
    {
        int alpha = mPaint.getAlpha();
        if (alpha == 255){
            //不透明
          return  PixelFormat.OPAQUE;
        }
        if (alpha == 0){
            // 全透明
            return PixelFormat.TRANSPARENT;
        }
        // 其他情况即是 半透明
        return PixelFormat.TRANSLUCENT;
    }
/**-----------------------------------------------------------------------------------------------------------------*/
    
/**---------------------------------------动画效果实现--------------------------------------------------------------*/
    //设置一个点击事件
    public void onTouch(MotionEvent event)
    {
        //实现刷新Drawable的方法（前提需要在定义的按钮中实现实现几步操作）
        invalidateSelf();
        //设置各种点击状态下的绘制情况
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchCancel(event.getX(),event.getY());
                break;
        }
    }
    //当点击的时候，从点击的位置画圆，每点击一次半径加10
    private void onTouchDown(float x, float y) {
        mRipplePointX = x;
        mRipplePointY = y;
        isTouchUp=false;
        startEnterRunnable();
    }

    private void onTouchMove(float x, float y) {
    }
    private void onTouchUp(float x, float y) 
    {
    	isTouchUp=true;
    	if (isEnterFinsh) 
    	{
			startExitRunnable();
		}
    }
    private void onTouchCancel(float x, float y) 
    {
    	isTouchUp=true;
    	if (isEnterFinsh) 
    	{
			startExitRunnable();
		}
    }
    
    //开始下一次点击
    private void startEnterRunnable()
    {
    	isEnterFinsh=false;
    	mCircleAlpha=255;
    	mEnterprogress=0;
    	//执行下一次点击时，清除效果，防止界面刷新越来越快
    	unscheduleSelf(mEnterRunnable);
    	//调用动画效果实现的方法
    	scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    	
    }
    
    private void EnterFinsh()
    {
    	isEnterFinsh=true;
    	if (isTouchUp) {
			startExitRunnable();
		}
    }
    //定义一个开始快结束慢的差值器
    private Interpolator mEnterInterpolator =new DecelerateInterpolator(3);

    //自动扩散的动画（其实就是让其每毫秒都去执行半径增加的画圆绘制）
    private Runnable mEnterRunnable=new Runnable() {
		
		public void run() 
		{
			mEnterprogress+=0.01f;
			if (mEnterprogress>1) 
			{
				onProgressChange(1);
				EnterFinsh();
				return;
			}
			//获取差值器运算进度的值（产生数字区间，由快到慢）
			float nowProgress=mEnterInterpolator.getInterpolation(mEnterprogress);
			onProgressChange(nowProgress);
			invalidateSelf();
            /**调用本身，每毫秒的重复去执行这个操作，达到自动扩散的效果
			延迟16毫秒，使刷新频率接近为每秒60次（一秒钟刷新60次，就相当于16毫秒刷新一次，可是刷新机制需要三步才能完成
			会消耗一些时间，所以延迟16毫秒，抵消一些浪费的时间）*/
			scheduleSelf(this,SystemClock.uptimeMillis()+16);
		}
	};
	private void onProgressChange(float progress)
	{
		mRippleRadius=250*progress;
		mBgAlpha=getProgressValue(0, 182, progress);
	}
	
	private int getProgressValue(int start,int end,float progress)
	{
		return (int) (start+(end-start)*progress);	
	}
/**---------------------------------------------------------------------------------*/
	
	 //定义开始减淡每次递增的时间值(用来控制圆半径的增幅速度)
    private float mExitIncrement=16f/340;
    
  //调控退减淡速度（就是圆半径的增幅速度）的进度值
	private float mExitprogress=0;
	
	 //定义一个退出效果的差值器（由慢到快）
    private Interpolator mExitInterpolator =new AccelerateInterpolator(3);
    
    //开启退出自动减淡动画
    private void startExitRunnable()
    {
    	mExitprogress=0;
    	unscheduleSelf(mEnterRunnable);
    	//执行下一次点击时，清除效果，防止界面刷新越来越快
    	unscheduleSelf(mExitRunnable);
    	//调用动画效果实现的方法
    	scheduleSelf(mExitRunnable, SystemClock.uptimeMillis());
    	
    }
    
    //当自动减淡动画完成时调用
    private void onExitFinsh()
    {
    	//可以写一些点击事件
    }

    //退出自动减淡的动画（其实就是让其每毫秒都去执行半径增加的画圆绘制）
    private Runnable mExitRunnable=new Runnable() {
		
		public void run() 
		{
			mExitprogress+=mExitIncrement;
			if (mExitprogress>1) 
			{
			/**因为当动画结束时，progress是一个0-1的小数，所以不可能精确的等于1
			 * 大于或者小于1的情况占大比例，比如最后一次为0.97,那么下一次如果大于1
			 * 那么就不会执行减淡的操作则progress就没有达到1，减淡效果就不能达到完全没有颜色（只有到达1时）
			 * 所以要在动画结束后，手动设置progress为1，上面的开始动画也是一样要设置*/
				onExitProgressChange(1);
				onExitFinsh();
				return;
			}
			//获取差值器运算出的扩散进度值（产生的是一系列不同数值的数字区间，由快到慢）
			float nowProgress=mExitInterpolator.getInterpolation(mExitprogress);
			onExitProgressChange(nowProgress);
			
			scheduleSelf(this,SystemClock.uptimeMillis()+16);
		}
	};
	
	  //退出效果的进度改变值
		private void onExitProgressChange(float progress)
		{
		/**原来是通过控制画笔的透明度（mPaintAlpha=255）来达到颜色的加深减淡，但是这种方式会导致我们设置的任何
		颜色的透明度都会变成不透明（255），所以我们现在通过控制背景和圆形区域的当前透明度来控制X和Y的值
		进而改变颜色的加深减淡	*/
			
			//背景减淡
			mBgAlpha=(int) getProgressValue(Max_Bg_Alpha, 0, progress);
			//圆形减淡
			mCircleAlpha=(int) getProgressValue(Max_Circle_Alpha, 0, progress);
			
			invalidateSelf();
		}
}
