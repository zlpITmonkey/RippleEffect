package com.example.rippleeffect;

import android.R.color;
import android.R.integer;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.style.BackgroundColorSpan;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by didik on 2017/2/8.
 */

public class RippleDrawable2 extends Drawable {

    private int mAlpha = 255;/*透明度范围 0 ~ 255*/
    private int mRippleColor = 0;
    //当前背景达到最大的透明度
    private int Max_Bg_Alpha=182;
    //当前圆达到最大的透明度
    private int Max_Circle_Alpha=255;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG /*抗锯齿*/ );

    //圆心点坐标
    private float mRipplePointX = 0;
    private float mRipplePointY = 0;
    private float mRippleRadius=0;
    
    //当前背景透明度和圆的透明度（与Y值，和X值取均值时就为实际的透明度）
  	private int mBackgroundAlpha;
  	private int mCircleAlpha;
  	
  	//用户手指是否抬起
  	private Boolean isOnTouchUp;
  	
  	//自动扩散动画是否完成
  	private Boolean isEnterfinsh;
  	
  	

    public RippleDrawable2() {
        // 开启抗锯齿,让曲线边缘更圆滑
        mPaint.setAntiAlias(true);
        // 开启防抖动
        mPaint.setDither(true);
        //设置颜色
        setRippleColor(0x5ca3a1a1);
    }
    
