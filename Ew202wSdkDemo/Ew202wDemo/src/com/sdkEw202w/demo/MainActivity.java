package com.sdkEw202w.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.fragment.ConfigWifiFragment;
import com.sdkEw202w.demo.fragment.ControlFragment;
import com.sdkEw202w.demo.fragment.DeviceFragment;
import com.sdkEw202w.demo.fragment.SettingFragment;
import com.sdkEw202w.demo.util.LogUtil;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.manager.CONNECTION_STATE;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends BaseActivity {

	private RadioGroup rgTab;
	private FragmentManager fragmentMgr;
	private Fragment deviceFragment, controlFragment, settingFragment, configWifiFragment;
	private ProgressDialog upgradeDialog;
	private final int requestCode = 101;// 权限请求码
	private boolean hasPermissionDismiss = false;// 有权限没有通过
	private String dismissPermission = "";
	private List<String> unauthoPersssions = new ArrayList<String>();
	private String[] permissions = new String[] { Manifest.permission.ACCESS_FINE_LOCATION };
	// 缓存数据
	public static String deviceName, deviceId, version;
	private boolean granted = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
		initListener();
		initUI();
		initData();
		checkPermissions();
	}

	public void initData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				copyAssetAndWrite("SN902B_20180509.1.24_release.MVA");
			}
		}).run();

	}

	private void checkPermissions() {
		granted = false;
		if (Build.VERSION.SDK_INT >= 23) {
			unauthoPersssions.clear();
			// 逐个判断你要的权限是否已经通过
			for (int i = 0; i < permissions.length; i++) {
				if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
					unauthoPersssions.add(permissions[i]);// 添加还未授予的权限
				}
			}
			// 申请权限
			if (unauthoPersssions.size() > 0) {// 有权限没有通过，需要申请
				ActivityCompat.requestPermissions(this, new String[] { unauthoPersssions.get(0) }, requestCode);
			} else {
				granted = true;
			}
		} else {
			granted = true;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		hasPermissionDismiss = false;
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (this.requestCode == requestCode) {
			for (int i = 0; i < grantResults.length; i++) {
				if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
					hasPermissionDismiss = true;
					dismissPermission = permissions[i];
					if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, dismissPermission)) {

					}
					break;
				}
			}

			// 如果有权限没有被允许
			if (hasPermissionDismiss) {

			} else {
				checkPermissions();
			}
		}
	}

	@Override
	protected void findView() {
		// TODO Auto-generated method stub
		super.findView();
		rgTab = (RadioGroup) findViewById(R.id.rg_tab);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		mHelper.addConnectionStateCallback(stateCallback);
		rgTab.setOnCheckedChangeListener(checkedChangeListener);
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
		super.initUI();
		fragmentMgr = getFragmentManager();
		deviceFragment = new DeviceFragment();
		controlFragment = new ControlFragment();
		settingFragment = new SettingFragment();
		// configWifiFragment = new ConfigWifiFragment();
		rgTab.check(R.id.rb_device);
		// rgTab.check(R.id.rb_control);
		// rgTab.check(R.id.rb_data);
		ivBack.setVisibility(View.GONE);

		upgradeDialog = new ProgressDialog(this);
		upgradeDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
		upgradeDialog.setMax(100);
		upgradeDialog.setIcon(android.R.drawable.ic_dialog_info);
		upgradeDialog.setTitle(R.string.fireware_update);
		upgradeDialog.setMessage(getString(R.string.upgrading));
		upgradeDialog.setCancelable(false);
		upgradeDialog.setCanceledOnTouchOutside(false);
	}

	public void setTitle(int res) {
		tvTitle.setText(res);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == ivBack) {
			exit();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void exit() {
		// mHelper.release();
		clearCache();
		// Intent intent = new Intent(this, SplashActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		finish();
	}

	private void clearCache() {
		deviceName = null;
		deviceId = null;
		version = null;
	}

	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			LogUtil.log(TAG + " onStateChanged state:" + state);
			if (state == CONNECTION_STATE.DISCONNECT) {

			}
		}
	};

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			FragmentTransaction trans = fragmentMgr.beginTransaction();
			if (checkedId == R.id.rb_device) {
				trans.replace(R.id.content, deviceFragment);
			} else if (checkedId == R.id.rb_control) {
				trans.replace(R.id.content, controlFragment);
			} else if (checkedId == R.id.rb_data) {
				trans.replace(R.id.content, settingFragment);
			}
			// else if(checkedId == R.id.rb_config_wifi){
			// trans.replace(R.id.content, configWifiFragment);
			// }
			trans.commit();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

		}
	}

	private void copyAssetAndWrite(String fileName) {
		try {
			File cacheDir = mActivity.getCacheDir();
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			File outFile = new File(cacheDir, fileName);

			InputStream is = mActivity.getResources().getAssets().open(fileName);
			FileOutputStream fos = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
			int byteCount;
			while ((byteCount = is.read(buffer)) != -1) {
				fos.write(buffer, 0, byteCount);
			}
			fos.flush();
			is.close();
			fos.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHelper.removeConnectionStateCallback(stateCallback);
	}

	public void showUpgradeDialog() {
		upgradeDialog.show();
	}

	public void setUpgradeProgress(int progress) {
		upgradeDialog.setProgress(progress);
	}

	public void hideUpgradeDialog() {
		upgradeDialog.dismiss();
	}

}
