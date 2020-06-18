package com.sdkEw202w.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.Calendar;

import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.util.TimeUtill;
import com.sdkEw202w.demo.wheelview.NumericWheelAdapter;
import com.sdkEw202w.demo.wheelview.OnItemSelectedListener;
import com.sdkEw202w.demo.wheelview.WheelView;
import com.sleepace.sdk.core.nox.domain.ClockDormancyBean;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.StringUtil;

/**
 * ClockSleep activity
 */
public class ClockSleepActivity extends BaseActivity {

	private View startTimeView, endTimeView;
	private TextView tvStartTime, tvEndTime, tvTips;
	private WheelView wvHour, wvMinute, wvAPM;
	private CheckBox clock_sleep_switch;
	private TextView tv_clock_sleep_tips;
	private LinearLayout layout_time;
	private RelativeLayout mRlWheelView;
	public static final String EXTRA_CLOCK_SLEEP = "CLOCK_SLEEP_EXTRA";
	public static final int MSG_TYPE_SAVE_CLOCK_SLEEP = 0x11;// 保存时钟休眠
	public static final int MSG_TYPE_SAVE_CLOCK_SLEEP_FAIL = 0x12;// 保存时钟休眠的时候连接失败

	private String[] hourItems;
	private String[] minuteItems = new String[60];

	private int selectTime = 0;
	private int sHour, sMinute, eHour, eMinute;

	ClockDormancyBean clockDormancyBean;

	private ClockDormancyBean mInitClockDormancyBean;

