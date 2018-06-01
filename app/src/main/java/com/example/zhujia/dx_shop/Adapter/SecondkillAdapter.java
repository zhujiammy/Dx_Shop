package com.example.zhujia.dx_shop.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.ProductDetailskillActivity;
import com.example.zhujia.dx_shop.Activity.SecondkillActivity;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.Data.SecondkillData;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.BaseRecyclerAdapter;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.insertComma;
import com.lid.lib.LabelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import jp.shts.android.library.TriangleLabelView;


/**
 * Created by ZhengJiao on 2017/4/27.
 */
public class SecondkillAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener{


    private List<SecondkillData> data;
    private final SecondkillActivity context;
    private SharedPreferences sharedPreferences;
    private int viewType = 0;//0:LinearViewHolder  1:GridViewHolder
    @SuppressLint("WrongConstant")
    public SecondkillAdapter(List<SecondkillData> data, SecondkillActivity context) {
        this.context = context;
        this.data=data;
        sharedPreferences =context.getSharedPreferences("Session",
                Context.MODE_APPEND);
    }


    private OnitemClickListener onitemClickListener=null;



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
        this.viewType = type;
    }

    @Override
    //用来获取当前项Item是哪种类型的布局
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View baseView;
        if (viewType == 0) {
            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview_kill, parent, false);
            LinearViewHolder linearViewHolder = new LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;
        } else {
            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gridview_goods_list, parent, false);
            GridViewHolder gridViewHolder = new GridViewHolder(baseView);
            baseView.setOnClickListener(this);
            return gridViewHolder;
        }

    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position, List<Map<String, Object>> data) {

    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, final int position) {
        if (viewType==0){
            final LinearViewHolder linearViewHolder= (LinearViewHolder) holder;
            Glide.with(context).load(Constant.loadimag+data.get(position).getListImg()).into(linearViewHolder.model_img);
            linearViewHolder.model_title.setText(data.get(position).getModelName());
            linearViewHolder.sale_price.setText("¥"+data.get(position).getSalePrice());
            linearViewHolder.sale_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            linearViewHolder.crushsale_price.setText("¥"+data.get(position).getCrush_salePrice());
            linearViewHolder.progesss1.setMax(Integer.parseInt(data.get(position).getTotalQuantity()));
            linearViewHolder.progesss1.setProgress(Integer.parseInt(data.get(position).getUsedQuantity()));
            double UsedQuantit=Double.parseDouble(data.get(position).getUsedQuantity());
            double TotalQuantity=Double.parseDouble(data.get(position).getTotalQuantity());
            try {
                double  number = insertComma.div(UsedQuantit,TotalQuantity,2);
                Log.e("TAG", "onBindViewHolder: "+UsedQuantit+"，"+ TotalQuantity);
                double bfb=number*100;
                int i = (new Double(bfb)).intValue();

                linearViewHolder.ysh.setText("已售"+i+"%");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }



            if(data.get(position).getSwitchs().equals("0")){
                //还没开始
                try {
                    long startTime= Long.parseLong(insertComma.dateToStamp(data.get(position).getStartTime()));
                    new CountDownTimer(startTime-System.currentTimeMillis(),1000){

                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onTick(long millisUntilFinished) {
                            linearViewHolder.title.setText("距开始:");
                            linearViewHolder.time.setText(insertComma.secToTime(Math.toIntExact(millisUntilFinished / 1000)));
                        }

                        @Override
                        public void onFinish() {
                            context.onRefresh();
                        }
                    }.start();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            if(data.get(position).getSwitchs().equals("1")){
                //已经开始
                try {
                    long startTime= Long.parseLong(insertComma.dateToStamp(data.get(position).getEndTime()));
                    new CountDownTimer(startTime-System.currentTimeMillis(),1000){


                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onTick(long millisUntilFinished) {
                            linearViewHolder.title.setText("距结束:");
                            linearViewHolder.time.setText(insertComma.secToTime(Math.toIntExact(millisUntilFinished / 1000)));
                        }

                        @Override
                        public void onFinish() {
                            context.onRefresh();

                        }
                    }.start();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            linearViewHolder.goshop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent intent =new Intent(context,ProductDetailskillActivity.class);
                    intent.putExtra("skuId",data.get(position).getSkuId());
                    intent.putExtra("ID",data.get(position).getId());
                    intent.putExtra("switch",data.get(position).getSwitchs());
                    context.startActivity(intent);
                }
            });
            linearViewHolder.itemView.setTag(position);


        }else {
            GridViewHolder gridViewHolder= (GridViewHolder) holder;
            gridViewHolder.itemView.setTag(position);

            LabelView label = new LabelView(context);


            if (position%2==0)
                gridViewHolder.rightView.setVisibility(View.VISIBLE);
            else
                gridViewHolder.rightView.setVisibility(View.GONE);
            gridViewHolder.itemView.setTag(position);
        }
    }


    public static class LinearViewHolder extends BaseRecyclerViewHolder {

        private com.lid.lib.LabelImageView model_img;
        private TextView model_title,series_name,sale_price,crushsale_price,time,title,ysh;
        private ImageView add_cart;
        private Button goshop;
        private ProgressBar progesss1;


        public LinearViewHolder(View itemView) {
            super(itemView);
            model_img= (com.lid.lib.LabelImageView) itemView.findViewById(R.id.model_img);
            model_title= (TextView) itemView.findViewById(R.id.model_title);
            series_name= (TextView) itemView.findViewById(R.id.series_name);
            sale_price= (TextView) itemView.findViewById(R.id.sale_price);
            crushsale_price=(TextView)itemView.findViewById(R.id.crushsale_price);
            time=(TextView)itemView.findViewById(R.id.time);
            title=(TextView)itemView.findViewById(R.id.title);
            goshop=(Button)itemView.findViewById(R.id.goshop);
            ysh=(TextView)itemView.findViewById(R.id.ysh);
            progesss1=(ProgressBar)itemView.findViewById(R.id.progesss1);
        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {

        private com.lid.lib.LabelImageView model_img;
        private TextView model_title,series_name,sale_price,label;
        private View rightView;
        private LinearLayout labelview;

        public GridViewHolder(View itemView) {
            super(itemView);
            model_img= (com.lid.lib.LabelImageView) itemView.findViewById(R.id.model_img);
            model_title= (TextView) itemView.findViewById(R.id.model_title);
            series_name= (TextView) itemView.findViewById(R.id.series_name);
            sale_price= (TextView) itemView.findViewById(R.id.sale_price);
            rightView=itemView.findViewById(R.id.view_right);
            labelview=(LinearLayout)itemView.findViewById(R.id.labelview);
            label=(TextView)itemView.findViewById(R.id.label);


        }
    }

}
