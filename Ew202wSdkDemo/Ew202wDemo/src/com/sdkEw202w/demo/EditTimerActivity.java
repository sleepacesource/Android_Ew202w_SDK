package com.sdkEw202w.demo;

import java.util.ArrayList;
import java.util.List;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.util.Utils;
import com.sdkEw202w.demo.view.SelectTimeDialog;
import com.sdkEw202w.demo.view.SelectTimeDialog.TimeSelectedListener;
import com.sleepace.sdk.core.nox.domain.AromaTimer;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EditTimerActivity extends BaseActivity {

	private View vStartTime, vDuration;
	private TextView tvStartTime, tvDuration;
	private Button btnDel;

	private SelectTimeDialog timeDialog, durationDialog;

	private String action;
	private AromaTimer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_clock);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		super.findView();
		vStartTime = findViewById(R.id.layout_start_time);
		vDuration = findViewById(R.id.layout_duration);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		tvDuration = (TextView) findViewById(R.id.tv_duration);
		btnDel = (Button) findViewById(R.id.btn_delete_clock);
	}

	public void initListener() {
		super.initListener();
		tvRight.setOnClickListener(this);
		vStartTime.setOnClickListener(this);
		vDuration.setOnClickListener(this);
		btnDel.setOnClickListener(this);
	}

	public void initUI() {
		super.initUI();
		tvRight.setText(R.string.save);

		action = getIntent().getStringExtra("action");
		if ("add".equals(action)) {
			tvTitle.setText(R.string.add_timer);
			btnDel.setVisibility(View.GONE);
			timer = new AromaTimer();
			timer.setSeqId(DemoApp.getSeqId());
			timer.setContinueTime((short) 1);
		} else {
			tvTitle.setText(R.string.edit_timer);
			btnDel.setVisibility(View.VISIBLE);
			timer = (AromaTimer) getIntent().getSerializableExtra("timer");
		}

		timeDialog = new SelectTimeDialog(this, "%02d");
		timeDialog.setTimeSelectedListener(timeListener);

		durationDialog = new SelectTimeDialog(this, "%d");
		durationDialog.setTimeSelectedListener(durationListener);

		initTimeView();
		initDurationView();
	}

	private TimeSelectedListener timeListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(byte hour, byte minute) {
			// TODO Auto-generated method stub
			timer.setStartHour(hour);
			timer.setStartMin(minute);
			initTimeView();
		}
	};

	private TimeSelectedListener durationListener = new TimeSelectedListener() {
		@Override
		public void onTimeSelected(byte hour, byte minute) {
			// TODO Auto-generated method stub
			short duration = (short) (hour * 60 + minute);
			if (duration == 0) {
				Toast.makeText(mActivity, R.string.timer_no_0, Toast.LENGTH_SHORT).show();
			} else {
				timer.setContinueTime(duration);
				initDurationView();
			}
		}
	};

	private void initTimeView() {
		tvStartTime.setText(String.format("%02d:%02d", timer.getStartHour(), timer.getStartMin()));
	}

	private void initDurationView() {
		tvDuration.setText(Utils.getDuration(mActivity, timer.getContinueTime()));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == tvRight) {
			showLoading();
			List<AromaTimer> list = new ArrayList<AromaTimer>();
			list.add(timer);
			// mHelper.editeTimeAromaList(list, 3000, new IResultCallback() {
			// @Override
			// public void onResultCallback(final CallbackData cd) {
			// // TODO Auto-generated method stub
			// if(!ActivityUtil.isActivityAlive(mActivity)) {
			// return;
			// }
			//
			// runOnUiThread(new Runnable() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// hideLoading();
			// if(cd.isSuccess()) {
			// Intent data = new Intent();
			// data.putExtra("timer", timer);
			// setResult(RESULT_OK, data);
			// finish();
			// }else {
			// showErrTips(cd);
			// }
			// }
			// });
			// }
			// });
		} else if (v == vStartTime) {
			timeDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_start_time), getString(R.string.confirm), null, null);
			timeDialog.setDefaultValue(timer.getStartHour(), timer.getStartMin());
			timeDialog.show();
		} else if (v == vDuration) {
			durationDialog.setLabel(getString(R.string.cancel), getString(R.string.sa_last_time), getString(R.string.confirm),
			        getString(R.string.hour), getString(R.string.min));
			short duration = timer.getContinueTime();
			byte hour = (byte) (duration / 60);
			byte min = (byte) (duration % 60);
			durationDialog.setDefaultValue(hour, min);
			durationDialog.show();
		} else if (v == btnDel) {
			// showLoading();
			// mHelper.deleteTimeAroma(timer.getSeqId(), 3000, new
			// IResultCallback() {
			// @Override
			// public void onResultCallback(final CallbackData cd) {
			// // TODO Auto-generated method stub
			// if(!ActivityUtil.isActivityAlive(mActivity)) {
			// return;
			// }
			//
			// runOnUiThread(new Runnable() {
			// public void run() {
			// hideLoading();
			// if(cd.isSuccess()) {
			// Intent data = new Intent();
			// data.putExtra("action", "delete");
			// data.putExtra("timer", timer);
			// setResult(RESULT_OK, data);
			// finish();
			// }else {
			// showErrTips(cd);
			// }
			// }
			// });
			// }
			// });
		}
	}

}
