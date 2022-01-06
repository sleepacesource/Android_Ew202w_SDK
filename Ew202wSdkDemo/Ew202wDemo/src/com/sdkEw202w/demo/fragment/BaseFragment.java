package com.sdkEw202w.demo.fragment;

import com.sdkEw202w.demo.DemoApp;
import com.sdkEw202w.demo.MainActivity;
import com.sdkEw202w.demo.R;
import com.sdkEw202w.demo.BaseActivity.MyOnTouchListener;
import com.sdkEw202w.demo.util.ActivityUtil;
import com.sdkEw202w.demo.view.LoadingDialog;
import com.sleepace.sdk.constant.StatusCode;
import com.sleepace.sdk.ew202w.Ew202wHelper;
import com.sleepace.sdk.manager.CallbackData;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment implements OnClickListener{
	
	protected String TAG = getClass().getSimpleName();
	protected MainActivity mActivity;
	public Ew202wHelper mHelper;
	private LoadingDialog mLoadingDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (MainActivity) getActivity();
		mHelper = Ew202wHelper.getInstance(mActivity);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public Ew202wHelper getDeviceHelper() {
		return mHelper;
	}

	protected void findView(View root) {
		// TODO Auto-generated method stub
	}


	protected void initListener() {
		// TODO Auto-generated method stub
	}

	protected void initUI() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void registerTouchListener(MyOnTouchListener myOnTouchListener) {
		mActivity.registerTouchListener(myOnTouchListener);
    }
	
    public void unregisterTouchListener(MyOnTouchListener myOnTouchListener) {
    	mActivity.unregisterTouchListener(myOnTouchListener);
    }
    
	public void showLoading() {
        String tips = "";
        showLoading(tips);
    }

    public void showLoading(int resId) {
        String tips = getString(resId);
        showLoading(tips);
    }

    public void showLoading(String tips) {
        if (!isAdded() || !ActivityUtil.isActivityAlive(mActivity)) {
            return;
        }

        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mActivity);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
        }
        
        mLoadingDialog.show();
    }
    
	public void showErrMsg(CallbackData cd){
		if(isAdded()){
			if(cd.getStatus()==16){
				Toast.makeText(mActivity, getString(R.string.entered_light), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(mActivity, getString(R.string.device_connect_fail), Toast.LENGTH_SHORT).show();
			}
		}

		
	}

    public boolean isLoadingDialogShowing() {
        return mLoadingDialog != null && mLoadingDialog.isShowing();
    }

    public void hideLoading() {
        if (!isAdded() || !ActivityUtil.isActivityAlive(mActivity)) {
            return;
        }
        if (isLoadingDialogShowing()) {
            mLoadingDialog.dismiss();
        }
    }
	
}



