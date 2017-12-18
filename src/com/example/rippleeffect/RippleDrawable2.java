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

    private int mAlpha = 255;/*͸���ȷ�Χ 0 ~ 255*/
    private int mRippleColor = 0;
    //��ǰ�����ﵽ����͸����
    private int Max_Bg_Alpha=182;
    //��ǰԲ�ﵽ����͸����
    private int Max_Circle_Alpha=255;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG /*�����*/ );

    //Բ�ĵ�����
    private float mRipplePointX = 0;
    private float mRipplePointY = 0;
    private float mRippleRadius=0;
    
    //��ǰ����͸���Ⱥ�Բ��͸���ȣ���Yֵ����Xֵȡ��ֵʱ��Ϊʵ�ʵ�͸���ȣ�
  	private int mBackgroundAlpha;
  	private int mCircleAlpha;
  	
  	//�û���ָ�Ƿ�̧��
  	private Boolean isOnTouchUp;
  	
  	//�Զ���ɢ�����Ƿ����
  	private Boolean isEnterfinsh;
  	
  	

    public RippleDrawable2() {
        // ���������,�����߱�Ե��Բ��
        mPaint.setAntiAlias(true);
        // ����������
        mPaint.setDither(true);
        //������ɫ
        setRippleColor(0x5ca3a1a1);
    }
    
