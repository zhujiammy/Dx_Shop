package com.example.zhujia.dx_shop.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.Activity.ConfirmationOrder;
import com.example.zhujia.dx_shop.Data.DataBean;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by DXSW5 on 2018/3/14.
 */

public class ShoppingCart_Page extends Fragment implements View.OnClickListener {

    private View view;
    private ListView mListView;// 列表

    private ListAdapter mListAdapter;// adapter

    private List<DataBean> mListData = new ArrayList<DataBean>();// 数据

    private boolean isBatchModel;// 是否可删除模式

    private RelativeLayout mBottonLayout;
    private CheckBox mCheckAll; // 全选 全不选

    private TextView mEdit; // 切换到删除模式

    private TextView mPriceAll; // 商品总价

    private TextView mSelectNum; // 选中数量


    private TextView mDelete; // 删除 结算

    private double totalPrice = 0; // 商品总价

    private Handler mHandler;

    private LinearLayout emptyView;
    private Toolbar toolbar;
    int maxId = 0;

    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId;
    private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
    private MenuItem item;

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.shoppingcart_page,container,false);
        sharedPreferences =getActivity().getSharedPreferences("Session",
                Context.MODE_APPEND);
        initUI();
        initListener();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
    }



    @SuppressLint("NewApi")
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            //相当于Fragment的onPause
            Log.e("TAG", "onHiddenChanged: "+"购物车不可见" );
        } else {
            // 相当于Fragment的onResume
            Log.e("TAG", "onHiddenChanged: "+"购物车可见" );
            TOKEN=sharedPreferences.getString("token","");
            loginUserId=sharedPreferences.getString("userId","");
            refreshListView();
            mListData.clear();
            loaddata();
            mListData=getData();


        }
    }

    private void initListener()
    {
        mDelete.setOnClickListener(this);
        mCheckAll.setOnClickListener(this);

    }

    private void initUI(){
        mBottonLayout = (RelativeLayout)view.findViewById(R.id.cart_rl_allprie_total);
        mCheckAll = (CheckBox) view.findViewById(R.id.check_box);
        mPriceAll = (TextView) view.findViewById(R.id.tv_cart_total);
        mSelectNum = (TextView) view.findViewById(R.id.tv_cart_select_num);
        mDelete = (TextView) view.findViewById(R.id.tv_cart_buy_or_del);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setSelector(R.drawable.list_selector);
    }



    private void loaddata(){
        if (mListData != null && mListData.size() > 0)
            maxId = mListData.get(mListData.size() - 1).getId();
        new HttpUtils().Post(Constant.APPURLS+"order/shopping/list",TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
    private List<DataBean> getData()
    {
        final List<DataBean> result1 = new ArrayList<DataBean>();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {

                    switch (msg.what){
                        case 0:

                           JSONObject reslutJSONObject=new JSONObject(msg.obj.toString());
                           if(reslutJSONObject.getString("code").equals("404")){
                               refreshListView();
                               mSelectState.clear();
                               totalPrice = 0;
                               mSelectNum.setText("已选" + 0 + "件商品");
                               mPriceAll.setText("￥" + 0.00 + "");
                               mCheckAll.setChecked(false);
                           }
                            JSONArray resultlist=reslutJSONObject.getJSONArray("object");
                            DataBean data = null;
                            int id=0;
                            for (int i = 0; i < resultlist.length(); i++)
                            {
                                id++;
                                JSONObject object=resultlist.getJSONObject(i);
                                data = new DataBean();
                                data.setId(id);
                                data.setID(object.getString("id"));
                                data.setProductName(object.getString("productName"));
                                data.setProductAttr(object.getString("productAttr"));
                                data.setSalePrice(object.getDouble("salePrice"));
                                data.setImage(object.getString("image"));
                                data.setProductType(object.getString("productType"));
                                data.setQuantity(object.getInt("quantity"));
                                if(!object.isNull("promotionTitle")){
                                    data.setPromotionTitle(object.getString("promotionTitle"));
                                }else {
                                    data.setPromotionTitle("null");
                                }
                                result1.add(data);
                            }
                           refreshListView();
                            break;

                        case 2:
                            JSONObject reslutJSONObjects=new JSONObject(msg.obj.toString());
                            break;
                        case 3:
                            JSONObject object=new JSONObject(msg.obj.toString());
                            if(object.getString("code").equals("200")){
                                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                                isBatchModel =true;
                                item.setTitle(getResources().getString(R.string.menu_edit));
                                mBottonLayout.setVisibility(View.VISIBLE);
                                mDelete.setText(getResources().getString(R.string.menu_sett));
                                refreshListView();
                                mSelectState.clear();
                                totalPrice = 0;
                                mSelectNum.setText("已选" + 0 + "件商品");
                                mPriceAll.setText("￥" + 0.00 + "");
                                mCheckAll.setChecked(false);
                            }else {
                                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        };
        return result1;
    }

    private void refreshListView()
    {
        if (mListAdapter == null)
        {
            View emptyView = view.findViewById(R.id.emptyView);
            mListAdapter = new ListAdapter();
            mListView.setAdapter(mListAdapter);
            mListView.setEmptyView(emptyView);
            mSelectState.clear();
            mListData.clear();
            mCheckAll.setChecked(false);
            mListView.setOnItemClickListener(mListAdapter);



        } else
        {
            mListAdapter.notifyDataSetChanged();

        }
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
/*
            case R.id.subtitle:
                isBatchModel = !isBatchModel;
                if (isBatchModel)
                {
                    mEdit.setText(getResources().getString(R.string.menu_enter));
                    mDelete.setText(getResources().getString(R.string.menu_del));
                    mBottonLayout.setVisibility(View.GONE);
                    mFavorite.setVisibility(View.VISIBLE);

                } else
                {
                    mEdit.setText(getResources().getString(R.string.menu_edit));

                    mFavorite.setVisibility(View.GONE);
                    mBottonLayout.setVisibility(View.VISIBLE);
                    mDelete.setText(getResources().getString(R.string.menu_sett));
                }

                break;*/

            case R.id.check_box:
                if (mCheckAll.isChecked())
                {

                    totalPrice = 0;
                    if (mListData != null)
                    {
                        mSelectState.clear();
                        int size = mListData.size();
                        if (size == 0)
                        {
                            return;
                        }
                        for (int i = 0; i < size; i++)
                        {
                            int _id = (int) mListData.get(i).getId();
                            mSelectState.put(_id, true);
                            totalPrice += mListData.get(i).getQuantity() * mListData.get(i).getSalePrice();

                        }
                        refreshListView();

                        mPriceAll.setText("￥" +   new DecimalFormat("0.00").format(totalPrice) + "");
                        mSelectNum.setText("已选" + mSelectState.size() + "件商品");

                    }
                } else
                {
                    if (mListAdapter != null)
                    {
                        totalPrice = 0;
                        mSelectState.clear();
                        refreshListView();
                        mPriceAll.setText("￥" + 0.00 + "");
                        mSelectNum.setText("已选" + 0 + "件商品");

                    }
                }
                break;

            case R.id.tv_cart_buy_or_del:
                if (isBatchModel)
                {
                    List<Integer> ids = getSelectedIds();
                    doDelete(ids);
                } else
                {
                    dosettlemetn();
                }
                break;

            default:
                break;
        }
    }



    private final List<Integer> getSelectedIds()
    {
        ArrayList<Integer> selectedIds = new ArrayList<Integer>();
        for (int index = 0; index < mSelectState.size(); index++)
        {
            if (mSelectState.valueAt(index))
            {
                selectedIds.add(mSelectState.keyAt(index));
            }
        }
        return selectedIds;
    }

    private class ListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener
    {
        HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

        @Override
        public int getCount()
        {
            return mListData.size();
        }

        @Override
        public Object getItem(int position)
        {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            View view = convertView;
            if (view == null)
            {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.cart_list_item, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else
            {
                holder = (ViewHolder) view.getTag();
            }

            final DataBean data = mListData.get(position);
            bindListItem(holder, data);
            holder.add.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub

                    int _id = (int) mListData.get(position).getId();
                    String ID=mListData.get(position).getID();

                    boolean selected = mSelectState.get(_id, false);

                    mListData.get(position).setQuantity(mListData.get(position).getQuantity() + 1);
                    updatecartNum(data.getQuantity(),ID);
                    notifyDataSetChanged();
                    if (selected)
                    {
                        totalPrice += mListData.get(position).getSalePrice();
                        mPriceAll.setText("￥" + new DecimalFormat("0.00").format(totalPrice) + "");

                    }

                }
            });

            holder.red.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    // TODO Auto-generated method stub
                    if (mListData.get(position).getQuantity() == 1)
                        return;

                    int _id = (int) mListData.get(position).getId();
                    String ID=mListData.get(position).getID();
                    boolean selected = mSelectState.get(_id, false);
                    mListData.get(position).setQuantity(mListData.get(position).getQuantity() - 1);

                    updatecartNum(data.getQuantity(),ID);
                    notifyDataSetChanged();

                    if (selected)
                    {
                        totalPrice -= mListData.get(position).getSalePrice();
                        mPriceAll.setText("￥" + new DecimalFormat("0.00").format(totalPrice) + "");

                    }

                }
            });

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        state.put(position,b);
                    }else {
                        state.remove(position);
                    }
                }
            });

            holder.del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ID=mListData.get(position).getID();
                    try {
                        JSONObject object = new JSONObject();
                        object.put("productItemId",ID);
                        object.put("quantity","0");
                        final String params = object.toString();
                        new HttpUtils().postJson(Constant.APPURLS+"order/shopping/add",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
                            @Override
                            public void onSuccess(String data) {
                                // TODO Auto-generated method stub
                                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");
                                try {
                                    JSONObject object1=new JSONObject(data);
                                    if(object1.getString("code").equals("200")){

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        });
                        mListData.remove(position);
                        mListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            final ViewHolder finalHolder = holder;
            holder.lin_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DataBean bean = mListData.get(position);

                    final int _id = (int) bean.getId();

                    final boolean selected = !mSelectState.get(_id, false);
                    finalHolder.checkBox.toggle();
                    if (selected)
                    {
                        mSelectState.put(_id, true);
                        totalPrice += bean.getQuantity() * bean.getSalePrice();
                    } else
                    {
                        mSelectState.delete(_id);
                        totalPrice -= bean.getQuantity() * bean.getSalePrice();
                    }
                    mSelectNum.setText("已选" + mSelectState.size() + "件商品");
                    mPriceAll.setText("￥" + new DecimalFormat("0.00").format(totalPrice) + "");

                    if (mSelectState.size() == mListData.size())
                    {
                        mCheckAll.setChecked(true);
                    } else
                    {
                        mCheckAll.setChecked(false);
                    }
                }
            });
            return view;
        }

        //购物车数量

        private void updatecartNum(int amount,String catid){
            try {
                JSONObject object = new JSONObject();
                object.put("productItemId",catid);
                object.put("quantity",String.valueOf(amount));
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

        }




        private void bindListItem(ViewHolder holder, final DataBean data)
        {

            final HashMap<Integer,Boolean>state=new HashMap<Integer,Boolean>();
            holder.productName.setText(data.getProductName());
            holder.productAttr.setText(data.getProductAttr());
            holder.salePrice.setText("￥" + new DecimalFormat("0.00").format(data.getSalePrice()));
            if(data.getPromotionTitle().equals("null")){
                holder.promotionTitle.setVisibility(View.GONE);
            }else {
                holder.promotionTitle.setText(data.getPromotionTitle());
                holder.promotionTitle.setVisibility(View.VISIBLE);
            }
            holder.quantity.setText(data.getQuantity() + "");
            Glide.with(getActivity()).load(Constant.loadimag+data.getImage()).into(holder.image);

            final int _id = data.getId();
            final boolean selected = mSelectState.get(_id, false);
            holder.checkBox.setChecked(selected);



        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {




        }

    }
    class ViewHolder
    {
        CheckBox checkBox;

        LinearLayout lin_check;
        ImageView image;
        EditText quantity;
        TextView salePrice;
        TextView productName;
        TextView productAttr;
        ImageView add;
        ImageView red;
        Button del_btn;
        TextView promotionTitle;

        public ViewHolder(View view)
        {
            checkBox = (CheckBox) view.findViewById(R.id.check_box);
            image = (ImageView) view.findViewById(R.id.image);
            quantity = (EditText) view.findViewById(R.id.quantity);
            salePrice = (TextView) view.findViewById(R.id.salePrice);
            productName = (TextView) view.findViewById(R.id.productName);
            productAttr=(TextView)view.findViewById(R.id.productAttr);
            red = (ImageView) view.findViewById(R.id.iv_sub);
            add = (ImageView) view.findViewById(R.id.iv_add);
            lin_check=(LinearLayout)view.findViewById(R.id.lin_check);
            del_btn=(Button)view.findViewById(R.id.btnDelete1);
            promotionTitle=(TextView)view.findViewById(R.id.promotionTitle);
        }
    }

    private void doDelete(final List<Integer> ids)
    {

        List<String>IDS=new LinkedList<String>();
        for (int i = 0; i < mListData.size(); i++)
        {
            long dataId = mListData.get(i).getId();
            for (int j = 0; j < ids.size(); j++)
            {
                int deleteId = ids.get(j);

                if (dataId == deleteId)
                {
                    String a= String.valueOf(mListData.get(j).getID());
                    Log.e(TAG, "doDelete: "+a );
                    IDS.add(a);
                    mListData.remove(i);
                    i--;
                    ids.remove(j);
                    j--;
                }
            }
        }

        DelShop(IDS.toString());


    }
    @SuppressLint("NewApi")
    private void DelShop(String id){
        try {
            String ids=id.replace("[","").replace("]","");
            Log.e("TAG", "length: "+ids.length());
            if(ids.length()==0){
                Toast.makeText(getActivity(),"还没选择需要删除的物品",Toast.LENGTH_SHORT).show();
            }else {

                String [] stringArr= ids.split(",");
                JSONArray j=new JSONArray(stringArr);
                final String params=j.toString().replaceAll( "\\\\","").replace(" ", "");
                Log.e("TAG", "ids: "+params);
                //新增
                new HttpUtils().postJson(Constant.APPURLS+"order/shopping/deleByIds",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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
            }



        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void dosettlemetn(){

        List<String>imgurls=new LinkedList<String>();
        List<String>contents=new LinkedList<String>();
        List<Integer>nums=new LinkedList<Integer>();
        List<Double> prices=new LinkedList<>();
        List<String>totals=new LinkedList<String>();
        List<Integer> ids=new LinkedList<>();
        List<String>IDS=new LinkedList<String>();
        List<String>ProductAttrs=new LinkedList<String>();
        List<String>promotionTitles=new LinkedList<>();
        List<String>productTypes=new LinkedList<>();
        HashMap<Integer, Boolean> state =mListAdapter.state;
        String imgurl1 = null,content=null,total=null,productAttr=null,promotionTitle=null,productType=null;
        int id;
        String ID;
        int num=0;
        double price=0;
        for (int j=0;j<mListData.size();j++){
            if(state.get(j)!=null){
                imgurl1=mListData.get(j).getImage();
                content=mListData.get(j).getProductName();
                num= mListData.get(j).getQuantity();
                price=mListData.get(j).getSalePrice();
                productType=mListData.get(j).getProductType();
                total= String.valueOf(mListData.get(j).getQuantity()*mListData.get(j).getSalePrice());
                id= mListData.get(j).getId();
                ID=mListData.get(j).getID();
                promotionTitle=mListData.get(j).getPromotionTitle();
                productAttr=mListData.get(j).getProductAttr();

                imgurls.add(imgurl1);
                contents.add(content);
                nums.add(num);
                prices.add(price);
                totals.add(total);
                ids.add(id);
                IDS.add(ID);
                ProductAttrs.add(productAttr);
                promotionTitles.add(promotionTitle);
                productTypes.add(productType);
            }

        }
        if(imgurls.isEmpty()){

        }else {
                getActivity().finish();
               Intent intent=new Intent(getActivity(),ConfirmationOrder.class);
                intent.putExtra("image",imgurls.toString());
                intent.putExtra("quantity",nums.toString());
                intent.putExtra("salePrice",prices.toString());
                intent.putExtra("id",IDS.toString());
                intent.putExtra("productName",contents.toString());
                intent.putExtra("productAttr",ProductAttrs.toString());
                intent.putExtra("totals",totals.toString());
                intent.putExtra("promotionTitle",promotionTitles.toString());
                intent.putExtra("productType",productTypes.toString());
                startActivity(intent);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.meun_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        this.item=item;
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id==R.id.edit_btn){
            isBatchModel = !isBatchModel;
            if (isBatchModel)
            {
                item.setTitle(getResources().getString(R.string.menu_enter));
                mDelete.setText(getResources().getString(R.string.menu_del));
                mBottonLayout.setVisibility(View.INVISIBLE);
            } else
            {
                item.setTitle(getResources().getString(R.string.menu_edit));
                mBottonLayout.setVisibility(View.VISIBLE);
                mDelete.setText(getResources().getString(R.string.menu_sett));
            }

        }



        return super.onOptionsItemSelected(item);
    }

}
