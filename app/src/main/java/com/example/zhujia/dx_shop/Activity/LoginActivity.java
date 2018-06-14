package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.AESUtil;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.IEditTextChangeListener;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.WorksSizeCheckUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private ClearEditText loginName,passWord;
    private Button login_btn;
    private Toolbar toolbar;
    private TextView register_btn,fpass;
    private Intent intent_page;
    ProgressDialog progressDialog;
    private boolean flag = false;
    private JSONObject reslutJSONObject;
    private  SharedPreferences sp;
    private String AESPASSWORD="dxracer";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
        sp= getSharedPreferences("Session", Activity.MODE_PRIVATE);
        initUI();



    }



    private void initUI(){
        intent_page=getIntent();
        login_btn=(Button)findViewById(R.id.login_btn);
        loginName=(ClearEditText)findViewById(R.id.loginName);
        passWord=(ClearEditText)findViewById(R.id.passWord);
        register_btn=(TextView)findViewById(R.id.register_btn);
        fpass=(TextView)findViewById(R.id.fpass);
        fpass.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        login_btn.setEnabled(false);
        register_btn.setOnClickListener(this);
        WorksSizeCheckUtil.textChangeListener textChangeListener = new WorksSizeCheckUtil.textChangeListener(login_btn);
        textChangeListener.addAllEditText(loginName,passWord);


        WorksSizeCheckUtil.setChangeListener(new IEditTextChangeListener() {
            @SuppressLint("NewApi")
            @Override
            public void textChange(boolean isHasContent) {
                if(!isHasContent){
                    login_btn.setBackground(getResources().getDrawable(R.drawable.loginbt_f));
                    login_btn.setEnabled(false);
                }else {
                    login_btn.setBackground(getResources().getDrawable(R.drawable.loginbt));
                    login_btn.setEnabled(true);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

        //登录
        if(v==login_btn){
            JSONObject object = new JSONObject();
            try {
                object.put("loginName",loginName.getText().toString());
                object.put("passWord",passWord.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String params=object.toString();
                progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getResources().getString(R.string.Loggingin));
                progressDialog.show();
                new HttpUtils().LoginPost(Constant.APPURLS+"customer/login",params,new HttpUtils.HttpCallback() {

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
        //注册
        if(v==register_btn){
            Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
        }
        //忘记密码
        if(v==fpass){
            Intent intent=new Intent(getApplicationContext(),ForgetPasswordActivity.class);
            startActivity(intent);
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
            try{
            switch (msg.what) {
                case 0:// 解析返回数据
                    //toMainActivity();
                    break;
                case 1:


                        reslutJSONObject=new JSONObject(msg.obj.toString());
                        String result_code=reslutJSONObject.getString("code");
                        if(result_code.equals("200")){
                            JSONObject object=reslutJSONObject.getJSONObject("object");
                            //存储TOKEN信息
                            SharedPreferences.Editor editor=sp.edit();
                            //系统用户
                            editor.putString("token",object.getString("token"));
                            editor.putString("userId",object.getString("userId"));
                            editor.putString("LoginState","yes");
                            editor.putString("loginName",loginName.getText().toString());
                            editor.putString("password", AESUtil.encrypt(AESPASSWORD,passWord.getText().toString()));
                            editor.putString("AESPASSWORD",AESPASSWORD);
                            editor.commit();
                            loaduserinfo(object.getString("token"),object.getString("userId"));
                            //跳转
                            mHandler.postDelayed(runnable,2000);


                        }else {
                            String msgs=reslutJSONObject.getString("object");
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),msgs, Toast.LENGTH_SHORT).show();

                        }

                        Log.d("code","result_code"+result_code);


                    break;

                    //保存用户信息
                case 2:
                        reslutJSONObject=new JSONObject(msg.obj.toString());
                        SharedPreferences.Editor editors=sp.edit();
                        editors.putString("iconUrl",reslutJSONObject.getString("iconUrl"));//头像地址
                        editors.putString("nickName",reslutJSONObject.getString("nickName"));//昵称
                        editors.putString("customerName",reslutJSONObject.getString("customerName"));//姓名
                        editors.putString("customerMobile",reslutJSONObject.getString("customerMobile"));//手机号码
                        editors.putString("birthday",reslutJSONObject.getString("birthday"));//生日
                        editors.putString("sex",reslutJSONObject.getString("sex"));//性别
                        editors.putString("email",reslutJSONObject.getString("email"));//电子邮件
                        editors.putString("qq",reslutJSONObject.getString("qq"));//qq
                        editors.putString("constellation",reslutJSONObject.getString("constellation"));//星座
                        editors.putString("occupation",reslutJSONObject.getString("occupation"));//职业
                        editors.putString("place",reslutJSONObject.getString("place"));//居之地
                        editors.putString("hometown",reslutJSONObject.getString("hometown"));//家乡
                        editors.commit();

                    break;

                default:
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;
            }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    };
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            progressDialog.dismiss();
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            if(intent_page.getStringExtra("page").equals("3")){
                intent.putExtra("select","3");
                startActivity(intent);
                finish();
            }else if(intent_page.getStringExtra("page").equals("x")){
                intent.putExtra("select","3");
                startActivity(intent);
                finish();
            }

        }
    };


    private void loaduserinfo(String TOKEN,String loginUserId){
        Log.e("TAG", "loaduserinfo: "+TOKEN+"-----------"+loginUserId );
        new HttpUtils().postJson(Constant.APPURLS+"account","",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,2,data
                );
                mHandler.sendMessage(msg);
            }

        });
    }

}
