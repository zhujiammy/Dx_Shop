package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.ProductDetailsActivity;
import com.example.zhujia.dx_shop.Adapter.ItemRecommendAdapter;
import com.example.zhujia.dx_shop.Adapter.ViewAdapter;
import com.example.zhujia.dx_shop.Data.AttrKey;
import com.example.zhujia.dx_shop.Data.AttrValues;
import com.example.zhujia.dx_shop.Data.Data;
import com.example.zhujia.dx_shop.Data.InventoryList;
import com.example.zhujia.dx_shop.Data.Objects;
import com.example.zhujia.dx_shop.Data.Product;
import com.example.zhujia.dx_shop.Data.ProductAttrList;
import com.example.zhujia.dx_shop.Data.ProductImageList;
import com.example.zhujia.dx_shop.Data.ProductItemList;
import com.example.zhujia.dx_shop.Data.Promotions;
import com.example.zhujia.dx_shop.Data.RecommendGoodsBean;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.FNRadioGroup;
import com.example.zhujia.dx_shop.Tools.GoodsConfigFragment;
import com.example.zhujia.dx_shop.Tools.MRadioButton;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.SlideDetailsLayout;
import com.gxz.PagerSlidingTabStrip;
import com.hmy.popwindow.PopWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * item页ViewPager里的商品Fragment
 */
