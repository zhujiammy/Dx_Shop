package com.example.zhujia.dx_shop.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.zhujia.dx_shop.Fragment.AfterSale;
import com.example.zhujia.dx_shop.Fragment.AllOrder;
import com.example.zhujia.dx_shop.Fragment.Completed;
import com.example.zhujia.dx_shop.Fragment.Goodsreceived;
import com.example.zhujia.dx_shop.Fragment.PendingDelivery;
import com.example.zhujia.dx_shop.Fragment.PendingPayment;
import com.example.zhujia.dx_shop.R;

public class MyOrderActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private Toolbar toolbar;
    private FragmentManager manager;
    private PendingPayment pendingPayment;
    private AllOrder allOrder;
    private PendingDelivery pendingDelivery;
    private Goodsreceived goodsreceived;
    private Completed completed;
    private AfterSale afterSale;
    private SharedPreferences sharedPreferences;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder);
        intent=getIntent();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
        if(intent.getStringExtra("orderstatue")!=null){
            int i= Integer.parseInt(intent.getStringExtra("orderstatue"));
            select(i);
        }

    }


    TabLayout.OnTabSelectedListener listener=new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            FragmentTransaction transaction=manager.beginTransaction();
            if(tab.getText().equals("全部订单")){

                transaction.show(allOrder);
                transaction.hide(pendingPayment);
                transaction.hide(pendingDelivery);
                transaction.hide(goodsreceived);
                transaction.hide(completed);
                transaction.hide(afterSale);
                transaction.commit();
            }
            if(tab.getText().equals("待付款")){
                transaction.show(pendingPayment);
                transaction.hide(pendingDelivery);
                transaction.hide(goodsreceived);
                transaction.hide(completed);
                transaction.hide(afterSale);
                transaction.hide(allOrder);
                transaction.commit();
            }
            if(tab.getText().equals("待发货")){
                transaction.hide(pendingPayment);
                transaction.show(pendingDelivery);
                transaction.hide(goodsreceived);
                transaction.hide(completed);
                transaction.hide(afterSale);
                transaction.hide(allOrder);
                transaction.commit();
            }
            if(tab.getText().equals("待收货")){
                transaction.hide(pendingPayment);
                transaction.hide(pendingDelivery);
                transaction.show(goodsreceived);
                transaction.hide(completed);
                transaction.hide(afterSale);
                transaction.hide(allOrder);
                transaction.commit();
            }
            if(tab.getText().equals("已完成")){
                transaction.hide(pendingPayment);
                transaction.hide(pendingDelivery);
                transaction.hide(goodsreceived);
                transaction.show(completed);
                transaction.hide(afterSale);
                transaction.hide(allOrder);
                transaction.commit();
            }
            if(tab.getText().equals("售后")){
                transaction.hide(pendingPayment);
                transaction.hide(pendingDelivery);
                transaction.hide(goodsreceived);
                transaction.hide(completed);
                transaction.show(afterSale);
                transaction.hide(allOrder);
                transaction.commit();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void initUI(){

        mTabLayout=(TabLayout) findViewById(R.id.tabLayout2);
        mTabLayout.addTab(mTabLayout.newTab().setText("全部订单"));

        mTabLayout.addTab(mTabLayout.newTab().setText("待付款"));

        mTabLayout.addTab(mTabLayout.newTab().setText("待发货"));

        mTabLayout.addTab(mTabLayout.newTab().setText("待收货"));

        mTabLayout.addTab(mTabLayout.newTab().setText("已完成"));

        mTabLayout.addTab(mTabLayout.newTab().setText("售后"));

        mTabLayout.addOnTabSelectedListener(listener);
        pendingPayment=new PendingPayment();
        pendingDelivery=new PendingDelivery();
        goodsreceived=new Goodsreceived();
        completed=new Completed();
        afterSale=new AfterSale();
        allOrder=new AllOrder();

        manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.main_content,pendingPayment);
        transaction.add(R.id.main_content,pendingDelivery);
        transaction.add(R.id.main_content,goodsreceived);
        transaction.add(R.id.main_content,completed);
        transaction.add(R.id.main_content,afterSale);
        transaction.add(R.id.main_content,allOrder);
        transaction.show(allOrder);
        transaction.hide(pendingPayment);
        transaction.hide(pendingDelivery);
        transaction.hide(goodsreceived);
        transaction.hide(completed);
        transaction.hide(afterSale);
        transaction.commit();
    }

  public void select(int i){
      mTabLayout.getTabAt(i).select();
  }
}
