package com.example.zhujia.dx_shop.Fragment;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.NewProduct;
import com.example.zhujia.dx_shop.Activity.ProductDetailsActivity;
import com.example.zhujia.dx_shop.Activity.SearchActivity;
import com.example.zhujia.dx_shop.Activity.SearchProduct;
import com.example.zhujia.dx_shop.Adapter.AddressManagementAdapter;
import com.example.zhujia.dx_shop.Adapter.GoodsListRecyclerViewAdapter;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.zhujia.dx_shop.Tools.insertComma.isMobile;

//产品列表
public class TypePage extends Fragment implements View.OnClickListener ,OnRefreshListener,OnLoadMoreListener {

    private View view;
    private Intent intent;
    private String iconUrl,nickNamestr,customerNamestr;
    private SharedPreferences sharedPreferences;
    private Button add_address_btn;
    JSONObject object;
    JSONObject pager;
    boolean hasMoreData;
    private Handler mHandler;
    private List<Data> mListData=new ArrayList<>();
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    private String LoginState,TOKEN,loginUserId;
    private  int total;
    private SuperRefreshRecyclerView recyclerView;
    private boolean flag=false;
    private TextView shaixuan;
    private int goodsType=0;
    private LinearLayout layout_search;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mDrawerContent;
    private ImageView ivGoodsType;
    private GoodsListRecyclerViewAdapter adapter;
    private ImageView ivStick;//置顶
    private String catalog,type,series,brand;

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.type_page,container,false);
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        initUI();
        Bundle bundle=getArguments();
        if(bundle!=null){
            this.catalog=bundle.getString("catalogs");
            this.type=bundle.getString("types");
            this.series=bundle.getString("seriess");
            this.brand=bundle.getString("brands");
        }else {
            this.catalog="";
            this.type="";
            this.series="";
            this.brand="";
        }

        loaddata();//加载列表数据
        adapter=new GoodsListRecyclerViewAdapter(getData(),getActivity());

        return view;
    }

    public void file(String catalog,String type,String series,String brand,Context context){
        this.catalog=catalog;
        this.type=type;
        this.series=series;
        this.brand=brand;
        loaddata();//加载列表数据
        adapter=new GoodsListRecyclerViewAdapter(getData(),context);

    }

    private void initUI(){
        //初始化
        layout_search=(LinearLayout)view.findViewById(R.id.layout_search);
        layout_search.setOnClickListener(this);
        recyclerView= (SuperRefreshRecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.inits(new LinearLayoutManager(getActivity()),this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
        ivGoodsType= (ImageView) view.findViewById(R.id.iv_goods_type);
        ivGoodsType.setOnClickListener(this);
        ivStick= (ImageView)view.findViewById(R.id.iv_stick);
        ivStick.setOnClickListener(this);
        shaixuan=(TextView)view.findViewById(R.id.shaixuan);
        shaixuan.setOnClickListener(this);
        if(goodsType!=0){
            DividerItemDecoration divider = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
            divider.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.reccyclerviewf));
            recyclerView.addItemDecoration(divider);
        }
        ivGoodsType.setImageResource(R.mipmap.good_type_grid);
        //设置滑动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见位置
                    int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
                    //当滑动到第十个以上时显示置顶图标
                    if (firstVisibleItemPosition>10) {
                        ivStick.setVisibility(View.VISIBLE);
                    }else {
                        ivStick.setVisibility(View.GONE);
                    }
                }
            }
        });
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerContent = (FrameLayout) view.findViewById(R.id.drawer_content);
        Fragment fragment = new FilterFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_content,fragment).commit();
    }




    private void loaddata(){
        Log.e("TAG", "loaddata: "+catalog+type+series+brand );
        new HttpUtils().Get(Constant.APPURLS+"product/search?catalog="+catalog+"&type="+type+"&series="+series+"&brand="+brand+"&startRow=0&pageSize=20",new HttpUtils.HttpCallback() {
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
    //加载更多
    private void initItemMoreData() {

        new HttpUtils().Get(Constant.APPURLS+"product/search?catalog="+catalog+"&type="+type+"&series="+series+"&brand="+brand+"startRow="+total+"&pageSize=20",new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");
                if ((null == data) || (data.equals(""))) {
                    // 网络连接异常

                    mHandler.sendEmptyMessage(9);

                }else {
                    JSONObject resulutJsonobj;

                    try
                    {

                        resulutJsonobj=new JSONObject(data);
                            contentjsonarry=resulutJsonobj.getJSONArray("itemsList");
                            if(contentjsonarry.length()<0){
                                hasMoreData=false;
                            }
                            hasMoreData=true;
                            fillDataToList(contentjsonarry);
                            if(!hasMoreData){
                                mHandler.sendEmptyMessage(4);
                            }else {
                                mHandler.sendEmptyMessage(3);
                            }



                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
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
            Log.e("TAG", "onHiddenChanged: "+"产品列表不可见" );
            mDrawerLayout.closeDrawer(mDrawerContent);
        } else {
            // 相当于Fragment的onResume
            Log.e("TAG", "onHiddenChanged: "+"产品列表可见" );

        }
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
                            reslutJSONObject=new JSONObject(msg.obj.toString());
                            total=reslutJSONObject.getInt("total");
                            mListData.clear();
                            contentjsonarry=reslutJSONObject.getJSONArray("itemsList");
                            fillDataToList(contentjsonarry);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            recyclerView.showData();
                            recyclerView.setRefreshing(false);
                            adapter.setOnitemClickListener(new GoodsListRecyclerViewAdapter.OnitemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                  intent =new Intent(getActivity(),ProductDetailsActivity.class);
                                  intent.putExtra("id",mListData.get(position).getId());
                                  startActivity(intent);
                                }
                            });
                            break;


                        case 1:
                            JSONObject header=new JSONObject(msg.obj.toString());
                            if(header.getString("code").equals("200")){
                                loaddata();
                            }
                            break;

                        case 3:
                            adapter.notifyDataSetChanged();
                            recyclerView.setLoadingMore(false);
                            break;
                        case 4:
                            adapter.notifyDataSetChanged();
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



    private void fillDataToList(JSONArray data) throws JSONException {

        Data rechargData = null;
        for (int i = 0; i < data.length(); i++) {
            rechargData = new Data();
            JSONObject object = data.getJSONObject(i);
            rechargData.setId(object.getString("id"));
            rechargData.setModel_img(object.getString("model_img"));
            rechargData.setOriginal_img(object.getString("original_img"));
            rechargData.setCatalog_name(object.getString("catalog_name"));
            rechargData.setType_name(object.getString("type_name"));
            rechargData.setBrand_name(object.getString("brand_name"));
            rechargData.setModel_no(object.getString("model_no"));
            rechargData.setSale_price(object.getString("sale_price"));
            rechargData.setSmall_img(object.getString("small_img"));
            rechargData.setCatalog_id(object.getString("catalog_id"));
            rechargData.setModel_name(object.getString("model_name"));
            rechargData.setSeries_name(object.getString("series_name"));
            rechargData.setModel_title(object.getString("model_title"));
            rechargData.setStatus(object.getString("status"));
            if(!object.isNull("promotionTitle")){
                rechargData.setPromotionTitle(object.getString("promotionTitle"));
            }else {
                rechargData.setPromotionTitle("null");
            }
            mListData.add(rechargData);
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_goods_type://切换布局
                if (goodsType==0)
                {
                    mListData.clear();
                    ivGoodsType.setImageResource(R.mipmap.good_type_linear);
                    //1：设置布局类型
                    loaddata();
                    adapter = new GoodsListRecyclerViewAdapter(getData(),getActivity());
                    adapter.notifyDataSetChanged();
                    adapter.setType(1);
                    //2：设置对应的布局管理器
                    recyclerView.inits(new GridLayoutManager(getActivity(),2),this,this);
                    //3：刷新adapter
                    adapter.notifyDataSetChanged();
                    goodsType=1;
                    flag=true;

                }else
                {
                    mListData.clear();
                    ivGoodsType.setImageResource(R.mipmap.good_type_grid);
                    loaddata();
                    adapter = new GoodsListRecyclerViewAdapter(getData(),getActivity());
                    adapter.notifyDataSetChanged();
                    adapter.setType(0);
                    recyclerView.inits(new LinearLayoutManager(getActivity()),this,this);
                    adapter.notifyDataSetChanged();
                    goodsType=0;
                    flag=false;
                }
                break;
            case R.id.iv_stick://置顶
                recyclerView.scrollToPosition(0);
                break;
            case R.id.layout_search:
                Intent intent=new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.shaixuan:
                //筛选
                mDrawerLayout.openDrawer(mDrawerContent);
                break;

        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {



            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoadMore() {
        initItemMoreData();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListData.clear();
                adapter.notifyDataSetChanged();
                catalog="";
                type="";
                series="";
                brand="";
                loaddata();
            }
        },1000);
    }
}
