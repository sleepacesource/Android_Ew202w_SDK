package com.sdkEw202w.demo.fragment;

import java.lang.reflect.Field;

import com.sdkEw202w.demo.AlarmListActivity;
import com.sdkEw202w.demo.ClockSleepActivity;
import com.sdkEw202w.demo.EditAlarmActivity;
import com.sdkEw202w.demo.MainActivity;
import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.util.TimeUtill;
import com.sdkEw202w.demo.view.CommonDialog;
import com.sdkEw202w.demo.wheelview.NumericWheelAdapter;
import com.sdkEw202w.demo.wheelview.OnItemSelectedListener;
import com.sdkEw202w.demo.wheelview.WheelView;
import com.sleepace.sdk.core.nox.domain.BleNoxWorkStatus;
import com.sleepace.sdk.core.nox.domain.ClockDormancyBean;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.StringUtil;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SettingFragment extends BaseFragment {
	private View maskView;
	private View vAlarm;
	private Button btnRestore;
	private RelativeLayout mRelayoutTimeStyle, mRelayoutClockDormancy;
	private ClockDormancyBean clockDormancyBean;
	private BleNoxWorkStatus bleNoxWorkStatus = new BleNoxWorkStatus();;
	private CheckBox cbSynTime;
	public static final int CODE_REQUEST_CLOCK_SLEEP = 10001;
	private TextView mTvDormancyTime, mTvTimeDetail;
	private int[] timeStyle = new int[] { 12, 24 };// 时间制式
	private WheelView mWv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_setting, null);
		// LogUtil.log(TAG + " onCreateView-----------");
		findView(view);
		initListener();
		initUI();

		return view;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		vAlarm = root.findViewById(R.id.tv_alarm);
		mRelayoutTimeStyle = (RelativeLayout) root.findViewById(R.id.rl_time_style);
		mRelayoutClockDormancy = (RelativeLayout) root.findViewById(R.id.rl_clock_dormancy);
		cbSynTime = (CheckBox) root.findViewById(R.id.cb_syn_time);
		btnRestore = (Button) root.findViewById(R.id.btn_restore);
		mTvDormancyTime = (TextView) root.findViewById(R.id.tv_dormancy_time);
		mTvTimeDetail = (TextView) root.findViewById(R.id.tv_time_detail);
	}

	protected void initData() {
//		clockDormancyBean = new ClockDormancyBean();
//		clockDormancyBean.setStartHour(12);
//		clockDormancyBean.setStartMin(30);
//		clockDormancyBean.setEndHour(12);
//		clockDormancyBean.setEndMin(30);
//		clockDormancyBean.setFlag(1);
		if (bleNoxWorkStatus != null) {
			cbSynTime.setChecked(bleNoxWorkStatus.getClockSynSwitch() == 1);
			// 时间制式
			byte timeSys = bleNoxWorkStatus.getTimeFormat();// 0:12小时制，1，24小时制
			SdkLog.log(TAG, "时间制式:" + timeSys);
			updateTimeSys(timeSys);
		}

	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		vAlarm.setOnClickListener(this);
		mRelayoutTimeStyle.setOnClickListener(this);
		btnRestore.setOnClickListener(this);
		cbSynTime.setOnCheckedChangeListener(onCheckedChangeListener);
		mRelayoutClockDormancy.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getDeviceHelper().isConnected();
		initPageState(isConnected);
		initWorkMode();
		clockDormancyGet();
	}

	private void initWorkMode() {
		getDeviceHelper().getWorkStatus(MainActivity.deviceId, 3000, new IResultCallback<BleNoxWorkStatus>() {

			@Override
			public void onResultCallback(final CallbackData<BleNoxWorkStatus> cd) {
				// TODO Auto-generated method stub
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						SdkLog.log(TAG, "工作模式回調結果:" + cd);
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (cd.isSuccess()) {
									bleNoxWorkStatus = cd.getResult();
									initData();
								}else{							
									showErrMsg(cd);
									
								}

							}
						});
					}
				});
			}
		});
	}
	
	
	private void clockDormancyGet() {
		mHelper.clockDormancyGet(MainActivity.deviceId, 3000, new IResultCallback<ClockDormancyBean>() {

			@Override
			public void onResultCallback(final CallbackData<ClockDormancyBean> cd) {
				// TODO Auto-generated method stub
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (cd.isSuccess()) {
							clockDormancyBean = cd.getResult();
							SdkLog.log(TAG, "----clockDormancyBean---:" + clockDormancyBean);
							if (clockDormancyBean != null) {
							
								setClockSleepTime(clockDormancyBean);
							} else{
								String time = getResources().getString(R.string.close1);
								mTvDormancyTime.setText(time);
							}
						} 
					}
				});
			}
		});
	}

	private void initPageState(boolean isConnected) {
		isConnected = true;
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}

	private void initBtnConnectState(boolean isConnected) {
		btnRestore.setEnabled(isConnected);
	}

	private void setPageEnable(boolean enable) {
		if (enable) {
			maskView.setVisibility(View.GONE);
		} else {
			maskView.setVisibility(View.VISIBLE);
		}
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.setting);

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
					btnRestore.setEnabled(state == CONNECTION_STATE.CONNECTED);
				}
			});
		}
	};

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView == cbSynTime) {
//	            Object tag = buttonView.getTag();
	            SdkLog.log(TAG + " onCheckedChanged isChecked:" + isChecked );
//	            if (tag == null) {//说明不需要回调
//	                buttonView.setTag(TAG);
//	                return;
//	            }
				bleNoxWorkStatus.setClockSynSwitch(isChecked ? (byte) 1 : 0);
				byte vaue = isChecked ? (byte) 1 : 0;
				mHelper.synTimeSwitch(MainActivity.deviceId, vaue, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {

							}
						});
					}
				});
			}
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
		if (v == vAlarm) {

			Intent intent = new Intent(mActivity, AlarmListActivity.class);
			startActivity(intent);
		} else if (v == btnRestore) {
			getDeviceHelper().deviceInit(MainActivity.deviceId, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(CallbackData cd) {
					// TODO Auto-generated method stub
					if(!cd.isSuccess()){
						showErrMsg(cd);
					}
					
				}
			});
		} else if (v == mRelayoutTimeStyle) {
			selectTimeStyle();
		} else if (v == mRelayoutClockDormancy) {
			Intent intentClockSleep = new Intent(mActivity, ClockSleepActivity.class);
			// intentClockSleep.putExtra("extra_device", mDevice);
			startActivityForResult(intentClockSleep, CODE_REQUEST_CLOCK_SLEEP);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == mActivity.RESULT_OK) {
			if (requestCode == CODE_REQUEST_CLOCK_SLEEP) {
				ClockDormancyBean clockSleep = (ClockDormancyBean) data.getSerializableExtra(ClockSleepActivity.EXTRA_CLOCK_SLEEP);
				if (clockSleep != null) {
					setClockSleepTime(clockSleep);
				}
			}
		}
	}

	private void setClockSleepTime(ClockDormancyBean clockDormancyBean) {
		String time = getResources().getString(R.string.close1);
		/*
		 * if (!noxClockSleep.isClockSleep) { return; }
		 */
		if (clockDormancyBean.getFlag() == 1) {
			if (TimeUtill.HourIs24(mActivity)) {
				time = TimeUtill.formatMinute(clockDormancyBean.getStartHour(), clockDormancyBean.getStartMin()) + "~"
				        + TimeUtill.formatMinute(clockDormancyBean.getEndHour(), clockDormancyBean.getEndMin());
			} else {
				String sTimeUnit = "", eTimeUnit = "";
				sTimeUnit = TimeUtill.isAM(clockDormancyBean.getStartHour(), clockDormancyBean.getStartMin()) ? getString(R.string.am)
				        : getString(R.string.pm);
				eTimeUnit = TimeUtill.isAM(clockDormancyBean.getEndHour(), clockDormancyBean.getEndMin()) ? getString(R.string.am)
				        : getString(R.string.pm);

				time = TimeUtill.getHour12(clockDormancyBean.getStartHour()) + ":"
				        + StringUtil.DF_2.format(clockDormancyBean.getStartMin()) + sTimeUnit + "-"
				        + TimeUtill.getHour12(clockDormancyBean.getEndHour()) + ":" + StringUtil.DF_2.format(clockDormancyBean.getEndMin())
				        + eTimeUnit;
			}
		}
		mTvDormancyTime.setText(time);
	}

	/*
	 * 更新时间制式，0,12小时制，1,24小时制
	 */
	private void updateTimeSys(byte timeSys) {
		if (timeSys == 0) {
			mTvTimeDetail.setText(R.string.time_format_12);
		} else {
			mTvTimeDetail.setText(R.string.time_format_24);
		}
	}

	private void selectTimeStyle() {

		CommonDialog dialog = new CommonDialog(mActivity, R.style.DateDialog, R.layout.dialog_time_frame_select, 1.0f);
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
		window.setWindowAnimations(R.style.common_dialog_style); // 添加动画
		dialog.setMethod(new CommonDialog.Method() {
			@Override
			public void setView(final CommonDialog dialog, View view) {

				mWv = (WheelView) view.findViewById(R.id.dialog_scene_sleephelper_phone_wv_time);
				mWv.setCurrentItem(bleNoxWorkStatus.getTimeFormat());
				view.findViewById(R.id.rl_dialog).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				view.findViewById(R.id.dialog_scene_sleephelper_phone_tv_operation_cancel).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

				view.findViewById(R.id.dialog_scene_sleephelper_phone_tv_operation_complete).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.dismiss();
						int currentIndex = mWv.getCurrentItem();
						final byte currentTimeSys = (byte) (currentIndex == 12 ? 0 : 1);
						SdkLog.log(TAG, "currentTimeSys:" + currentTimeSys);
						// if (bleNoxWorkStatus.getTimeFormat() !=
						// currentTimeSys) {

						mHelper.timerSysSetting(MainActivity.deviceId, currentTimeSys, new IResultCallback() {

							@Override
							public void onResultCallback(final CallbackData cd) {
								// TODO Auto-generated method stub
								mActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										if (cd.isSuccess()) {
											mWv.setCurrentItem(currentTimeSys);
											SdkLog.log(TAG, "更新时间制式成功:" + currentTimeSys);
											updateTimeSys(currentTimeSys);
										} else {
											SdkLog.log(TAG, "更新时间制式失败:" + currentTimeSys);
											mWv.setCurrentItem(currentTimeSys);
											updateTimeSys(currentTimeSys);

										}
									}
								});

							}
						});

						// }

					}
				});

				mWv.setType(WheelView.WHEELVIEW_TIME_STYLE);
				mWv.setAdapter(new NumericWheelAdapter(timeStyle, 0));
				mWv.setTextSize(20);
				mWv.setCyclic(false);
				mWv.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(int index) {

					}
				});

			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	@Override
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
