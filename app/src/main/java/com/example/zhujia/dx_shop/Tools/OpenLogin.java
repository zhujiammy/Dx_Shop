package com.example.zhujia.dx_shop.Tools;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.example.zhujia.dx_shop.R;
import com.hmy.popwindow.PopWindow;

public class OpenLogin {

    private PopWindow popWindow;
    private Button ok_btn,cancel,ok_btn1;
    private View customView;
    private Activity context;
    private ClearEditText phonenum;

    public OpenLogin(Activity context) {
        this.context=context;
        intiUI(context);
    }


    private void intiUI(Activity activity){

        customView = View.inflate(context, R.layout.register, null);
        popWindow = new PopWindow.Builder(activity)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();

    }


    public void show(){
        popWindow.show(customView);
    }

    public void close(){
        popWindow.dismiss();
    }
}