/**---------------------------------------���ƽ����׼������--------------------------------------------------------------*/
    
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
      
      //��ȡԲ����͸����ֵ��Ҳ����Xֵ��ͨ����֪��Zֵ��Yֵ�����Xֵ��
      private int getCircleAlpha(int preAlpha,int bgAlpha)
      {
    	  int dAlpha=preAlpha-bgAlpha;
		  return (int) ((dAlpha*255f)/(255f-bgAlpha)); 	  
      }
      
      
    //��ҳ��ˢ��ʱ������
    @Override
    public void draw(Canvas canvas) 
    {
    	//��ȡ�û�������ɫ��ʵ��͸���ȣ�Ҳ����Zֵ��
    	int preAlpha=mPaint.getAlpha();
    /**�û�������ɫ��ʵ��͸�����뵱ǰ������͸���ȵ��Ӻ��ʵ��͸����
     *��Ҳ����ȡ��ֵ���Yֵ��������û���͸������Ϊ��ǰ����͸���ȵ����ֵ��Yֵ�������뵱ǰ�ı���͸��ֵȡ��ֵ��
     *����ʵ��Ҫ���ֵ�͸���ȣ����û�͸���ȵ������ֵ��ԭ����Ϊ���ñ���͸������Ҳ�ͷ���Yֵ��͸����ֵҪ��XֵС���Ե���ɫ����*/
    	int bgAlpha=(int) (preAlpha*(mBackgroundAlpha/255f));
    	
    /**Բ��͸�������ֵXֵ��Ҳ����˵�ǵ�ǰԲ��͸���ȵ�����ֵ����Ϊ����Yֵ��Zֵ��һ���̶�ֵʱ
    *��ôԲ��͸����ҲҪ��һ���̶�ֵ�����ܳ������ֵ��Ҫ��Ȼ�����ֵ�͸���ȾͲ�����µ����û����õ�͸�����ˣ�
    *���ֵ����Xֵ*/
    	int maxCircleAlpha=getCircleAlpha(preAlpha, bgAlpha);
    	
   //Բ��͸�������ֵ�뵱ǰԲ��͸���ȵ��Ӻ��ʵ��͸���ȣ�Ҳ���Ǿ�ֵ���Xֵ�����ֵ�Ϳ��Ե���ʵ�ʵ�͸�����ˣ�
    	int mcircleAlpha=(int) (maxCircleAlpha*(mCircleAlpha/255f));
    	
    	//���Ʊ���(����ͨ����ǰ������Բ͸���ȵĸı�Ϳ��Կ�����ɫ�ļ�������)
    	mPaint.setAlpha(bgAlpha);
    	canvas.drawColor(mPaint.getColor());
    	
        //��һ��Բ
    	mPaint.setAlpha(mcircleAlpha);
        canvas.drawCircle(mRipplePointX, mRipplePointY, mRippleRadius, mPaint);
        
       /**����Ϊ��ʼֵ��Ҳ�����û���͸���ȣ�����������ã���ô��һ��ִ����ֱ�Ӵ�XYֵ��ȥ�ı�
                   ����������һ�κ���һ�ε�͸���Ȼ���ƫ�ÿ��һ�Σ�ƫ��ͻ������*/
        mPaint.setAlpha(preAlpha);
    }

    //������ɫ�ķ���
    public void setRippleColor(int color) 
    {
        // ������ֱ������
        // mPaint.setColor(color);
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
    //�������ʱ�򣬴ӵ����λ�û�Բ��ÿ���һ�ΰ뾶������Ӧ��ֵ
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
    	//���˳��������ʱ�������˳���������һ�����:�û����������̧��
    	if (isEnterfinsh) 
    	{
			startExitRunnable();
		}
    }
    private void onTouchCancel(float x, float y) 
    {
    	isOnTouchUp=true;
    	//���˳��������ʱ�������˳�����
    	if (isEnterfinsh) 
    	{
			startExitRunnable();
		}
    }
    
    //��ʼ��һ�ε���������Զ���ɢ������
    private void startEnterRunnable()
    {
    	//���õ�ǰԲ��͸����
    	mCircleAlpha=Max_Circle_Alpha;
    	isEnterfinsh=false;
    	mEnterprogress=0;
    	//��ֹ�û�������죬�˳���δִ�����
    	unscheduleSelf(mExitRunnable);
    	//ִ����һ�ε��ʱ�����Ч������ֹ����ˢ��Խ��Խ��
    	unscheduleSelf(mEnterRunnable);
    	//���ö���Ч��ʵ�ֵķ���
    	scheduleSelf(mEnterRunnable, SystemClock.uptimeMillis());
    	
    }
    
    //���Զ���ɢ�������ʱ����
    private void onEnterFinsh()
    {
    	isEnterfinsh=true;
    	//����ʼ������ɣ������û���̧��ʱ�������˳��������ڶ���������û�����ʱ����ʼ�����Ѿ�����ˣ�
    	if (isOnTouchUp) 
    	{
			startExitRunnable();
		}
    }
    
    //���ؿ�ʼ��ɢ�ٶȣ�����Բ�뾶�������ٶȣ��Ľ���ֵ
    private float mEnterprogress;
    
    //���忪ʼ��ɢÿ�ε�����ʱ��ֵ(��������Բ�뾶�������ٶ�)
	private float mEnterIncrement=16f/360;
    //����һ����ʼ��������Ĳ�ֵ��
    private Interpolator mEnterInterpolator =new DecelerateInterpolator(3);

    //�Զ���ɢ�Ķ�������ʵ��������ÿ���붼ȥִ�а뾶���ӵĻ�Բ���ƣ�
    private Runnable mEnterRunnable=new Runnable() {
		
		public void run() 
		{
			mEnterprogress+=mEnterIncrement;
			//��mEnterprogress����1����ֹͣ����
			if (mEnterprogress>1) 
			{
				onProgressChange(1);
				onEnterFinsh();
				return;
			}
			//��ȡ��ֵ�����������ɢ����ֵ����������һϵ�в�ͬ��ֵ���������䣬�ɿ쵽����
			float nowProgress=mEnterInterpolator.getInterpolation(mEnterprogress);
			//
			onProgressChange(nowProgress);
			
            /**���ñ���ÿ������ظ�ȥִ������������ﵽ�Զ���ɢ��Ч��
			�ӳ�16���룬ʹˢ��Ƶ�ʽӽ�Ϊÿ��60�Σ�һ����ˢ��60�Σ����൱��16����ˢ��һ�Σ�����ˢ�»�����Ҫ�����������
			������һЩʱ�䣬�����ӳ�16���룬����һЩ�˷ѵ�ʱ�䣩*/
			scheduleSelf(this,SystemClock.uptimeMillis()+16);
		}
	};
	
/**-----------------------------------------------------------------------------*/
	