	/**
	 * 初始化
	 */
	int[] hourData;
	int[] minData;

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock_sleep);
		initData();
		findView();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		clockDormancyGet();

	}

	private void initData() {
		if (TimeUtill.HourIs24(this)) {
			hourData = new int[24];
			for (int i = 0; i < 24; i++) {
				hourData[i] = i;
			}
		} else {
			hourData = new int[12];
			for (int i = 1; i < 12; i++) {
				hourData[i] = i;
			}
		}
		minData = new int[60];
	}

	public void findView() {
		super.findView();
		startTimeView = findViewById(R.id.layout_start_time);
		endTimeView = findViewById(R.id.layout_end_time);
		layout_time = (LinearLayout) findViewById(R.id.layout_time);
		clock_sleep_switch = (CheckBox) findViewById(R.id.clock_sleep_switch);
		tv_clock_sleep_tips = (TextView) findViewById(R.id.tv_clock_sleep_tips);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		tvTips = (TextView) findViewById(R.id.tv_tips_set_sleep_time);
		wvHour = (WheelView) findViewById(R.id.hour);
		wvMinute = (WheelView) findViewById(R.id.minute);
		wvAPM = (WheelView) findViewById(R.id.apm);
		mRlWheelView = (RelativeLayout) findViewById(R.id.body);

		if (!TimeUtill.HourIs24(this)) {
			wvHour.setAdapter(new NumericWheelAdapter(1, 12));
		} else {
			wvHour.setAdapter(new NumericWheelAdapter(0, 23));
		}
		tvRight.setText(getString(R.string.save));
		tvTitle.setText(getString(R.string.clock_sleep));
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
								initUI(clockDormancyBean);
							} else {
								clockDormancyBean = new ClockDormancyBean();
								clockDormancyBean.setStartHour(22);
								clockDormancyBean.setStartMin(0);
								clockDormancyBean.setEndHour(7);
								clockDormancyBean.setEndMin(0);
								clockDormancyBean.setFlag(0);
								initUI(clockDormancyBean);
							}
						} else {
							clockDormancyBean = new ClockDormancyBean();
							clockDormancyBean.setStartHour(22);
							clockDormancyBean.setStartMin(0);
							clockDormancyBean.setEndHour(7);
							clockDormancyBean.setEndMin(0);
							clockDormancyBean.setFlag(0);
							initUI(clockDormancyBean);
						}
					}
				});
			}
		});
	}

	public void initListener() {
		super.initListener();
		// mHelper.addConnectionStateCallback(stateCallback);
		startTimeView.setOnClickListener(this);
		endTimeView.setOnClickListener(this);
		clock_sleep_switch.setOnCheckedChangeListener(checkChangedListenerImpl);
		ivBack.setOnClickListener(this);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}
		});
		tvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showLoading();

				mHelper.clockDormancySet(MainActivity.deviceId, clockDormancyBean, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
						if (!ActivityUtil.isActivityAlive(mActivity)) {
							return;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								hideLoading();
								if (cd.isSuccess()) {
									Intent data = new Intent();
									data.putExtra(EXTRA_CLOCK_SLEEP, clockDormancyBean);
									setResult(RESULT_OK, data);
									finish();
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}
		});
	}

	public void initUI(ClockDormancyBean clockDormancyBean) {

		sHour = clockDormancyBean.getStartHour();
		sMinute = clockDormancyBean.getStartMin();
		eHour = clockDormancyBean.getEndHour();
		eMinute = clockDormancyBean.getEndMin();

		if (clockDormancyBean.getFlag() == 1) {
			clock_sleep_switch.setChecked(true);
			tv_clock_sleep_tips.setText(String.format(getString(R.string.device_clock_sleep), getString(R.string.device_name_wakeup)));
			mRlWheelView.setVisibility(View.VISIBLE);
			startTimeView.setVisibility(View.VISIBLE);
			endTimeView.setVisibility(View.VISIBLE);
		} else {
			clock_sleep_switch.setChecked(false);
			tv_clock_sleep_tips.setText(String.format(getString(R.string.device_clock_sleep), getString(R.string.device_name_wakeup)));
			mRlWheelView.setVisibility(View.GONE);
			startTimeView.setVisibility(View.GONE);
			endTimeView.setVisibility(View.GONE);
		}

		if (TimeUtill.HourIs24(this)) {
			hourItems = new String[24];
		} else {
			hourItems = new String[12];
		}

		for (int i = 0; i < minuteItems.length; i++) {
			if (hourItems.length == 24) {
				if (i < hourItems.length) {
					hourItems[i] = StringUtil.DF_2.format(i);
				}
			} else {
				if (i < hourItems.length) {
					hourItems[i] = StringUtil.DF_2.format(i + 1);
				}
			}
			minuteItems[i] = StringUtil.DF_2.format(i);
		}
		initWheelView();
		setStartTimeText();
		setEndTimeText();
		setWheelViewText(sHour, sMinute);
	}

	private OnCheckedChangeListener checkChangedListenerImpl = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			clockDormancyBean.setFlag(isChecked ? 1 : 0);
			if (isChecked) {
				clockDormancyBean.setFlag(1);
				tv_clock_sleep_tips.setText(String.format(getString(R.string.device_clock_sleep), getString(R.string.device_name_wakeup)));
				mRlWheelView.setVisibility(View.VISIBLE);
				startTimeView.setVisibility(View.VISIBLE);
				endTimeView.setVisibility(View.VISIBLE);
			} else {
				clockDormancyBean.setFlag(0);
				tv_clock_sleep_tips.setText(String.format(getString(R.string.device_clock_sleep), getString(R.string.device_name_wakeup)));
				mRlWheelView.setVisibility(View.GONE);
				startTimeView.setVisibility(View.GONE);
				endTimeView.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v == startTimeView) {
			CURRENT_TIME = START_TIME;
			if (v.getTag() == null) {
				v.setTag("checked");
				endTimeView.setTag(null);
				v.setBackgroundResource(R.color.COLOR_8);
				endTimeView.setBackgroundResource(R.color.white);
				selectTime = 0;
				setWheelViewText(sHour, sMinute);
			}
		} else if (v == endTimeView) {
			CURRENT_TIME = END_TIME;
			if (v.getTag() == null) {
				v.setTag("checked");
				startTimeView.setTag(null);
				v.setBackgroundResource(R.color.COLOR_8);
				startTimeView.setBackgroundResource(R.color.white);
				selectTime = 1;
				setWheelViewText(eHour, eMinute);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private void setStartTimeText() {
		if (TimeUtill.HourIs24(this)) {
			tvStartTime.setText(StringUtil.DF_2.format(sHour) + ":" + StringUtil.DF_2.format(sMinute));
		} else {
			tvStartTime.setText(StringUtil.DF_2.format(TimeUtill.getHour12(sHour)) + ":" + StringUtil.DF_2.format(sMinute)
			        + (sHour < 12 ? getString(R.string.am) : getString(R.string.pm)));
		}
		clockDormancyBean.setStartHour(sHour);
		clockDormancyBean.setStartMin(sMinute);

	}

	private void setEndTimeText() {
		if (TimeUtill.HourIs24(this)) {
			tvEndTime.setText(StringUtil.DF_2.format(eHour) + ":" + StringUtil.DF_2.format(eMinute));
		} else {
			tvEndTime.setText(StringUtil.DF_2.format(TimeUtill.getHour12(eHour)) + ":" + StringUtil.DF_2.format(eMinute)
			        + (eHour < 12 ? getString(R.string.am) : getString(R.string.pm)));
		}
		clockDormancyBean.setEndHour(eHour);
		clockDormancyBean.setEndMin(eMinute);
	}

	private void setWheelViewText(int hour, int minute) {
		int apm = TimeUtill.isAM(hour, minute) ? Calendar.AM : Calendar.PM;
		// LogUtil.showMsg(TAG + " setWheelViewText h:" + hour + ",m:" +
		// minute);
		if (TimeUtill.HourIs24(this)) {
			wvAPM.setVisibility(View.GONE);
			wvHour.setCurrentItem(hour);
		} else {
			wvAPM.setVisibility(View.VISIBLE);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.AM_PM, apm);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			int h12 = cal.get(Calendar.HOUR);
			wvHour.setCurrentItem(h12 - 1);
		}
		if (apm == Calendar.AM) {
			wvAPM.setCurrentItem(0);
		} else {
			wvAPM.setCurrentItem(1);
		}
		wvMinute.setCurrentItem(minute);
	}

	private boolean SaveInfos = true;

	// 更换控件部分
	private boolean is24;
	int newStartItem;
	int newEndItem;
	private int lastPosition;
	private static final int START_TIME = 0;
	private static final int END_TIME = 1;
	private int CURRENT_TIME = START_TIME;

	private void initWheelView() {
		int[] data = getAPM();
		is24 = TimeUtill.HourIs24(this);

		wvHour.setTextSize(20);
		wvHour.setCyclic(true);
		wvHour.setOnItemSelectedListener(onHourItemSelectedListener);

		wvMinute.setAdapter(new NumericWheelAdapter(0, 59));
		wvMinute.setTextSize(20);
		wvMinute.setCyclic(true);
		wvMinute.setOnItemSelectedListener(onMiniteItemSelectedListener);

		wvAPM.setAdapter(new NumericWheelAdapter(data, 0));
		wvAPM.setTextSize(20);
		wvAPM.setCyclic(false);
		wvAPM.setOnItemSelectedListener(onAmPmItemSelectedListener);
		if (is24) {
			// 24
			wvHour.setRate(5 / 4.0f);
			wvMinute.setRate(1 / 2.0f);
			wvAPM.setVisibility(View.GONE);
		} else {
			// 12
			wvHour.setRate(1.5f);
			wvMinute.setRate(1.0f);
			wvAPM.setRate(0.5f);
			wvAPM.setVisibility(View.VISIBLE);
		}
	}

	public static final int AM = 10000;
	public static final int PM = 10001;

	public static int[] getAPM() {
		int[] data = new int[] { AM, PM };
		return data;
	}

	// 更新控件快速滑动
	private OnItemSelectedListener onHourItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			setHourTime(CURRENT_TIME, index);
		}
	};

	private OnItemSelectedListener onMiniteItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			setMinuteTime(CURRENT_TIME, index);
		}
	};

	private OnItemSelectedListener onAmPmItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			setAPMTime(CURRENT_TIME, index);
		}
	};

	/**
	 * @param type
	 */
	private void setHourTime(int type, int index) {

		setNewItem(type);
		if (type == START_TIME) {
			if (is24) {
				sHour = (byte) index;
			} else {
				if (newStartItem == 1) {
					if (index == 12) {
						sHour = (byte) index;
					} else {
						sHour = (byte) (index + 12);
					}
				}
				if (newStartItem == 0) {
					if (index == 12) {
						sHour = 0;
					} else {
						sHour = (byte) index;
					}
				}
				sHour = (byte) (sHour % 24);
				lastPosition = index;

			}
			setStartTimeText();
		} else if (type == END_TIME) {
			if (is24) {
				eHour = (byte) index;
			} else {
				eHour = TimeUtill.getHour24(index, eMinute, newEndItem);
				lastPosition = index;
			}
			setEndTimeText();
		}
	}

	/**
	 * @param type
	 */
	private void setMinuteTime(int type, int index) {
		if (type == START_TIME) {
			if (sMinute != index) {
				sMinute = (byte) index;
			}
			setStartTimeText();
		} else if (type == END_TIME) {
			if (eMinute != index) {
				eMinute = (byte) index;
			}
			setEndTimeText();
		}
	}

	/**
	 * @param type
	 */
	private void setAPMTime(int type, int index) {

		setNewItem(type);

		boolean isAM = (index == 10000 || index == 0) ? true : false;

		setTime(CURRENT_TIME, isAM);
	}

	private void setNewItem(int type) {

		if (type == START_TIME) {
			if (sHour > 12 || sHour == 12) {
				newStartItem = 1;
			} else {
				newStartItem = 0;
			}
		} else if (type == END_TIME) {
			if (eHour > 12 || eHour == 12) {
				newEndItem = 1;
			} else {
				newEndItem = 0;
			}
		}
	}

	private void setTime(int type, boolean isAM) {
		if (type == START_TIME) {
			if (!isAM) {
				if (sHour < 12) {
					sHour = (byte) (sHour + 12);
				}
				newStartItem = 1;
			} else {
				if (sHour > 12) {
					sHour = (byte) (sHour - 12);
				} else if (sHour == 12) {
					sHour = 0;
				}
				newStartItem = 0;
			}
			setStartTimeText();
		} else if (type == END_TIME) {
			if (!isAM) {
				if (eHour < 12) {
					eHour = (byte) (eHour + 12);
				}
				newEndItem = 1;
			} else {
				if (eHour > 12) {
					eHour = (byte) (eHour - 12);
				} else if (eHour == 12) {
					eHour = 0;
				}
				newEndItem = 0;
			}
			setEndTimeText();
		}
	}

}
