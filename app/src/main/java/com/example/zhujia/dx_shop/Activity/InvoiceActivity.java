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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.CitySelect1Activity;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.hmy.popwindow.PopWindow;

import org.json.JSONException;
import org.json.JSONObject;

public class InvoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private ClearEditText orderNo,invoiceTitle,receiveAddress,receivePerson,receivePhone,bankName,bankNo,invoiceCode,registerAddress;
    private RadioButton invoiceType1,invoiceType2;
    private TextView pcd;
    private Button submit;
    private LinearLayout bank_lin;
    private Intent intent;
    private String LoginState,TOKEN,loginUserId;
    private SharedPreferences sharedPreferences;
    private CitySelect1Activity citySelect1Activity;
    private PopWindow popWindow;
    private String receiveProvince,receiveCity,receiveDistrict;
    private View customView;

    private Toolbar toolbar;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        intent=getIntent();
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
    }

    private void initUI(){
        pcd=(TextView)findViewById(R.id.pcd);
        pcd.setOnClickListener(this);
        customView = View.inflate(InvoiceActivity.this, R.layout.address_select, null);
        popWindow = new PopWindow.Builder(InvoiceActivity.this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();

        citySelect1Activity=(CitySelect1Activity) customView.findViewById(R.id.apvAddress);
        citySelect1Activity.setOnAddressPickerSure(new CitySelect1Activity.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String Province, String City, String District, String ProvinceCode, String CityCode, String DistrictCode) {
                pcd.setText(Province+" "+City+" "+District);
                receiveProvince=Province;
                receiveCity=City;
                receiveDistrict=District;
                popWindow.dismiss();
            }
        });
        orderNo=(ClearEditText)findViewById(R.id.orderNo);
        orderNo.setText(intent.getStringExtra("orderno"));
        invoiceTitle=(ClearEditText)findViewById(R.id.invoiceTitle);
        receiveAddress=(ClearEditText)findViewById(R.id.receiveAddress);
        receivePerson=(ClearEditText)findViewById(R.id.receivePerson);
        receivePhone=(ClearEditText)findViewById(R.id.receivePhone);
        bankName=(ClearEditText)findViewById(R.id.bankName);
        bankNo=(ClearEditText)findViewById(R.id.bankNo);
        invoiceCode=(ClearEditText)findViewById(R.id.invoiceCode);
        registerAddress=(ClearEditText)findViewById(R.id.registerAddress);
        invoiceType1=(RadioButton) findViewById(R.id.invoiceType1);
        invoiceType2=(RadioButton) findViewById(R.id.invoiceType2);
        invoiceType1.setOnClickListener(this);
        invoiceType2.setOnClickListener(this);
        bank_lin=(LinearLayout)findViewById(R.id.bank_lin);
        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        Log.e("TAG", "initUI: "+ intent.getStringExtra("invoicestr"));
        String json=intent.getStringExtra("invoicestr");
        if(json!=null){
            try {
                JSONObject jsonObject=new JSONObject(json);
                if(jsonObject.getString("invoiceStatus").equals("created")){
                    if(jsonObject.getString("invoiceType").equals("增值税普通发票")){
                        invoiceType1.setChecked(true);
                    }
                    if(jsonObject.getString("invoiceType").equals("增值税专用发票")){
                        invoiceType2.setChecked(true);
                    }
                    invoiceTitle.setText(jsonObject.getString("invoiceTitle"));
                    registerAddress.setText(jsonObject.getString("registerAddress"));
                    invoiceCode.setText(jsonObject.getString("invoiceCode"));
                    bankName.setText(jsonObject.getString("bankName"));
                    bankNo.setText(jsonObject.getString("bankNo"));
                    receivePerson.setText(jsonObject.getString("receivePerson"));
                    receivePhone.setText(jsonObject.getString("receivePhone"));
                    pcd.setText(jsonObject.getString("receiveProvince")+" "+jsonObject.getString("receiveCity")+" "+jsonObject.getString("receiveDistrict"));
                    receiveProvince=jsonObject.getString("receiveProvince");
                    receiveCity=jsonObject.getString("receiveCity");
                    receiveDistrict=jsonObject.getString("receiveDistrict");
                    receiveAddress.setText(jsonObject.getString("receiveAddress"));
                    submit.setEnabled(true);
                }else {
                    submit.setEnabled(false);
                    if(jsonObject.getString("invoiceType").equals("增值税普通发票")){
                        invoiceType1.setChecked(true);
                    }
                    if(jsonObject.getString("invoiceType").equals("增值税专用发票")){
                        invoiceType2.setChecked(true);
                    }
                    invoiceTitle.setText(jsonObject.getString("invoiceTitle"));
                    registerAddress.setText(jsonObject.getString("registerAddress"));
                    invoiceCode.setText(jsonObject.getString("invoiceCode"));
                    bankName.setText(jsonObject.getString("bankName"));
                    bankNo.setText(jsonObject.getString("bankNo"));
                    receivePerson.setText(jsonObject.getString("receivePerson"));
                    receivePhone.setText(jsonObject.getString("receivePhone"));
                    pcd.setText(jsonObject.getString("receiveProvince")+" "+jsonObject.getString("receiveCity")+" "+jsonObject.getString("receiveDistrict"));
                    receiveAddress.setText(jsonObject.getString("receiveAddress"));

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if(v==invoiceType1){
            bank_lin.setVisibility(View.GONE);
        }
        if(v==invoiceType2){
            bank_lin.setVisibility(View.VISIBLE);
        }
        if(v==pcd){
            //地址选择
            popWindow.show();
        }

        if(v==submit){
            JSONObject object = new JSONObject();
            try {
                if(invoiceType2.isChecked()){
                    object.put("invoiceType", invoiceType2.getText().toString());
                    object.put("bankName", bankName.getText().toString());
                    object.put("bankNo", bankNo.getText().toString());
                    object.put("invoiceCode", invoiceCode.getText().toString());
                    object.put("registerAddress", registerAddress.getText().toString());
                }
                    object.put("invoiceTitle", invoiceTitle.getText().toString());
                    object.put("invoiceType", invoiceType1.getText().toString());
                    object.put("orderNo", orderNo.getText().toString());
                    object.put("receiveAddress", receiveAddress.getText().toString());
                    object.put("receiveCity", receiveCity);
                    object.put("receiveDistrict", receiveDistrict);
                    object.put("receivePerson", receivePerson.getText().toString());
                    object.put("receivePhone", receivePhone.getText().toString());
                    object.put("receiveProvince", receiveProvince);



                final String params = object.toString();
                    //新增
                Log.e("TAG", "onClick: "+params );
                    new HttpUtils().postJson(Constant.APPURLS + "order/invoice/save", params, TOKEN, loginUserId, new HttpUtils.HttpCallback() {
                        @Override
                        public void onSuccess(String data) {
                            // TODO Auto-generated method stub
                            com.example.zhujia.dx_shop.Tools.Log.printJson("tag", data, "header");

                            Message msg = Message.obtain(
                                    mHandler, 1, data
                            );
                            mHandler.sendMessage(msg);
                        }

                    });


        }catch (JSONException e){
                e.printStackTrace();
            }
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

        }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
};
}
