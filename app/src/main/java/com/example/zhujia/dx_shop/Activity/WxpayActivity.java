package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.zhujia.dx_shop.R;

import java.util.HashMap;
import java.util.Map;

public class WxpayActivity extends AppCompatActivity {
    WebView webView;
    Intent intent;
    String msg;
    Map<String, String> extraHeaders;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xxpay);
        intent = getIntent();
        msg = intent.getStringExtra("msg");
        webView = (WebView) findViewById(R.id.wxpay);
        webView.getSettings().setJavaScriptEnabled(true);
        Log.e("TAG", "onCreate: "+msg);
        Map<String,String> map=new HashMap<String, String>();
        map.put("Referer","http://test-shop.dxracer.com.cn");

        webView.setWebViewClient(new WebViewClient() {

            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("weixin://wap/pay?")) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                       startActivity(intent);
                       finish();
                    } catch (android.content.ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(),"未安装微信",Toast.LENGTH_SHORT).show();
                        finish();
                        Log.e("TAG", "shouldOverrideUrlLoading: e=" + e.toString());
                    }
                    return true;
                }  else {
                    extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("Referer", "test-shop.dxracer.com.cn://");
                    view.loadUrl(url, extraHeaders);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) { // 重写此方法可以让webview处理https请求
                handler.proceed();
            }
        });
        webView.loadUrl(msg,map);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.stopLoading();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
    }


}
