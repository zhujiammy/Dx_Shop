package com.example.zhujia.dx_shop.Tools;

/**
 * Created by zhuji on 2016/11/6.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zhujia.dx_shop.R;

import java.util.ArrayList;

/**
 * 城市选择类
 *
 */
public class CitySelect1Activity extends RelativeLayout implements View.OnClickListener {

    private TextView clear;

    private LinearLayout linar_btn;
    private TextView location_tv;
    private ListView lv_city;
    // private ListView listView;
    // private LinearLayout record;
    private ImageView br,br1,br2;
    private TextView mTvSure;
    private ArrayList<MyRegion> regions;
    private OnAddressPickerSureListener mOnAddressPickerSureListener;
    private CityAdapter adapter;
    private static int PROVINCE = 0x00;
    private static int CITY = 0x01;
    private static int DISTRICT = 0x02;
    private CityUtils util;
    private SharedPreferences sharedPreferences;
    String address,loction_address;
    private TextView[] tvs = new TextView[3];
    private int[] ids = { R.id.rb_province, R.id.rb_city, R.id.rb_district };//顶栏省市县
    private Context mContext;
    private City city;
    int last, current;

    public CitySelect1Activity(Context context) {
        super(context);
        viewInit(context);
    }

    public CitySelect1Activity(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        viewInit(context);
    }

    public CitySelect1Activity(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewInit(context);
    }


