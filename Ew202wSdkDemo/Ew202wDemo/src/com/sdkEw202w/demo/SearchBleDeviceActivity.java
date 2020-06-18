package com.sdkEw202w.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sdkEw202w.demo.fragment.ConfigWifiFragment;
import com.sdkEw202w.demo.util.BleDeviceNameUtil;
import com.sleepace.sdk.domain.BleDevice;
import com.sleepace.sdk.interfs.IBleScanCallback;
import com.sleepace.sdk.manager.DeviceType;
import com.sleepace.sdk.manager.ble.BleHelper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SearchBleDeviceActivity extends Activity implements OnClickListener {
	private static final String TAG = SearchBleDeviceActivity.class.getSimpleName();
	private ImageView ivBack, ivRefresh;
	private TextView tvTitle, tvRefresh;
	private View vRefresh;
	private ListView listView;
	private LayoutInflater inflater;
	private BleAdapter adapter;
	private RotateAnimation animation;
	private BleHelper bleHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_ble);
		bleHelper = BleHelper.getInstance(this);
		findView();
		initListener();
		initUI();
	}

	public void findView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		vRefresh = findViewById(R.id.layout_refresh);
		tvRefresh = (TextView) findViewById(R.id.tv_refresh);
		ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
		listView = (ListView) findViewById(R.id.list);
	}

	public void initListener() {
		ivBack.setOnClickListener(this);
		vRefresh.setOnClickListener(this);
		listView.setOnItemClickListener(onItemClickListener);
	}

	public void initUI() {
		inflater = getLayoutInflater();

		animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(2000);// 设置动画持续时间
		animation.setRepeatCount(Animation.INFINITE);
		animation.setInterpolator(new LinearInterpolator());

		// tvTitle.setText(R.string.search_device);
		adapter = new BleAdapter();
		listView.setAdapter(adapter);

		vRefresh.performClick();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bleHelper.stopScan();
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
			bleHelper.stopScan();
			BleDevice device = adapter.getItem(position);
			Intent data = new Intent();
			data.putExtra(ConfigWifiFragment.EXTRA_DEVICE, device);
			setResult(RESULT_OK, data);
			finish();
		}
	};

	private void initRefreshView() {
		// tvRefresh.setText(R.string.refresh_deviceid_list);
		ivRefresh.clearAnimation();
		ivRefresh.setImageResource(R.drawable.bg_refresh);
	}

	private void initSearchView() {
		// tvRefresh.setText(R.string.refreshing);
		ivRefresh.setImageResource(R.drawable.device_loading);
		ivRefresh.startAnimation(animation);
	}

	@Override
	public void onClick(View v) {
		if (v == ivBack) {
			finish();
		} else if (v == vRefresh) {
			if (bleHelper.isBluetoothOpen()) {
				bleHelper.scanBleDevice(scanCallback);
			} else {
				Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enabler, BleHelper.REQCODE_OPEN_BT);
			}
		}
	}

	private IBleScanCallback scanCallback = new IBleScanCallback() {

		@Override
		public void onStopScan() {
			// TODO Auto-generated method stub
			initRefreshView();
		}

		@Override
		public void onStartScan() {
			// TODO Auto-generated method stub
			initSearchView();
			adapter.clearData();
		}

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String modelName = device.getName();
					if (modelName != null) {
						modelName = modelName.trim();
					}
					String deviceName = BleDeviceNameUtil.getBleDeviceName(0xff, scanRecord);
					if (deviceName != null) {
						deviceName = deviceName.trim();
					}

					// Log.e(TAG," onLeScan modelName:"
					// +modelName+",deviceName:" + deviceName);
					if (!TextUtils.isEmpty(deviceName) && checkEW2W(deviceName)) {
						BleDevice ble = new BleDevice();
						ble.setModelName(modelName);
						ble.setAddress(device.getAddress());
						ble.setDeviceName(deviceName);
						ble.setDeviceId(deviceName);
						ble.setDeviceType(getDeviceTypeByName(deviceName));
						adapter.addBleDevice(ble);
					}
				}
			});
		}
	};

	private DeviceType getDeviceTypeByName(String deviceName) {
		if (checkRestOnZ300(deviceName)) {
			return DeviceType.DEVICE_TYPE_Z3;
		} else if (checkEWW(deviceName)) {
			return DeviceType.DEVICE_TYPE_EW201W;
		} else if (checkNoxSAW(deviceName)) {
			return DeviceType.DEVICE_TYPE_NOX_SAW;
		} else if (checkM600(deviceName)) {
			return DeviceType.DEVICE_TYPE_M600;
		} else if (checkM800(deviceName)) {
			return DeviceType.DEVICE_TYPE_M800;
		} else if (checkEW2W(deviceName)) {
			return DeviceType.DEVICE_TYPE_EW202W;
		}
		return null;
	}

	private boolean checkRestOnZ300(String deviceName) {
		if (deviceName == null)
			return false;
		Pattern p = Pattern.compile("^(Z3)[0-9a-zA-Z-]{11}$");
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	private boolean checkM600(String deviceName) {
		if (deviceName == null)
			return false;
		Pattern p = Pattern.compile("^(M6)[0-9a-zA-Z-]{11}$");
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	private boolean checkM800(String deviceName) {
		if (deviceName == null)
			return false;
		Pattern p = Pattern.compile("^(M800)[0-9a-zA-Z-]{9}$");
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	private boolean checkEWW(String deviceName) {
		if (deviceName == null)
			return false;
		Pattern p = Pattern.compile("^(EW1W)[0-9a-zA-Z]{9}$");
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	private boolean checkEW2W(String deviceName) {
		if (deviceName == null)
			return false;
		Pattern p = Pattern.compile("^(EW22W)[0-9a-zA-Z]{8}$");
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	private static boolean checkNoxSAW(String deviceName) {
		if (deviceName == null)
			return false;
		Pattern p = Pattern.compile("^(SA11)[0-9a-zA-Z]{9}$");
		Matcher m = p.matcher(deviceName);
		return m.matches();
	}

	class BleAdapter extends BaseAdapter {
		private List<BleDevice> list = new ArrayList<BleDevice>();

		class ViewHolder {
			TextView tvName;
			TextView tvDeviceId;
		}

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public BleDevice getItem(int position) {

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
				convertView = inflater.inflate(R.layout.list_reston_item, null);
				holder = new ViewHolder();
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tvDeviceId = (TextView) convertView.findViewById(R.id.tv_deviceid);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BleDevice item = getItem(position);
			holder.tvName.setText(item.getModelName());
			holder.tvDeviceId.setText(item.getDeviceName());
			return convertView;
		}

		public void addBleDevice(BleDevice bleDevice) {

			boolean exists = false;
			for (BleDevice d : list) {
				if (d.getAddress().equals(bleDevice.getAddress())) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				list.add(bleDevice);
				notifyDataSetChanged();
			}
		}

		public List<BleDevice> getData() {
			return list;
		}

		public void clearData() {
			list.clear();
			notifyDataSetChanged();
		}
	}
}
