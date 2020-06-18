package com.sdkEw202w.demo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdkEw202w.demo.DemoApp;
import com.sdkEw202w.demo.MainActivity;
import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.SearchBleDeviceActivity;
import com.sleepace.sdk.core.nox.domain.BleNoxDeviceInfo;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CallbackData;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.util.SdkLog;

public class ConfigWifiFragment extends BaseFragment {

	private static final String TAG = ConfigWifiFragment.class.getSimpleName();
	private ImageView ivBack;
	private View vDeviceId;
	private TextView tvTitle, tvDeviceId;
	private EditText etAddress, etPort, etSsid, etPwd;
	private Button btnConfig, btnGetToken;
	private EditText etToken, etAccount, etPsw;
	private String ip = "172.14.1.100";
	private int port = 9010;
	public static final String EXTRA_DEVICE = "extra_device";
	private BleDevice device;

	private ProgressDialog loadingDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_config_wifi, null);

		// LogUtil.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		// ivBack = (ImageView)root.findViewById(R.id.iv_back);
		// tvTitle = (TextView) root.findViewById(R.id.tv_title);
		vDeviceId = root.findViewById(R.id.layout_deviceid);
		tvDeviceId = (TextView) root.findViewById(R.id.tv_deviceid);
		etAddress = (EditText) root.findViewById(R.id.et_address);
		etPort = (EditText) root.findViewById(R.id.et_port);
		etSsid = (EditText) root.findViewById(R.id.et_ssid);
		etPwd = (EditText) root.findViewById(R.id.et_pwd);
		btnConfig = (Button) root.findViewById(R.id.btn_config);
		etToken = (EditText) root.findViewById(R.id.et_token);
		etAccount = (EditText) root.findViewById(R.id.et_account);
		etPsw = (EditText) root.findViewById(R.id.et_psw);
		btnGetToken = (Button) root.findViewById(R.id.btn_get_tonke);

	}

	protected void initListener() {
		// TODO Auto-generated method stub
		// tvTitle.setOnClickListener(this);
		vDeviceId.setOnClickListener(this);
		btnConfig.setOnClickListener(this);
		btnGetToken.setOnClickListener(this);

	}

	protected void initUI() {
		// TODO Auto-generated method stub
		// ivBack.setVisibility(View.GONE);
		// tvTitle.setText(R.string.demo_name_ble_wifi);
		etSsid.setText("xie_xie_long");
		etPwd.setText("88888888");
		etToken.setText(DemoApp.TOKEN);
		etAddress.setText(ip);
		etAddress.setSelection(etAddress.length());
		etPort.setText(String.valueOf(port));
		etPort.setSelection(etPort.length());

		loadingDialog = new ProgressDialog(mActivity);
		loadingDialog.setCancelable(false);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setMessage(getString(R.string.loading_pair_wifi));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == vDeviceId) {
			Intent intent = new Intent(mActivity, SearchBleDeviceActivity.class);
			startActivityForResult(intent, 100);
		} else if (v == btnConfig) {
			if (TextUtils.isEmpty(tvDeviceId.getText())) {
				Toast.makeText(mActivity, R.string.select_device, Toast.LENGTH_SHORT).show();
				return;
			}

			if (device == null || device.getDeviceType() == null) {
				return;
			}

			ip = etAddress.getText().toString();
			String strPort = etPort.getText().toString();

			// if(TextUtils.isEmpty(ip)){
			// Toast.makeText(mActivity, R.string.hint_ip,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			//
			// if(TextUtils.isEmpty(strPort)){
			// Toast.makeText(mActivity, R.string.hint_port,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			//
			port = Integer.valueOf(strPort);
			final String ssid = etSsid.getText().toString();
			final String pwd = etPwd.getText().toString();
			if (TextUtils.isEmpty(ssid)) {
				Toast.makeText(mActivity, R.string.input_wifi_name, Toast.LENGTH_SHORT).show();
				return;
			}

			loadingDialog.show();

			mHelper.configWifi(device.getAddress(), ip, port, ssid, pwd, "2222", "10000", callback);

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100 && resultCode == -1) {
			device = (BleDevice) data.getSerializableExtra(EXTRA_DEVICE);
			tvDeviceId.setText(device.getDeviceName());
		}
		SdkLog.log(TAG + " onActivityResult req:" + requestCode + ",res:" + resultCode + ",d:" + device);
	}

	private IResultCallback callback = new IResultCallback() {
		@Override
		public void onResultCallback(final CallbackData cd) {
			// TODO Auto-generated method stub
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!isActivityAlive(mActivity)) {
						return;
					}
					SdkLog.log(TAG + " callback " + cd);
					loadingDialog.dismiss();
					if (cd.getResult() != null) {
						BleNoxDeviceInfo device = (BleNoxDeviceInfo) cd.getResult();
						MainActivity.deviceId = device.getDeviceID();
						MainActivity.version = device.getFirmwareVersion();
						MainActivity.deviceName = device.getDeviceName();

					}

					if (cd.getCallbackType() == IDeviceManager.METHOD_CONNECT) {

						showConfigResult(mActivity, cd.isSuccess());
					}

				}
			});
		}
	};

	public boolean isActivityAlive(Activity activity) {
		if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
			return false;
		}
		return true;
	}

	private void showConfigResult(Context context, boolean result) {
		final Dialog dialog = new Dialog(context, R.style.myDialog);
		dialog.setContentView(R.layout.dialog_warn_tips);
		TextView tvTips = (TextView) dialog.findViewById(R.id.warn_tips);
		Button btn = (Button) dialog.findViewById(R.id.warn_bt);

		int msgRes = result ? R.string.reminder_configuration_success : R.string.reminder_configuration_fail;
		tvTips.setText(msgRes);
		btn.setText(R.string.btn_ok);

		OnClickListener mListner = new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		};

		btn.setOnClickListener(mListner);
		Window win = dialog.getWindow();
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

}
