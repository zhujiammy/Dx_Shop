package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zhujia.dx_shop.Data.AllData;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RefundActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId,status;
    private Intent intent;
    private String orderNostr;
    private Button submit;
    private EditText remarks;
    private Spinner RefundCause;
    private String RefundCauseId,isImg;
    private List<AllData> dicts1 = new ArrayList<AllData>();
    private  ArrayAdapter<AllData> arrAdapterpay1;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refund_xml);
        intent=getIntent();
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        orderNostr=intent.getStringExtra("orderno");
        Log.e("TAG", "onCreate: "+orderNostr );
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
        initUI();
        loadRefundcause();
    }

    private void initUI(){
        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        remarks=(EditText) findViewById(R.id.remarks);
        RefundCause=(Spinner)findViewById(R.id.RefundCauseId);
        RefundCause.setOnItemSelectedListener(listener);
    }


    //退款退货原因
    private void  loadRefundcause(){
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"refund/getRefundCauseList",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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

    Spinner.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RefundCauseId=((AllData)RefundCause.getSelectedItem()).getStr();
            isImg=((AllData)RefundCause.getSelectedItem()).getIsImg();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    public void onClick(View v) {
        if(v==submit){
            new HttpUtils().Post(Constant.APPURLS+"refund/creare?orderNo="+orderNostr+"&remarks="+remarks.getText().toString(),TOKEN,loginUserId,new HttpUtils.HttpCallback() {
                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
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
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what){

                    case 1:
                        JSONObject object=new JSONObject(msg.obj.toString());
                        if(object.getString("code").equals("200")){
                            Toast.makeText(getApplicationContext(),object.getString("object"),Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent();
                            intent.putExtra("types","1");
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                        break;
                    case 2:
                        JSONArray paymentTypearry=new JSONArray(msg.obj.toString());
                        dicts1.add(new AllData("0","请选择",""));
                        for(int i=0;i<paymentTypearry.length();i++){
                            JSONObject object1=paymentTypearry.getJSONObject(i);
                            dicts1.add(new AllData(object1.getString("causeId"),object1.getString("content"),object1.getString("isImg")));
                            arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                            //设置样式
                            arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            RefundCause.setAdapter(arrAdapterpay1);
                        }



                        break;

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };
}
