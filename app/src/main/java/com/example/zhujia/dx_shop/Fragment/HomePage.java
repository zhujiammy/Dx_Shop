package com.example.zhujia.dx_shop.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.NewProduct;
import com.example.zhujia.dx_shop.Activity.ProductDetailsActivity;
import com.example.zhujia.dx_shop.Activity.ProductTypeActivity;
import com.example.zhujia.dx_shop.Activity.SecondkillActivity;
import com.example.zhujia.dx_shop.Adapter.GoodsListRecyclerViewAdapter;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.FixedGridLayout;
import com.example.zhujia.dx_shop.Tools.FragmentUserVisibleController;
import com.example.zhujia.dx_shop.Tools.GlideImageLoader;
import com.example.zhujia.dx_shop.Tools.MaskableImageView;
import com.example.zhujia.dx_shop.Tools.MyScrollView;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_shop.Tools.OnRefreshListener;
import com.example.zhujia.dx_shop.Tools.SuperRefreshRecyclerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DXSW5 on 2018/3/14.
 */

public class HomePage extends Fragment implements View.OnClickListener, FragmentUserVisibleController.UserVisibleCallback,OnRefreshListener,OnLoadMoreListener {

    private View view;
    private Banner banner;
    private ViewGroup.LayoutParams lp;
    private TextView MSG,QR;

    private Handler mHandler;
    private List<String>list;
    private MyScrollView scrolll;
    private RelativeLayout line;
    private TextView seach_text;
    private SharedPreferences sharedPreferences;
    private  Drawable drawable,drawableqr,drawablemsg;
    private String LoginState,TOKEN,loginUserId;
    private FragmentUserVisibleController userVisibleController;
    private LinearLayout lin_center,ggw;
    private LayoutInflater inflater;
    private ImageView newproduct;
    private SuperRefreshRecyclerView recyclerView;
    private GoodsListRecyclerViewAdapter adapter;
    private List<Data> mListData=new ArrayList<>();
    JSONObject reslutJSONObject;
    JSONArray contentjsonarry;
    private Intent intent;
    private ImageView Secondkill;


