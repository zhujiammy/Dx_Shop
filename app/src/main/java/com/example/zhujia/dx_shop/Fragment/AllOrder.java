package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.example.zhujia.dx_shop.Activity.OrderDetailsActivity;
import com.example.zhujia.dx_shop.Activity.WxpayActivity;
import com.example.zhujia.dx_shop.Adapter.AddressManagementAdapter;
import com.example.zhujia.dx_shop.Adapter.AllOrderAdapter;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.Data.OrderData;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.CommomDialog;
import com.example.zhujia.dx_shop.Tools.LoadingAlertDialog;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.Net.NetWorkUtils;
import com.example.zhujia.dx_shop.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_shop.Tools.OnRefreshListener;
import com.example.zhujia.dx_shop.Tools.SuperRefreshRecyclerView;
import com.example.zhujia.dx_shop.util.PayResult;
import com.hmy.popwindow.PopWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * 所有订单
 * */

public class AllOrder extends Fragment implements OnRefreshListener,OnLoadMoreListener,View.OnClickListener {

    private View view;
    private SuperRefreshRecyclerView recyclerView;
    private String LoginState,TOKEN,loginUserId;
    private SharedPreferences sharedPreferences;
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    boolean hasMoreData;
    public static final int  INTENT=1004;
    private AllOrderAdapter adapter;
    private List<OrderData> mListData=new ArrayList<>();
    private Handler mHandler;
    private String OrderStatus;
    private PopWindow popWindow;
    private String orderNostr;
    private LinearLayout alipay,wxpay;
    private ImageView close_pop;
    private View customView;
    private Context context;
    LoadingAlertDialog dialog1;
    private NetWorkUtils netWorkUtils;//网络状态
    private static final int SDK_PAY_FLAG = 5;
    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.allorder,container,false);
        initUI();
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        loadorderstatu();//加载订单状态
        loaddata();
        context=getActivity();
        adapter=new AllOrderAdapter(AllOrder.this,getData());
        return view;
    }

    private void initUI(){
        recyclerView= (SuperRefreshRecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.init(this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLoadingMoreEnable(false);
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.reccyclerviewf));
        recyclerView.addItemDecoration(divider);
        customView = View.inflate(getActivity(), R.layout.paytype, null);
        alipay=(LinearLayout)customView.findViewById(R.id.alipay);
        alipay.setOnClickListener(this);
        wxpay=(LinearLayout)customView.findViewById(R.id.wxpay);
        wxpay.setOnClickListener(this);
        close_pop=(ImageView)customView.findViewById(R.id.close_pop);
        close_pop.setOnClickListener(this);
        popWindow = new PopWindow.Builder(getActivity())
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();
/*
        View  emtview=View.inflate(getActivity(),R.layout.emtview,null);

        recyclerView.setEmptyView(emtview);
        recyclerView.showEmpty(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }


    private void loadorderstatu(){
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/enums",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
    }

    private void loaddata(){
        dialog1=new LoadingAlertDialog(getActivity());
        dialog1.show("加载中");
        if(netWorkUtils.isNetworkConnected(getActivity())){
            new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/list",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
        }else {
            dialog1.dismiss();
            Toast.makeText(getActivity(),"当前无网络连接",Toast.LENGTH_SHORT).show();
        }

    }


    @SuppressLint("NewApi")
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            //相当于Fragment的onPause
            Log.e("TAG", "onHiddenChanged: "+"全部订单不可见" );
        } else {
            // 相当于Fragment的onResume
            Log.e("TAG", "onHiddenChanged: "+"全部订单可见" );
        }
    }


    /**
     * 消息处理Handler
     */
    @SuppressLint("HandlerLeak")
    private List<OrderData>getData(){
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                try{
                    switch (msg.what) {

                        case 0:
                            //返回item类型数据
                            reslutJSONObject=new JSONObject(msg.obj.toString());
                            if(reslutJSONObject.getString("code").equals("404")){
                                dialog1.dismiss();
                                View  emtview=View.inflate(getContext(),R.layout.emtview,null);
                                recyclerView.setEmptyView(emtview);
                                recyclerView.showEmpty(new View.OnClickListener() {
                                    public void onClick(View v) {

                                    }
                                });
                            }else {
                                mListData.clear();
                                fillDataToList(reslutJSONObject);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                recyclerView.showData();
                                recyclerView.setRefreshing(false);
                                recyclerView.setLoadingMore(false);
                                dialog1.dismiss();
                            }
                            break;


                        case 1:
                            JSONObject header=new JSONObject(msg.obj.toString());
                            if(header.getString("code").equals("200")){
                                OrderStatus=header.getString("object");
                            }
                            break;
                        case 2:
                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Log.e("TAG", "handleMessage: "+msg.obj.toString());
                            final String orderInfo = msg.obj.toString();
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(getActivity());
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
                            dialog1.dismiss();
                            break;
                        case 4:
                            JSONObject jsonObject=new JSONObject(msg.obj.toString());
                            String msgs=jsonObject.getString("msg");
                            if(jsonObject.getString("code").equals("200")){
                                dialog1.dismiss();
                                Toast.makeText(getActivity(),msgs,Toast.LENGTH_SHORT).show();
                                onRefresh();
                            }
                            if(jsonObject.getString("code").equals("503")){
                                dialog1.dismiss();
                                Toast.makeText(getActivity(),msgs,Toast.LENGTH_SHORT).show();
                                onRefresh();
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
                                Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                                onRefresh();

                            } else {
                                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                                Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                                onRefresh();
                            }
                            break;
                        }
                        case 6:
                            JSONObject okjsonobject=new JSONObject(msg.obj.toString());
                            String msgstr=okjsonobject.getString("msg");
                            if(okjsonobject.getString("code").equals("200")){
                                dialog1.dismiss();
                                Toast.makeText(getActivity(),msgstr,Toast.LENGTH_SHORT).show();
                                onRefresh();
                            }
                            if(okjsonobject.getString("code").equals("503")){
                                dialog1.dismiss();
                                Toast.makeText(getActivity(),msgstr,Toast.LENGTH_SHORT).show();
                                onRefresh();
                            }
                            break;
                        case 7:
                            Log.e("TAG", "handleMessage: "+msg.obj.toString());
                            JSONObject object1=new JSONObject(msg.obj.toString());
                            if(object1.getString("code").equals("200")){
                                dialog1.dismiss();
                                Intent intent=new Intent(getActivity(),WxpayActivity.class);
                                intent.putExtra("msg",object1.getString("msg"));
                                startActivity(intent);
                            }
                            break;
                        default:
                            Toast.makeText(getActivity(), "网络异常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        return mListData;
    }


    public void  orderDetails(int position){
        Intent intent=new Intent(getActivity(),OrderDetailsActivity.class);
        intent.putExtra("orderNo",mListData.get(position).getOrderNo());
        intent.putExtra("types","1");
        startActivityForResult(intent,INTENT);

    }

    @SuppressLint("NewApi")
    private void fillDataToList(JSONObject data) throws JSONException {

        mListData.clear();
        contentjsonarry=data.getJSONArray("object");
        OrderData rechargData = null;
        for (int i = 0; i < contentjsonarry.length(); i++) {
            rechargData = new OrderData();
            JSONObject object = contentjsonarry.getJSONObject(i);
            JSONObject order=object.getJSONObject("order");
            rechargData.setOrderItems(object.getString("orderItems"));
            rechargData.setOrderNo(order.getString("orderNo"));
            rechargData.setCustomerId(order.getString("customerId"));
            rechargData.setCreateTime(order.getString("createTime"));
            rechargData.setOrderType(order.getString("orderType"));
            rechargData.setOrderTotalFee(order.getDouble("orderTotalFee"));
            rechargData.setPaiedFee(order.getDouble("paiedFee"));
            rechargData.setDiscountFee(order.getDouble("discountFee"));
            rechargData.setProductFee(order.getDouble("productFee"));
            rechargData.setOrderStatus(order.getString("orderStatus"));
            rechargData.setPaiedTime(order.getString("paiedTime"));
            rechargData.setOrderSource(order.getString("orderSource"));
            rechargData.setPostageFee(order.getString("postageFee"));
            rechargData.setDeliverTime(order.getString("deliverTime"));
            rechargData.setExpressNo(order.getString("expressNo"));
            rechargData.setLogistics(order.getString("logistics"));
            rechargData.setIsRefund(order.getString("isRefund"));
            rechargData.setRefundTime(order.getString("refundTime"));
            rechargData.setRefundFee(order.getString("refundFee"));
            rechargData.setIsReutrn(order.getString("isReutrn"));
            rechargData.setReturnTime(order.getString("returnTime"));
            rechargData.setCancelTime(order.getString("cancelTime"));
            rechargData.setDiscountInfo(order.getString("discountInfo"));
            rechargData.setStatuslist(OrderStatus);
            mListData.add(rechargData);
        }

    }



    @Override
    public void onLoadMore() {

    }

    public void pay(String orderNostr){
        this.orderNostr=orderNostr;
        popWindow.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case INTENT:
                    if(data.getStringExtra("statue").equals("1")){
                        recyclerView.setRefreshing(true);
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void cancelorder(final String orderNostr){
        this.orderNostr=orderNostr;
        new CommomDialog(getActivity(), R.style.dialog, "确定取消订单？", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    //取消订单
                    dialog1=new LoadingAlertDialog(getActivity());
                    dialog1.show("请稍等...");
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
        }).setTitle("提示").show();
    }

    public void okorder(final String orderNostr){
        this.orderNostr=orderNostr;
        new CommomDialog(getActivity(), R.style.dialog, "确认签收？", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    //取消订单
                    dialog1=new LoadingAlertDialog(getActivity());
                    dialog1.show("请稍等...");
                    new HttpUtils().Post(Constant.APPURLS+"order/receive/"+orderNostr,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

                        @Override
                        public void onSuccess(String data) {
                            // TODO Auto-generated method stub
                            com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                            Message msg= Message.obtain(
                                    mHandler,6,data
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

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                mListData.clear();
                loaddata();
            }
        },1000);
    }

    @Override
    public void onClick(View v) {
        if(v==close_pop){
            popWindow.dismiss();
        }
        if(v==alipay){
            //支付宝支付
            dialog1=new LoadingAlertDialog(getActivity());
            dialog1.show("请稍等...");
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

        if(v==wxpay){
            //微信支付
            dialog1=new LoadingAlertDialog(getActivity());
            dialog1.show("请稍等...");
            new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/weixin/h5/"+orderNostr,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

                @Override
                public void onSuccess(String data) {
                    // TODO Auto-generated method stub
                    com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                    Message msg= Message.obtain(
                            mHandler,7,data
                    );
                    mHandler.sendMessage(msg);
                }

            });
        }
    }
}
