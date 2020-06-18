package com.sdkEw202w.demo;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.util.LogUtil;
import com.sdkEw202w.demo.util.Utils;
import com.sdkEw202w.demo.view.SelectTimeDialog;
import com.sdkEw202w.demo.view.SelectValueDialog;
import com.sdkEw202w.demo.view.SelectTimeDialog.TimeSelectedListener;
import com.sdkEw202w.demo.view.SelectValueDialog.ValueSelectedListener;
import com.sleepace.sdk.core.nox.domain.BleNoxAlarmInfo;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.util.SdkLog;
import com.sleepace.sdk.util.TimeUtil;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class EditAlarmActivity extends BaseActivity {

	private View vStartTime, vRepeat, vMusic, vVolume, vSnooze;
	private TextView tvStartTime, tvRepeat, tvMusic, tvVolume, tvSnooze, tvSnoozeTips;
	private CheckBox cbLight, cbAroma, cbSnooze;
	private Button btnPreview, btnDel;

	private SelectTimeDialog timeDialog;
	private SelectValueDialog volumeDialog, snoozeDurationDialog;

	private String action;
	private BleNoxAlarmInfo alarm;
	private byte snoozeLength;

	private boolean isPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_alarm);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		vStartTime = findViewById(R.id.layout_start_time);
		vRepeat = findViewById(R.id.layout_repeat);
		vMusic = findViewById(R.id.layout_music);
		vVolume = findViewById(R.id.layout_volume);
		vSnooze = findViewById(R.id.layout_snooze_time);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		tvRepeat = (TextView) findViewById(R.id.tv_reply);
		tvMusic = (TextView) findViewById(R.id.tv_music);
		tvVolume = (TextView) findViewById(R.id.tv_volume);
		cbLight = (CheckBox) findViewById(R.id.cb_light);
		cbAroma = (CheckBox) findViewById(R.id.cb_aroma);
		cbSnooze = (CheckBox) findViewById(R.id.cb_snooze);
		tvSnooze = (TextView) findViewById(R.id.tv_snooze_time);
		tvSnoozeTips = (TextView) findViewById(R.id.tv_tips_snooze);
		btnPreview = (Button) findViewById(R.id.btn_preview);
		btnDel = (Button) findViewById(R.id.btn_del);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		vStartTime.setOnClickListener(this);
		vRepeat.setOnClickListener(this);
		vMusic.setOnClickListener(this);
		vVolume.setOnClickListener(this);
		vSnooze.setOnClickListener(this);
		btnPreview.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		cbLight.setOnCheckedChangeListener(onCheckedChangeListener);
		cbAroma.setOnCheckedChangeListener(onCheckedChangeListener);
		cbSnooze.setOnCheckedChangeListener(onCheckedChangeListener);
	}

	public void initUI() {
		super.initUI();
		tvRight.setText(R.string.save);

		action = getIntent().getStringExtra("action");
		if ("add".equals(action)) {
			tvTitle.setText(R.string.add_alarm);
			btnDel.setVisibility(View.INVISIBLE);
			alarm = new BleNoxAlarmInfo();
			alarm.setAlarmID(DemoApp.getSeqId());
			alarm.setOpen(true);
			alarm.setHour((byte) 8);
			alarm.setRepeat((byte) 0);
			alarm.setMusicID(DemoApp.ALARM_MUSIC[0][0]);
			alarm.setVolume((byte) 16);
			alarm.setBrightness((byte) 80);
			alarm.setAromaRate((byte) 2);
			alarm.setSnoozeCount((byte) 0);
			alarm.setSnoozeLength((byte) 5);
			alarm.setTimestamp(TimeUtil.getCurrentTimeInt());
			alarm.setUserFul((byte) 1);
			snoozeLength = 5;
		} else {
			tvTitle.setText(R.string.edit_alarm);
			btnDel.setVisibility(View.VISIBLE);
			alarm = (BleNoxAlarmInfo) getIntent().getSerializableExtra("alarm");
			snoozeLength = alarm.getSnoozeLength();
			if (alarm.getAlarmID() == 0) {
				btnDel.setVisibility(View.GONE);
			}

		}

		timeDialog = new SelectTimeDialog(this, "%02d");
		timeDialog.setTimeSelectedListener(timeListener);

		int[] volumes = new int[16];
		for (int i = 1; i <= volumes.length; i++) {
			volumes[i - 1] = i;
		}
		volumeDialog = new SelectValueDialog(this, volumes);
		volumeDialog.setValueSelectedListener(valueSelectedListener);

		int[] data = { 5, 10, 15 };
		snoozeDurationDialog = new SelectValueDialog(this, data);
		snoozeDurationDialog.setValueSelectedListener(valueSelectedListener);

		initTimeView();
		initRepeatView();
		initMusicView();
		initVolumeView();
		initLightView();
		initSnoozeView();
		initAromaView();

	}

	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (buttonView == cbLight) {
				alarm.setBrightness(isChecked ? (byte) 80 : 0);
			} else if (buttonView == cbAroma) {
				alarm.setAromaRate(isChecked ? INoxManager.AromaSpeed.COMMON.getValue() : INoxManager.AromaSpeed.CLOSE.getValue());
			}else if(buttonView ==cbSnooze){
				alarm.setSnoozeCount(isChecked ? (byte)3:0);
				initSnoozeView();
			}
		}
	};

	private ValueSelectedListener valueSelectedListener = new ValueSelectedListener() {
		@Override
		public void onValueSelected(SelectValueDialog dialog, byte value) {
			// TODO Auto-generated method stub
			LogUtil.log(TAG + " onValueSelected val:" + value);
			if (dialog == snoozeDurationDialog) {
				snoozeLength = value;
				alarm.setSnoozeLength(value);
				initSnoozeView();
			} else if (dialog == volumeDialog) {
				alarm.setVolume(value);
				initVolumeView();
			}
		}
	};

	private TimeSelectedListener timeListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(byte hour, byte minute) {
			// TODO Auto-generated method stub
			alarm.setHour(hour);
			alarm.setMinute(minute);
			initTimeView();
		}
	};

	private void initTimeView() {
		tvStartTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
	}

	private void initRepeatView() {
		tvRepeat.setText(Utils.getSelectDay(this, alarm.getRepeat()));
	}

	private void initMusicView() {
		int res = Utils.getAlarmMusicName(alarm.getMusicID());
		// LogUtil.log(TAG+" initMusicView mid:" + alarm.getMusicID()+",res:" +
		// res);
		if (res > 0) {
			tvMusic.setText(res);
		} else {
			tvMusic.setText("");
		}
	}

	private void initVolumeView() {
		tvVolume.setText(String.valueOf(alarm.getVolume()));
	}

	private void initSnoozeView() {
		
		if(alarm.getSnoozeCount()>0){
			cbSnooze.setChecked(true);
			vSnooze.setVisibility(View.VISIBLE);
			tvSnooze.setText(String.valueOf(alarm.getSnoozeLength()));
		}else{
			cbSnooze.setChecked(false);
			vSnooze.setVisibility(View.GONE);
			tvSnooze.setText(String.valueOf(alarm.getSnoozeLength()));
		}
		
	}
	private void initLightView() {
		cbLight.setChecked(alarm.getBrightness() > 0);
	}


	private void initAromaView() {
		cbAroma.setChecked(alarm.getAromaRate() > 0);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // TODO Auto-generated method stub
	      if (keyCode == KeyEvent.KEYCODE_BACK) {
				mHelper.stopAlarmPreview(MainActivity.deviceId, 3000, new IResultCallback() {
					@Override
					public void onResultCallback(final CallbackData cd) {
						// TODO Auto-generated method stub
					}
				});
	        }
	    return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == tvRight) {
			showLoading();
			if("add".equals(action)){
				mHelper.alarmAdd(MainActivity.deviceId, alarm, 3000, new IResultCallback() {
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
									finish();
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}else{	
				alarm.setOpen(true);
				mHelper.alarmConfig(MainActivity.deviceId, alarm, 3000, new IResultCallback() {
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
									finish();
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}

		}else if(v == ivBack){
			mHelper.stopAlarmPreview(MainActivity.deviceId, 3000, new IResultCallback() {
				@Override
				public void onResultCallback(final CallbackData cd) {
					// TODO Auto-generated method stub
				
				}
			});
		} else if (v == vStartTime) {
			timeDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_start_time), getString(R.string.confirm), null, null);
			timeDialog.setDefaultValue(alarm.getHour(), alarm.getMinute());
			timeDialog.show();
		} else if (v == vRepeat) {
			Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_REPEAT);
			intent.putExtra("repeat", alarm.getRepeat());
			startActivityForResult(intent, 100);
		} else if (v == vSnooze) {
			snoozeDurationDialog.setLabel(getString(R.string.cancel), getString(R.string.snooze_duration), getString(R.string.confirm),
			        null);
			snoozeDurationDialog.setDefaultValue(alarm.getSnoozeLength());
			snoozeDurationDialog.show();
		} else if (v == vMusic) {
			Intent intent = new Intent(this, DataListActivity.class);
			intent.putExtra("dataType", DataListActivity.DATA_TYPE_ALARM_MUSIC);
			intent.putExtra("musicId", alarm.getMusicID());
			startActivityForResult(intent, 101);
		} else if (v == vVolume) {
			volumeDialog.setLabel(getString(R.string.cancel), getString(R.string.volume), getString(R.string.confirm), null);
			volumeDialog.setDefaultValue(alarm.getVolume());
			volumeDialog.show();
		} else if (v == btnPreview) {
			showLoading();
			if (!isPreview) {

				LogUtil.log(TAG + " preview alarm:" + alarm);
				mHelper.startAlarmPreview(MainActivity.deviceId, alarm.getVolume(), alarm.getBrightness(), alarm.getMusicID(), 3000,
				        new IResultCallback() {
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
									        isPreview = true;
									        btnPreview.setText(R.string.preview_stop);
								        } else {
									        showErrTips(cd);
								        }
							        }
						        });
					        }
				        });
			} else {
				mHelper.stopAlarmPreview(MainActivity.deviceId, 3000, new IResultCallback() {
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
									isPreview = false;
									btnPreview.setText(R.string.preview_alarm);
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}
		} else if (v == btnDel) {
			// if (alarm.getAlarmID() == 0) {//内置闹钟无法删除
			// //alarm_no_delete
			// Toast.makeText(this, "内置闹钟无法删除", Toast.LENGTH_SHORT).show();
			// return;
			// }

			showLoading();
			alarm.userFul = 0;
			
			if("add".equals(action)){
				mHelper.alarmAdd(MainActivity.deviceId, alarm, 3000, new IResultCallback() {
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
									finish();
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}else{
				alarm.setOpen(true);
				mHelper.alarmConfig(MainActivity.deviceId, alarm, 3000, new IResultCallback() {
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
									finish();
								} else {
									showErrTips(cd);
								}
							}
						});
					}
				});
			}

		}
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			byte repeat = data.getByteExtra("repeat", (byte) 0);
			alarm.setRepeat(repeat);
			if (repeat != 0) {
				alarm.setTimestamp(0);
			}
			initRepeatView();
		} else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
			int musicId = data.getIntExtra("musicId", 0);
			alarm.setMusicID(musicId);
			initMusicView();
		}
	}
}