/**---------------------------------------绘制界面的准备工作--------------------------------------------------------------*/
    
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
      
      //获取圆最大的透明度值，也就是X值（通过已知的Z值和Y值，求出X值）
      private int getCircleAlpha(int preAlpha,int bgAlpha)
      {
    	  int dAlpha=preAlpha-bgAlpha;
		  return (int) ((dAlpha*255f)/(255f-bgAlpha)); 	  
      }
      
      
    //当页面刷新时便会调用
    @Override
    public void draw(Canvas canvas) 
    {
    	//获取用户设置颜色的实际透明度（也就是Z值）
    	int preAlpha=mPaint.getAlpha();
    /**用户设置颜色的实际透明度与当前背景的透明度叠加后的实际透明度
     *（也就是取均值后的Y值，这里把用户的透明度作为当前背景透明度的最大值（Y值），再与当前的背景透明值取均值，
     *就是实际要呈现的透明度）把用户透明度当成最大值的原因是为了让背景透明更大，也就符合Y值不透明的值要比X值小，显得颜色更淡*/
    	int bgAlpha=(int) (preAlpha*(mBackgroundAlpha/255f));
    	
    /**圆的透明度最大值X值（也可以说是当前圆的透明度的限制值，因为，当Y值和Z值是一个固定值时
    *那么圆的透明度也要是一个固定值，不能超过这个值，要不然最后呈现的透明度就不会大致等于用户设置的透明度了）
    *这个值就是X值*/
    	int maxCircleAlpha=getCircleAlpha(preAlpha, bgAlpha);
    	
   //圆的透明度最大值与当前圆的透明度叠加后的实际透明度（也就是均值后的X值，这个值就可以调控实际的透明度了）
    	int mcircleAlpha=(int) (maxCircleAlpha*(mCircleAlpha/255f));
    	
    	//绘制背景(后面通过当前背景和圆透明度的改变就可以控制颜色的减淡加深)
    	mPaint.setAlpha(bgAlpha);
    	canvas.drawColor(mPaint.getColor());
    	
        //画一个圆
    	mPaint.setAlpha(mcircleAlpha);
        canvas.drawCircle(mRipplePointX, mRipplePointY, mRippleRadius, mPaint);
        
       /**设置为初始值（也就是用户的透明度），如果不设置，那么下一次执行是直接从XY值上去改变
                   这样做，上一次和下一次的透明度会有偏差，每多一次，偏差就会更严重*/
        mPaint.setAlpha(preAlpha);
    }

    //设置颜色的方法
    public void setRippleColor(int color) 
    {
        // 不建议直接设置
        // mPaint.setColor(color);
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
    //当点击的时候，从点击的位置画圆，每点击一次半径增加相应的值
    private void onTouchDown(float x, float y) {
        DownPointX = x;
        DownPointY = y;
        isOnTouchUp=false;
        startEnterRunnable();
    }

    private void onTouchMove(float x, float y) {
    }
    private void onTouchUp(float x, float y) 
    {
    	isOnTouchUp=true;
    	//当退出动画完成时，启动退出动画（第一种情况:用户点击后立刻抬起）
    	if (isEnterfinsh) 
    	{
			startExitRunnable();
		}
    }
    private void onTouchCancel(float x, float y) 
    {
    	isOnTouchUp=true;
    	//当退出动画完成时，启动退出动画
    	if (isEnterfinsh) 
    	{
			startExitRunnable();
		}
    }
    
    //开始下一次点击（启动自动扩散动画）
    private void startEnterRunnable()
    {
    	//设置当前圆的透明度
    	mCircleAlpha=Max_Circle_Alpha;
    	isEnterfinsh=false;
    	mEnterprogress=0;
    	//防止用户点击过快，退出还未执行完毕
    	unscheduleSelf(mExitRunnable);
    	//执行下一次点击时，清除效果，防止界面刷新越来越快
    	unscheduleSelf(mEnterRunnable);
    	//调用动画效果实现的方法
    	scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    	
    }
    
    //当自动扩散动画完成时调用
    private void onEnterFinsh()
    {
    	isEnterfinsh=true;
    	//当开始动画完成，并且用户手抬起时，启动退出动画（第二种情况：用户长按时，开始动画已经完成了）
    	if (isOnTouchUp) 
    	{
			startExitRunnable();
		}
    }
    
    //调控开始扩散速度（就是圆半径的增幅速度）的进度值
    private float mEnterprogress;
    
    //定义开始扩散每次递增的时间值(用来控制圆半径的增幅速度)
	private float mEnterIncrement=16f/360;
    //定义一个开始快结束慢的差值器
    private Interpolator mEnterInterpolator =new DecelerateInterpolator(3);

    //自动扩散的动画（其实就是让其每毫秒都去执行半径增加的画圆绘制）
    private Runnable mEnterRunnable=new Runnable() {
		
		public void run() 
		{
			mEnterprogress+=mEnterIncrement;
			//当mEnterprogress大于1，则停止动画
			if (mEnterprogress>1) 
			{
				onProgressChange(1);
				onEnterFinsh();
				return;
			}
			//获取差值器运算出的扩散进度值（产生的是一系列不同数值的数字区间，由快到慢）
			float nowProgress=mEnterInterpolator.getInterpolation(mEnterprogress);
			//
			onProgressChange(nowProgress);
			
            /**调用本身，每毫秒的重复去执行这个操作，达到自动扩散的效果
			延迟16毫秒，使刷新频率接近为每秒60次（一秒钟刷新60次，就相当于16毫秒刷新一次，可是刷新机制需要三步才能完成
			会消耗一些时间，所以延迟16毫秒，抵消一些浪费的时间）*/
			scheduleSelf(this,SystemClock.uptimeMillis()+16);
		}
	};
	
/**-----------------------------------------------------------------------------*/
	
/**------------------------------效果升级（使绘制的圆形区域不断向控件中心靠近，并且背景的透明度越来越深）---------------------------------------------*/
	 
	//按下时的点击坐标
	private float DownPointX,DownPointY;
	//控件的中心点坐标
	private float CenterPointX,CenterPointY;
	//扩散的开始和结束半径（用来给定一个扩散范围）
	private float StartRadius,EndRadius;
	
	//当绘制区域改变时调用,获取每次改变时的中心点位置坐标
	@Override
	protected void onBoundsChange(Rect bounds) 
	{
		super.onBoundsChange(bounds);
		CenterPointX=bounds.centerX();
		CenterPointY=bounds.centerY();	
		//最大半径(取xy坐标其中的最大值，便为最大半径)
	    float maxRadius=Math.max(CenterPointX, CenterPointY);
	    
	    //从0开始
	    StartRadius=maxRadius*0f;
	    //到0.8f结束
	    EndRadius=maxRadius*0.8f;		
	}
	
	//当进度值改变时所做的操作（开始动画）
	private void onProgressChange(float progress)
	{
		//根据进度值的调控，由快到慢的增加半径
		mRippleRadius=getProgressValue(StartRadius, EndRadius, progress);
		//根据不同进度值，计算圆心的坐标，使其逐渐靠近圆心
		mRipplePointX=getProgressValue(DownPointX, CenterPointX, progress);
		mRipplePointY=getProgressValue(DownPointY, CenterPointY, progress);
		
		//加深背景（圆形不需要，因为圆透明度在开始动画中是一个固定值，只需要在开始时设置一个值就行）
		mBackgroundAlpha=(int) getProgressValue(0, Max_Bg_Alpha, progress);
		invalidateSelf();
	}
	
	/*根据不同的进度值，计算开始点变化到结束点所需进度的长度，返回值为新的点（对于坐标来说是新的点，其他的是值）
	 * 移动到新的点位置的距离，本来只需要计算结束到开始的长度，但是这是一个扩散（即半径不断变大）的移动，是由进度值去调控的
	 * 所以结束到开始的长度要*当前的进度，这才为真实的长度 */
	private float getProgressValue(float start,float end,float progress)
	{
		/**新的点位置等于：
		 * 开始的坐标点+真实的长度（即两点之间坐标的差值*当前的进度值），即为新的点位置*/	
		return start+(end-start)*progress;	
	}

/*现在我们通过改变背景和圆的透明度来控制加深减淡，所以就不需要这种方法了
 * //更改颜色透明度，达到背景颜色不断加深的效果（就是不断合成新的颜色，颜色是固定的不过是改变透明度）
	private int changeColorAlpha(int color,int alpha)
	{
		//获取透明度以及红绿蓝三种颜色
		int a= (color >>> 24) & 0xFF;
		int r= (color >>16) & 0xFF;
		int g= (color >>8) & 0xFF;
		int b= (color) & 0xFF;
		
		//获取叠加之后的透明度（跟上面获取透明度的方法类似）
		a=(int) (a*(alpha/255f));
		
		//返回一个新的颜色
		return (a << 24) | (r << 16) | (g << 8) | b;	
	}*/
	
/**----------------------------------------------------------------------------------------------------*/
	
/**------------------------------------退出涟漪效果实现（即为减淡,需要减淡背景和圆，减淡到没有）----------------------------------------------------------------*/
	
	 //定义开始减淡每次递增的时间值(用来控制圆半径的增幅速度)
    private float mExitIncrement=16f/360;
    
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
			mBackgroundAlpha=(int) getProgressValue(Max_Bg_Alpha, 0, progress);
			//圆形减淡
			mCircleAlpha=(int) getProgressValue(Max_Circle_Alpha, 0, progress);
			
			invalidateSelf();
		}

}
