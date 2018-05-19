package com.example.zhujia.dx_shop.Tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.zhujia.dx_shop.R;
import com.wang.avi.AVLoadingIndicatorView;

public class CustomDialog extends ProgressDialog {

    private AVLoadingIndicatorView avLoadingIndicatorView;
    public CustomDialog(Context context) {
        super(context);

    }
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.waiting_activity);//loading的xml文件
        avLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.avi);
        avLoadingIndicatorView.setIndicator("indicator");
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }
    @Override
    public void show() {//开启

    }
    @Override
    public void dismiss() {//关闭

    }
}