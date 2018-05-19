package com.example.zhujia.dx_shop.Service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import cn.jpush.android.service.AlarmReceiver;

public class LongRunningService extends Service {

    private SharedPreferences sharedPreferences;
    private AlarmManager mManager;
    private PendingIntent mPi;
    private String LoginState,loginuserId,Token;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("TAG", "onCreate: ");
        super.onCreate();

    }
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        LoginState=sharedPreferences.getString("LoginState","");
        loginuserId=sharedPreferences.getString("userId","");
        Token=sharedPreferences.getString("token","");
        mManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 3600000;//每隔多久请求一次 lo是时间 单位毫秒
        Intent i = new Intent(this, com.example.zhujia.dx_shop.Service.AlarmReceiver.class);//开启广播
        mPi = PendingIntent.getBroadcast(this, 0, i, 0);

            if (LoginState.equals("yes")) {
                mManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, mPi);//启动
                //这里放请求网络的逻辑 可以先打个log看看
                Log.e("TAG", "onStartCommand: "+"60钟后刷新token" );
                refreshtoken();
            } else {
                Toast.makeText(getApplicationContext(), "你好请重新登录", Toast.LENGTH_SHORT).show();
            }

        return super.onStartCommand(intent, flags, startId);
    }

    //刷新token
    private void refreshtoken(){
        new HttpUtils().Post(Constant.APPURLS+"customer/refreshToken",Token,loginuserId,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,2,data
                );
                mHandler.sendMessage(msg);
            }

            public void onError(String msg) {
                Log.e("TAG", "onError: "+msg );
            }

        });
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                switch (msg.what) {
                    case 2:
                        JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                        String result_code=reslutJSONObject.getString("code");
                        if(result_code.equals("200")){
                            //存储TOKEN信息
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            //系统用户
                            editor.putString("token",reslutJSONObject.getString("msg"));
                            editor.commit();
                        }
                        break;

                    default:
                        Toast.makeText(getApplicationContext(),"网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }



        }
    };
}
