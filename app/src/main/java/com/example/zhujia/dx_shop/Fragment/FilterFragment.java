package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Data.AttrValues;
import com.example.zhujia.dx_shop.Data.ProductItemList;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.FNRadioGroup;
import com.example.zhujia.dx_shop.Tools.MRadioButton;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;


public class FilterFragment extends Fragment implements View.OnClickListener{
    private DrawerLayout mDrawerLayout;
    private FrameLayout mDrawerContent;
    private ImageView iv_back;
    private Button btn_confirm;
    private FNRadioGroup catalog,type,brand,series;
    private String catalogs="",types="",seriess="",brands="";
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter, null);
        initView(view);
        initEvent();
        loaddata();
        return view;
    }

    private void initEvent() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mDrawerContent);
            }
        });


    }

    private void initView(View view) {
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerContent = (FrameLayout) getActivity().findViewById(R.id.drawer_content);
        btn_confirm=(Button)view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        catalog=(FNRadioGroup)view.findViewById(R.id.catalog);
        type=(FNRadioGroup)view.findViewById(R.id.type);
        series=(FNRadioGroup)view.findViewById(R.id.series);
        brand=(FNRadioGroup)view.findViewById(R.id.brand);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);

    }

    private void loaddata(){
        new HttpUtils().Get(Constant.APPURLS+"product/catalog",new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");
                Message msg= Message.obtain(
                        mHandler,0,data
                );
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(String msg) {
                Log.e("TAG", "onError: "+msg );
            }

        });

        new HttpUtils().Get(Constant.APPURLS+"product/type",new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");
                Message msg= Message.obtain(
                        mHandler,1,data
                );
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(String msg) {
                Log.e("TAG", "onError: "+msg );
            }

        });

        new HttpUtils().Get(Constant.APPURLS+"product/series",new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");
                Message msg= Message.obtain(
                        mHandler,2,data
                );
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(String msg) {
                Log.e("TAG", "onError: "+msg );
            }

        });

        new HttpUtils().Get(Constant.APPURLS+"product/brand",new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");
                Message msg= Message.obtain(
                        mHandler,3,data
                );
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(String msg) {
                Log.e("TAG", "onError: "+msg );
            }

        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what){
                    case 0:
                        org.json.JSONArray jsonArray=new org.json.JSONArray(msg.obj.toString());
                        int num=0;
                        MRadioButton radioButton = null;
                        for(int i=0;i<jsonArray.length();i++) {
                            final org.json.JSONObject object = jsonArray.getJSONObject(i);
                            num++;
                            radioButton = new MRadioButton(getActivity());
                            radioButton.setText(object.getString("catalogName"));
                            radioButton.setBackground(getActivity().getResources().getDrawable(R.drawable.radiobutton_background));
                            radioButton.setButtonDrawable(null);
                            radioButton.setGravity(Gravity.CENTER);
                            radioButton.setId(num);
                            radioButton.setPadding(30, 30, 30, 30);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
                            catalog.addView(radioButton, lp);
                            final MRadioButton finalRadioButton = radioButton;
                            radioButton.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View v) {
                                    try {
                                        catalogs=object.getString("id");
                                    } catch (org.json.JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                        }

                        break;

                    case 1:
                        org.json.JSONArray jsonArray1=new org.json.JSONArray(msg.obj.toString());
                        int num1=0;
                        MRadioButton radioButton1 = null;
                        for(int i=0;i<jsonArray1.length();i++) {
                            final org.json.JSONObject object = jsonArray1.getJSONObject(i);
                            num1++;
                            radioButton1 = new MRadioButton(getActivity());
                            radioButton1.setText(object.getString("typeName"));
                            radioButton1.setBackground(getActivity().getResources().getDrawable(R.drawable.radiobutton_background));
                            radioButton1.setButtonDrawable(null);
                            radioButton1.setGravity(Gravity.CENTER);
                            radioButton1.setId(num1);
                            radioButton1.setPadding(30, 30, 30, 30);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
                            type.addView(radioButton1, lp);
                            final MRadioButton finalRadioButton = radioButton1;
                            radioButton1.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View v) {
                                    try {
                                        types=object.getString("id");
                                    } catch (org.json.JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                        }

                        break;

                    case 2:
                        org.json.JSONArray jsonArray2=new org.json.JSONArray(msg.obj.toString());
                        int num2=0;
                        MRadioButton radioButton2 = null;
                        for(int i=0;i<jsonArray2.length();i++) {
                            final org.json.JSONObject object = jsonArray2.getJSONObject(i);
                            num2++;
                            radioButton2 = new MRadioButton(getActivity());
                            radioButton2.setText(object.getString("seriesName"));
                            radioButton2.setBackground(getActivity().getResources().getDrawable(R.drawable.radiobutton_background));
                            radioButton2.setButtonDrawable(null);
                            radioButton2.setGravity(Gravity.CENTER);
                            radioButton2.setId(num2);
                            radioButton2.setPadding(30, 30, 30, 30);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
                            series.addView(radioButton2, lp);
                            final MRadioButton finalRadioButton = radioButton2;
                            radioButton2.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View v) {
                                    try {
                                        seriess=object.getString("id");
                                    } catch (org.json.JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                        }

                        break;

                    case 3:
                        org.json.JSONArray jsonArray3=new org.json.JSONArray(msg.obj.toString());
                        int num3=0;
                        MRadioButton radioButton3 = null;
                        for(int i=0;i<jsonArray3.length();i++) {
                            final org.json.JSONObject object = jsonArray3.getJSONObject(i);
                            num3++;
                            radioButton3 = new MRadioButton(getActivity());
                            radioButton3.setText(object.getString("brandName"));
                            radioButton3.setBackground(getActivity().getResources().getDrawable(R.drawable.radiobutton_background));
                            radioButton3.setButtonDrawable(null);
                            radioButton3.setGravity(Gravity.CENTER);
                            radioButton3.setId(num3);
                            radioButton3.setPadding(30, 30, 30, 30);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
                            brand.addView(radioButton3, lp);
                            final MRadioButton finalRadioButton = radioButton3;
                            radioButton3.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View v) {
                                    try {
                                        brands=object.getString("id");
                                    } catch (org.json.JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                        }

                        break;
                }
            }catch (org.json.JSONException e){
               e.printStackTrace();
            }
        }
    };


    @Override
    public void onClick(View v) {
        if(v==btn_confirm){
            mDrawerLayout.closeDrawer(mDrawerContent);
            TypePage fragment = new TypePage();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Bundle bundle = new Bundle();
            //catalogs,types,seriess,brands
            bundle.putString("catalogs",catalogs);
            bundle.putString("types",types);
            bundle.putString("seriess",seriess);
            bundle.putString("brands",brands);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fl_content, fragment).commit();
        }
    }
}
