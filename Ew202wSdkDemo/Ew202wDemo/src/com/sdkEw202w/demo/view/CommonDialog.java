package com.sdkEw202w.demo.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CommonDialog extends AlertDialog {

    public Context context;
    public int themeResId;
    public int Layout;
    public Method method;
    public float size;
    private float hei=0;
    //失去焦点的时候是否dismiss掉
    private boolean dismissOnNoFocus;

    public CommonDialog(Context context, int themeResId, int Layout, float size) {
        super(context);

        this.context = context;
        this.themeResId = themeResId;
        this.Layout = Layout;
        this.size= size;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            if(dismissOnNoFocus) {
                dismiss();
            }
        }
    }

    public boolean isDismissOnNoFocus() {
        return dismissOnNoFocus;
    }

    public void setDismissOnNoFocus(boolean dismissOnNoFocus) {
        this.dismissOnNoFocus = dismissOnNoFocus;
    }

    public void setheight(float hei){
        this.hei = hei;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDialog();
    }

    private void initDialog() {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(Layout, null);

        Window window = this.getWindow();
        WindowManager.LayoutParams windowparams = window.getAttributes();

        WindowManager windowManager = null;
        if(windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        Display display = windowManager.getDefaultDisplay();

        Rect rect = new Rect();
        View view1 = window.getDecorView();
        view1.getWindowVisibleDisplayFrame(rect);

        windowparams.width = (int)(display.getWidth()*size);
        if(hei!=0){
            windowparams.height = (int)(display.getWidth()*hei);
        }

        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes((WindowManager.LayoutParams) windowparams);

        method.setView(this,view);
        setContentView(view);
    }



    public interface Method {
        public void setView(CommonDialog dialog, View view);
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
