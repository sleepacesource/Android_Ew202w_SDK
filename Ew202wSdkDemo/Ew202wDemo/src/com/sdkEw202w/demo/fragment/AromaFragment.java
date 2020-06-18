package com.sdkEw202w.demo.fragment;

import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.util.LogUtil;
import com.sdkEw202w.demo.util.Utils;
import com.sleepace.sdk.core.nox.interfs.INoxManager;
import com.sleepace.sdk.interfs.IConnectionStateCallback;
import com.sleepace.sdk.interfs.IDeviceManager;
import com.sleepace.sdk.interfs.IResultCallback;
import com.sleepace.sdk.manager.CONNECTION_STATE;
import com.sleepace.sdk.manager.CallbackData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AromaFragment extends BaseFragment {
	private View maskView;
	private RadioGroup rgAroma;
	private Button btnCloseAroma;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.fragment_aroma, null);
//		LogUtil.log(TAG+" onCreateView-----------");
		findView(root);
		initListener();
		initUI();
		return root;
	}
	
	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		maskView = root.findViewById(R.id.mask);
		rgAroma = (RadioGroup) root.findViewById(R.id.rg_aroma_speed);
		btnCloseAroma = (Button) root.findViewById(R.id.btn_close_aroma);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		getDeviceHelper().addConnectionStateCallback(stateCallback);
		rgAroma.setOnCheckedChangeListener(checkedChangeListener);
		btnCloseAroma.setOnClickListener(this);
	}
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			if(group.getTag() != null) {
				group.setTag(null);
				return;
			}
			
			byte speed = 0;
			
			if(checkedId == R.id.rb_fast){
				speed = INoxManager.AromaSpeed.FAST.getValue();
			}else if(checkedId == R.id.rb_mid){
				speed = INoxManager.AromaSpeed.COMMON.getValue();
			}else if(checkedId == R.id.rb_slow){
				speed = INoxManager.AromaSpeed.SLOW.getValue();
			}
			
			LogUtil.log(TAG+" onCheckedChanged speed:" + speed);
			
			
		}
	};


	protected void initUI() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isConnected = getDeviceHelper().isConnected();
		initPageState(isConnected);
	}
	
	private void initPageState(boolean isConnected) {
		initBtnConnectState(isConnected);
		setPageEnable(isConnected);
	}
	
	private void initBtnConnectState(boolean isConnected) {
		btnCloseAroma.setEnabled(isConnected);
		Utils.setRadioGroupEnable(rgAroma, isConnected);
	}
	
	private void setPageEnable(boolean enable){
		if(enable) {
			maskView.setVisibility(View.GONE);
		}else {
			maskView.setVisibility(View.VISIBLE);
		}
	}
	
	
	private IConnectionStateCallback stateCallback = new IConnectionStateCallback() {
		@Override
		public void onStateChanged(IDeviceManager manager, final CONNECTION_STATE state) {
			// TODO Auto-generated method stub
			
			if(!isAdded()){
				return;
			}
			
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					initPageState(state == CONNECTION_STATE.CONNECTED);
					
					if(state == CONNECTION_STATE.DISCONNECT){
						
					}else if(state == CONNECTION_STATE.CONNECTED){
						
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
		if(v == btnCloseAroma) {
//			getDeviceHelper().setAroma((byte)0, 3000, new IResultCallback() {
//				@Override
//				public void onResultCallback(final CallbackData cd) {
//					// TODO Auto-generated method stub
//					if(!isAdded()) {
//						return;
//					}
//					
//					LogUtil.log(TAG+" close aroma cd:" + cd);
//					mActivity.runOnUiThread(new Runnable() {
//						public void run() {
//							if(cd.isSuccess()) {
//								rgAroma.setTag("ok");
//								rgAroma.check(-1);
//							}else {
//								mActivity.showErrTips(cd);
//							}
//						}
//					});
//				}
//			});
		}
	}
	
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		try {
//			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//			childFragmentManager.setAccessible(true);
//			childFragmentManager.set(this, null);
//		} catch (NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
//	}
}










