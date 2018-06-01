package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhujia.dx_shop.Adapter.ItemTitlePagerAdapter;
import com.example.zhujia.dx_shop.Fragment.GoodsCommentFragment;
import com.example.zhujia.dx_shop.Fragment.GoodsCommentFragmentKill;
import com.example.zhujia.dx_shop.Fragment.GoodsDetailFragment;
import com.example.zhujia.dx_shop.Fragment.GoodsDetailFragmentKill;
import com.example.zhujia.dx_shop.Fragment.GoodsInfoFragment;
import com.example.zhujia.dx_shop.Fragment.GoodsInfoFragmentKill;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.NoScrollViewPager;
import com.gxz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailskillActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    public PagerSlidingTabStrip psts_tabs;
    public NoScrollViewPager vp_content;
    private Toolbar toolbar;

    private List<Fragment> fragmentList = new ArrayList<>();
    private GoodsInfoFragmentKill goodsInfoFragment;
    private GoodsDetailFragmentKill goodsDetailFragment;
    private GoodsCommentFragmentKill goodsCommentFragment;
    private String id;
    private String ID;
    private String switchs;
    public TextView tv_title,add_cart,buy;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetailskill);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        intent=getIntent();
        id=intent.getStringExtra("skuId");
        ID=intent.getStringExtra("ID");
        switchs=intent.getStringExtra("switch");
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("ID",ID);
        bundle.putString("switch",switchs);
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
        tv_title = (TextView) findViewById(R.id.tv_title);
        add_cart=(TextView)findViewById(R.id.add_carts);
        add_cart.setOnClickListener(this);
        buy=(TextView)findViewById(R.id.buy);
        buy.setOnClickListener(this);
        psts_tabs = (PagerSlidingTabStrip) findViewById(R.id.psts_tabs);
        vp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
        fragmentList.add(goodsInfoFragment = new GoodsInfoFragmentKill());
        fragmentList.add(goodsDetailFragment = new GoodsDetailFragmentKill());
        fragmentList.add(goodsCommentFragment = new GoodsCommentFragmentKill());
        fragmentList.get(0).setArguments(bundle);
        fragmentList.get(1).setArguments(bundle);
        fragmentList.get(2).setArguments(bundle);
        vp_content.setAdapter(new ItemTitlePagerAdapter(getSupportFragmentManager(),
                fragmentList, new String[]{"商品", "详情", "评价"}));
        vp_content.setOffscreenPageLimit(3);
        psts_tabs.setViewPager(vp_content);




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.shopcart){

            intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("select","2");
            startActivity(intent);
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.goshopcart,menu);//加载menu布局
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v==buy){
          goodsInfoFragment.GOShop();
        }
    }
}
