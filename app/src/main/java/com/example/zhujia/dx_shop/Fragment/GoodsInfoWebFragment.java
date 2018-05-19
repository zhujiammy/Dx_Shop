package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 图文详情webview的Fragment
 */
public class GoodsInfoWebFragment extends Fragment {
    public LinearLayout linproductimg;
    private LayoutInflater inflater;
    private String id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_item_info_web, null);
        initWebView(rootView);
        return rootView;
    }

    public void initWebView(View rootView) {
        linproductimg=(LinearLayout)rootView.findViewById(R.id.linproductimg);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(isAdded()){
            id=getArguments().getString("id");
            Log.e("TAG", "图文详情webview: "+id );
        }

        loadproductimg();
    }



    private void loadproductimg(){
        new HttpUtils().Post(Constant.APPURLS+"product/img/"+id,"","",new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,1,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what){
                    case 1:
                        JSONArray object=new JSONArray(msg.obj.toString());
                        for(int i=0;i<object.length();i++){
                            JSONObject object1=object.getJSONObject(i);
                            View views = View.inflate(getActivity(),R.layout.productimg,null);
                            ImageView imageView=(ImageView)views.findViewById(R.id.productimg);
                            Glide.with(getActivity()).load(Constant.loadimag+object1.getString("imgUrl")).into(imageView);
                            linproductimg.addView(views);
                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    private class GoodsDetailWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }
}
