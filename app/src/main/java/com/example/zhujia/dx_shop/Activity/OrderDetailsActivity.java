package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Data.OrderStatus;
import com.example.zhujia.dx_shop.Fragment.AllOrder;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.CommomDialog;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.insertComma;
import com.example.zhujia.dx_shop.util.PayResult;
import com.hmy.popwindow.PopWindow;

import net.sf.json.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private Intent intent;
    private String orderNostr;
    private String LoginState,TOKEN,loginUserId,status;
    private Button pay,cancel_order;
    private TextView orderStatus,receiverName,copy_btn,receiverAddress,createTime,orderNo,orderTotalFee,productFee,productFees,logistics,expressno,deliverTime;
    private LinearLayout lin_group;
    private ImageView close_pop;
    private PopWindow popWindow;
    private View customView;
    private String intentstatus="null";
    private LinearLayout alipay;
    private static final int SDK_PAY_FLAG = 5;
    private List<OrderStatus> orderStatuses= new ArrayList<OrderStatus>();
    private LinearLayout logistics_lin;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetails);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        intent=getIntent();
        orderNostr=intent.getStringExtra("orderNo");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent.getStringExtra("type")==null){
                    if(intentstatus.equals("null")){
                        finish();
                    }else {
                        intent=new Intent(getApplicationContext(), MyOrderActivity.class);
                        intent.putExtra("intentstatus",intentstatus);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    intent=new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("select","2");
                    startActivity(intent);
                    finish();
                }

            }
        });
        initUI();
        loadorderDetail();
        loadorderstatu();

    }

    @Override
    public void onBackPressed() {
        if(intent.getStringExtra("type")==null){
            if(intentstatus.equals("null")){
                finish();
            }else {
                intent=new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra("intentstatus",intentstatus);
                startActivity(intent);
                finish();
            }

        }else{
            intent=new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("select","2");
            startActivity(intent);
            finish();
        }
        return;
    }

    private void initUI(){
        logistics_lin=(LinearLayout)findViewById(R.id.logistics_lin);
        logistics_lin.setVisibility(View.GONE);
        logistics=(TextView)findViewById(R.id.logistics);
        expressno=(TextView)findViewById(R.id.expressno);
        deliverTime=(TextView)findViewById(R.id.deliverTime);
        orderStatus=(TextView)findViewById(R.id.orderstatu);
        receiverName=(TextView)findViewById(R.id.receiverName);
        receiverAddress=(TextView)findViewById(R.id.receiverAddress);
        lin_group=(LinearLayout)findViewById(R.id.lin_group);
        createTime=(TextView)findViewById(R.id.createTime);
        orderNo=(TextView)findViewById(R.id.orderNo);
        orderTotalFee=(TextView)findViewById(R.id.orderTotalFee);
        productFee=(TextView)findViewById(R.id.productFee);
        productFees=(TextView)findViewById(R.id.productFees);
        pay=(Button)findViewById(R.id.pay);
        cancel_order=(Button)findViewById(R.id.cancel_order);
        cancel_order.setOnClickListener(this);
        pay.setOnClickListener(this);
        copy_btn=(TextView)findViewById(R.id.copy_btn);
        copy_btn.setOnClickListener(this);


        customView = View.inflate(getApplicationContext(), R.layout.paytype, null);
        alipay=(LinearLayout)customView.findViewById(R.id.alipay);
        alipay.setOnClickListener(this);
        close_pop=(ImageView)customView.findViewById(R.id.close_pop);
        close_pop.setOnClickListener(this);
        popWindow = new PopWindow.Builder(OrderDetailsActivity.this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();

    }

    private void loadorderstatu(){
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/enums",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
    }

    private void loadorderDetail(){
        Log.e("TAG", "loadorderDetail: "+orderNostr);
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/"+orderNostr,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

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
                        JSONObject object=new JSONObject(msg.obj.toString());
                        JSONObject shippingOrder=object.getJSONObject("shippingOrder");
                        JSONObject shippingAddress=object.getJSONObject("shippingAddress");
                        receiverName.setText(shippingAddress.getString("receiverName")+"         "+shippingAddress.getString("receiverMobile"));
                        receiverAddress.setText(shippingAddress.getString("receiverState")+shippingAddress.getString("receiverCity")+shippingAddress.getString("receiverDistrict")+shippingAddress.getString("receiverAddress"));
                        status=shippingOrder.getString("orderStatus");
                        //createTime,orderNo,orderTotalFee,productFee,productFees
                        orderNo.setText("订单编号: "+shippingOrder.getString("orderNo"));
                        orderTotalFee.setText("¥"+shippingOrder.getString("orderTotalFee"));
                        productFee.setText("¥"+shippingOrder.getString("orderTotalFee"));
                        productFees.setText("¥"+shippingOrder.getString("orderTotalFee"));
                        createTime.setText("创建时间: "+insertComma.stampToDate(shippingOrder.getString("createTime")));

                        for(int j=0;j<orderStatuses.size();j++){
                            if(status!=null){
                                if(status.equals(orderStatuses.get(j).getStr())){
                                    orderStatus.setText(orderStatuses.get(j).getText());
                                }
                            }

                        }

                        if(status.equals("01")){
                            pay.setVisibility(View.VISIBLE);
                            cancel_order.setVisibility(View.VISIBLE);
                        }
                        if(status.equals("02")){
                            pay.setVisibility(View.GONE);
                            cancel_order.setVisibility(View.VISIBLE);
                        }
                        if(status.equals("03")){
                            pay.setVisibility(View.GONE);
                            cancel_order.setVisibility(View.GONE);
                        }

                        if(status.equals("04")||status.equals("07")||status.equals("08")){
                            pay.setVisibility(View.GONE);
                            cancel_order.setVisibility(View.GONE);
                        }
                        if(status.equals("07")){
                            logistics_lin.setVisibility(View.VISIBLE);
                            logistics.setText(shippingOrder.getString("logistics"));
                            expressno.setText(shippingOrder.getString("expressNo"));
                            try {
                                deliverTime.setText(insertComma.dateToStamp(shippingOrder.getString("deliverTime")));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        JSONArray shippingOrderItems=object.getJSONArray("shippingOrderItems");
                        for(int i=0;i<shippingOrderItems.length();i++){
                            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.orderviewdata,null);
                            JSONObject obj=shippingOrderItems.getJSONObject(i);
                            TextView productTitle=(TextView)view.findViewById(R.id.productTitle);
                            TextView quantity=(TextView)view.findViewById(R.id.quantity);
                            TextView productFee=(TextView)view.findViewById(R.id.productFee);
                            TextView productAttrs=(TextView)view.findViewById(R.id.productAttrs);
                            ImageView productItemImg=(ImageView)view.findViewById(R.id.productItemImg);
                            double orderFee=Double.parseDouble(obj.getString("orderFee"));
                            double quantitys=Double.parseDouble(obj.getString("quantity"));
                            double number= 0;
                            try {
                                number = insertComma.div(orderFee,quantitys,1);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            int price = (new Double(number)).intValue();
                            productTitle.setText(obj.getString("productTitle"));
                            quantity.setText("X"+obj.getString("quantity"));
                            productFee.setText("¥"+new DecimalFormat("0.00").format(price));
                            productAttrs.setText(obj.getString("productAttrs"));
                            Glide.with(getApplicationContext()).load(Constant.loadimag+obj.getString("productItemImg")).into(productItemImg);
                            lin_group.addView(view);
                        }
                        break;

                    case 2:
                            JSONObject statusobj=new JSONObject(msg.obj.toString());
                            JSONArray statuslist = statusobj.getJSONArray("object");
                            for(int i=0;i<statuslist.length();i++){
                                JSONObject object1=statuslist.getJSONObject(i);
                                orderStatuses.add(new OrderStatus(object1.getString("key"),object1.getString("value")));
                            }
                        break;

                    case 3:
                        Log.e("TAG", "handleMessage: "+msg.obj.toString());
                        final String orderInfo = msg.obj.toString();

                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(OrderDetailsActivity.this);
                                Map<String, String> result = alipay.payV2(orderInfo, true);
                                Log.i("msp", result.toString());

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };

                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                        break;
                    case 4:
                        JSONObject jsonObject=new JSONObject(msg.obj.toString());
                        if(jsonObject.getString("code").equals("200")){
                            Toast.makeText(getApplicationContext(),"取消成功",Toast.LENGTH_SHORT).show();
                            intentstatus="flag";
                            initUI();
                            lin_group.removeAllViews();
                            loadorderDetail();
                            loadorderstatu();
                        }
                        break;
                    case SDK_PAY_FLAG: {
                        @SuppressWarnings("unchecked")
                        PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                        /**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, "9000")) {
                            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                            Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
                            intentstatus="flag";
                            initUI();
                            loadorderDetail();
                            loadorderstatu();
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v==pay){
            popWindow.show();
        }
        if(v==copy_btn){
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", expressno.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast toast = Toast.makeText(getApplicationContext(),"复制成功",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        if(v==close_pop){
            popWindow.dismiss();
        }
        if(v==alipay){
            //支付宝支付
            new HttpUtils().Post(Constant.APPURLS+"order/alipay/"+orderNostr,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
                    com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                    Message msg= Message.obtain(
                            mHandler,3,data
                    );
                    mHandler.sendMessage(msg);
                }

            });
        }
        if(v==cancel_order){

            new CommomDialog(this, R.style.dialog, "确定取消订单？", new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if(confirm){
                        //取消订单
                        new HttpUtils().Post(Constant.APPURLS+"order/cancel/"+orderNostr,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

                            @Override
                            public void onSuccess(String data) {
                                // TODO Auto-generated method stub
                                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                                Message msg= Message.obtain(
                                        mHandler,4,data
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
}
