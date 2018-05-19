package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.util.OrderInfoUtil2_0;
import com.example.zhujia.dx_shop.util.PayResult;
import com.hmy.popwindow.PopWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ConfirmationOrder extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    public static final int  INTENT=1003;
    private String LoginState,TOKEN,loginUserId;
    private Intent intent;
    private PopWindow popWindow;
    private View customView;
    private SharedPreferences sharedPreferences;
    private LinearLayout select_address,lin_group,confirm,alipay;
    private TextView shouhuoren,shouhuorendizhi,zongjiprice,prdouct_num,heji;
    private String image,quantity,salePrice,id,productName,productAttr,totals,addressId,person,phone,address,orderNo;

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2018051560151642";
    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDbRY+P700VP5NB9CctVCAtKEFdq7zGhwvH19Ub12hfEu8P/4sFcJsjRtyegwnM1fslGGLEQUGyOVF+SyyWfiVVms4x8eewZWSgVGsP/GtChgfjoql7tfwfvEbvqK0qlrlMxRV/zaD5fJUzPIDMyc3NCHceYfoAMQCeSTOsopMOpmJbN86ZO/yhBhHnkbwHDKn/nAzNWkiC6YGWH77Jpc++2r0Wm7ygs/3vjUQXp9+/Ef7QYE7yEi8pDio8Wog4UWIdx746xngCG1lUBliI3IjZpy9Btji22rjvmyHivJ47umR0kWwBab32MwLnKljMl0jEMHo46UD4HMo1Z2Gb5YovAgMBAAECggEAC2VMFsDrEqWhM5ZKyvJKUc57xzB7uR2foR04eB/zzq0fO4eeZAP8LBMsuBg7VCfyy3S+/VAe5/JOp++kO5YGvrIy8SmPTv2WAzcf8U6/DFAG7O+xOzq0Bbo1PZNYnJQEwVakzWWUbiLpZiZI01vRlExSVIPnCEeDrrZDNz+jwOkQHAYEg8O4prKYmzMMCIzIUwpryAHIbVB1Z8MW6d61C9901wvb9xLpBJmFGsOPy/KgEUgVZDfSquk2JuL7KRekRCcWhEdR/JTOK0txghVS5KF2K2Q9DKSEj88X5qYu1oY3xjf0yGCRrIxfF5jKq3IW/R+1f0S/FyxzZxS21wLZYQKBgQD7ZUDwvo5EsEww88KpHNCjJy3gz2liCq9GbfHCGvm5ruakELxcqMqg4t+FAKVVW5kqCKxGkEXhnNJqljy8qZEcFmi94042xlkfDGAHHGfsG1bcezn7plaYw39CYAyJzLeTcLuZYHUUYHuHugQBQDs55U8ONsiRF5XXPktJXKNoeQKBgQDfSa9FvmZvJwyb0bYtjWyD7WtnfNiqaIb99XvNtZ2WGAh88y4Rbf/nryMrwIYubYMNTvuJL6Y0Y4FErWWlXrzdRObXzVXPmL/EHlcJmtfAhhD6K8W2nAxgyV9j0GYCV3sz5QoPVYrUyzpwgX1b48pczAmparvxkv62khV8iH0t5wKBgETfybnrSwjwj/89WVCBr3tziXM0//c3XCvchUabo11G2LOMTj7Ik7MeVQSYV7OaA81rUN+IOU0FbPR8sqkxf6sFTK7xboU9ND94YXqxQpuCJfsD26/hM4pozR2/ONAsAQNo+EyeC5WIlW9q/BvnoBJNi6omQi7WGlw9Gd1AfxhpAoGAKtD/WhlbnR7YzRxWmb+PunCRcuO+zFaE5DfpiJW15S0wm30qLU9xjeTIksA1OrvnMGsf0rufXFZkWF4DqsVS4/8tYN5QASHiS+IhgIb/rjxHDyHLpOuheAHFV+eDWBmcV2QpHK2jlJBv4VkDR9aTXF4H0EPWTEmKrEmw8xNdJasCgYBkV19xuwYcG1WC0M5nJPAJ1zBjGkY2d+TYBKGM3LdRkKoKSVaGfFygtjGMuG8cXEjn8+DsVdeVMt4Z/jRU/FOuEpE8NVAkx3c06XJ+ANtiWGPV0ZyOOUU0gbisgsYTFZBsgaiYX4ELi3Bp5DiFJOBvNVb3ZZbf0rvyRIett6nn6g==";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 3;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmationorder);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        intent=getIntent();
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        addressId=sharedPreferences.getString("addressId","");
        person=sharedPreferences.getString("person","");
        phone=sharedPreferences.getString("phone","");
        address=sharedPreferences.getString("address","");
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
        loadintent();
        initlistener();
        initOrder();
    }

    private void  initUI(){

        select_address=(LinearLayout)findViewById(R.id.select_address);
        shouhuoren=(TextView)findViewById(R.id.shouhuoren);
        shouhuorendizhi=(TextView)findViewById(R.id.shouhuorendizhi);
        lin_group=(LinearLayout)findViewById(R.id.lin_group);
        zongjiprice=(TextView)findViewById(R.id.zongjiprice);
        prdouct_num=(TextView)findViewById(R.id.prdouct_num);
        confirm=(LinearLayout)findViewById(R.id.confirm);
        heji=(TextView) findViewById(R.id.heji);
        customView = View.inflate(getApplicationContext(), R.layout.paytype, null);
        alipay=(LinearLayout)customView.findViewById(R.id.alipay);
        alipay.setOnClickListener(this);
        popWindow = new PopWindow.Builder(ConfirmationOrder.this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();

    }

    private void loadintent(){
        shouhuoren.setText(person+"         "+phone);
        shouhuorendizhi.setText(address);
        image=intent.getStringExtra("image").replace("[","").replace("]","").replace(" ","");
        quantity=intent.getStringExtra("quantity").replace("[","").replace("]","");
        salePrice=intent.getStringExtra("salePrice").replace("[","").replace("]","");
        id=intent.getStringExtra("id").replace("[","").replace("]","");
        productName=intent.getStringExtra("productName").replace("[","").replace("]","");
        totals=intent.getStringExtra("totals").replace("[","").replace("]","");
        productAttr=intent.getStringExtra("productAttr").replace("[","").replace("]","").replace(" ","");

    }


    private void initlistener(){
        select_address.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case INTENT:
                    shouhuoren.setText(data.getStringExtra("person")+"         "+data.getStringExtra("phone"));
                    shouhuorendizhi.setText(data.getStringExtra("address"));
                    addressId=data.getStringExtra("id");
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initOrder(){

        String [] images= image.split(",");
        String [] quantitys=quantity.split(",");
        String [] salePrices=salePrice.split(",");
        String []ids=id.split(",");
        String[]productNames=productName.split(",");
        String[]productAttrs=productAttr.split(",");
        Log.e("TAG", "initOrder: "+productAttrs.length );
        String[]total1=totals.split(",");

        int num=0;
        for(int i=0;i<images.length;i++){
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.orderconfirmation,null);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            TextView productName=(TextView)view.findViewById(R.id.productName);
            TextView productAttr=(TextView)view.findViewById(R.id.productAttr);
            TextView salePrice=(TextView)view.findViewById(R.id.salePrice);
            TextView quantity=(TextView)view.findViewById(R.id.quantity);
            quantity.setText("X"+quantitys[i]);
            productAttr.setText(productAttrs[i]);
            salePrice.setText("￥"+salePrices[i]);
            Glide.with(getApplicationContext()).load(Constant.loadimag+images[i]).into(image);
            //order_total.setText("￥"+total1[i]);
            lin_group.addView(view);
            num++;

        }
        String numstr= String.valueOf(num);
        prdouct_num.setText("共计"+numstr+"件商品");
        float total=0;

        for(int i=0;i<total1.length;i++)
        {
            String totals=total1[i];
            total=total+Float.parseFloat(totals.trim());
        }
        String t= String.valueOf(total);
        zongjiprice.setText("￥"+t);
        heji.setText("￥"+t);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        if(v==select_address){
            //选择收货地址
            intent=new Intent(getApplicationContext(),AddressManagementActivity.class);
            startActivityForResult(intent,INTENT);
        }

        if(v==alipay){
            //支付宝支付
            new HttpUtils().Post(Constant.APPURLS+"order/alipay/"+orderNo,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

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
        if(v==confirm){
            //提交订单

            Log.e("TAG", "onClick: "+TOKEN+"  "+loginUserId);
            try {
                String [] stringArr= id.split(",");
                JSONArray j=new JSONArray(stringArr);
                JSONObject object = new JSONObject();

                    object.put("addressId",addressId);
                    object.put("productItemIds",j);
                    final String params=object.toString().replaceAll( "\\\\","").replace(" ", "");
                Log.e("TAG", "addressId: "+params);
                //新增
                new HttpUtils().postJson(Constant.APPURLS+"order/shopping/confirm",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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

            try {

                switch (msg.what){
                    case 1:
                        JSONObject object=new JSONObject(msg.obj.toString());
                        if(object.getString("code").equals("200")){
                            orderNo=object.getString("msg");
                            popWindow.show();

                        }
                        break;

                    case 2:
                        Log.e("TAG", "handleMessage: "+msg.obj.toString());
                        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
                            new AlertDialog.Builder(getApplicationContext()).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            //
                                            finish();
                                        }
                                    }).show();
                            return;
                        }
                        final String orderInfo = msg.obj.toString();

                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(ConfirmationOrder.this);
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
                            finish();
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
}
