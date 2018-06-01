package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.LoginActivity;
import com.example.zhujia.dx_shop.Activity.MyOrderActivity;
import com.example.zhujia.dx_shop.Activity.SettingActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DXSW5 on 2018/3/14.
 */

public class MyPage extends Fragment implements View.OnClickListener {

    private View view;
    private LinearLayout Pendingpayment_btn,PendingDelivery,Goodsreceived,Completed;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId;
    private CircleImageView profile_image;
    private TextView username,view_more;
    private ImageView setting;
    private Intent intent;
    private String iconUrl,nickNamestr;
    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.mypage,container,false);
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        initUI();
        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            //相当于Fragment的onPause

        } else {

        }
    }

    private void initUI(){

        Pendingpayment_btn=(LinearLayout)view.findViewById(R.id.Pendingpayment_btn);
        Pendingpayment_btn.setOnClickListener(this);
        PendingDelivery=(LinearLayout)view.findViewById(R.id.PendingDelivery);
        PendingDelivery.setOnClickListener(this);
        Goodsreceived=(LinearLayout)view.findViewById(R.id.Goodsreceived);
        Goodsreceived.setOnClickListener(this);
        Completed=(LinearLayout)view.findViewById(R.id.Completed);
        Completed.setOnClickListener(this);
        view_more=(TextView)view.findViewById(R.id.view_more);
        view_more.setOnClickListener(this);
        profile_image=(CircleImageView)view.findViewById(R.id.profile_image);
        username=(TextView)view.findViewById(R.id.user_name);
        setting=(ImageView)view.findViewById(R.id.setting);

        profile_image.setOnClickListener(this);
        username.setOnClickListener(this);
        setting.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        LoginState=sharedPreferences.getString("LoginState","");
        iconUrl=sharedPreferences.getString("iconUrl","");
        if(LoginState.equals("yes")){
            TOKEN=sharedPreferences.getString("token","");
            loginUserId=sharedPreferences.getString("userId","");
            loadenums();//获取订单状态
        }

        nickNamestr=sharedPreferences.getString("nickName","");
        if(!iconUrl.equals("")){
            Glide.with(getActivity()).load(iconUrl).into(profile_image);
        }else {
            profile_image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }

        if(!nickNamestr.equals("")){
            username.setText(nickNamestr);
        }else {
            if(LoginState.equals("yes")){
                username.setText("");
            }

        }



    }


    private void loadenums(){
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/enums",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
    }

    @Override
    public void onClick(View v) {

        if(v==Pendingpayment_btn){

            if(LoginState.equals("yes")){
                intent=new Intent(getActivity(), MyOrderActivity.class);
                intent.putExtra("orderstatue","01");
                startActivity(intent);
            }else {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("page","3");
                startActivity(intent);
            }

        }
        if(v==PendingDelivery){
            if(LoginState.equals("yes")){
                intent=new Intent(getActivity(), MyOrderActivity.class);
                intent.putExtra("orderstatue","02");
                startActivity(intent);
            }else {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("page","3");
                startActivity(intent);
            }
        }
        if(v==Goodsreceived){
            if(LoginState.equals("yes")){
                intent=new Intent(getActivity(), MyOrderActivity.class);
                intent.putExtra("orderstatue","03");
                startActivity(intent);
            }else {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("page","3");
                startActivity(intent);
            }
        }
        if(v==Completed){
            if(LoginState.equals("yes")){
                intent=new Intent(getActivity(), MyOrderActivity.class);
                intent.putExtra("orderstatue","04");
                startActivity(intent);
            }else {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("page","3");
                startActivity(intent);
            }
        }

        if(v==view_more){
            //查看更多订单
            if(LoginState.equals("yes")){
                intent=new Intent(getActivity(),MyOrderActivity.class);
                intent.putExtra("orderstatue","all");
                startActivity(intent);
            }else {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("page","3");
                startActivity(intent);
            }
        }

        if(v==profile_image||v==username||v==setting){
            //账户设置界面
            if(LoginState.equals("yes")){
                intent=new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("iconUrl",iconUrl);
                startActivity(intent);
            }else {
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("page","3");
                startActivity(intent);
            }

        }
    }

    /**
     * 消息处理Handler
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:// 解析返回数据
                    //toMainActivity();
                    break;

                default:
                    Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
