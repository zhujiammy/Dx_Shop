package com.example.zhujia.dx_shop.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zhujia.dx_shop.R;
import com.wang.avi.AVLoadingIndicatorView;

public class WaitActivity extends AppCompatActivity {

    private AVLoadingIndicatorView avLoadingIndicatorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);
        String indicator=getIntent().getStringExtra("indicator");
        avLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.avi);
        avLoadingIndicatorView.setIndicator(indicator);


    }

    public void hideClick(View view) {
        avLoadingIndicatorView.hide();
        // or avi.smoothToHide();
    }

    public void showClick(View view) {
        avLoadingIndicatorView.show();
        // or avi.smoothToShow();
    }
}
