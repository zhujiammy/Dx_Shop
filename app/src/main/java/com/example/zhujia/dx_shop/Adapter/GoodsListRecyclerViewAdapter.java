package com.example.zhujia.dx_shop.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.BaseRecyclerAdapter;
import com.example.zhujia.dx_shop.Tools.Net.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * Created by ZhengJiao on 2017/4/27.
 */
public class GoodsListRecyclerViewAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener{


    private List<Data> data;
    private final Context context;
    private SharedPreferences sharedPreferences;
    private int viewType = 0;//0:LinearViewHolder  1:GridViewHolder
    @SuppressLint("WrongConstant")
    public GoodsListRecyclerViewAdapter(List<Data> data, Context context) {
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
            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview_goods_list, parent, false);
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
            LinearViewHolder linearViewHolder= (LinearViewHolder) holder;
            if(!data.get(position).getModel_img().equals("")){
                Glide.with(context).load(Constant.loadimag+data.get(position).getModel_img()).into(linearViewHolder.model_img);
            }else {
                linearViewHolder.model_img.setImageDrawable(context.getResources().getDrawable(R.mipmap.mrt));
            }
            linearViewHolder.model_title.setText(data.get(position).getModel_title());
            linearViewHolder.series_name.setText(data.get(position).getSeries_name());
            linearViewHolder.sale_price.setText(data.get(position).getSale_price());
            linearViewHolder.itemView.setTag(position);

        }else {
            GridViewHolder gridViewHolder= (GridViewHolder) holder;
            gridViewHolder.itemView.setTag(position);
            if(!data.get(position).getModel_img().equals("")){
                Glide.with(context).load(Constant.loadimag+data.get(position).getModel_img()).into(gridViewHolder.model_img);
            }else {
                gridViewHolder.model_img.setImageDrawable(context.getResources().getDrawable(R.mipmap.mrt));
            }
            gridViewHolder.model_title.setText(data.get(position).getModel_title());
            gridViewHolder.series_name.setText(data.get(position).getSeries_name());
            gridViewHolder.sale_price.setText(data.get(position).getSale_price());

            if (position%2==0)
                gridViewHolder.rightView.setVisibility(View.VISIBLE);
            else
                gridViewHolder.rightView.setVisibility(View.GONE);
            gridViewHolder.itemView.setTag(position);
        }
    }


    public static class LinearViewHolder extends BaseRecyclerViewHolder {

        private ImageView model_img;
        private TextView model_title,series_name,sale_price;
        private ImageView add_cart;

        public LinearViewHolder(View itemView) {
            super(itemView);
            model_img= (ImageView) itemView.findViewById(R.id.model_img);
            model_title= (TextView) itemView.findViewById(R.id.model_title);
            series_name= (TextView) itemView.findViewById(R.id.series_name);
            sale_price= (TextView) itemView.findViewById(R.id.sale_price);
        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {

        private ImageView model_img;
        private TextView model_title,series_name,sale_price;
        private View rightView;

        public GridViewHolder(View itemView) {
            super(itemView);
            model_img= (ImageView) itemView.findViewById(R.id.model_img);
            model_title= (TextView) itemView.findViewById(R.id.model_title);
            series_name= (TextView) itemView.findViewById(R.id.series_name);
            sale_price= (TextView) itemView.findViewById(R.id.sale_price);
            rightView=itemView.findViewById(R.id.view_right);
        }
    }

}
