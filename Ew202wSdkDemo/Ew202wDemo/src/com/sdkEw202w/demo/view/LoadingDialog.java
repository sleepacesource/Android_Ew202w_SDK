package com.sdkEw202w.demo.view;

import com.sdkEw202w.demo.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class LoadingDialog extends Dialog {

    private TextView tvMsg;
    private String tips;

    public LoadingDialog(Context context) {
        super(context, R.style.myDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_part_dialog_loading);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        tvMsg.setText(tips);
    }

    public void setMessage(int resId) {
        this.tips = getContext().getString(resId);
        if (tvMsg != null) {
            tvMsg.setText(tips);
        }
    }

    public void setMessage(String tips) {
        this.tips = tips;
        if(tvMsg != null){
            tvMsg.setText(tips);
        }
    }
}
