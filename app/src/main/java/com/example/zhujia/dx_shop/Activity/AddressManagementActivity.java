package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Adapter.AddressManagementAdapter;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.CustomDialog;
import com.example.zhujia.dx_shop.Tools.IEditTextChangeListener;
import com.example.zhujia.dx_shop.Tools.ImageService;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_shop.Tools.OnRefreshListener;
import com.example.zhujia.dx_shop.Tools.SuperRefreshRecyclerView;
import com.example.zhujia.dx_shop.Tools.WorksSizeCheckUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.zhujia.dx_shop.Tools.insertComma.isMobile;

//地址管理
public class AddressManagementActivity extends AppCompatActivity implements View.OnClickListener ,OnRefreshListener,OnLoadMoreListener {




    private Toolbar toolbar;
    private Intent intent;
    private String iconUrl,nickNamestr,customerNamestr;
    private SharedPreferences sharedPreferences;
    private Button add_address_btn;
    JSONObject object;
    JSONObject pager;
    boolean hasMoreData;
    private Handler mHandler;
    private List<Data> mListData=new ArrayList<>();
    private int pageindex=1;
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    public static final int  REQUEST_CODE=1001;
    private AddressManagementAdapter adapter;
    private String LoginState,TOKEN,loginUserId;
    private  int total;
    private SuperRefreshRecyclerView recyclerView;
    public static final int  INTENT=1002;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addressmanagement);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        intent=getIntent();
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
        loaddata();//加载列表数据
        adapter=new AddressManagementAdapter(this,getData());


    }



    private void initUI(){
        //初始化
        recyclerView= (SuperRefreshRecyclerView)findViewById(R.id.recyclerview);
        recyclerView.init(this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
        add_address_btn=(Button)findViewById(R.id.add_address_btn);
        add_address_btn.setOnClickListener(this);
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.reccyclerviewf));
        recyclerView.addItemDecoration(divider);
        recyclerView.setLoadingMoreEnable(false);
    }






    private void loaddata(){
        new HttpUtils().Post(Constant.APPURLS+"address",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
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


    @SuppressLint("HandlerLeak")
    private List<Data>getData(){
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                try{
                    switch (msg.what) {

                        case 0:
                            //返回item类型数据
                            contentjsonarry=new JSONArray(msg.obj.toString());
                            mListData.clear();
                            fillDataToList(contentjsonarry);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerView.showData();
                            recyclerView.setRefreshing(false);
                            recyclerView.setLoadingMore(false);
                            break;


                        case 1:
                            JSONObject header=new JSONObject(msg.obj.toString());
                            if(header.getString("code").equals("200")){
                                loaddata();
                            }
                            break;

                        case 2:
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        return mListData;
    }



    private void fillDataToList(JSONArray data) throws JSONException {

            Data rechargData = null;
            for (int i = 0; i < data.length(); i++) {
                rechargData = new Data();
                JSONObject object = data.getJSONObject(i);
                rechargData.setId(object.getString("id"));
                rechargData.setPerson(object.getString("person"));
                rechargData.setPhone(object.getString("phone"));
                rechargData.setReceiveProvince(object.getString("receiveProvince"));
                rechargData.setReceiveCity(object.getString("receiveCity"));
                rechargData.setReceiveDistrict(object.getString("receiveDistrict"));
                rechargData.setAddress(object.getString("address"));
                rechargData.setTel(object.getString("tel"));
                rechargData.setIsDefault(object.getString("isDefault"));
                mListData.add(rechargData);
            }

    }


    @Override
    public void onClick(View v) {
        if(v==add_address_btn){
            //新增地址
            intent =new Intent(getApplicationContext(),AddAddressActivity.class);
            intent.putExtra("type","1");
            startActivityForResult(intent,INTENT);
        }

    }

    public void isDefault(String id,String isDefault){
        new HttpUtils().Post(Constant.APPURLS+"address/updateDefault?id="+id+"&isDefault="+isDefault+"",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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


    public void showPopwindows(final String id,final int position){
        View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        final View popView = View.inflate(getApplicationContext(), R.layout.popdialog, null);

        Button del_btn = (Button) popView.findViewById(R.id.del_btn);
        Button cancel_btn = (Button) popView.findViewById(R.id.cancel_btn);

        int width =getResources().getDisplayMetrics().widthPixels;
        int height =getResources().getDisplayMetrics().heightPixels;

        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.del_btn:
                        new HttpUtils().Post(Constant.APPURLS+"address/delete?id="+id+"",TOKEN,loginUserId,new HttpUtils.HttpCallback() {

                            @Override
                            public void onSuccess(String data) {
                                // TODO Auto-generated method stub
                                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                                try {
                                    org.json.JSONObject reslutJSONObject=new org.json.JSONObject(data);
                                    if(reslutJSONObject.getString("code").equals("200")){
                                        Message msg= Message.obtain(
                                                mHandler,2,data
                                        );
                                        mHandler.sendMessage(msg);

                                        mListData.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        break;
                    case R.id.cancel_btn:
                        popWindow.dismiss();
                        break;

                }
                popWindow.dismiss();
            }
        };

        del_btn.setOnClickListener(listener);
        cancel_btn.setOnClickListener(listener);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case INTENT:
                    if(data.getStringExtra("statue").equals("save")){
                       recyclerView.setRefreshing(true);
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
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
                mListData.clear();
                loaddata();
            }
        },1000);
    }
}
