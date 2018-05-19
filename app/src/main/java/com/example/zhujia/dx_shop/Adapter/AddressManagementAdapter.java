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
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.BaseRecyclerAdapter;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.zhujia.dx_shop.Activity.AddressManagementActivity.INTENT;

/**
 * Created by ZHUJIA on 2018/3/14.
 */

public class AddressManagementAdapter extends BaseRecyclerAdapter<BaseRecyclerAdapter.BaseRecyclerViewHolder> implements View.OnClickListener  {

    private List<Data> datas;
    public AddressManagementActivity context;
    private int type=0;
    private SharedPreferences sharedPreferences;
    private Handler mHandler;
    private AddressManagementAdapter.OnitemClickListener onitemClickListener=null;
    @SuppressLint("WrongConstant")
    public AddressManagementAdapter(AddressManagementActivity context1, List<Data>data){
        this.context=context1;
        this.datas=data;
        sharedPreferences =context1.getSharedPreferences("Session", Context.MODE_APPEND);

    }
    @Override
    public void onClick(View view) {
        if(onitemClickListener!=null){
            onitemClickListener.onItemClick(view,(int)view.getTag());
        }
    }

    public void setOnitemClickListener(AddressManagementAdapter.OnitemClickListener onitemClickListener) {
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

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addressmanagement_data, parent, false);
            AddressManagementAdapter.LinearViewHolder linearViewHolder = new AddressManagementAdapter.LinearViewHolder(baseView);
            baseView.setOnClickListener(this);
            return linearViewHolder;

        }
        else {

            baseView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addressmanagement_data, parent, false);
            AddressManagementAdapter.GridViewHolder gridViewHolder = new AddressManagementAdapter.GridViewHolder(baseView);
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
            final AddressManagementAdapter.LinearViewHolder linearViewHolder= (AddressManagementAdapter.LinearViewHolder) holder;
            linearViewHolder.person.setText(datas.get(position).getPerson());
            linearViewHolder.phone.setText(datas.get(position).getPhone());
            linearViewHolder.address.setText(datas.get(position).getReceiveProvince()+datas.get(position).getReceiveCity()+datas.get(position).getReceiveDistrict()+" "+datas.get(position).getAddress());
            if(datas.get(position).getIsDefault().equals("Y")){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                //系统用户
                editor.putString("addressId",datas.get(position).getId());
                editor.putString("person",datas.get(position).getPerson());
                editor.putString("phone",datas.get(position).getPhone());
                editor.putString("address",datas.get(position).getReceiveProvince()+datas.get(position).getReceiveCity()+datas.get(position).getReceiveDistrict()+" "+datas.get(position).getAddress());
                editor.commit();
                linearViewHolder.isDefault.setChecked(true);
                linearViewHolder.isDefault.setBackground(context.getResources().getDrawable(R.drawable.radio_button));
            }else {
                linearViewHolder.isDefault.setChecked(false);
                linearViewHolder.isDefault.setBackground(context.getResources().getDrawable(R.drawable.radio_button));
            }

            //设为默认
            linearViewHolder.choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearViewHolder.isDefault.setChecked(true);
                    context.isDefault(datas.get(position).getId(),"Y");
                }
            });
            //删除
            linearViewHolder.del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showPopwindows(datas.get(position).getId(),position);
                }
            });

            //编辑
            linearViewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//新增信息
                   Intent intent =new Intent(context,AddAddressActivity.class);
                    intent.putExtra("type","2");
                    intent.putExtra("id",datas.get(position).getId());
                    intent.putExtra("person",datas.get(position).getPerson());
                    intent.putExtra("phone",datas.get(position).getPhone());
                    intent.putExtra("receiveProvince",datas.get(position).getReceiveProvince());
                    intent.putExtra("receiveCity",datas.get(position).getReceiveCity());
                    intent.putExtra("receiveDistrict",datas.get(position).getReceiveDistrict());
                    intent.putExtra("address",datas.get(position).getAddress());
                    context.startActivityForResult(intent,INTENT);
                }
            });

            linearViewHolder.lin_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.putExtra("id",datas.get(position).getId());
                    intent.putExtra("person",datas.get(position).getPerson());
                    intent.putExtra("phone",datas.get(position).getPhone());
                    intent.putExtra("address",datas.get(position).getReceiveProvince()+datas.get(position).getReceiveCity()+datas.get(position).getReceiveDistrict()+" "+datas.get(position).getAddress());
                    context.setResult(RESULT_OK,intent);
                    context.finish();
                }
            });


        }
    }







    public static class LinearViewHolder extends BaseRecyclerViewHolder {

        private TextView person,phone,address;
        private RadioButton isDefault;
        private LinearLayout lin_btn,del_btn,edit_btn,choose;
        public LinearViewHolder(View itemView) {
            super(itemView);
            del_btn=(LinearLayout) itemView.findViewById(R.id.del_btn);
            edit_btn=(LinearLayout)itemView.findViewById(R.id.edit_btn);
            isDefault=(RadioButton)itemView.findViewById(R.id.isDefault);
            person=(TextView)itemView.findViewById(R.id.person);
            phone=(TextView)itemView.findViewById(R.id.phone);
            address=(TextView)itemView.findViewById(R.id.address);
            lin_btn=(LinearLayout)itemView.findViewById(R.id.lin_btn);
            choose=(LinearLayout)itemView.findViewById(R.id.choose);
        }
    }

    public static class GridViewHolder extends BaseRecyclerViewHolder {


        public GridViewHolder(View itemView) {
            super(itemView);

        }
    }

}