package com.sdkEw202w.demo.fragment;

import com.sdkEw202w.demo.MainActivity;
import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.BaseActivity.MyOnTouchListener;
import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.util.LogUtil;
import com.sdkEw202w.demo.util.Utils;
import com.sleepace.sdk.constant.StatusCode;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IMonitorManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.util.SdkLog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LightFragment extends BaseFragment {
	private View maskView;
	private EditText etR, etG, etB, etW, etBrightness;
	private Button btnSendColor, btnSendBrightness, btnCloseLight;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_light, null);
		// LogUtil.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		etR = (EditText) root.findViewById(R.id.et_r);
		etG = (EditText) root.findViewById(R.id.et_g);
		etB = (EditText) root.findViewById(R.id.et_b);
		etW = (EditText) root.findViewById(R.id.et_w);
		etBrightness = (EditText) root.findViewById(R.id.et_brightness);
		btnSendColor = (Button) root.findViewById(R.id.btn_w);
		btnSendBrightness = (Button) root.findViewById(R.id.btn_brightness);
		btnCloseLight = (Button) root.findViewById(R.id.btn_close_light);
		etR.setText("255");
		etG.setText("104");
		etB.setText("0");
		etW.setText("30");
		etBrightness.setText("100");
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		etR.addTextChangedListener(rgbwWatcher);
		etG.addTextChangedListener(rgbwWatcher);
		etB.addTextChangedListener(rgbwWatcher);
		etW.addTextChangedListener(rgbwWatcher);
		etBrightness.addTextChangedListener(brightnessWatcher);
		registerTouchListener(touchListener);
	}

	private MyOnTouchListener touchListener = new MyOnTouchListener() {
		@Override
		public boolean onTouch(MotionEvent ev) {
			// TODO Auto-generated method stub
			View view = mActivity.getCurrentFocus();
			ActivityUtil.hideKeyboard(ev, view, mActivity);
			return false;
		}
	};

	private TextWatcher rgbwWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if (!TextUtils.isEmpty(str)) {
				int rgbw = Integer.valueOf(str);
				if (rgbw > 255) {
					Toast.makeText(mActivity, R.string.input_0_255, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private TextWatcher brightnessWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String str = s.toString();
			if (!TextUtils.isEmpty(str)) {
				Utils.inputTips(etBrightness, 100);
			}
		}
	};

	protected void initUI() {
		// TODO Auto-generated method stub
		// etR.setText("45");
		// etG.setText("120");
		// etB.setText("98");
		// etW.setText("182");
		// etBrightness.setText("52");
		
		if(!TextUtils.isEmpty(MainActivity.deviceId)) {
			mHelper.queryDeviceOnlineState(DeviceType.DEVICE_TYPE_EW202W.getType(), MainActivity.deviceId, new IResultCallback<Byte>() {
				@Override
				public void onResultCallback(CallbackData<Byte> cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG+" queryDeviceOnlineState----------"+ cd);
					if(cd.isSuccess()) {
						if(cd.getResult() == 1) {//在线
							
						}else {//离线
							
						}
					}
				}
			});
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getDeviceHelper().isConnected();
		initPageState(true);
	}

	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}

	private void initBtnConnectState(boolean isConnected) {
		btnSendColor.setEnabled(isConnected);
		btnSendBrightness.setEnabled(isConnected);
		btnCloseLight.setEnabled(isConnected);
	}

	private void setPageEnable(boolean enable) {
		if (enable) {
			maskView.setVisibility(View.GONE);
		} else {
			maskView.setVisibility(View.VISIBLE);
		}
	}


	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub

			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initPageState(state == CONNECTION_STATE.CONNECTED);

					if (state == CONNECTION_STATE.DISCONNECT) {

					} else if (state == CONNECTION_STATE.CONNECTED) {

					}
				}
			});
		}
	};

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		getDeviceHelper().removeConnectionStateCallback(stateCallback);
		unregisterTouchListener(touchListener);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnSendColor) {

			if (Utils.inputTips(etR, 255)) {
				return;
			}
			if (Utils.inputTips(etG, 255)) {
				return;
			}
			if (Utils.inputTips(etB, 255)) {
				return;
			}
			if (Utils.inputTips(etW, 255)) {
				return;
			}

			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();

			byte brightness = 50;
			String strBrightness = etBrightness.getText().toString();
			if (!TextUtils.isEmpty(strBrightness)) {
				brightness = (byte) (int) Integer.valueOf(strBrightness);
			}

			byte r = (byte) (int) Integer.valueOf(strR);
			byte g = (byte) (int) Integer.valueOf(strG);
			byte b = (byte) (int) Integer.valueOf(strB);
			byte w = (byte) (int) Integer.valueOf(strW);

			SLPLight light = new SLPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);
			// final byte lightMode, final byte brightness, final SLPLight light
			SdkLog.log(TAG, "开灯操作设备id:" + MainActivity.deviceId);
			getDeviceHelper().turnOnSleepAidLight(MainActivity.deviceId, (byte) 255, brightness, light, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new  Runnable() {
	                    public void run() {
	        				if(!cd.isSuccess()){
	    						showErrMsg(cd);
	    					}
	                    }
                    });
	
				}
			});
		} else if (v == btnSendBrightness) {

			if (Utils.inputTips(etBrightness, 100)) {
				return;
			}

			String strBrightness = etBrightness.getText().toString();
			byte brightness = (byte) (int) Integer.valueOf(strBrightness);

			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			if (TextUtils.isEmpty(strR) || TextUtils.isEmpty(strG) || TextUtils.isEmpty(strB) || TextUtils.isEmpty(strW)) {
				return;
			}
			byte r = (byte) (int) Integer.valueOf(strR);
			byte g = (byte) (int) Integer.valueOf(strG);
			byte b = (byte) (int) Integer.valueOf(strB);
			byte w = (byte) (int) Integer.valueOf(strW);

			SLPLight light = new SLPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);

			getDeviceHelper().lightBrightness(MainActivity.deviceId, (byte) 255, brightness, light, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new Runnable() {
	                    public void run() {
	    					if(!cd.isSuccess()){
	    						showErrMsg(cd);
	    					}
	                    }
                    });

				}
			});

		} else if (v == btnCloseLight) {
			getDeviceHelper().turnOffLight(MainActivity.deviceId, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(!cd.isSuccess()){
								showErrMsg(cd);
							}
						}
					});

				}
			});
		}
	}
	


	// @Override
	// public void onDetach() {
	// super.onDetach();
	// try {
	// Field childFragmentManager =
	// Fragment.class.getDeclaredField("mChildFragmentManager");
	// childFragmentManager.setAccessible(true);
	// childFragmentManager.set(this, null);
	// } catch (NoSuchFieldException e) {
	// throw new RuntimeException(e);
	// } catch (IllegalAccessException e) {
	// throw new RuntimeException(e);
	// }
	// }

}
