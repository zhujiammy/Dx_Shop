package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.OnLoadMoreListener;
import com.example.zhujia.dx_shop.Tools.OnRefreshListener;
import com.example.zhujia.dx_shop.Tools.SuperRefreshRecyclerView;


//待发货

public class PendingDelivery extends Fragment implements OnRefreshListener,OnLoadMoreListener {
    private View view;
    private SuperRefreshRecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.pendingdelivery,container,false);
        initUI();
        return view;
    }


    private void initUI(){
        recyclerView= (SuperRefreshRecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.init(this,this);
        recyclerView.setRefreshEnabled(true);
        recyclerView.setLoadingMoreEnable(true);
        recyclerView.setHasFixedSize(true);

        View  emtview=View.inflate(getActivity(),R.layout.emtview,null);

        recyclerView.setEmptyView(emtview);
        recyclerView.showEmpty(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @SuppressLint("NewApi")
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            //相当于Fragment的onPause
            Log.e("TAG", "onHiddenChanged: "+"待发货不可见" );
        } else {
            // 相当于Fragment的onResume
            Log.e("TAG", "onHiddenChanged: "+"待发货可见" );
        }
    }




    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

}
