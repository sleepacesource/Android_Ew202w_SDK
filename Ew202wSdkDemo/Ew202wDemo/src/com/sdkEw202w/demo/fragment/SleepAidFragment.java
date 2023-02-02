package com.sdkEw202w.demo.fragment;

import com.sdkEw202w.demo.DataListActivity;
import com.sdkEw202w.demo.DemoApp;
import com.sdkEw202w.demo.MainActivity;
import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.BaseActivity.MyOnTouchListener;
import com.sdkEw202w.demo.bean.MusicInfo;
import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.util.LogUtil;
import com.sdkEw202w.demo.util.Utils;
import com.sdkEw202w.demo.view.SelectValueDialog;
import com.sdkEw202w.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.BleNoxAidInfo;
import com.sleepace.sdk.core.nox.domain.BleNoxWorkStatus;
import com.sleepace.sdk.core.nox.domain.SLPLight;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.core.nox.interfs.ISleepAidManager;
import com.sleepace.sdk.core.nox.interfs.INoxManager.AromaSpeed;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.WorkSource;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SleepAidFragment extends BaseFragment {
	private View maskView;
	private TextView tvMusic, tvSleepAidTimeValue, tvSleepAidTips;
	private View vSleepAidTime;
	private EditText etVolume, etR, etG, etB, etW, etBrightness;
	private Button btnSendVolume, btnPlayMusic, btnSendColor, btnSendBrightness, btnCloseLight, btnSave;

	private SelectValueDialog valueDialog;

	private BleNoxAidInfo aidInfo = new BleNoxAidInfo();
	private MusicInfo music = new MusicInfo();
	private byte colorCode;
	private boolean playing;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_sleepaid, null);
		findView(root);
		initUi();
		initListener();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		tvMusic = (TextView) root.findViewById(R.id.tv_music_name);
		etVolume = (EditText) root.findViewById(R.id.et_volume);
		etR = (EditText) root.findViewById(R.id.et_r);
		etG = (EditText) root.findViewById(R.id.et_g);
		etB = (EditText) root.findViewById(R.id.et_b);
		etW = (EditText) root.findViewById(R.id.et_w);
		etBrightness = (EditText) root.findViewById(R.id.et_brightness);
		btnSendVolume = (Button) root.findViewById(R.id.btn_volume);
		btnPlayMusic = (Button) root.findViewById(R.id.btn_music);
		btnSendColor = (Button) root.findViewById(R.id.btn_w);
		btnSendBrightness = (Button) root.findViewById(R.id.btn_brightness);
		btnCloseLight = (Button) root.findViewById(R.id.btn_close_light);
		btnSave = (Button) root.findViewById(R.id.btn_save);
		vSleepAidTime = root.findViewById(R.id.layout_sleepaid_time);
		tvSleepAidTimeValue = (TextView) root.findViewById(R.id.tv_sleepaid_time_value);
		tvSleepAidTips = (TextView) root.findViewById(R.id.tv_tips_sleep_aid);
		mActivity.setTitle(R.string.device);
	}

	private void initUi() {
		// TODO Auto-generated method stub
		initDefaultAidInfo();
		SdkLog.log(TAG, "---getDeviceHelper().isConnected()--：" + getDeviceHelper().isConnected()+"---MainActivity.deviceId---:"+MainActivity.deviceId);
		if (getDeviceHelper().isConnected()) {
			// 助眠获取
			mHelper.sleepAidConfigGet(MainActivity.deviceId, 3000, new IResultCallback<BleNoxAidInfo>() {
				@Override
				public void onResultCallback(final CallbackData<BleNoxAidInfo> cd) {
					// TODO Auto-generated method stub
					SdkLog.log(TAG, "---sleepAidConfigGet--：" + cd);
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (cd.isSuccess()) {
								if (cd.getResult() != null) {
									aidInfo = cd.getResult();
								}
								initAidInfo(aidInfo);
							} else {
								showErrMsg(cd);
							}
						}
					});
					
					getDeviceHelper().getWorkStatus(MainActivity.deviceId, 3000, new IResultCallback<BleNoxWorkStatus>() {
						@Override
						public void onResultCallback(CallbackData<BleNoxWorkStatus> cd) {
							// TODO Auto-generated method stub
							SdkLog.log(TAG, "---getWorkStatus--：" + cd);
						}
						
					});
				}
			});
		}
	}

	private void initAidInfo(BleNoxAidInfo aidInfo) {
		int[] data = new int[45];
		for (int i = 1; i <= data.length; i++) {
			data[i - 1] = i;
		}

		valueDialog = new SelectValueDialog(mActivity, data);
		valueDialog.setValueSelectedListener(valueSelectedListener);

		etVolume.setText(String.valueOf(aidInfo.getVolume()));
		SLPLight slpLight = aidInfo.getLight();
		int r = slpLight.getR();
		int g = slpLight.getG();
		int b = slpLight.getB();
		int w = slpLight.getW();
		r = r == -1 ? 255 : r;
		g= g == -1 ? 255 : g;
		b = b == -1 ? 255 : b;
		w = w == -1 ? 255 : w;
		etR.setText(String.valueOf(r));
		etG.setText(String.valueOf(g));
		etB.setText(String.valueOf(b));
		etW.setText(String.valueOf(w));
		int brightNess = aidInfo.getBrightness();
		etBrightness.setText(String.valueOf(brightNess));
		music.setMusicID(aidInfo.getMusicId());
		music.setMusicName(getString(Utils.getSleepAidMusicName(aidInfo.getMusicId())));

		initMusicButtonStatus();
		initSleepAidDurationView();
		initMusicView();

	}

	private void initDefaultAidInfo() {

		colorCode = (byte) 0xff;
		aidInfo.setAidStopDuration((byte) 1);
		int[] data = new int[45];
		for (int i = 1; i <= data.length; i++) {
			data[i - 1] = i;
		}

		valueDialog = new SelectValueDialog(mActivity, data);
		valueDialog.setValueSelectedListener(valueSelectedListener);
		music.setMusicID(DemoApp.SLEEPAID_MUSIC[10][0]);
		music.setMusicName(getString(Utils.getSleepAidMusicName(music.getMusicID())));
		initMusicView();

		etVolume.setText("10");
		etR.setText("255");
		etG.setText("120");
		etB.setText("0");
		etW.setText("0");
		etBrightness.setText("38");
		initMusicButtonStatus();
		initSleepAidDurationView();
		SLPLight light = new SLPLight();
		light.setR((byte) 255);
		light.setR((byte) 120);
		light.setR((byte) 0);
		light.setR((byte) 0);
		aidInfo.setLight(light);
		aidInfo.setAidStopDuration((byte) 1);
		aidInfo.setAromaRate(AromaSpeed.COMMON.getValue());
		aidInfo.setMusicId((short) music.getMusicID());
		aidInfo.setBrightness((byte) 38);
		aidInfo.setVolume((byte) 10);
		byte flag = (byte) (playing ? 0 : 1);
		aidInfo.setMusicOpenFlag(flag);
		aidInfo.setCircleMode((byte) 1);
		aidInfo.setBrightness((byte) 38);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		tvMusic.setOnClickListener(this);
		vSleepAidTime.setOnClickListener(this);
		btnSendVolume.setOnClickListener(this);
		btnPlayMusic.setOnClickListener(this);
		btnSendColor.setOnClickListener(this);
		btnSendBrightness.setOnClickListener(this);
		btnCloseLight.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		etVolume.addTextChangedListener(volumeWatcher);
		etG.addTextChangedListener(gWatcher);
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

	private TextWatcher volumeWatcher = new TextWatcher() {

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
				Utils.inputTips(etVolume, 16);
			}
		}
	};

	private TextWatcher gWatcher = new TextWatcher() {

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
				Utils.inputTips(etG, 120);
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

	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, byte value) {
			// TODO Auto-generated method stub
			LogUtil.log(TAG + " onValueSelected val:" + value);
			aidInfo.setSmartFlag((byte)0);
			aidInfo.setAidStopDuration(value);
			initSleepAidDurationView();
		}
	};

	private void initMusicView() {
		tvMusic.setText(music.getMusicName());
	}

	private void initSleepAidDurationView() {
		String str = Utils.getDuration(mActivity, aidInfo.getAidStopDuration());
		LogUtil.log(TAG + " initSleepAidDurationView str:" + str + ",duration:" + aidInfo.getAidStopDuration());
		tvSleepAidTimeValue.setText(str);
		tvSleepAidTips.setText(getString(R.string.music_aroma_light_close2, String.valueOf(aidInfo.getAidStopDuration())));
	}

	private void initMusicButtonStatus() {
		if (playing) {
			btnPlayMusic.setText(R.string.pause);
		} else {
			btnPlayMusic.setText(R.string.play);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getDeviceHelper().isConnected();
		initPageState(isConnected);
	}

	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);

	}

	private void initBtnConnectState(boolean isConnected) {
		btnSendVolume.setEnabled(isConnected);
		btnPlayMusic.setEnabled(isConnected);
		btnSendColor.setEnabled(isConnected);
		btnSendBrightness.setEnabled(isConnected);
		btnCloseLight.setEnabled(isConnected);
		btnSave.setEnabled(isConnected);
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
		if (v == tvMusic) {
			Intent intent = new Intent(mActivity, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_SLEEPAID_MUSIC);
			intent.putExtra("musicId", music.getMusicID());
			startActivityForResult(intent, 100);
		} else if (v == vSleepAidTime) {
			valueDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_last_time), getString(R.string.confirm), null);
			valueDialog.setDefaultValue((byte) aidInfo.getAidStopDuration());
			valueDialog.show();
		}

		else if (v == btnSendColor) {
			if (Utils.inputTips(etR, 255)) {
				return;
			}
			if (Utils.inputTips(etG, 120)) {
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
			aidInfo.setLight(light);
//			aidInfo.setLightOpenFlag((byte) 1);
			aidInfo.setBrightness(brightness);
			sleepAidConfigSet(aidInfo);

		} else if (v == btnSendBrightness) {
			if (Utils.inputTips(etBrightness, 100)) {
				return;
			}
			
			if (Utils.inputTips(etR, 255)) {
				return;
			}
			if (Utils.inputTips(etG, 120)) {
				return;
			}
			if (Utils.inputTips(etB, 255)) {
				return;
			}
			if (Utils.inputTips(etW, 255)) {
				return;
			}
			
			String strBrightness = etBrightness.getText().toString();
			byte brightness = (byte) (int) Integer.valueOf(strBrightness);

			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			byte r = (byte) (int) Integer.valueOf(strR);
			byte g = (byte) (int) Integer.valueOf(strG);
			byte b = (byte) (int) Integer.valueOf(strB);
			byte w = (byte) (int) Integer.valueOf(strW);

			SLPLight light = new SLPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);
			aidInfo.setLight(light);
//			aidInfo.setLightOpenFlag((byte) 1);
			aidInfo.setBrightness(brightness);
			sleepAidConfigSet(aidInfo);
		} else if (v == btnCloseLight) {
			aidInfo.setLightOpenFlag((byte) 0);
			sleepAidConfigSet(aidInfo);
		} else if (v == btnSave) {

			if (Utils.inputTips(etVolume, 16)) {
				return;
			}

			if (Utils.inputTips(etR, 255)) {
				return;
			}
			if (Utils.inputTips(etG, 120)) {
				return;
			}
			if (Utils.inputTips(etB, 255)) {
				return;
			}
			if (Utils.inputTips(etW, 255)) {
				return;
			}

			if (Utils.inputTips(etBrightness, 100)) {
				return;
			}

			String strVolume = etVolume.getText().toString();
			byte volume = (byte) (int) Integer.valueOf(strVolume);
			aidInfo.setVolume(volume);

			String strR = etR.getText().toString();
			String strG = etG.getText().toString();
			String strB = etB.getText().toString();
			String strW = etW.getText().toString();
			String strBrightness = etBrightness.getText().toString();

			byte r = (byte) (int) Integer.valueOf(strR);
			byte g = (byte) (int) Integer.valueOf(strG);
			byte b = (byte) (int) Integer.valueOf(strB);
			byte w = (byte) (int) Integer.valueOf(strW);
			byte brightness = (byte) (int) Integer.valueOf(strBrightness);

			SLPLight light = new SLPLight();
			light.setR(r);
			light.setG(g);
			light.setB(b);
			light.setW(w);

			aidInfo.setMusicOpenFlag((byte) (volume > 0 ? 1 : 0));
			aidInfo.setLightOpenFlag((byte) (brightness > 0 ? 1: 0));
			aidInfo.setLight(light);
			aidInfo.setBrightness(brightness);

			LogUtil.log(TAG + " sleepAidConfig:" + aidInfo);
			sleepAidConfigSet(aidInfo);
		}

		else if (v == btnSendVolume) {
			if (Utils.inputTips(etVolume, 16)) {
				return;
			}

			String strVolume = etVolume.getText().toString();
			byte volume = (byte) (int) Integer.valueOf(strVolume);
			aidInfo.setVolume(volume);
			sleepAidConfigSet(aidInfo);
		}

		else if (v == btnPlayMusic) {
			if (!playing) {
				playMusic();
			} else {
				aidInfo.setMusicOpenFlag((byte) 0);
				getDeviceHelper().sleepAidConfig(MainActivity.deviceId, aidInfo, 3000, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						if (!isAdded()) {
							return;
						}

						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mActivity.hideLoading();
								if (cd.isSuccess()) {
									playing = false;
									initMusicButtonStatus();
								}else{
									showErrMsg(cd);
								}
							}
						});
					}
				});
			}
		}
	}

	private void sleepAidConfigSet(final BleNoxAidInfo aidInfo) {
		mActivity.hideLoading();
		getDeviceHelper().sleepAidConfig(MainActivity.deviceId, aidInfo, 3000, new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				if (!isAdded()) {
					return;
				}

				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.hideLoading();
						if (cd.isSuccess()) {
							Toast.makeText(mActivity, R.string.save_succeed, Toast.LENGTH_SHORT).show();
							playing = aidInfo.getMusicOpenFlag() == 1;
							initMusicButtonStatus();
						} else {
							mActivity.showErrTips(cd);
						}
					}
				});
			}
		});
	}

	private void playMusic() {

		if (Utils.inputTips(etVolume, 16)) {
			return;
		}

		String strVolume = etVolume.getText().toString();
		byte volume = (byte) (int) Integer.valueOf(strVolume);
		aidInfo.setVolume(volume);
		aidInfo.setMusicId((short) music.getMusicID());
		aidInfo.setMusicOpenFlag((byte) 1);
		getDeviceHelper().sleepAidConfig(MainActivity.deviceId, aidInfo, 3000, new IResultCallback() {
			@Override
			public void onResultCallback(final CallbackData cd) {
				// TODO Auto-generated method stub
				if (!isAdded()) {
					return;
				}

				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.hideLoading();
						if (cd.isSuccess()) {
							playing = true;
							initMusicButtonStatus();
						} else {
							mActivity.showErrTips(cd);
						}
					}
				});
			}
		});


	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			int musicId = data.getIntExtra("musicId", 0);
			music.setMusicID(musicId);
			music.setMusicName(getString(Utils.getSleepAidMusicName(musicId)));
			initMusicView();
			if (playing) {
				playMusic();
			}
		}
	}

}
