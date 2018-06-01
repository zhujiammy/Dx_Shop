package com.example.zhujia.dx_shop.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.AddAddressActivity;
import com.example.zhujia.dx_shop.Activity.AddressManagementActivity;
import com.example.zhujia.dx_shop.Activity.PersonalInfoActivity;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.Data.OrderData;
import com.example.zhujia.dx_shop.Data.OrderStatus;
import com.example.zhujia.dx_shop.Fragment.AllOrder;
import com.example.zhujia.dx_shop.Fragment.Completed;
import com.example.zhujia.dx_shop.Fragment.Goodsreceived;
import com.example.zhujia.dx_shop.Fragment.PendingDelivery;
import com.example.zhujia.dx_shop.Fragment.PendingPayment;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.BaseRecyclerAdapter;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.insertComma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.zhujia.dx_shop.Activity.AddressManagementActivity.INTENT;

/**
 * Created by ZHUJIA on 2018/3/14.
 */

public class CompletedAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<OrderData> datas;
    public Completed context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private Handler mHandler;
    private List<OrderStatus> orderStatuses= new ArrayList<OrderStatus>();
    private OnitemClickListener onitemClickListener=null;
    private  LinearLayout lin_btn;
    @SuppressLint("WrongConstant")
    public CompletedAdapter(Completed context1, List<OrderData>data){
        this.context=context1;
        this.datas=data;
        sharedPreferences =context1.getActivity().getSharedPreferences("Session", Context.MODE_APPEND);

    }
    @Override
    public void onClick(View view) {
        if(onitemClickListener!=null){
            onitemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    public void setOnitemClickListener(OnitemClickListener onitemClickListener) {
        this.onitemClickListener = onitemClickListener;
    }

    public static interface OnitemClickListener{
        void onItemClick(View view, int position);
    }


    //点击切换布局的时候通过这个方法设置type
    public void setType(int type) {
        this.type = type;
    }

    @Override
    //用来获取当前项Item是哪种类型的布局
    public int getItemViewType(int position) {
        return type;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View baseView;
        if (viewType == 0) {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.allorder_data, parent, false);
            CompletedAdapter.LinearViewHolder linearViewHolder = new CompletedAdapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.allorder_data, parent, false);
            CompletedAdapter.GridViewHolder gridViewHolder = new CompletedAdapter.GridViewHolder(baseView);
            baseView.setOnClickListener(this);
            return gridViewHolder;
        }

    }



    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, List<Map<String, Object>> data) {

    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, final int position) {
        if (type==0){
            final CompletedAdapter.LinearViewHolder linearViewHolder= (CompletedAdapter.LinearViewHolder) holder;
            linearViewHolder.productFee.setText("合计:"+new DecimalFormat("0.00").format(datas.get(position).getOrderTotalFee()));
            linearViewHolder.orderNo.setText("订单编号:"+datas.get(position).getOrderNo());
            linearViewHolder.itemView.setTag(position);
            linearViewHolder.lin_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.orderDetails(position);
                }
            });




            try {
                JSONArray orderItems=new JSONArray(datas.get(position).getOrderItems());
                linearViewHolder.order_view.removeAllViews();
                int num=0;
                for(int i=0;i<orderItems.length();i++){
                    View view= LayoutInflater.from(context.getActivity()).inflate(R.layout.orderviewdata,null);
                    lin_btn=(LinearLayout)view.findViewById(R.id.lin_btn);
                    JSONObject object=orderItems.getJSONObject(i);
                    TextView productTitle=(TextView)view.findViewById(R.id.productTitle);
                    TextView quantity=(TextView)view.findViewById(R.id.quantity);
                    TextView productFee=(TextView)view.findViewById(R.id.productFee);
                    TextView productAttrs=(TextView)view.findViewById(R.id.productAttrs);
                    ImageView productItemImg=(ImageView)view.findViewById(R.id.productItemImg);
                    double orderFee=Double.parseDouble(object.getString("orderFee"));
                    double quantitys=Double.parseDouble(object.getString("quantity"));
                    double number= 0;
                    try {
                        number = insertComma.div(orderFee,quantitys,1);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    int price = (new Double(number)).intValue();

                    productTitle.setText(object.getString("productTitle"));
                    quantity.setText("X"+object.getString("quantity"));
                    productFee.setText("¥"+new DecimalFormat("0.00").format(price));
                    productAttrs.setText(object.getString("productAttrs"));
                    Glide.with(context).load(Constant.loadimag+object.getString("productItemImg")).into(productItemImg);
                    num++;
                    linearViewHolder.num.setText("共计"+String.valueOf(num)+"商品");
                    linearViewHolder.order_view.addView(view);
                    lin_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.orderDetails(position);
                        }
                    });
                }

                if(datas.get(position).getStatuslist()!=null){
                    JSONArray statuslist = new JSONArray(datas.get(position).getStatuslist());
                    for(int i=0;i<statuslist.length();i++){
                        JSONObject object1=statuslist.getJSONObject(i);
                        orderStatuses.add(new OrderStatus(object1.getString("key"),object1.getString("value")));

                    }
                    for(int j=0;j<orderStatuses.size();j++){
                        if(datas.get(position).getOrderStatus().equals(orderStatuses.get(j).getStr())){
                            linearViewHolder.orderstatu.setText(orderStatuses.get(j).getText());
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }







    public static class LinearViewHolder extends BaseRecyclerViewHolder {

        private TextView num,productFee,orderNo,orderstatu;
        private LinearLayout order_view,lin_btn;
        public LinearViewHolder(View itemView) {
            super(itemView);
            num=(TextView)itemView.findViewById(R.id.num);
            productFee=(TextView)itemView.findViewById(R.id.productFee);
            orderNo=(TextView)itemView.findViewById(R.id.orderNo);
            order_view=(LinearLayout)itemView.findViewById(R.id.order_view);
            orderstatu=(TextView)itemView.findViewById(R.id.orderstatu);
            lin_btn=(LinearLayout)itemView.findViewById(R.id.lin_btn);


        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}