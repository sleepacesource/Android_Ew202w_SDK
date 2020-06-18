package com.sdkEw202w.demo;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sdkEw202w.demo.util.TimeUtill;
import com.sdkEw202w.demo.wheelview.NumericWheelAdapter;
import com.sdkEw202w.demo.wheelview.OnItemSelectedListener;
import com.sdkEw202w.demo.wheelview.WheelAdapter;
import com.sdkEw202w.demo.wheelview.WheelView;
import com.sleepace.sdk.util.StringUtil;

public class SelectStartEndTimeActivity extends BaseActivity {

	private View startTimeView, endTimeView;
	private TextView tvStartTime, tvEndTime, tvTips;
	private WheelView wvHour, wvMinute, wvAPM;
	private byte sHour, sMinute, eHour, eMinute, sapm, eapm;
	private int lastPosition;
	private String[] hourItems;
	private String[] minuteItems = new String[60];
	private boolean is24;
	int newStartItem;
	int newEndItem;
	public static final int AM = 10000;
	public static final int PM = 10001;
	private int selectTime = 0;
	private static final int START_TIME = 0;
	private static final int END_TIME = 1;
	private int CURRENT_TIME = START_TIME;

	public static final String EXTRA_START_HOUR = "startHour";
	public static final String EXTRA_START_MINUTE = "startMinute";
	public static final String EXTRA_END_HOUR = "endHour";
	public static final String EXTRA_END_MINUTE = "endMinute";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_startend_time);
		findView();
		initUI();
		initListener();

	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		super.findView();

		startTimeView = findViewById(R.id.layout_start_time);
		endTimeView = findViewById(R.id.layout_end_time);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		tvTips = (TextView) findViewById(R.id.tv_tips_set_sleep_time);
		wvHour = (WheelView) findViewById(R.id.hour);
		wvMinute = (WheelView) findViewById(R.id.minute);
		wvAPM = (WheelView) findViewById(R.id.apm);

	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		super.initUI();

		sHour = (byte) 23;
		sMinute = (byte) 0;
		eHour = (byte) 6;
		eMinute = (byte) 0;
		sapm = (byte) (TimeUtill.isAM(sHour, sMinute) ? Calendar.AM : Calendar.PM);
		eapm = (byte) (TimeUtill.isAM(eHour, eMinute) ? Calendar.AM : Calendar.PM);
		lastPosition = sHour;
		lastPosition = sHour;

		tvTitle.setText(getString(R.string.setTime));

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

		if (TimeUtill.HourIs24(this)) {
			wvHour.setAdapter(new NumericWheelAdapter(0, 23));
		} else {
			wvHour.setAdapter(new NumericWheelAdapter(1, 12));
		}

		initWheelView();

		setStartTimeText();
		setEndTimeText();
		setWheelViewText(sHour, sMinute);
	}

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

	private void setAPMTime(int type, int index) {

		setNewItem(type);

		boolean isAM = index == 10000 ? true : false;
		setTime(CURRENT_TIME, isAM);
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

	public int[] getAPM() {
		int[] data = new int[] { AM, PM };
		return data;
	}

	private void setStartTimeText() {
		if (TimeUtill.HourIs24(this)) {
			tvStartTime.setText(StringUtil.DF_2.format(sHour) + ":" + StringUtil.DF_2.format(sMinute));
		} else {
			tvStartTime.setText(StringUtil.DF_2.format(TimeUtill.getHour12(sHour)) + ":" + StringUtil.DF_2.format(sMinute)
			        + (sHour < 12 ? getString(R.string.am) : getString(R.string.pm)));
		}
	}

	private void setEndTimeText() {
		if (TimeUtill.HourIs24(this)) {
			tvEndTime.setText(StringUtil.DF_2.format(eHour) + ":" + StringUtil.DF_2.format(eMinute));
		} else {
			tvEndTime.setText(StringUtil.DF_2.format(TimeUtill.getHour12(eHour)) + ":" + StringUtil.DF_2.format(eMinute)
			        + (eHour < 12 ? getString(R.string.am) : getString(R.string.pm)));
		}
	}

	@Override
	public void onClick(View v) {
		if (v == startTimeView) {
			CURRENT_TIME = START_TIME;
			if (v.getTag() == null) {
				v.setTag("checked");
				endTimeView.setTag(null);
				// v.setBackgroundResource(R.drawable.clock_sleep_background);
				// endTimeView.setBackgroundResource(R.color.COLOR_8);
				selectTime = 0;
				setWheelViewText(sHour, sMinute);
			}
		} else if (v == endTimeView) {
			CURRENT_TIME = END_TIME;
			if (v.getTag() == null) {
				v.setTag("checked");
				startTimeView.setTag(null);
				// v.setBackgroundResource(R.drawable.clock_sleep_background);
				// startTimeView.setBackgroundResource(R.color.COLOR_8);
				selectTime = 1;
				setWheelViewText(eHour, eMinute);
			}
		}
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		startTimeView.setOnClickListener(this);
		endTimeView.setOnClickListener(this);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				data.putExtra(EXTRA_START_HOUR, sHour);
				data.putExtra(EXTRA_START_MINUTE, sMinute);
				data.putExtra(EXTRA_END_HOUR, eHour);
				data.putExtra(EXTRA_END_MINUTE, eMinute);
				setResult(RESULT_OK, data);
				finish();

			}
		});

	}

}
