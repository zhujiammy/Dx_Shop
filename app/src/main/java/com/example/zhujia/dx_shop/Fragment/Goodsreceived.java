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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zhujia.dx_shop.Activity.OrderDetailsActivity;
import com.example.zhujia.dx_shop.Adapter.AddressManagementAdapter;
import com.example.zhujia.dx_shop.Adapter.AllOrderAdapter;
import com.example.zhujia.dx_shop.Adapter.GoodsreceivedAdapter;
import com.example.zhujia.dx_shop.Adapter.PendingDeliveryAdapter;
import com.example.zhujia.dx_shop.Adapter.PendingPaymentAdapter;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.Data.OrderData;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_shop.Tools.OnRefreshListener;
import com.example.zhujia.dx_shop.Tools.SuperRefreshRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
 * 待收货
 * */

public class Goodsreceived extends Fragment implements OnRefreshListener,OnLoadMoreListener {

    private View view;
    private SuperRefreshRecyclerView recyclerView;
    private String LoginState,TOKEN,loginUserId;
    private SharedPreferences sharedPreferences;
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    boolean hasMoreData;
    private GoodsreceivedAdapter adapter;
    private List<OrderData> mListData=new ArrayList<>();
    private Handler mHandler;
    private String OrderStatus;
    private Context context;
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
        adapter=new GoodsreceivedAdapter(Goodsreceived.this,getData());
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
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"order/list?orderStatus=07",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
                                View  emtview=View.inflate(getActivity(),R.layout.emtview,null);

                                recyclerView.setEmptyView(emtview);
                                recyclerView.showEmpty(new View.OnClickListener() {
                                    @Override
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
        startActivity(intent);
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
}
