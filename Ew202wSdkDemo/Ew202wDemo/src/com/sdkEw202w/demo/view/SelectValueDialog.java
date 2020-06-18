package com.sdkEw202w.demo.view;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.wheelview.NumericWheelAdapter;
import com.sdkEw202w.demo.wheelview.OnItemSelectedListener;
import com.sdkEw202w.demo.wheelview.WheelAdapter;
import com.sdkEw202w.demo.wheelview.WheelView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SelectValueDialog extends Dialog {
	private static final String TAG = SelectValueDialog.class.getSimpleName();
	private TextView tvCancel, tvTitle, tvOk, tvUnit;
	private WheelView wvValue;
    private WheelAdapter adapter;
    
    private String leftBtnLabel, title, rightBtnLabel;
    private String unit;
    private ValueSelectedListener valueSelectedListener;
    private int[] data;
    private byte value;

	public SelectValueDialog(Context context) {
		super(context, R.style.myDialog);
		// TODO Auto-generated constructor stub
	}
	
	public SelectValueDialog(Context context, int[] data) {
		super(context, R.style.myDialog);
		// TODO Auto-generated constructor stub
		this.data = data;
	}
	
	public void setLabel(String leftBtnLabel, String title, String rightBtnLabel, String unit) {
		this.leftBtnLabel = leftBtnLabel;
		this.title = title;
		this.rightBtnLabel = rightBtnLabel;
		this.unit = unit;
		initView();
	}
	
	public void setDefaultValue(byte value) {
		this.value = value;
		initView();
	}
	
	public void setValueSelectedListener(ValueSelectedListener valueSelectedListener) {
		this.valueSelectedListener = valueSelectedListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_value);
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = getContext().getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = d.widthPixels;
		lp.gravity = Gravity.BOTTOM;
		dialogWindow.setAttributes(lp);
		
		tvCancel = (TextView) findViewById(R.id.tv_cancel);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvOk = (TextView) findViewById(R.id.tv_ok);
		tvUnit = (TextView) findViewById(R.id.tv_label);
		
		tvCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		tvOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				if(valueSelectedListener != null) {
					valueSelectedListener.onValueSelected(SelectValueDialog.this, value);
				}
			}
		});
		
		wvValue = (WheelView) findViewById(R.id.val);
		
        wvValue.setAdapter(new NumericWheelAdapter(data, 0));
        wvValue.setTextSize(20);
        wvValue.setCyclic(true);
        wvValue.setOnItemSelectedListener(onHourItemSelectedListener);

        wvValue.setRate(5 / 4.0f);
        
        initView();
	}
	
	private void initView() {
		if(tvCancel == null) {
			return;
		}
		
		tvCancel.setText(leftBtnLabel);
		tvTitle.setText(title);
		tvOk.setText(rightBtnLabel);
		
		if(TextUtils.isEmpty(unit)) {
			tvUnit.setVisibility(View.GONE);
		}else {
			tvUnit.setVisibility(View.VISIBLE);
			tvUnit.setText(unit);
		}
		
		int idx = 0;
		for(int i =0;i<data.length;i++) {
			if(data[i] == value) {
				idx = i;
				break;
			}
		}
		wvValue.setCurrentItem(idx);
	}
	
	 //更新控件快速滑动
    private OnItemSelectedListener onHourItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(int index) {
//            LogUtil.log(TAG+" hour onItemSelected idx:" + index+",val:" + wvHour.getCurrentItem());
        	value = (byte) index;
        }
    };
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return TAG;
    }
    
    public interface ValueSelectedListener{
    	void onValueSelected(SelectValueDialog dialog, byte value);
    }

}