    public HomePage(){
        userVisibleController = new FragmentUserVisibleController(this, this);
    }

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fullScreen(getActivity());
        view=inflater.inflate(R.layout.home_page_xml,container,false);
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        initUI();
        getposter();
        getadvert();
        loaddata();//加载列表数据
        adapter=new GoodsListRecyclerViewAdapter(getData(),getActivity());
        return view;
    }

    private void loaddata(){
        new HttpUtils().Get(Constant.APPURLS+"index/index/product",new HttpUtils.HttpCallback() {
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

    @SuppressLint("HandlerLeak")
    private List<Data>getData(){
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                try{
                    switch (msg.what) {
                        case 0:
                            JSONObject object=new JSONObject(msg.obj.toString());
                            JSONArray objectarray=object.getJSONArray("object");
                            list=new ArrayList<>();
                            for(int i=0;i<objectarray.length();i++){
                                JSONObject object1=objectarray.getJSONObject(i);
                                list.add(Constant.loadimag+object1.getString("phoneUrl"));
                            }

                            //设置图片加载器
                            banner.setImageLoader(new GlideImageLoader());
                            banner.setDelayTime(3000);
                            //设置图片集合
                            banner.setImages(list);
                            //banner设置方法全部调用完毕时最后调用
                            banner.start();
                            break;

                        case 1:
                            JSONObject objects=new JSONObject(msg.obj.toString());
                            JSONArray objectarrays=objects.getJSONArray("object");
                            Log.e("TAG", "handleMessage: "+objectarrays.length() );
                            for(int i=0;i<objectarrays.length();i++){
                                View views = View.inflate(getActivity(),R.layout.advertisingposition,null);
                                LinearLayout linearLayout=(LinearLayout)views.findViewById(R.id.lin2);
                                JSONObject object1=objectarrays.getJSONObject(i);
                                JSONArray list=object1.getJSONArray("list");
                                for(int j=0;j<list.length();j++){

                                    View viewd = View.inflate(getActivity(),R.layout.advertisement,null);
                                    final JSONObject object2=list.getJSONObject(j);
                                    float wei=object2.getInt("proportion");
                                    LinearLayout.LayoutParams weight1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, wei);
                                    ImageView imageView=(ImageView)viewd.findViewById(R.id.images);
                                    Glide.with(getActivity()).load(Constant.loadimag+object2.getString("imgUrl")).into(imageView);
                                    final String linkUrl=object2.getString("linkUrl");
                                    viewd.setLayoutParams(weight1);
                                    linearLayout.addView(viewd);

                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(getActivity(),ProductTypeActivity.class);
                                            intent.putExtra("linkUrl",linkUrl);
                                            startActivity(intent);
                                        }
                                    });
                                }
                                ggw.addView(views);
                            }
                            break;

                        case 2:
                            //返回item类型数据
                            reslutJSONObject=new JSONObject(msg.obj.toString());
                            mListData.clear();
                            contentjsonarry=reslutJSONObject.getJSONArray("object");
                            fillDataToList(contentjsonarry);
                            recyclerView.setAdapter(adapter);
                            adapter.setType(1);
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


    private void getposter(){
        //首页轮播图
        new HttpUtils().Get(Constant.APPURLS+"index/poster",new HttpUtils.HttpCallback() {
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

    private void getadvert(){
        //广告位
        new HttpUtils().Get(Constant.APPURLS+"index/advert",new HttpUtils.HttpCallback() {
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



    private void initUI(){
        scrolll=(MyScrollView)view.findViewById(R.id.scrolll);
        scrolll.setOnScrollListener(listener);
        line=(RelativeLayout)view.findViewById(R.id.line);
        seach_text=(TextView)view.findViewById(R.id.seach_text);
        QR=(TextView)view.findViewById(R.id.QR);
        MSG=(TextView)view.findViewById(R.id.MSG);
        Secondkill=(ImageView)view.findViewById(R.id.Secondkill);
        Secondkill.setOnClickListener(this);
        banner = (Banner) view.findViewById(R.id.banner);
       // lin_center=(LinearLayout)view.findViewById(R.id.lin_center);
        ggw=(LinearLayout)view.findViewById(R.id.ggw);
        newproduct=(ImageView)view.findViewById(R.id.newproduct);
        newproduct.setOnClickListener(this);
        //初始化
        recyclerView= (SuperRefreshRecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.inits(new GridLayoutManager(getActivity(),2),this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);
       /*
        for(int i=0;i<2;i++){
            View views = View.inflate(getActivity(),R.layout.lin_center,null);
            lin_center.addView(views);
        }*/


    }








    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    @Override
    public void onClick(View v) {

        if(v==newproduct){
            Intent intent=new Intent(getActivity(),NewProduct.class);
            startActivity(intent);
        }
        if(v==Secondkill){
            intent=new Intent(getActivity(),SecondkillActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userVisibleController.activityCreated();
    }

    @Override
    public void onResume() {
        super.onResume();
        userVisibleController.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        userVisibleController.pause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        userVisibleController.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void setWaitingShowToUser(boolean waitingShowToUser) {
        userVisibleController.setWaitingShowToUser(waitingShowToUser);
    }

    @Override
    public boolean isWaitingShowToUser() {
        return userVisibleController.isWaitingShowToUser();
    }

    @Override
    public boolean isVisibleToUser() {
        return userVisibleController.isVisibleToUser();
    }

    @Override
    public void callSuperSetUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {


    }

    @SuppressLint("NewApi")
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            //相当于Fragment的onPause
            Log.e("TAG", "onHiddenChanged: "+"不可见" );
        } else {
            // 相当于Fragment的onResume
            Log.e("TAG", "onHiddenChanged: "+"可见" );
            Log.e("TAGsss", "onHiddenChanged: "+line.getBackground().getOpacity());
            if(String.valueOf(line.getBackground().getOpacity()).equals("-2")){
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                seach_text.setBackground(getActivity().getResources().getDrawable(R.drawable.seachstyle));
                drawable = getResources().getDrawable(R.drawable.seach_s);
                drawable.setBounds(0, 0, 40, 40);
                seach_text.setCompoundDrawables(drawable,null,null,null);
                seach_text.setHintTextColor(getActivity().getResources().getColor(R.color.monsoon));

                drawableqr = getResources().getDrawable(R.mipmap.qr);
                drawableqr.setBounds(0, 0, 48, 48);
                drawablemsg = getResources().getDrawable(R.mipmap.msg);
                drawablemsg.setBounds(0, 0, 48, 48);
                QR.setTextColor(getActivity().getResources().getColor(R.color.white));
                QR.setCompoundDrawables(null,drawableqr,null,null);
                MSG.setTextColor(getActivity().getResources().getColor(R.color.white));
                MSG.setCompoundDrawables(null,drawablemsg,null,null);
            }else {
                seach_text.setBackground(getActivity().getResources().getDrawable(R.drawable.seachstyles));
                seach_text.setHintTextColor(getActivity().getResources().getColor(R.color.white));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                drawable = getResources().getDrawable(R.drawable.seach_w);
                drawable.setBounds(0, 0, 40, 40);
                seach_text.setCompoundDrawables(drawable,null,null,null);

                drawableqr = getResources().getDrawable(R.mipmap.qrs);
                drawableqr.setBounds(0, 0, 48, 48);
                drawablemsg = getResources().getDrawable(R.mipmap.msgs);
                drawablemsg.setBounds(0, 0, 48, 48);
                QR.setTextColor(getActivity().getResources().getColor(R.color.monsoon));
                QR.setCompoundDrawables(null,drawableqr,null,null);
                MSG.setTextColor(getActivity().getResources().getColor(R.color.monsoon));
                MSG.setCompoundDrawables(null,drawablemsg,null,null);
            }


        }
    }

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }



    //将像素转换为px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //将px转换为dp
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    MyScrollView.OnScrollListener listener=new MyScrollView.OnScrollListener() {
        @SuppressLint("NewApi")
        @Override
        public void onScroll(int scrollY) {

            int i=dip2px(getActivity(),scrollY);
            int dp=px2dp(getActivity(),i);
            if(dp>200){
                seach_text.setBackground(getActivity().getResources().getDrawable(R.drawable.seachstyles));
                seach_text.setHintTextColor(getActivity().getResources().getColor(R.color.white));
                line.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                drawable = getResources().getDrawable(R.drawable.seach_w);
                drawable.setBounds(0, 0, 40, 40);
                seach_text.setCompoundDrawables(drawable,null,null,null);


                drawableqr = getResources().getDrawable(R.mipmap.qrs);
                drawableqr.setBounds(0, 0, 48, 48);
                drawablemsg = getResources().getDrawable(R.mipmap.msgs);
                drawablemsg.setBounds(0, 0, 48, 48);
                QR.setTextColor(getActivity().getResources().getColor(R.color.monsoon));
                QR.setCompoundDrawables(null,drawableqr,null,null);
                MSG.setTextColor(getActivity().getResources().getColor(R.color.monsoon));
                MSG.setCompoundDrawables(null,drawablemsg,null,null);

                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            }else {
                line.setBackgroundColor(getActivity().getResources().getColor(R.color.tran));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                seach_text.setBackground(getActivity().getResources().getDrawable(R.drawable.seachstyle));
                seach_text.setHintTextColor(getActivity().getResources().getColor(R.color.monsoon));
                drawable = getResources().getDrawable(R.drawable.seach_s);
                drawable.setBounds(0, 0, 40, 40);
                seach_text.setCompoundDrawables(drawable,null,null,null);

                drawableqr = getResources().getDrawable(R.mipmap.qr);
                drawableqr.setBounds(0, 0, 48, 48);
                drawablemsg = getResources().getDrawable(R.mipmap.msg);
                drawablemsg.setBounds(0, 0, 48, 48);
                QR.setTextColor(getActivity().getResources().getColor(R.color.white));
                QR.setCompoundDrawables(null,drawableqr,null,null);
                MSG.setTextColor(getActivity().getResources().getColor(R.color.white));
                MSG.setCompoundDrawables(null,drawablemsg,null,null);

            }


        }
    };

    @Override
    public void onLoadMore() {

    }

    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListData.clear();
                adapter.notifyDataSetChanged();
                loaddata();
            }
        },1000);
    }
}
