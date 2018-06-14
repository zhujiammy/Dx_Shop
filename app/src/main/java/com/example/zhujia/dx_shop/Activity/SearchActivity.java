package com.example.zhujia.dx_shop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.ItemCallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;

public class SearchActivity extends AppCompatActivity{

    private SearchView searchView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seachview);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 3. 绑定组件
        searchView = (SearchView) findViewById(R.id.search_view);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                System.out.println("我收到了" + string);
                Intent intent =new Intent(getApplicationContext(), SearchProduct.class);
                intent.putExtra("keyword",string);
                startActivity(intent);

            }
        });

        searchView.setItemCallBack(new ItemCallBack() {
            @Override
            public void ItemAciton(String string) {
                Intent intent =new Intent(getApplicationContext(), SearchProduct.class);
                intent.putExtra("keyword",string);
                startActivity(intent);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {
                finish();
            }
        });

    }
}