/**------------------------------Ч��������ʹ���Ƶ�Բ�����򲻶���ؼ����Ŀ��������ұ�����͸����Խ��Խ�---------------------------------------------*/
	 
	//����ʱ�ĵ������
	private float DownPointX,DownPointY;
	//�ؼ������ĵ�����
	private float CenterPointX,CenterPointY;
	//��ɢ�Ŀ�ʼ�ͽ����뾶����������һ����ɢ��Χ��
	private float StartRadius,EndRadius;
	
	//����������ı�ʱ����,��ȡÿ�θı�ʱ�����ĵ�λ������
	@Override
	protected void onBoundsChange(Rect bounds) 
	{
		super.onBoundsChange(bounds);
		CenterPointX=bounds.centerX();
		CenterPointY=bounds.centerY();	
		//���뾶(ȡxy�������е����ֵ����Ϊ���뾶)
	    float maxRadius=Math.max(CenterPointX, CenterPointY);
	    
	    //��0��ʼ
	    StartRadius=maxRadius*0f;
	    //��0.8f����
	    EndRadius=maxRadius*0.8f;		
	}
	
	//������ֵ�ı�ʱ�����Ĳ�������ʼ������
	private void onProgressChange(float progress)
	{
		//���ݽ���ֵ�ĵ��أ��ɿ쵽�������Ӱ뾶
		mRippleRadius=getProgressValue(StartRadius, EndRadius, progress);
		//���ݲ�ͬ����ֵ������Բ�ĵ����꣬ʹ���𽥿���Բ��
		mRipplePointX=getProgressValue(DownPointX, CenterPointX, progress);
		mRipplePointY=getProgressValue(DownPointY, CenterPointY, progress);
		
		//�������Բ�β���Ҫ����ΪԲ͸�����ڿ�ʼ��������һ���̶�ֵ��ֻ��Ҫ�ڿ�ʼʱ����һ��ֵ���У�
		mBackgroundAlpha=(int) getProgressValue(0, Max_Bg_Alpha, progress);
		invalidateSelf();
	}
	
	/*���ݲ�ͬ�Ľ���ֵ�����㿪ʼ��仯��������������ȵĳ��ȣ�����ֵΪ�µĵ㣨����������˵���µĵ㣬��������ֵ��
	 * �ƶ����µĵ�λ�õľ��룬����ֻ��Ҫ�����������ʼ�ĳ��ȣ���������һ����ɢ�����뾶���ϱ�󣩵��ƶ������ɽ���ֵȥ���ص�
	 * ���Խ�������ʼ�ĳ���Ҫ*��ǰ�Ľ��ȣ����Ϊ��ʵ�ĳ��� */
	private float getProgressValue(float start,float end,float progress)
	{
		/**�µĵ�λ�õ��ڣ�
		 * ��ʼ�������+��ʵ�ĳ��ȣ�������֮������Ĳ�ֵ*��ǰ�Ľ���ֵ������Ϊ�µĵ�λ��*/	
		return start+(end-start)*progress;	
	}

/*��������ͨ���ı䱳����Բ��͸���������Ƽ�����������ԾͲ���Ҫ���ַ�����
 * //������ɫ͸���ȣ��ﵽ������ɫ���ϼ����Ч�������ǲ��Ϻϳ��µ���ɫ����ɫ�ǹ̶��Ĳ����Ǹı�͸���ȣ�
	private int changeColorAlpha(int color,int alpha)
	{
		//��ȡ͸�����Լ�������������ɫ
		int a= (color >>> 24) & 0xFF;
		int r= (color >>16) & 0xFF;
		int g= (color >>8) & 0xFF;
		int b= (color) & 0xFF;
		
		//��ȡ����֮���͸���ȣ��������ȡ͸���ȵķ������ƣ�
		a=(int) (a*(alpha/255f));
		
		//����һ���µ���ɫ
		return (a << 24) | (r << 16) | (g << 8) | b;	
	}*/
	
/**----------------------------------------------------------------------------------------------------*/
	
/**------------------------------------�˳�����Ч��ʵ�֣���Ϊ����,��Ҫ����������Բ��������û�У�----------------------------------------------------------------*/
	
	 //���忪ʼ����ÿ�ε�����ʱ��ֵ(��������Բ�뾶�������ٶ�)
    private float mExitIncrement=16f/360;
    
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
			mBackgroundAlpha=(int) getProgressValue(Max_Bg_Alpha, 0, progress);
			//Բ�μ���
			mCircleAlpha=(int) getProgressValue(Max_Circle_Alpha, 0, progress);
			
			invalidateSelf();
		}

}
