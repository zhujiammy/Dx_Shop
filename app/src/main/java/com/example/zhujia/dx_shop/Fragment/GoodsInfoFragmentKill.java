package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.example.zhujia.dx_shop.Activity.AddressManagementActivity;
import com.example.zhujia.dx_shop.Activity.OrderDetailsActivity;
import com.example.zhujia.dx_shop.Activity.ProductDetailskillActivity;
import com.example.zhujia.dx_shop.Data.RecommendGoodsBean;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.FNRadioGroup;
import com.example.zhujia.dx_shop.Tools.GlideImageLoader;
import com.example.zhujia.dx_shop.Tools.GoodsConfigFragmentKill;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.SlideDetailsLayout;
import com.example.zhujia.dx_shop.Tools.insertComma;
import com.example.zhujia.dx_shop.util.PayResult;
import com.gxz.PagerSlidingTabStrip;
import com.youth.banner.Banner;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * item页ViewPager里的商品Fragment
 */
public class GoodsInfoFragmentKill extends Fragment implements View.OnClickListener, SlideDetailsLayout.OnSlideDetailsListener {
    private PagerSlidingTabStrip psts_tabs;
    private SlideDetailsLayout sv_switch;
    private ScrollView sv_goods_info;
    private FloatingActionButton fab_up_slide;
    public ConvenientBanner  vp_recommend;
    private Banner banner;
    private LinearLayout ll_goods_detail, ll_goods_config;
    private TextView tv_goods_detail, tv_goods_config,tv_goods_title,title_tv;
    private View v_tab_cursor;
    public FrameLayout fl_content;
    public LinearLayout  ll_activity, ll_recommend, ll_pull_up,select_address;

