package com.sdkEw202w.demo;

import java.util.ArrayList;
import java.util.List;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.util.LogUtil;
import com.sdkEw202w.demo.util.Utils;
import com.sleepace.sdk.core.nox.domain.BleNoxAlarmInfo;
import com.sleepace.sdk.core.nox.domain.BleNoxWorkStatus;
import com.sleepace.sdk.ew202w.Ew202wHelper.WorkeModeCallBack;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.ble.BleHelper;
import com.sleepace.sdk.util.SdkLog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmListActivity extends BaseActivity {
	private View dataView;
	private TextView tvTips;
	private ListView listView;
	private LayoutInflater inflater;
	private AlarmAdapter adapter;
	private Button addAlarm;
	private List<BleNoxAlarmInfo> list = new ArrayList<BleNoxAlarmInfo>();
	private List<BleNoxAlarmInfo> listTemp = new ArrayList<BleNoxAlarmInfo>();
	Handler handler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		listView = (ListView) findViewById(R.id.list);
		dataView = findViewById(R.id.layout_data);
		tvTips = (TextView) findViewById(R.id.tv_tips);
		addAlarm=(Button)findViewById(R.id.btn_add_alarm);
	
	}

	public void initListener() {
		super.initListener();
		//ivRight.setOnClickListener(this);
		listView.setOnItemClickListener(onItemClickListener);
		addAlarm.setOnClickListener(this);
	}

	public void initUI() {
		inflater = getLayoutInflater();
		tvTitle.setText(R.string.alarm);
		//ivRight.setImageResource(R.drawable.device_btn_add_nor);
		ivRight.setVisibility(View.GONE);
		addAlarm.setVisibility(View.VISIBLE);
		adapter = new AlarmAdapter();
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initAlarmList();
		mHelper.registWorkeModeCb(new WorkeModeCallBack() {

			@Override
			public void onWorkModeStatus(BleNoxWorkStatus workMode) {
				// TODO Auto-generated method stub
				SdkLog.log(TAG, "registWokeModeCb：" + workMode);
			}
		});

	}

	private void initAlarmList() {
		showLoading();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				hideLoading();
			}
		}, 15000);
		mHelper.getAlarmList(MainActivity.deviceId, 3000, new IResultCallback<List<BleNoxAlarmInfo>>() {
			@Override
			public void onResultCallback(final CallbackData<List<BleNoxAlarmInfo>> cd) {
				// TODO Auto-generated method stub
				if (!ActivityUtil.isActivityAlive(mActivity)) {
					return;
				}
				SdkLog.log(TAG, "getAlarmList:" + cd);
				runOnUiThread(new Runnable() {
					public void run() {
						hideLoading();

						if (cd.getResult() == null) {
							tvTips.setText(R.string.sa_no_alarm);
							return;
						}
						List<BleNoxAlarmInfo> alarmList = cd.getResult();
						if (cd.isSuccess()) {
							list.clear();
							listTemp.clear();
							list.addAll(cd.getResult());
							listTemp.addAll(cd.getResult());
							adapter.notifyDataSetChanged();
						} else {
							showErrTips(cd);
						}
					}
				});
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BleHelper.REQCODE_OPEN_BT) {

		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			BleNoxAlarmInfo alarm = adapter.getItem(position);
			Intent intent = new Intent(mActivity, EditAlarmActivity.class);
			intent.putExtra("action", "edit");
			intent.putExtra("alarm", alarm);
			startActivity(intent);
		}
	};

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == addAlarm) {
			if (list.size() >= 5) {
				Toast.makeText(this, R.string.more_5, Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(this, EditAlarmActivity.class);
				intent.putExtra("action", "add");
				startActivityForResult(intent, 100);
			}
		}
	}

	class AlarmAdapter extends BaseAdapter {
		
		AlarmAdapter() {
		}

		class ViewHolder {
			TextView tvTime;
			TextView tvRepeat;
			CheckBox cbSwitch;
			TextView tvLocalAlarm;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public BleNoxAlarmInfo getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_clock_item, null);
				holder = new ViewHolder();
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
				holder.tvRepeat = (TextView) convertView.findViewById(R.id.tv_continue);
				holder.cbSwitch = (CheckBox) convertView.findViewById(R.id.cb_swtich);
				holder.tvLocalAlarm = (TextView) convertView.findViewById(R.id.tv_local);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final BleNoxAlarmInfo item = getItem(position);
			holder.tvTime.setText(String.format("%02d:%02d", item.getHour(), item.getMinute()));
			holder.tvRepeat.setText(Utils.getSelectDay(mActivity, item.getRepeat()));
			holder.cbSwitch.setTag(item.getAlarmID());
			//holder.cbSwitch.setChecked(item.isOpen());
			if (item.getAlarmID() == 0) {
				
				holder.tvLocalAlarm.setVisibility(View.VISIBLE);
			} else {
				holder.tvLocalAlarm.setVisibility(View.GONE);
			}
			holder.cbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
					// TODO Auto-generated method stub
					// LogUtil.log(TAG+" alarm open changed:" +
					// isChecked+",tag:" + buttonView.getTag()+",item:" + item);
					
					if (buttonView.getTag() != null) {
						long alarmId = Long.valueOf(buttonView.getTag().toString());
						//SdkLog.log(TAG, "---alarmId---:"+alarmId+"---item.getAlarmID()---:"+item.getAlarmID()+"---isChecked---:"+isChecked+"--item.isOpen()--:"+item.isOpen());
						if (alarmId != item.getAlarmID() || isChecked == item.isOpen() ) {
							return;
						}
					}

					LogUtil.log(TAG + " alarm open changed:" + isChecked);
					item.setOpen(isChecked);
					mHelper.alarmConfig(MainActivity.deviceId, item, 3000, new IResultCallback() {
						@Override
						public void onResultCallback(final CallbackData cd) {
							// TODO Auto-generated method stub
							LogUtil.log(TAG + " alarmConfig cd:" + cd);
							runOnUiThread(new Runnable() {
	                            public void run() {
	    							if(!cd.isSuccess()){	    								
	    								showErrTips(cd);
	    								item.setOpen(!isChecked);
	    								notifyDataSetChanged();
	    							}
	                            }
                            });

						}
					});
				}
			});
			holder.cbSwitch.setChecked(item.isOpen());
			return convertView;
		}


		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub
			if (getCount() > 0) {
				dataView.setVisibility(View.VISIBLE);
				tvTips.setVisibility(View.GONE);
				super.notifyDataSetChanged();
			} else {
				dataView.setVisibility(View.GONE);
				tvTips.setVisibility(View.VISIBLE);
				tvTips.setText(R.string.sa_no_alarm);
			}
		}
	}

}
