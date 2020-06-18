package com.sdkEw202w.demo.fragment;

import java.lang.reflect.Field;

import com.sdkEw202w.demo.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ControlFragment extends BaseFragment {

	private RadioGroup rgTab;
	private FragmentManager fragmentMgr;
	private Fragment lightFragment, aromaFragment, sleepAidFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_control, null);
		// LogUtil.log(TAG+" onCreateView-----------");
		findView(view);
		initListener();
		initUI();
		return view;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
		super.findView(root);
		rgTab = (RadioGroup) root.findViewById(R.id.rg_tab);
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		rgTab.setOnCheckedChangeListener(checkedChangeListener);
	}

	protected void initUI() {
		// TODO Auto-generated method stub
		mActivity.setTitle(R.string.control);
		fragmentMgr = getChildFragmentManager();
		lightFragment = new LightFragment();
		aromaFragment = new AromaFragment();
		sleepAidFragment = new SleepAidFragment();
		rgTab.check(R.id.rb_light);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			FragmentTransaction trans = fragmentMgr.beginTransaction();
			if (checkedId == R.id.rb_light) {
				trans.replace(R.id.content, lightFragment);
			} else if (checkedId == R.id.rb_sleepaid) {
				trans.replace(R.id.content, sleepAidFragment);
			}
			trans.commit();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
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
