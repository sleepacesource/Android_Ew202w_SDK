package com.sdkEw202w.demo.fragment;

import java.lang.reflect.Field;
import java.util.List;

import com.sdkEw202w.demo.DemoApp;
import com.sdkEw202w.demo.MainActivity;
import com.sdkEw202w.demo.R;
import com.sleepace.sdk.core.nox.domain.SLPTimeInfo;
import com.sleepace.sdk.core.nox.util.Constants;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.TimeUtil;
import com.sleepace.sdk.wifidevice.bean.DeviceInfo;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeviceFragment extends BaseFragment {
	private Button btnConnectDevice, btnUpgrade,btnBindDevice,btnUnbindDevice;
	private boolean upgrading = false;
	private EditText etDeviceId, etToken, etServer, etVersion, etChannelId, etPlatForm;
	private SharedPreferences mSp;
	private String SP_CHANNEL_ID = "sp_channel_id";
	private String SP_TOKEN = "sp_token";
	private String SP_SERVER_HOST = "sp_server_host";
	private String SP_DEVECI_ID = "sp_device_id";
	Handler handler = new Handler();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_device, null);
		// LogUtil.log(TAG+" onCreateView-----------");
		findView(root);
		initData();
		initListener();
		initUI();

		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		btnConnectDevice = (Button) root.findViewById(R.id.btn_connect_device);
		btnUpgrade = (Button) root.findViewById(R.id.btn_upgrade_fireware);
		etDeviceId = (EditText) root.findViewById(R.id.et_deviceId);
		etToken = (EditText) root.findViewById(R.id.et_token);
		etServer = (EditText) root.findViewById(R.id.et_server);
		etVersion = (EditText) root.findViewById(R.id.et_version);

		etChannelId = (EditText) root.findViewById(R.id.et_channel_id);
		etPlatForm = (EditText) root.findViewById(R.id.et_plat_form);
		etVersion.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		btnBindDevice = (Button) root.findViewById(R.id.btn_bind_device);
		btnUnbindDevice = (Button) root.findViewById(R.id.btn_unbind_device);
		btnBindDevice.setVisibility(View.VISIBLE);
		btnUnbindDevice.setVisibility(View.VISIBLE);
	}

	private void initData() {
		mSp = mActivity.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
		String channelId = mSp.getString(SP_CHANNEL_ID, "");
		String token = mSp.getString(SP_TOKEN, "");
		String serverHost = mSp.getString(SP_SERVER_HOST, "");
		String deviceId = mSp.getString(SP_DEVECI_ID, "");
		if (TextUtils.isEmpty(channelId)) {
			etChannelId.setText(DemoApp.CHNANEL_ID);
			etPlatForm.setText(DemoApp.THIED_PLATFORM);
		} else {
			etPlatForm.setText(channelId);
			etChannelId.setText(channelId);
		}

		if (TextUtils.isEmpty(token)) {
			etToken.setText(DemoApp.TOKEN);
		} else {

			etToken.setText(token);
		}

		if (TextUtils.isEmpty(serverHost)) {
			etServer.setText(DemoApp.SERVER_HOST);
		} else {

			etServer.setText(serverHost);
		}

		if (TextUtils.isEmpty(deviceId)) {
			etDeviceId.setText("akclsxdsyi8m9");
		} else {
			etDeviceId.setText(deviceId);
			MainActivity.deviceId = deviceId;
		}

		// etPlatForm.setText(DemoApp.THIED_PLATFORM);
		// etToken.setText(DemoApp.TOKEN);
		// etServer.setText(DemoApp.SERVER_HOST);
		// etDeviceId.setText("EW22W20C00044");

	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		btnConnectDevice.setOnClickListener(this);
		btnUpgrade.setOnClickListener(this);
		etDeviceId.addTextChangedListener(deviceIdWatcher);
		etVersion.addTextChangedListener(versionWatcher);
		btnUnbindDevice.setOnClickListener(this);
		btnBindDevice.setOnClickListener(this);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.device);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getDeviceHelper().isConnected();

		SdkLog.log(TAG + "设备连接状态:" + isConnected);
		initPageState(isConnected);
	}

	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}

	private void initBtnConnectState(boolean isConnected) {
		if (isConnected) {
			btnConnectDevice.setText(R.string.disconnect);
			btnConnectDevice.setTag("disconnect");
		} else {
			btnConnectDevice.setText(R.string.connect_server);
			btnConnectDevice.setTag("connect");
		}
	}

	private void setPageEnable(boolean enable) {
		String version = etVersion.getText().toString();
		if (enable && !TextUtils.isEmpty(version)) {
			btnUpgrade.setEnabled(true);
		} else {
			btnUpgrade.setEnabled(false);
		}
		SdkLog.log(TAG, "--setPageEnable--："+enable);
		btnBindDevice.setEnabled(enable);
		btnUnbindDevice.setEnabled(enable);
	}

	private TextWatcher versionWatcher = new TextWatcher() {

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
			setPageEnable(getDeviceHelper().isConnected());
		}
	};

	private TextWatcher deviceIdWatcher = new TextWatcher() {

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
			MainActivity.deviceId = str;
		}
	};

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			SdkLog.log(TAG + "===onStateChanged===:" + state);
			if (!isAdded()) {
				return;
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initPageState(state == CONNECTION_STATE.CONNECTED);

					if (state == CONNECTION_STATE.DISCONNECT) {
						if (upgrading) {
							upgrading = false;
							mActivity.hideUpgradeDialog();
							mActivity.setUpgradeProgress(0);
						}

					} else if (state == CONNECTION_STATE.CONNECTED) {

						if (upgrading) {
							upgrading = false;
							btnUpgrade.setEnabled(true);
							mActivity.hideUpgradeDialog();
						}

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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnConnectDevice) {
			Object tag = v.getTag();
			if (tag == null || "connect".equals(tag)) {
				final String deviceId = etDeviceId.getText().toString();
				final String tokenStr = etToken.getText().toString();
				final String serverHost = etServer.getText().toString();
				final String channelId = etChannelId.getText().toString();
				if (TextUtils.isEmpty(serverHost) || TextUtils.isEmpty(tokenStr) || TextUtils.isEmpty(channelId)) {
					Toast.makeText(mActivity, "params error", Toast.LENGTH_SHORT).show();
					return;
				}
				showLoading();
				MainActivity.deviceId=deviceId;
				mSp.edit().putString(SP_CHANNEL_ID, channelId).commit();
				mSp.edit().putString(SP_DEVECI_ID, deviceId).commit();
				mSp.edit().putString(SP_SERVER_HOST, serverHost).commit();
				mSp.edit().putString(SP_TOKEN, tokenStr).commit();
				mHelper.authentication(tokenStr, channelId, serverHost,new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						SdkLog.log(TAG+" authentication----------"+ cd);
						mActivity.runOnUiThread(new Runnable() {
							public void run() {
								hideLoading();
								if (!cd.isSuccess()) {
									if (cd.getResult() != null) {
										Toast.makeText(mActivity, getString(R.string.device_connect_fail), Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(mActivity, getString(R.string.connection_succeeded), Toast.LENGTH_SHORT).show();
									mHelper.getDeviceList(new IResultCallback<List<DeviceInfo>>() {
										@Override
										public void onResultCallback(CallbackData<List<DeviceInfo>> cd) {
											// TODO Auto-generated method stub
											SdkLog.log(TAG+" getDeviceList----------"+ cd);
											if(cd.isSuccess() && cd.getResult() != null && cd.getResult().size() > 0) {
												for(DeviceInfo info : cd.getResult()) {
													if(info.getDeviceType() == DeviceType.DEVICE_TYPE_EW202W.getType()) {
														MainActivity.deviceId = info.getDeviceId();
														etDeviceId.setText(info.getDeviceId());
//														mHelper.queryDeviceOnlineState(DeviceType.DEVICE_TYPE_EW202W.getType(), info.getDeviceId(), new IResultCallback<Byte>() {
//															@Override
//															public void onResultCallback(CallbackData<Byte> cd) {
//																// TODO Auto-generated method stub
//																SdkLog.log(TAG+" queryDeviceOnlineState----------"+ cd);
//															}
//														});
														
//														SLPTimeInfo time = new SLPTimeInfo();
//														time.setTimestamp((int) (System.currentTimeMillis()/1000));
//														time.setTimezone(TimeUtil.getTimeZoneSecond());
//														time.setTimeStyle((byte)12);
//														mHelper.syncTime(deviceId, time, new IResultCallback() {
//															@Override
//															public void onResultCallback(CallbackData cd) {
//																// TODO Auto-generated method stub
//																SdkLog.log(TAG+" syncTime----1------"+ cd);
//															}
//														});
														break;
													}
												}
											}
										}
									});
								}
							}
						});
					}
				});
			} else {// 断开设备
				getDeviceHelper().disconnect();
			}
		} else if (v == btnUpgrade) {
			String version = etVersion.getText().toString();
			
			if (TextUtils.isEmpty(version)) {
				return;
			}
			// showLoading();
			mActivity.showUpgradeDialog();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(!upgrading){//timeout
				        mActivity.hideUpgradeDialog();
				        Toast.makeText(mActivity, getString(R.string.up_failed), Toast.LENGTH_SHORT).show();
					}
				}
			}, 25000);
			version=version.replace(".", "");
			short deviceVersion=Short.valueOf(version);
			SdkLog.log(TAG, "固件升级：" + MainActivity.deviceId + "==deviceVersion==:" + deviceVersion);
			mHelper.deviceUpgrade(MainActivity.deviceId, deviceVersion, DeviceType.DEVICE_TYPE_EW202W.getType(), 2,
			        new IResultCallback<Integer>() {
				        @Override
				        public void onResultCallback(final CallbackData cd) {
					        // TODO Auto-generated method stub
					        SdkLog.log(TAG, "升级回调：" + cd);
					        upgrading=true;
					        mActivity.runOnUiThread(new Runnable() {

						        @Override
						        public void run() {

							        // TODO Auto-generated method stub
							        if (cd.isSuccess()) {
								        if (cd.getResult() != null) {
									        int porgress = (Integer) cd.getResult();
									        mActivity.setUpgradeProgress(porgress);
									        if (porgress == 100) {
										        mActivity.hideUpgradeDialog();
										        Toast.makeText(mActivity, getString(R.string.up_success), Toast.LENGTH_SHORT).show();
									        }
								        }

							        } else {
							        	mActivity.hideUpgradeDialog();
								        Toast.makeText(mActivity, getString(R.string.up_failed), Toast.LENGTH_SHORT).show();
							        }

						        }
					        });

				        }
			        });

		}else if(v==btnBindDevice){
			
			final String deviceId = etDeviceId.getText().toString();

			if (TextUtils.isEmpty(deviceId)) {
				
				Toast.makeText(mActivity, getString(R.string.id_cipher), Toast.LENGTH_SHORT).show();
				return;
			}
			
			mSp.edit().putString(SP_DEVECI_ID, deviceId).commit();
			
			mHelper.bindDevice(deviceId, new IResultCallback() {

				@Override
                public void onResultCallback(final CallbackData cd) {
	                // TODO Auto-generated method stub
	                SdkLog.log(TAG, "--bindDevice--:"+cd);
	                mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(cd.isSuccess()){
						        Toast.makeText(mActivity, getString(R.string.bind_account_success), Toast.LENGTH_SHORT).show();
							}else{
						        Toast.makeText(mActivity, getString(R.string.bind_fail), Toast.LENGTH_SHORT).show();
							}
						}
					});
                }
			});
			
		}else if(v==btnUnbindDevice){
			final String deviceId = etDeviceId.getText().toString();
			if (TextUtils.isEmpty(deviceId)) {
				
				return;
			}
			mSp.edit().putString(SP_DEVECI_ID, deviceId).commit();
			
			mHelper.unBindDevice(deviceId, new IResultCallback() {

				@Override
                public void onResultCallback(final CallbackData cd) {
	                // TODO Auto-generated method stub
					   SdkLog.log(TAG, "--unBindDevice--:"+cd);
				          mActivity.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(cd.isSuccess()){
								        Toast.makeText(mActivity, getString(R.string.unbind_success), Toast.LENGTH_SHORT).show();
									}else{
								        Toast.makeText(mActivity, getString(R.string.unbind_fail), Toast.LENGTH_SHORT).show();
									}
								}
							});
                }
			});
		}
	}

	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