    /*
     * 初始化
     */
    private void viewInit(Context context) {

        mContext = context;
        View rootView = inflate(mContext, R.layout.selectaddress, this);
        city = new City();

        for (int i = 0; i < tvs.length; i++) {
            tvs[i] = (TextView) findViewById(ids[i]);//对应的城市Id
            tvs[i].setOnClickListener(this);//选择对应城市的点击事件
        }

        if(city==null){
            city = new City();
            city.setProvince("");
            city.setCity("");
            city.setDistrict("");
        }else{
            if(city.getProvince()!=null&&!city.getProvince().equals("")){
                tvs[0].setText(city.getProvince());//省
            }
            if(city.getCity()!=null&&!city.getCity().equals("")){
                tvs[1].setText(city.getCity());//市
            }
            if(city.getDistrict()!=null&&!city.getDistrict().equals("")){
                tvs[2].setText(city.getDistrict());//县区
            }
        }
        //设置顶栏标题
        /* btn_right = (Button) findViewById(R.id.btn_right);
         */
        // linar_btn=(LinearLayout)findViewById(R.id.linar_btn);
        br=(ImageView)findViewById(R.id.i_br);
        br1=(ImageView)findViewById(R.id.i_br1);

        br2=(ImageView)findViewById(R.id.i_br2);
        mTvSure = rootView.findViewById(R.id.mTvSure);
        mTvSure.setOnClickListener(this);
        //location_tv=(TextView)findViewById(R.id.loction_tv);
        // listView = (Search_Listview) findViewById(R.id.listView);
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取到用户点击列表里的文字,并自动填充到搜索框内
                TextView textView = (TextView) view.findViewById(R.id.text_id);
                String str=textView.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("city",str);
                setResult(10,intent);
                finish();


            }
        });*/

        // location_tv.setText(loction_address);
        // record=(LinearLayout)findViewById(R.id.record);

        br1.setBackgroundColor(getResources().getColor(R.color.white));
        br2.setBackgroundColor(getResources().getColor(R.color.white));



        //findViewById(R.id.scrollview).setVisibility(View.GONE);


        util = new CityUtils(context, hand);
        util.initProvince();
        lv_city = (ListView) findViewById(R.id.lv_city);
        regions = new ArrayList<MyRegion>();
        adapter = new CityAdapter(context);
        lv_city.setAdapter(adapter);
        lv_city.setFocusable(false);
        lv_city.setOnItemClickListener(onItemClickListener);

    }



    @SuppressLint("HandlerLeak")
    Handler hand = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1:
                    System.out.println("省份列表what======" + msg.what);

                    regions = (ArrayList<MyRegion>) msg.obj;
                    adapter.clear();
                    adapter.addAll(regions);
                    adapter.update();
                    break;

                case 2:
                    System.out.println("城市列表what======" + msg.what);
                    regions = (ArrayList<MyRegion>) msg.obj;
                    adapter.clear();
                    adapter.addAll(regions);
                    adapter.update();
                    lv_city.setSelection(0);
                    break;

                case 3:
                    System.out.println("区/县列表what======" + msg.what);
                    regions = (ArrayList<MyRegion>) msg.obj;
                    adapter.clear();
                    adapter.addAll(regions);
                    adapter.update();
                    lv_city.setSelection(0);
                    break;
            }
        };
    };

    //点确定
    private void sure() {
        Log.e("TAG", "sure: "+city.getProvince()+city.getCity()+city.getDistrict());
        if (city.getProvince()!=null&&city.getCity()!=null && city.getDistrict()!=null) {
            //   回调接口
            if (mOnAddressPickerSureListener != null) {
                mOnAddressPickerSureListener.onSureClick(city.getProvince(),city.getCity(),city.getDistrict(),city.getProvinceCode(),city.getCityCode(),city.getDistrictCode());
            }
        } else {
            Toast.makeText(mContext, "地址还没有选完整哦", Toast.LENGTH_SHORT).show();
        }

    }
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            if (current == PROVINCE) {
                String newProvince = regions.get(arg2).getName();
                if (!newProvince.equals(city.getProvince())) {
                    city.setProvince(newProvince);
                    tvs[0].setText(regions.get(arg2).getName());
                    city.setRegionId(regions.get(arg2).getId());
                    city.setProvinceCode(regions.get(arg2).getId());
                    city.setCityCode("");
                    city.setDistrictCode("");
                    tvs[1].setText("请选择");
                    br.setBackgroundColor(getResources().getColor(R.color.white));
                    br1.setBackgroundColor(getResources().getColor(R.color.red));
                    tvs[2].setText(" ");
                }

                current = 1;
                //点击省份列表中的省份就初始化城市列表
                util.initCities(city.getProvinceCode());
            } else if (current == CITY) {
                String newCity = regions.get(arg2).getName();
                if (!newCity.equals(city.getCity())) {
                    city.setCity(newCity);
                    tvs[1].setText(regions.get(arg2).getName());
                    city.setRegionId(regions.get(arg2).getId());
                    city.setCityCode(regions.get(arg2).getId());
                    city.setDistrictCode("");
                    tvs[2].setText("请选择");
                    br1.setBackgroundColor(getResources().getColor(R.color.white));
                    br2.setBackgroundColor(getResources().getColor(R.color.red));
                }

                //点击城市列表中的城市就初始化区县列表
                util.initDistricts(city.getCityCode());
                current = 2;

            } else if (current == DISTRICT) {
                current = 2;
                city.setDistrictCode(regions.get(arg2).getId());
                city.setRegionId(regions.get(arg2).getId());
                city.setDistrict(regions.get(arg2).getName());
                tvs[2].setText(regions.get(arg2).getName());
                Intent in = new Intent();
                in.putExtra("city", city);
                String str=city.getProvince()+" "+city.getCity()+" "+city.getDistrict();




            }
            tvs[last].setBackgroundColor(Color.WHITE);
            tvs[current].setBackgroundColor(Color.WHITE);
            last = current;
        }
    };



    //



    class CityAdapter extends ArrayAdapter<MyRegion> {

        LayoutInflater inflater;

        public CityAdapter(Context con) {
            super(con, 0);
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public View getView(int arg0, View v, ViewGroup arg2) {
            v = inflater.inflate(R.layout.city_item, null);
            TextView tv_city = (TextView) v.findViewById(R.id.tv_city);
            tv_city.setText(getItem(arg0).getName());
            return v;
        }

        public void update() {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

        if(v==mTvSure){
            sure();
        }

        if (ids[0] == v.getId()) {
            current = 0;
            util.initProvince();
            tvs[last].setBackgroundColor(Color.WHITE);
            tvs[current].setBackgroundColor(Color.WHITE);
            br.setBackgroundColor(getResources().getColor(R.color.red));
            br1.setBackgroundColor(getResources().getColor(R.color.white));
            br2.setBackgroundColor(getResources().getColor(R.color.white));
            last = current;
        } else if (ids[1] == v.getId()) {
            if (city.getProvinceCode() == null|| city.getProvinceCode().equals("")) {
                current = 0;
                Toast.makeText(mContext, "您还没有选择省份",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            util.initCities(city.getProvinceCode());
            current = 1;
            tvs[last].setBackgroundColor(Color.WHITE);
            tvs[current].setBackgroundColor(Color.WHITE);
            br.setBackgroundColor(getResources().getColor(R.color.white));
            br1.setBackgroundColor(getResources().getColor(R.color.red));
            br2.setBackgroundColor(getResources().getColor(R.color.white));
            last = current;
            last = current;
        } else if (ids[2] == v.getId()) {
            if (city.getProvinceCode() == null
                    || city.getProvinceCode().equals("")) {
                Toast.makeText(mContext, "您还没有选择省份",
                        Toast.LENGTH_SHORT).show();
                current = 0;
                util.initProvince();
                return;
            } else if (city.getCityCode() == null
                    || city.getCityCode().equals("")) {
                Toast.makeText(mContext, "您还没有选择城市",
                        Toast.LENGTH_SHORT).show();
                current = 1;
                util.initCities(city.getProvince());
                return;
            }
            current = 2;
            util.initDistricts(city.getCityCode());
            tvs[last].setBackgroundColor(Color.WHITE);
            tvs[current].setBackgroundColor(Color.WHITE);
            br.setBackgroundColor(getResources().getColor(R.color.white));
            br1.setBackgroundColor(getResources().getColor(R.color.white));
            br2.setBackgroundColor(getResources().getColor(R.color.red));
            last = current;
            last = current;
        }

    }



    /**
     * 点确定回调这个接口
     */
    public interface OnAddressPickerSureListener {
        void onSureClick(String Province, String City, String District, String ProvinceCode, String CityCode, String DistrictCode);
    }

    public void setOnAddressPickerSure(OnAddressPickerSureListener listener) {
        this.mOnAddressPickerSureListener = listener;
    }


}

