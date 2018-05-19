package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.CommomDialog;
import com.example.zhujia.dx_shop.Tools.CustomDialog;
import com.example.zhujia.dx_shop.Tools.IEditTextChangeListener;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.WorksSizeCheckUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.zhujia.dx_shop.Tools.insertComma.isMobile;

//账户设置界面
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {



    private RelativeLayout accout_setting;
    private Toolbar toolbar;
    private Intent intent;
    private CircleImageView profile_image;
    private TextView customerName,nickName,address_tx;
    private String iconUrl,nickNamestr,customerNamestr;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId;
    private TextView login_out_btn;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        intent=getIntent();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        initUI();


    }



    private void initUI(){

        accout_setting=(RelativeLayout)findViewById(R.id.accout_setting);
        accout_setting.setOnClickListener(this);
        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        customerName=(TextView)findViewById(R.id.customerName);
        nickName=(TextView)findViewById(R.id.nickName);
        address_tx=(TextView)findViewById(R.id.address_tx);
        address_tx.setOnClickListener(this);
        login_out_btn=(TextView)findViewById(R.id.login_out_btn);
        login_out_btn.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        iconUrl=sharedPreferences.getString("iconUrl","");
        nickNamestr=sharedPreferences.getString("nickName","");
        customerNamestr=sharedPreferences.getString("customerName","");
        if(!iconUrl.equals("")){
            Glide.with(getApplicationContext()).load(iconUrl).into(profile_image);
        }else {
            profile_image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }
        if(!customerNamestr.equals("")){
            customerName.setText(customerNamestr);
        }else {
            customerName.setText("");
        }
        if(!nickNamestr.equals("")){
            nickName.setText(nickNamestr);
        }else {
            nickName.setText("");
        }
    }

    @Override
    public void onClick(View v) {

        if(v==accout_setting){

            intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
            startActivity(intent);
        }

        //地址管理
        if(v==address_tx){

            intent=new Intent(getApplicationContext(),AddressManagementActivity.class);
            startActivity(intent);
        }

        if(v==login_out_btn){
            //注销

            //弹出提示框
            new CommomDialog(this, R.style.dialog, "确定退出登录？", new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if(confirm){
                        new HttpUtils().Post(Constant.APPURLS+"customer/logout",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
                        dialog.dismiss();
                    }

                }
            })
                    .setTitle("提示").show();

        }

    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {
                    case 1:
                        JSONObject header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            finish();

                            intent=new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("select","3");
                            startActivity(intent);

                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };


}
