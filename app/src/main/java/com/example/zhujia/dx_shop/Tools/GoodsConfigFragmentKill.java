package com.example.zhujia.dx_shop.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.ProductDetailsActivity;
import com.example.zhujia.dx_shop.Activity.ProductDetailskillActivity;
import com.example.zhujia.dx_shop.Adapter.GoodsConfigAdapter;
import com.example.zhujia.dx_shop.Data.GoodsConfigBean;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 图文详情里的规格参数的Fragment
 */
public class GoodsConfigFragmentKill extends Fragment {
    public ProductDetailskillActivity activity;
    public ListView lv_config;
    private String id;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (ProductDetailskillActivity) context;
    }


    @Override
    public void onStart() {
        super.onStart();
        if(isAdded()){
            id=getArguments().getString("id");
            Log.e("TAG", "规格参数: "+id);
        }

        loadproductdesc();
    }

    private void loadproductdesc() {
        new HttpUtils().Post(Constant.APPURLS+"product/desc/"+id,"","",new HttpUtils.HttpCallback() {

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
                        JSONArray jsonArray=new JSONArray(msg.obj.toString());
                        List<GoodsConfigBean> data = new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.getJSONObject(i);
                            data.add(new GoodsConfigBean(object.getString("attrValue"),object.getString("attrCode")));
                            lv_config.setAdapter(new GoodsConfigAdapter(activity, data));
                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_config, null);
        lv_config = (ListView) view.findViewById(R.id.lv_config);
        lv_config.setFocusable(false);
        return view;
    }


}