    private ImageView img;
    private TextView itemNo;
    /** 当前商品详情数据页的索引分别是图文详情、规格参数 */
    private int nowIndex;
    private float fromX;
    public GoodsConfigFragmentKill goodsConfigFragment;
    public GoodsInfoWebFragment goodsInfoWebFragment;
    private Fragment nowFragment;
    private List<TextView> tabTextList;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    public ProductDetailskillActivity activity;
    private LayoutInflater inflater;
    private FNRadioGroup groupradion;
    private TextView attrKey_tv,attrKeys;
    private String productModelAttrsID,addressId,person,phone,address;
    private String productItemId;
    private String productModelAttr;
    private String ysid="null",zcid="null";
    private LinearLayout lins;
    private ImageView ivSub ;
    private EditText etGoodNum;
    private List<String>imgUrls;
    private ImageView iv_add,iv_sub,close_pop;
    private int countNum = 1;//购买商品数量
    private TextView add_carts,salePrice,crushsalePrice,times,shouhuoren,shouhuorendizhi;
    private String LoginState,TOKEN,loginUserId,orderNo;
    private SharedPreferences sharedPreferences;
    private PopupWindow popWindow;
    private View customView;
    private String id,ID;
    private Bundle bundle;
    private String switchs;
    private Intent intent;
    private int quantity;
    private LinearLayout alipay;
    private static final int SDK_PAY_FLAG = 4;
    private View rootview;
    public static final int  INTENT=1004;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ProductDetailskillActivity) context;
    }



    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }




    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_goods_info_kill, null);
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        addressId=sharedPreferences.getString("addressId","");
        person=sharedPreferences.getString("person","");
        phone=sharedPreferences.getString("phone","");
        address=sharedPreferences.getString("address","");
        initView(rootView);
        initListener();
        initData();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
        if(isAdded()){
            id=getArguments().getString("id");
            ID=getArguments().getString("ID");
            Log.e("TAG", "onStart: "+id );
        }

        loaddata();
        setDetailData();

    }

    private void initListener() {
        fab_up_slide.setOnClickListener(this);
        ll_activity.setOnClickListener(this);
        ll_pull_up.setOnClickListener(this);
        ll_goods_detail.setOnClickListener(this);
        ll_goods_config.setOnClickListener(this);
        sv_switch.setOnSlideDetailsListener(this);
    }

    private void initView(View rootView) {
        customView = View.inflate(getActivity(), R.layout.paytype, null);
        rootview = rootView.findViewById(R.id.root_main);
        alipay=(LinearLayout)customView.findViewById(R.id.alipay);
        alipay.setOnClickListener(this);
        close_pop=(ImageView)customView.findViewById(R.id.close_pop);
        close_pop.setOnClickListener(this);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        popWindow=new PopupWindow(customView,width,height);
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(false);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setOnDismissListener(new poponDismissListener());
        etGoodNum = (EditText) rootView.findViewById(R.id.quantity);
        iv_sub = (ImageView) rootView.findViewById(R.id.iv_sub);
        iv_sub.setOnClickListener(this);
        iv_add = (ImageView) rootView.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        shouhuoren=(TextView)rootView.findViewById(R.id.shouhuoren);
        shouhuorendizhi=(TextView)rootView.findViewById(R.id.shouhuorendizhi);
        shouhuoren.setText(person+"         "+phone);
        shouhuorendizhi.setText(address);
        banner = (Banner) rootView.findViewById(R.id.banner);
        psts_tabs = (PagerSlidingTabStrip)rootView.findViewById(R.id.psts_tabs);
        fab_up_slide = (FloatingActionButton) rootView.findViewById(R.id.fab_up_slide);
        sv_switch = (SlideDetailsLayout) rootView.findViewById(R.id.sv_switch);
        sv_goods_info = (ScrollView) rootView.findViewById(R.id.sv_goods_info);
        v_tab_cursor = rootView.findViewById(R.id.v_tab_cursor);
        vp_recommend = (ConvenientBanner) rootView.findViewById(R.id.vp_recommend);
        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);
        ll_activity = (LinearLayout) rootView.findViewById(R.id.ll_activity);
        ll_recommend = (LinearLayout) rootView.findViewById(R.id.ll_recommend);
        ll_pull_up = (LinearLayout) rootView.findViewById(R.id.ll_pull_up);
        ll_goods_detail = (LinearLayout) rootView.findViewById(R.id.ll_goods_detail);
        ll_goods_config = (LinearLayout) rootView.findViewById(R.id.ll_goods_config);
        tv_goods_detail = (TextView) rootView.findViewById(R.id.tv_goods_detail);
        tv_goods_config = (TextView) rootView.findViewById(R.id.tv_goods_config);
        tv_goods_title=(TextView)rootView.findViewById(R.id.tv_goods_title);
        crushsalePrice=(TextView)rootView.findViewById(R.id.crushsalePrice);
        salePrice=(TextView)rootView.findViewById(R.id.salePrice);
        times=(TextView)rootView.findViewById(R.id.times);
        title_tv=(TextView)rootView.findViewById(R.id.title_tv);
        select_address=(LinearLayout)rootView.findViewById(R.id.select_address);
        select_address.setOnClickListener(this);
        fab_up_slide.hide();
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     * @author cg
     *
     */
    class poponDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }

    private void initData() {
        fragmentList = new ArrayList<>();
        tabTextList = new ArrayList<>();
        tabTextList.add(tv_goods_detail);
        tabTextList.add(tv_goods_config);
    }



    /**
     * 加载完商品详情执行
     */
    public void setDetailData() {
        bundle = new Bundle();
        bundle.putString("id",ID);
        goodsConfigFragment = new GoodsConfigFragmentKill();
        goodsInfoWebFragment = new GoodsInfoWebFragment();
        goodsInfoWebFragment.setArguments(bundle);
        goodsConfigFragment.setArguments(bundle);
        fragmentList.add(goodsConfigFragment);
        fragmentList.add(goodsInfoWebFragment);

        nowFragment = goodsInfoWebFragment;
        fragmentManager = getChildFragmentManager();
        //默认显示商品详情tab
        fragmentManager.beginTransaction().replace(R.id.fl_content, nowFragment).commitAllowingStateLoss();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_pull_up:
                //上拉查看图文详情
                sv_switch.smoothOpen(true);
                break;

            case R.id.fab_up_slide:
                //点击滑动到顶部
                sv_goods_info.smoothScrollTo(0, 0);
                sv_switch.smoothClose(true);
                break;

            case R.id.ll_goods_detail:
                //商品详情tab
                nowIndex = 0;
                scrollCursor();
                switchFragment(nowFragment, goodsInfoWebFragment);
                nowFragment = goodsInfoWebFragment;
                break;

            case R.id.ll_goods_config:
                //规格参数tab
                nowIndex = 1;
                scrollCursor();
                switchFragment(nowFragment, goodsConfigFragment);
                nowFragment = goodsConfigFragment;
                break;


            case R.id.iv_add:
                if (countNum <quantity) {
                    countNum = countNum + 1;
                    etGoodNum.setText(countNum + "");
                } else {
                    Toast.makeText(getActivity(), "不能再加啦!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_sub:
                if (countNum > 1) {
                    countNum = countNum - 1;
                    etGoodNum.setText(countNum + "");
                } else {
                    Toast.makeText(getActivity(), "不能再减啦!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_address:
                //选择收货地址
                intent=new Intent(getActivity(),AddressManagementActivity.class);
                startActivityForResult(intent,INTENT);
                break;
            case R.id.alipay:
                //支付宝支付
                new HttpUtils().Post(Constant.APPURLS+"order/alipay/"+orderNo,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

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
                break;
            case R.id.close_pop:
                popWindow.dismiss();
                Intent intent=new Intent(getActivity(),OrderDetailsActivity.class);
                intent.putExtra("orderNo",orderNo);
                intent.putExtra("type","shopcart");
                startActivity(intent);
                getActivity().finish();
                break;
            default:
                break;

        }
    }


    @Override
    public void onStatucChanged(SlideDetailsLayout.Status status) {
        if (status == SlideDetailsLayout.Status.OPEN) {
            //当前为图文详情页
            fab_up_slide.show();
            activity.vp_content.setNoScroll(true);
            activity.tv_title.setVisibility(View.VISIBLE);
            activity.psts_tabs.setVisibility(View.GONE);
        } else {
            //当前为商品详情页
            fab_up_slide.hide();
            activity.vp_content.setNoScroll(false);
            activity.tv_title.setVisibility(View.GONE);
            activity.psts_tabs.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 滑动游标
     */
    private void scrollCursor() {
        TranslateAnimation anim = new TranslateAnimation(fromX, nowIndex * v_tab_cursor.getWidth(), 0, 0);
        anim.setFillAfter(true);//设置动画结束时停在动画结束的位置
        anim.setDuration(50);
        //保存动画结束时游标的位置,作为下次滑动的起点
        fromX = nowIndex * v_tab_cursor.getWidth();
        v_tab_cursor.startAnimation(anim);

        //设置Tab切换颜色
        for (int i = 0; i < tabTextList.size(); i++) {
            tabTextList.get(i).setTextColor(i == nowIndex ? getResources().getColor(R.color.text_red) : getResources().getColor(R.color.text_black));
        }
    }

    /**
     * 切换Fragment
     * <p>(hide、show、add)
     * @param fromFragment
     * @param toFragment
     */
    private void switchFragment(Fragment fromFragment, Fragment toFragment) {
        if (nowFragment != toFragment) {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (!toFragment.isAdded()) {    // 先判断是否被add过
                fragmentTransaction.hide(fromFragment).add(R.id.fl_content, toFragment).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到activity中
            } else {
                fragmentTransaction.hide(fromFragment).show(toFragment).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }


    //请求商品详情
    private void loaddata(){
        Log.e("TAG", "loaddata: "+Constant.APPURLS+"product/"+id );
        new HttpUtils().GetOrderStatu(Constant.APPURLS+"promotion/crush/"+id,"","",new HttpUtils.HttpCallback() {

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





    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){

        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             try {
                switch (msg.what){
                    case 1:
                     JSONObject object=new JSONObject(msg.obj.toString());
                     JSONObject jsonObject=object.getJSONObject("object");
                     JSONObject productItem=jsonObject.getJSONObject("productItem");
                     imgUrls=new ArrayList<>();
                     imgUrls.add(Constant.loadimag+productItem.getString("listImg"));
                        //设置图片加载器
                        banner.setImageLoader(new GlideImageLoader());
                        banner.setDelayTime(6000);
                        //设置图片集合
                        banner.setImages(imgUrls);
                        //banner设置方法全部调用完毕时最后调用
                        banner.start();
                        JSONObject product=jsonObject.getJSONObject("product");
                        tv_goods_title.setText(product.getString("modelTitle"));
                        JSONObject crush=jsonObject.getJSONObject("crush");
                        quantity=crush.getInt("unitQuantity");
                        crushsalePrice.setText("¥"+new DecimalFormat("0.00").format(crush.getDouble("salePrice")));
                        salePrice.setText("¥"+new DecimalFormat("0.00").format(productItem.getDouble("salePrice")));
                        salePrice.setPaintFlags(Paint. STRIKE_THRU_TEXT_FLAG);
                        switchs=jsonObject.getString("switch");
                        try {
                            if(jsonObject.getString("switch").equals("0")){
                                title_tv.setText("距离开始还剩:");
                                long startTime= Long.parseLong(insertComma.dateToStamp(crush.getString("startTime")));
                                new CountDownTimer(startTime-System.currentTimeMillis(),1000){


                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        times.setText(insertComma.secToTime(Math.toIntExact(millisUntilFinished / 1000)));
                                    }

                                    @Override
                                    public void onFinish() {
                                        loaddata();
                                    }
                                }.start();
                            }else {
                                title_tv.setText("距离结束还剩:");
                                long startTime= Long.parseLong(insertComma.dateToStamp(crush.getString("endTime")));
                                new CountDownTimer(startTime-System.currentTimeMillis(),1000){


                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        times.setText(insertComma.secToTime(Math.toIntExact(millisUntilFinished / 1000)));
                                    }

                                    @Override
                                    public void onFinish() {
                                        loaddata();

                                    }
                                }.start();
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        JSONObject jsonObject1=new JSONObject(msg.obj.toString());
                        if(jsonObject1.getString("code").equals("200")){
                            orderNo=jsonObject1.getString("object");
                            popWindow.showAtLocation(rootview, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                            backgroundAlpha(0.5f);
                        }
                        if(jsonObject1.getString("code").equals("500")){
                            Toast.makeText(getActivity(),jsonObject1.getString("object"),Toast.LENGTH_SHORT).show();
                        }
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
                            Toast.makeText(getActivity(),"支付成功", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getActivity(),OrderDetailsActivity.class);
                            intent.putExtra("orderNo",orderNo);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getActivity(),OrderDetailsActivity.class);
                            intent.putExtra("orderNo",orderNo);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        break;
                    }

                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

    //立即购买
    public void GOShop(){
        if(switchs.equals("0")){
            Toast.makeText(getActivity(),"活动尚未开始！",Toast.LENGTH_SHORT).show();
        }else {
            //提交订单

            Log.e("TAG", "onClick: "+TOKEN+"  "+loginUserId);
            try {
                JSONObject object = new JSONObject();
                object.put("addressId",addressId);
                object.put("quantity",etGoodNum.getText().toString());
                object.put("skuId",id);
                final String params=object.toString();
                Log.e("TAG", "addressId: "+params);
                //新增
                new HttpUtils().postJson(Constant.APPURLS+"promotion/crush/confirm",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 处理推荐商品数据(每两个分为一组)
     *
     * @param data
     * @return
     */
    public static List<List<RecommendGoodsBean>> handleRecommendGoods(List<RecommendGoodsBean> data) {
        List<List<RecommendGoodsBean>> handleData = new ArrayList<>();
        int length = data.size() / 2;
        if (data.size() % 2 != 0) {
            length = data.size() / 2 + 1;
        }
        for (int i = 0; i < length; i++) {
            List<RecommendGoodsBean> recommendGoods = new ArrayList<>();
            for (int j = 0; j < (i * 2 + j == data.size() ? 1 : 2); j++) {
                recommendGoods.add(data.get(i * 2 + j));
            }
            handleData.add(recommendGoods);
        }
        return handleData;
    }
}