public class GoodsInfoFragment extends Fragment implements View.OnClickListener, SlideDetailsLayout.OnSlideDetailsListener {
    private PagerSlidingTabStrip psts_tabs;
    private SlideDetailsLayout sv_switch;
    private ScrollView sv_goods_info;
    private FloatingActionButton fab_up_slide;
    public ConvenientBanner  vp_recommend;
    private com.example.zhujia.dx_shop.Tools.Banner banner;
    private ViewPager view_pager;
    private List<View> viewList = new ArrayList<View>();
    WebView webView;
    private PagerAdapter viewAdapter;
    private LayoutInflater inflater;
    private LinearLayout ll_goods_detail, ll_goods_config;
    private TextView tv_goods_detail, tv_goods_config;
    private View v_tab_cursor;
    public FrameLayout fl_content;
    public LinearLayout ll_current_goods, ll_activity, ll_comment, ll_recommend, ll_pull_up;
    public TextView cuxiao,price,tv_goods_title, tv_new_price, tv_old_price,kucun_tv,tv_current_goods, tv_comment_count, tv_good_comment;
    private PopWindow popWindow;
    private View customView;
    private ImageView img;
    private TextView salePrice,itemNo;
    /** 当前商品详情数据页的索引分别是图文详情、规格参数 */
    private int nowIndex;
    private float fromX;
    private List<Data>list=new ArrayList<>();
    private List<Data>productItem=new ArrayList<>();
    private List<Objects>object=new ArrayList<>();
    private List<ProductImageList>productImageLists=new ArrayList<>();
    private List<Promotions>promotions=new ArrayList<>();
    private List <ProductItemList>productItemLists=new ArrayList<>();
    private List<InventoryList>inventoryLists=new ArrayList<>();
    private List<ProductAttrList>productAttrLists=new ArrayList<>();
    private List<AttrValues>attrValues=new ArrayList<>();
    public GoodsConfigFragment goodsConfigFragment;
    public GoodsInfoWebFragment goodsInfoWebFragment;
    private Fragment nowFragment;
    private List<TextView> tabTextList;
    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    public ProductDetailsActivity activity;
    private LayoutInflater inflaters;
    private FNRadioGroup groupradion;
    private TextView attrKey_tv,attrKeys;
    private String productModelAttrsID;
    private String productItemId;
    private String productModelAttr;
    private String ysid="null",zcid="null";
    private LinearLayout lins;
    private ImageView ivSub ;
    private EditText etGoodNum ;
    private List<String>imgUrls;
    private ImageView ivAdd;
    private int countNum = 1;//购买商品数量
    private TextView add_carts;
    private String LoginState,TOKEN,loginUserId;
    private SharedPreferences sharedPreferences;
    private String id;
    private Bundle bundle;
    View view2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (ProductDetailsActivity) context;
    }



    @Override
    public void onStop() {
        super.onStop();
    }




    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_goods_info, null);
        initView(rootView);
        initListener();
        initData();
        return rootView;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isAdded()){
            id=getArguments().getString("id");
            Log.e("TAG", "onStart: "+id );
        }

        loaddata();
        setDetailData();

   /*     new HttpUtils().Post(Constant.APPURLS+"product/desc/"+id,TOKEN,loginUserId,new HttpUtils.HttpCallback() {

            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,1,data
                );
                mHandler.sendMessage(msg);
            }

        });*/
    }

    private void initListener() {
        fab_up_slide.setOnClickListener(this);
        ll_current_goods.setOnClickListener(this);
        ll_activity.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        ll_pull_up.setOnClickListener(this);
        ll_goods_detail.setOnClickListener(this);
        ll_goods_config.setOnClickListener(this);
        sv_switch.setOnSlideDetailsListener(this);
    }

    private void initView(View rootView) {
        view_pager=(ViewPager)rootView.findViewById(R.id.view_pager);
        psts_tabs = (PagerSlidingTabStrip)rootView.findViewById(R.id.psts_tabs);
        fab_up_slide = (FloatingActionButton) rootView.findViewById(R.id.fab_up_slide);
        sv_switch = (SlideDetailsLayout) rootView.findViewById(R.id.sv_switch);
        sv_goods_info = (ScrollView) rootView.findViewById(R.id.sv_goods_info);
        v_tab_cursor = rootView.findViewById(R.id.v_tab_cursor);
        vp_recommend = (ConvenientBanner) rootView.findViewById(R.id.vp_recommend);
        fl_content = (FrameLayout) rootView.findViewById(R.id.fl_content);
        ll_current_goods = (LinearLayout) rootView.findViewById(R.id.ll_current_goods);
        ll_activity = (LinearLayout) rootView.findViewById(R.id.ll_activity);
        ll_comment = (LinearLayout) rootView.findViewById(R.id.ll_comment);
        ll_recommend = (LinearLayout) rootView.findViewById(R.id.ll_recommend);
        ll_pull_up = (LinearLayout) rootView.findViewById(R.id.ll_pull_up);
        ll_goods_detail = (LinearLayout) rootView.findViewById(R.id.ll_goods_detail);
        ll_goods_config = (LinearLayout) rootView.findViewById(R.id.ll_goods_config);
        tv_goods_detail = (TextView) rootView.findViewById(R.id.tv_goods_detail);
        tv_goods_config = (TextView) rootView.findViewById(R.id.tv_goods_config);
        tv_goods_title = (TextView) rootView.findViewById(R.id.tv_goods_title);
        tv_new_price = (TextView) rootView.findViewById(R.id.tv_new_price);
        tv_old_price = (TextView) rootView.findViewById(R.id.tv_old_price);
        tv_current_goods = (TextView) rootView.findViewById(R.id.tv_current_goods);
        tv_comment_count = (TextView) rootView.findViewById(R.id.tv_comment_count);
        tv_good_comment = (TextView) rootView.findViewById(R.id.tv_good_comment);
        customView = View.inflate(getActivity(), R.layout.choosemeal, null);
        ivSub = customView.findViewById(R.id.iv_sub);
        etGoodNum = customView.findViewById(R.id.et_good_num);
        ivAdd = customView.findViewById(R.id.iv_add);
        img=(ImageView)customView.findViewById(R.id.img);
        ivSub.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        salePrice=(TextView)customView.findViewById(R.id.salePrice);
        itemNo=(TextView)customView.findViewById(R.id.itemNo);
        lins=(LinearLayout)customView.findViewById(R.id.lins);
        add_carts=(TextView) customView.findViewById(R.id.add_carts);
        add_carts.setOnClickListener(this);
        cuxiao=(TextView)customView.findViewById(R.id.cuxiao);
        kucun_tv=(TextView)customView.findViewById(R.id.kucun);
        price=(TextView)customView.findViewById(R.id.price);
        popWindow = new PopWindow.Builder(getActivity())
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();
        setRecommendGoods();

        //设置文字中间一条横线
        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        fab_up_slide.hide();
        //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        vp_recommend.setPageIndicator(new int[]{R.drawable.shape_item_index_white, R.drawable.shape_item_index_red});
        vp_recommend.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
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
        bundle.putString("id",id);
        goodsConfigFragment = new GoodsConfigFragment();
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

    /**
     * 设置推荐商品
     */
    public void setRecommendGoods() {
        List<RecommendGoodsBean> data = new ArrayList<>();
        data.add(new RecommendGoodsBean("Letv/乐视 LETV体感-超级枪王 乐视TV超级电视产品玩具 体感游戏枪 电玩道具 黑色",
                "http://img4.hqbcdn.com/product/79/f3/79f3ef1b0b2283def1f01e12f21606d4.jpg", new BigDecimal(599), "799"));
        data.add(new RecommendGoodsBean("IPEGA/艾派格 幽灵之子 无线蓝牙游戏枪 游戏体感枪 苹果安卓智能游戏手柄 标配",
                "http://img2.hqbcdn.com/product/00/76/0076cedb0a7d728ec1c8ec149cff0d16.jpg", new BigDecimal(299), "399"));
        data.add(new RecommendGoodsBean("Letv/乐视 LETV体感-超级枪王 乐视TV超级电视产品玩具 体感游戏枪 电玩道具 黑色",
                "http://img4.hqbcdn.com/product/79/f3/79f3ef1b0b2283def1f01e12f21606d4.jpg", new BigDecimal(599), "799"));
        data.add(new RecommendGoodsBean("IPEGA/艾派格 幽灵之子 无线蓝牙游戏枪 游戏体感枪 苹果安卓智能游戏手柄 标配",
                "http://img2.hqbcdn.com/product/00/76/0076cedb0a7d728ec1c8ec149cff0d16.jpg", new BigDecimal(299), "399"));
        List<List<RecommendGoodsBean>> handledData = handleRecommendGoods(data);
        //设置如果只有一组数据时不能滑动
        vp_recommend.setManualPageable(handledData.size() == 1 ? false : true);
        vp_recommend.setCanLoop(handledData.size() == 1 ? false : true);
        vp_recommend.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new ItemRecommendAdapter();
            }
        }, handledData);
    }

    public void popshow(){
        popWindow.show();
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

            case R.id.ll_current_goods:
                //选择参数
                popWindow.show();
                break;


            case R.id.iv_add:
                if (countNum < 1000) {
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

            case R.id.add_carts:
                //加入购物车
                try {
                    JSONObject object = new JSONObject();
                    object.put("productItemId",productItemId);
                    object.put("quantity",etGoodNum.getText().toString());
                    final String params = object.toString();
                    new HttpUtils().postJson(Constant.APPURLS+"order/shopping/add",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }




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
        new HttpUtils().Post(Constant.APPURLS+"product/"+id,"","",new HttpUtils.HttpCallback() {

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

                    case 2:
                        JSONObject objectmsg=new JSONObject(msg.obj.toString());
                        if(objectmsg.getString("code").equals("200")){
                            Toast.makeText(getActivity(),objectmsg.getString("object"),Toast.LENGTH_SHORT).show();
                            popWindow.dismiss();
                        }

                        break;
                    case 1:
                        Objects objects=new Objects();
                        JSONObject jsonObject=new JSONObject(msg.obj.toString());
                        JSONObject Jsonobject=jsonObject.getJSONObject("object");
                        JSONObject jsonproduct=Jsonobject.getJSONObject("product");
                        Product product=new Product();
                        product.setId(jsonproduct.getString("id"));
                        product.setCatalogId(jsonproduct.getString("catalogId"));
                        product.setTypeId(jsonproduct.getString("typeId"));
                        product.setSeriesId(jsonproduct.getString("seriesId"));
                        product.setBrandId(jsonproduct.getString("brandId"));
                        product.setModelNo(jsonproduct.getString("modelNo"));
                        product.setModelName(jsonproduct.getString("modelName"));
                        product.setModelTitle(jsonproduct.getString("modelTitle"));
                        product.setCustomNo1(jsonproduct.getString("customNo1"));
                        product.setCustomNo2(jsonproduct.getString("customNo2"));
                        product.setStatus(jsonproduct.getString("status"));
                        product.setModelImg(jsonproduct.getString("modelImg"));
                        product.setSmallImg(jsonproduct.getString("smallImg"));
                        product.setSalePrice(jsonproduct.getInt("salePrice"));
                        product.setVideo(jsonproduct.getString("video"));
                        objects.setProduct(product);
                        View view1 = inflater.inflate(R.layout.webview, null);
                        webView=(WebView)view1.findViewById(R.id.web);
                        webView.getSettings().setDefaultTextEncodingName("utf-8") ;//这句话去掉也没事。。只是设置了编码格式
                        webView.getSettings().setJavaScriptEnabled(true);	//这句话必须保留。。不解释
                        webView.getSettings().setDomStorageEnabled(true);//这句话必须保留。。否则无法播放优酷视频网页。。其他的可以
                        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                        webView.setWebChromeClient(new MyWebChromeClient());//重写一下。有的时候可能会出现问题
                        webView.setWebViewClient(new WebViewClient(){//不写的话自动跳到默认浏览器了。。跳出APP了。。怎么能不写？
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {//这个方法必须重写。否则会出现优酷视频周末无法播放。周一-周五可以播放的问题
                                if(url.startsWith("intent")||url.startsWith("youku")){
                                    return true;
                                }else{
                                    return super.shouldOverrideUrlLoading(view, url);
                                }
                            }
                        });
                        webView.loadUrl("http://player.youku.com/embed/"+product.getVideo()+"");
                        viewList.add(view1);

                        tv_goods_title.setText(product.getModelTitle());
                        tv_new_price.setText(String.valueOf(product.getSalePrice()));

                        JSONArray promotionjsonarry=Jsonobject.getJSONArray("promotions");
                        if(promotionjsonarry.length()>0){
                            Promotions promotion=null;
                            for(int l=0;l<promotionjsonarry.length();l++){
                                promotion=new Promotions();
                                JSONObject promtionobj=promotionjsonarry.getJSONObject(l);
                                promotion.setOnSalePrice(promtionobj.getString("onSalePrice"));
                                promotion.setProductItemId(promtionobj.getString("productItemId"));
                                promotion.setActivityName(promtionobj.getString("activityName"));
                                promotion.setStartTime(promtionobj.getString("startTime"));
                                promotion.setEndTime(promtionobj.getString("endTime"));
                                promotion.setId(promtionobj.getString("id"));
                                promotion.setActivityType(promtionobj.getString("activityType"));
                                promotion.setProductModelId(promtionobj.getString("productModelId"));
                                promotion.setStatus(promtionobj.getString("status"));
                                promotions.add(promotion);

                            }
                        }


                        ProductImageList productImageList=null;
                        JSONArray productImagejsonarry=Jsonobject.getJSONArray("productImageList");
                        for(int i=0;i<productImagejsonarry.length();i++){
                            view2 = inflater.inflate(R.layout.banner_xml, null);
                            ImageView imageView = (com.example.zhujia.dx_shop.Tools.ResizableImageView) view2.findViewById(R.id.pic_item);
                            productImageList=new ProductImageList();
                            JSONObject object=productImagejsonarry.getJSONObject(i);
                            productImageList.setId(object.getString("id"));
                            productImageList.setProductModelId(object.getString("productModelId"));
                            productImageList.setListImg(object.getString("listImg"));
                            productImageList.setIndexNum(object.getInt("indexNum"));
                            Glide.with(getActivity()).load(Constant.loadimag+productImageList.getListImg()).into(imageView);
                            productImageLists.add(productImageList);
                            viewList.add(view2);
                        }
                        //实例化适配器
                        viewAdapter = new ViewAdapter(viewList);
                        //设置适配器
                        view_pager.setAdapter(viewAdapter);
                        ProductItemList productItemList=null;
                       JSONArray productItemListjsonarry=Jsonobject.getJSONArray("productItemList");
                        for(int d=0;d<productItemListjsonarry.length();d++){
                            productItemList=new ProductItemList();
                            JSONObject productitemobj=productItemListjsonarry.getJSONObject(d);
                            productItemList.setId(productitemobj.getString("id"));
                            productItemList.setProductId(productitemobj.getString("productId"));
                            productItemList.setItemNo(productitemobj.getString("itemNo"));
                            productItemList.setItemName(productitemobj.getString("itemName"));
                            productItemList.setItemTitle(productitemobj.getString("itemTitle"));
                            productItemList.setStatus(productitemobj.getString("status"));
                            productItemList.setCustomNo1(productitemobj.getString("customNo1"));
                            productItemList.setCustomNo2(productitemobj.getString("customNo2"));
                            productItemList.setProductModelAttrs(productitemobj.getString("productModelAttrs"));
                            productItemList.setIsDeleted(productitemobj.getString("isDeleted"));
                            productItemList.setSalePrice(productitemobj.getDouble("salePrice"));
                            productItemList.setListImg(productitemobj.getString("listImg"));
                            productItemLists.add(productItemList);

                        }
                        objects.setProductItemList(productItemLists);

                        InventoryList inventoryList=null;
                        JSONArray inventoryListsjsonarry=Jsonobject.getJSONArray("inventory");
                        for(int i=0;i<inventoryListsjsonarry.length();i++){
                            inventoryList=new InventoryList();
                            JSONObject object=inventoryListsjsonarry.getJSONObject(i);
                            inventoryList.setProductItemId(object.getString("skuId"));
                            inventoryList.setQuantity(object.getString("quantity"));
                            inventoryList.setLockQuantity(object.getString("lockQuantity"));
                            inventoryLists.add(inventoryList);
                        }



                        ProductAttrList productAttrList=null;
                         final JSONArray productAttrListjsonarray=Jsonobject.getJSONArray("productAttrList");
                        MRadioButton radioButton = null;
                        int num=0;
                        final Map<String,String>map=new HashMap<>();
                        final Map<String,String>map1=new HashMap<>();
                        lins.removeAllViews();
                        for(int j=0;j<productAttrListjsonarray.length();j++){
                            productAttrList=new ProductAttrList();
                            final View views = View.inflate(getActivity(),R.layout.layout_group,null);
                            attrKey_tv=(TextView)views.findViewById(R.id.attrKey);
                            groupradion=(FNRadioGroup)views.findViewById(R.id.groupradion);
                            JSONObject object1=productAttrListjsonarray.getJSONObject(j);
                            JSONObject attrKeyobject=object1.getJSONObject("attrKey");
                            AttrKey attrKey=new AttrKey();
                            attrKey.setId(attrKeyobject.getString("id"));
                            attrKey.setCatalogId(attrKeyobject.getString("catalogId"));
                            attrKey.setCatalogAttrValue(attrKeyobject.getString("catalogAttrValue"));
                            attrKey.setIndexNum(attrKeyobject.getInt("indexNum"));
                            attrKey.setIsColorAttr(attrKeyobject.getString("isColorAttr"));
                            productAttrList.setAttrKey(attrKey);
                            JSONArray attrvalue=object1.getJSONArray("attrValues");
                            attrKey_tv.setText(attrKey.getCatalogAttrValue());


                           AttrValues attrValue=null;
                            for(int c=0;c<attrvalue.length();c++){
                                attrValue=new AttrValues();
                                final JSONObject object2=attrvalue.getJSONObject(c);
                                attrValue.setId(object2.getString("id"));
                                attrValue.setProductModelId(object2.getString("productModelId"));
                                attrValue.setCatalogAttrId(object2.getString("catalogAttrId"));
                                attrValue.setModelAttrValue(object2.getString("modelAttrValue"));
                                attrValue.setIndexNum(object2.getInt("indexNum"));
                                attrValue.setIsDeleted(object2.getString("isDeleted"));
                                if(!object2.getString("listImg").equals("")){
                                    attrValue.setListImg(object2.getString("listImg"));
                                }else {
                                    attrValue.setListImg("null");
                                }

                                num++;
                                radioButton=new MRadioButton(getActivity());
                                radioButton.setText(attrValue.getModelAttrValue());
                                radioButton.setBackground(getActivity().getResources().getDrawable(R.drawable.radiobutton_background));
                                radioButton.setButtonDrawable(null);
                                radioButton.setGravity(Gravity.CENTER);
                                radioButton.setId(num);
                                radioButton.setPadding(30,30,30,30);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
                                groupradion.addView(radioButton,lp);
                                attrValues.add(attrValue);
                                productAttrList.setAttrValues(attrValues);

                                final MRadioButton finalRadioButton = radioButton;
                                final AttrValues finalAttrValue = attrValue;
                                final int finalJ = j;
                                final ProductItemList finalProductItemList = productItemList;
                                radioButton.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onClick(View v) {
                                        if(!finalAttrValue.getListImg().equals("null")){
                                            Glide.with(getActivity()).load(Constant.loadimag+finalAttrValue.getListImg()).into(img);
                                        }

                                        int kk=0;
                                        int ii=0;
                                        StringBuffer buffer=new StringBuffer();
                                        StringBuffer buffer1=new StringBuffer();

                                            map.put(String.valueOf(finalJ),finalAttrValue.getId());
                                        if(map.size()==productAttrListjsonarray.length()){
                                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                                String key = entry.getKey();
                                                String value = entry.getValue();
                                                String ModelAttrValue=entry.getValue();

                                                if(kk==0){
                                                    buffer.append(value);

                                                }else {
                                                    buffer.append(",").append(value);
                                                }
                                                kk++;

                                            }
                                            Log.e("TAG", "onClick: "+buffer.toString() );
                                            map1.put(String.valueOf(finalJ),finalAttrValue.getModelAttrValue());
                                            for (Map.Entry<String, String> entry : map1.entrySet()) {
                                                String key = entry.getKey();
                                                String value = entry.getValue();

                                                if(ii==0){
                                                    buffer1.append(value);
                                                }else {
                                                    buffer1.append(",").append(value);
                                                }
                                                ii++;

                                            }
                                            tv_current_goods.setText("已选"+" "+buffer1.toString());


                                            Log.e("TAG", "onClick: "+productItemLists.size() );
                                            for(int y=0;y<productItemLists.size();y++){
                                                if((buffer.toString().equals(productItemLists.get(y).getProductModelAttrs()))){
                                                    Glide.with(getActivity()).load(Constant.loadimag+productItemLists.get(y).getListImg()).into(img);
                                                    String productId=productItemLists.get(y).getId();
                                                    salePrice.setText("¥"+new DecimalFormat("0.00").format(productItemLists.get(y).getSalePrice()));
                                                    itemNo.setText("商品编号:"+productItemLists.get(y).getItemNo());
                                                    Log.e("TAG", "promotions:"+promotions.size());
                                                    Log.e("TAG", "onClick: "+new DecimalFormat("0.00").format(productItemLists.get(y).getSalePrice()));
                                                        if(promotions.size()==0){
                                                            productItemId=productItemLists.get(y).getId();
                                                        }else {
                                                            for(int t=0;t<promotions.size();t++){
                                                                if(productId.equals(promotions.get(t).getProductItemId())){
                                                                    cuxiao.setText(promotions.get(t).getActivityName());
                                                                    price.setText("¥"+promotions.get(t).getOnSalePrice());
                                                                    salePrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
                                                                   // Toast.makeText(getActivity(),promotions.get(t).getOnSalePrice(),Toast.LENGTH_SHORT).show();
                                                                    productItemId=promotions.get(t).getProductItemId();
                                                                }else {
                                                                    productItemId=productItemLists.get(y).getId();
                                                                    cuxiao.setText("");
                                                                    price.setText("");
                                                                    salePrice.getPaint().setFlags(0);
                                                                }
                                                            }
                                                        }



                                                    Log.e("TAG", "productItemId: "+productItemId );
                                                        for(int i=0;i<inventoryLists.size();i++){
                                                            if(productItemId.equals(inventoryLists.get(i).getProductItemId())){
                                                                int quantity= Integer.parseInt(inventoryLists.get(i).getQuantity());
                                                                int LockQuantity= Integer.parseInt(inventoryLists.get(i).getLockQuantity());
                                                                int kucun=quantity-LockQuantity;
                                                                kucun_tv.setText("库存:"+kucun);
                                                                if(kucun==0){
                                                                    add_carts.setEnabled(false);
                                                                    add_carts.setText("暂时没有库存哦！");
                                                                }else {
                                                                    add_carts.setEnabled(true);
                                                                    add_carts.setText("加入购物车");
                                                                }
                                                            }
                                                        }


                                                }
                                            }




                                        }

                                        // Toast.makeText(getActivity(),"id"+finalAttrValue.getId(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            groupradion.setOnCheckedChangeListener(new FNRadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(FNRadioGroup group, int checkedId) {

                                }
                            });

                            lins.addView(views);
                            productAttrLists.add(productAttrList);
                        }

                        objects.setProductAttrList(productAttrLists);
                        object.add(objects);






                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    };

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


    class MyWebChromeClient extends WebChromeClient {
        private View myView = null;

        // 全屏
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);

            ViewGroup parent = (ViewGroup) webView.getParent();
            parent.removeView(webView);

            // 设置背景色为黑色
            view.setBackgroundColor(getResources().getColor(R.color.BLAK));
            parent.addView(view);
            myView = view;

            setFullScreen();

        }

        // 退出全屏
        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (myView != null) {

                ViewGroup parent = (ViewGroup) myView.getParent();
                parent.removeView(myView);
                parent.addView(webView);
                myView = null;

                quitFullScreen();
            }
        }
    }

    /**
     * 设置全屏
     */
    private void setFullScreen() {
        // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        // 声明当前屏幕状态的参数并获取
        final WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setAttributes(attrs);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
