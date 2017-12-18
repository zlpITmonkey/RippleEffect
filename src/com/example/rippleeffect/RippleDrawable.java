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

    private int mAlpha = 255;/*͸���ȷ�Χ 0 ~ 255*/
    private int mRippleColor = 0;
    private Paint mPaint = new Paint(
            Paint.ANTI_ALIAS_FLAG /*�����*/
    );
    //��ǰ�����ﵽ����͸����
    private int Max_Bg_Alpha=182;
    //��ǰԲ�ﵽ����͸����
    private int Max_Circle_Alpha=255;
    
    private float mRipplePointX = 0;
    private float mRipplePointY = 0;
    private float mRippleRadius=0;
    private float mEnterprogress;
    //��ǰ������Բ��͸����
    private int mBgAlpha,mCircleAlpha;
    
    private Boolean isTouchUp;
    private Boolean isEnterFinsh;

    public RippleDrawable() {
        // ���������,�����߱�Ե��Բ��
        mPaint.setAntiAlias(true);
        // ����������
        mPaint.setDither(true);
        //������ɫ
        setRippleColor(0x5ca3a1a1);
    }
    
 
/**---------------------------------------���ƽ����׼������-------------------------------------------------------*/
    /**����ɫ����͸���ȸı��ʱ�����
     *����Ϊ��ɫ����ʹ���͸���ȣ���DrawableҲ��һ��͸���ȣ�����֮���͸���Ȼ�Ӱ����ɫ�ı仯
     *��������Ҫ������ʵ��͸����Ȼ������������ڿؼ��ϵ���ɫ����
     *�����ɫ����͸���ȸı��ˣ��ͻ�Ӱ�쵽�����ڿؼ��ϵĳ�����ɫ�����Ե���ɫ����͸���ȸı�ʱ��Ҫ����
     *�����������Ϊ�˵õ���ʵ����ɫ��*/
      private void onColorOrAlphaChange() 
      {
          mPaint.setColor(mRippleColor);
          //���drawable�Ĳ�Ϊ��͸��״̬
          if (mAlpha != 255) {
              // �õ���ɫ�����͸����
              // �������ַ�ʽ,1 �Ǵӻ�������ȡ͸����; 2 �Ǵ���ɫ����ȡ��ɫ��͸����; ��ȡһ�ּ���
              int alpha = mPaint.getAlpha();
              // alpha = Color.alpha(mRippleColor);

              // �õ����Ӻ��ʵ��͸����
              int realAlpha = (int) (alpha * (mAlpha / 255f));
              // ���õ�������ȥ
              mPaint.setAlpha(realAlpha);
          }
          // ��ɫ�ı�ʱ,ҲӦ��ˢ���Լ�
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

    //������ɫ�ķ���
    public void setRippleColor(int color) 
    {
        mRippleColor = color;
      // ��ɫ�ı�ʱ����
        onColorOrAlphaChange();
    }
    
    //���� Drawable ��͸����
    @Override
    public void setAlpha(int alpha) 
    {
        mAlpha = alpha;
     // ͸���ȸĸı�ʱҲҪ����
        onColorOrAlphaChange();
    }

     //������ɫ�˾�
    @Override
    public void setColorFilter(ColorFilter colorFilter) 
    {
        // ���û��ʵ���ɫΪ�������ɫ�˾�
        mPaint.setColorFilter(colorFilter);
        // ˢ���Լ�
        invalidateSelf();
    }

    //��ȡdrawable��͸����״̬
    @Override
    public int getOpacity() 
    {
        int alpha = mPaint.getAlpha();
        if (alpha == 255){
            //��͸��
          return  PixelFormat.OPAQUE;
        }
        if (alpha == 0){
            // ȫ͸��
            return PixelFormat.TRANSPARENT;
        }
        // ����������� ��͸��
        return PixelFormat.TRANSLUCENT;
    }
/**-----------------------------------------------------------------------------------------------------------------*/
    
/**---------------------------------------����Ч��ʵ��--------------------------------------------------------------*/
    //����һ������¼�
    public void onTouch(MotionEvent event)
    {
        //ʵ��ˢ��Drawable�ķ�����ǰ����Ҫ�ڶ���İ�ť��ʵ��ʵ�ּ���������
        invalidateSelf();
        //���ø��ֵ��״̬�µĻ������
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
    //�������ʱ�򣬴ӵ����λ�û�Բ��ÿ���һ�ΰ뾶��10
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
    
    //��ʼ��һ�ε��
    private void startEnterRunnable()
    {
    	isEnterFinsh=false;
    	mCircleAlpha=255;
    	mEnterprogress=0;
    	//ִ����һ�ε��ʱ�����Ч������ֹ����ˢ��Խ��Խ��
    	unscheduleSelf(mEnterRunnable);
    	//���ö���Ч��ʵ�ֵķ���
    	scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    	
    }
    
    private void EnterFinsh()
    {
    	isEnterFinsh=true;
    	if (isTouchUp) {
			startExitRunnable();
		}
    }
    //����һ����ʼ��������Ĳ�ֵ��
    private Interpolator mEnterInterpolator =new DecelerateInterpolator(3);

    //�Զ���ɢ�Ķ�������ʵ��������ÿ���붼ȥִ�а뾶���ӵĻ�Բ���ƣ�
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
			//��ȡ��ֵ��������ȵ�ֵ�������������䣬�ɿ쵽����
			float nowProgress=mEnterInterpolator.getInterpolation(mEnterprogress);
			onProgressChange(nowProgress);
			invalidateSelf();
            /**���ñ���ÿ������ظ�ȥִ������������ﵽ�Զ���ɢ��Ч��
			�ӳ�16���룬ʹˢ��Ƶ�ʽӽ�Ϊÿ��60�Σ�һ����ˢ��60�Σ����൱��16����ˢ��һ�Σ�����ˢ�»�����Ҫ�����������
			������һЩʱ�䣬�����ӳ�16���룬����һЩ�˷ѵ�ʱ�䣩*/
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
	
	 //���忪ʼ����ÿ�ε�����ʱ��ֵ(��������Բ�뾶�������ٶ�)
    private float mExitIncrement=16f/340;
    
  //�����˼����ٶȣ�����Բ�뾶�������ٶȣ��Ľ���ֵ
	private float mExitprogress=0;
	
	 //����һ���˳�Ч���Ĳ�ֵ�����������죩
    private Interpolator mExitInterpolator =new AccelerateInterpolator(3);
    
    //�����˳��Զ���������
    private void startExitRunnable()
    {
    	mExitprogress=0;
    	unscheduleSelf(mEnterRunnable);
    	//ִ����һ�ε��ʱ�����Ч������ֹ����ˢ��Խ��Խ��
    	unscheduleSelf(mExitRunnable);
    	//���ö���Ч��ʵ�ֵķ���
    	scheduleSelf(mExitRunnable, SystemClock.uptimeMillis());
    	
    }
    
    //���Զ������������ʱ����
    private void onExitFinsh()
    {
    	//����дһЩ����¼�
    }

    //�˳��Զ������Ķ�������ʵ��������ÿ���붼ȥִ�а뾶���ӵĻ�Բ���ƣ�
    private Runnable mExitRunnable=new Runnable() {
		
		public void run() 
		{
			mExitprogress+=mExitIncrement;
			if (mExitprogress>1) 
			{
			/**��Ϊ����������ʱ��progress��һ��0-1��С�������Բ����ܾ�ȷ�ĵ���1
			 * ���ڻ���С��1�����ռ��������������һ��Ϊ0.97,��ô��һ���������1
			 * ��ô�Ͳ���ִ�м����Ĳ�����progress��û�дﵽ1������Ч���Ͳ��ܴﵽ��ȫû����ɫ��ֻ�е���1ʱ��
			 * ����Ҫ�ڶ����������ֶ�����progressΪ1������Ŀ�ʼ����Ҳ��һ��Ҫ����*/
				onExitProgressChange(1);
				onExitFinsh();
				return;
			}
			//��ȡ��ֵ�����������ɢ����ֵ����������һϵ�в�ͬ��ֵ���������䣬�ɿ쵽����
			float nowProgress=mExitInterpolator.getInterpolation(mExitprogress);
			onExitProgressChange(nowProgress);
			
			scheduleSelf(this,SystemClock.uptimeMillis()+16);
		}
	};
	
	  //�˳�Ч���Ľ��ȸı�ֵ
		private void onExitProgressChange(float progress)
		{
		/**ԭ����ͨ�����ƻ��ʵ�͸���ȣ�mPaintAlpha=255�����ﵽ��ɫ�ļ���������������ַ�ʽ�ᵼ���������õ��κ�
		��ɫ��͸���ȶ����ɲ�͸����255����������������ͨ�����Ʊ�����Բ������ĵ�ǰ͸����������X��Y��ֵ
		�����ı���ɫ�ļ������	*/
			
			//��������
			mBgAlpha=(int) getProgressValue(Max_Bg_Alpha, 0, progress);
			//Բ�μ���
			mCircleAlpha=(int) getProgressValue(Max_Circle_Alpha, 0, progress);
			
			invalidateSelf();
		}
}
