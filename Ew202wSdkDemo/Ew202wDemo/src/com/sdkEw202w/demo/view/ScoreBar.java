package com.sdkEw202w.demo.view;

import com.sdkEw202w.demo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import static android.graphics.Paint.Style.STROKE;


/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 */
public class ScoreBar extends View {
	private static final String TAG = ScoreBar.class.getSimpleName();
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	private Paint textPaint;

	private boolean showText;
	private float textSize;
	private int textColor;
	
	/**
	 * 圆环的颜色
	 */
	private int roundColor;
	
	/**
	 * 圆环进度的颜色
	 */
	private int roundProgressColor;

	/**
	 * 圆环的宽度
	 */
	private float roundWidth;
	
	/**
	 * 最大进度
	 */
	private int max;
	
	/**
	 * 当前进度
	 */
	private int progress;


	public ScoreBar(Context context) {
		this(context, null);
	}

	public ScoreBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ScoreBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		paint = new Paint();

		
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ScoreBar);
		
		//获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.ScoreBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.ScoreBar_roundProgressColor, Color.GREEN);
		roundWidth = mTypedArray.getDimension(R.styleable.ScoreBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.ScoreBar_max, 100);
		showText = mTypedArray.getBoolean(R.styleable.ScoreBar_showProgressTip, false);
		textSize = mTypedArray.getDimension(R.styleable.ScoreBar_textSize, 20);
		textColor = mTypedArray.getColor(R.styleable.ScoreBar_textColor, Color.BLACK);

//		LogUtil.logE(TAG+" init roundC:" + roundColor+",roundW:" + roundWidth);

		mTypedArray.recycle();

		textPaint = new TextPaint();
		textPaint.setTextSize(textSize);
		textPaint.setColor(textColor);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measuredWidth, measuredHeight);
}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/**
		 * 画最外层的大圆环
		 */
		int centre = getWidth()/2; //获取圆心的x坐标
		int radius = (int) (centre - roundWidth/2); //圆环的半径
		paint.setColor(roundColor); //设置圆环的颜色
		paint.setStyle(STROKE); //设置空心
		paint.setStrokeWidth(roundWidth); //设置圆环的宽度
		paint.setAntiAlias(true);  //消除锯齿 
		canvas.drawCircle(centre, centre, radius, paint); //画出圆环

		/**
		 * 画圆弧 ，画圆环的进度
		 */
		
		//设置进度是实心还是空心
		paint.setStrokeWidth(roundWidth); //设置圆环的宽度
		paint.setColor(roundProgressColor);  //设置进度的颜色
		RectF oval = new RectF(centre - radius, centre - radius, centre
				+ radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
		paint.setStyle(STROKE);
		canvas.drawArc(oval, -90, 360 * progress / max, false, paint);  //根据进度画圆弧

		//画进度提示文本
		if(showText){
			String text = ((int) (progress / (float)max * 100) + "%");
			float txtW = textPaint.measureText(text);
			float x = (getWidth() - txtW)/2f;
			float y = getHeight() / 2 - (textPaint.getFontMetrics().descent + textPaint.getFontMetrics().ascent) / 2;
//			LogUtil.showMsg(TAG+" draw txt:"+ text+",tw:"+txtW+",x:"+x+",y:"+y);
			canvas.drawText(text, x, y, textPaint);
		}
	}
	
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
	 * 刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if(progress < 0){
			throw new IllegalArgumentException("progress not less than 0");
		}
		if(progress > max){
			progress = max;
		}
		if(progress <= max){
			this.progress = progress;
			postInvalidate();
		}
		
	}
	
	
	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

}
