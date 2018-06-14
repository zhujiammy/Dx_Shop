package com.example.zhujia.dx_shop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.pickerview.lib.WheelView;
import com.example.zhujia.dx_shop.Activity.LoginActivity;
import com.example.zhujia.dx_shop.Fragment.HomePage;
import com.example.zhujia.dx_shop.Fragment.MyPage;
import com.example.zhujia.dx_shop.Fragment.ShoppingCart_Page;
import com.example.zhujia.dx_shop.Fragment.TypePage;
import com.example.zhujia.dx_shop.Service.LongRunningService;
import com.example.zhujia.dx_shop.Tools.AESUtil;
import com.example.zhujia.dx_shop.Tools.CheckExitService;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.OpenLogin;
import com.example.zhujia.dx_shop.Tools.PermissionsActivity;
import com.example.zhujia.dx_shop.Tools.PermissionsChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    private FragmentManager manager;
    private HomePage homePage;
    private TypePage typePage;
    private ShoppingCart_Page shoppingCart_page;
    private MyPage myPage;
    private static final int REQUEST_CODE = 0; // 请求码
    private OpenLogin openLogin;
    private Toolbar toolbar;
    private String Id;
    private SharedPreferences sharedPreferences;
    Map<String,String> params;
    private List<String> list=new ArrayList<>();
    private NavigationController navigationController;
    private String LoginState;
    private Intent intent;
    private TextView tvToolTitle;
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.zhujia.dx_shop.MESSAGE_RECEIVED_ACTION";
    public static boolean isForeground = false;
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private String loginName,passWord,Token,userId;
    private JSONObject reslutJSONObject;
    private  SQLiteDatabase db;
    private static final String TABLENAME = "region";
    private static final String CREATETABLE = "CREATE TABLE " + TABLENAME +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id INTEGER,parent_id INTEGER,name TEXT)";

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            // Manifest.permission.ACCESS_COARSE_LOCATION,
            //Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            //Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        mPermissionsChecker = new PermissionsChecker(this);
        intent=getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        tvToolTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        LoginState=sharedPreferences.getString("LoginState","");
        registerMessageReceiver();
        initUI();
        Boolean user_first = sharedPreferences.getBoolean("FIRST", true);
        if(user_first&&LoginState.equals("yes")){
            sharedPreferences.edit().putBoolean("FIRST", false).commit();
        /*    intent=new Intent(getApplicationContext(), LongRunningService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);*/

        }

        openLogin=new OpenLogin(MainActivity.this);
        if(intent.getStringExtra("select")!=null){

            if(intent.getStringExtra("select").equals("2")){
                select(2);
            }
            if(intent.getStringExtra("select").equals("3")){
                select(3);
            }

        }

    }

    private void initUI(){
        tvToolTitle.setText(getResources().getString(R.string.homepage));
        tvToolTitle.setTextColor(Color.BLACK);
        PageBottomTabLayout tab = (PageBottomTabLayout) findViewById(R.id.tab);
        navigationController =tab.custom()
                .addItem(newItem(R.drawable.home,R.drawable.home_selected,getResources().getString(R.string.homepage)))
                .addItem(newItem(R.drawable.type,R.drawable.type_selected,getResources().getString(R.string.type)))
                .addItem(newItem(R.drawable.shopcar,R.drawable.shopcar_selected,getResources().getString(R.string.shoppcart)))
                .addItem(newItem(R.drawable.personalcenter,R.drawable.personalcenter_selected,getResources().getString(R.string.personalcenter)))
                .build();
        navigationController.addTabItemSelectedListener(listener);
      /*  username=(TextView)view.findViewById(R.id.username);
        companyName=(TextView)view.findViewById(R.id.companyName);
        //imageView=(ImageView)view.findViewById(R.id.imageView);
        //imageView.setOnClickListener(this);
        username.setText(user);
        companyName.setText(comp);*/
        homePage=new HomePage();
        typePage=new TypePage();
        shoppingCart_page=new ShoppingCart_Page();
        myPage=new MyPage();

        manager=getSupportFragmentManager();
        //初次登陆，显示首页，隐藏其他
        FragmentTransaction transaction=manager.beginTransaction();
        toolbar.setVisibility(View.GONE);
        transaction.add(R.id.main_content,homePage);
        transaction.add(R.id.main_content,typePage);
        transaction.add(R.id.main_content,shoppingCart_page);
        transaction.add(R.id.main_content,myPage);
        transaction.show(homePage);
        transaction.hide(typePage);
        transaction.hide(shoppingCart_page);
        transaction.hide(myPage);
        transaction.commit();

        //新建地址数据库
        //address();


    }








/*    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    OnTabItemSelectedListener listener=new OnTabItemSelectedListener() {
        @Override
        public void onSelected(int index, int old) {
            FragmentTransaction transaction=manager.beginTransaction();
            switch (index){
                //当选中首页id时，显示framelayout加载首页fragment
                case 0:
                    transaction.show(homePage);
                    transaction.hide(typePage);
                    transaction.hide(shoppingCart_page);
                    transaction.hide(myPage);
                    toolbar.setVisibility(View.GONE);
                    tvToolTitle.setText(getResources().getString(R.string.homepage));
                    transaction.commit();
                    break;

                case 1:
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    transaction.hide(homePage);
                    transaction.show(typePage);
                    transaction.hide(shoppingCart_page);
                    transaction.hide(myPage);
                    toolbar.setVisibility(View.GONE);
                    tvToolTitle.setText(getResources().getString(R.string.type));
                    transaction.commit();
                    break;
               case 2:
                       getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                       transaction.hide(homePage);
                       transaction.hide(typePage);
                       transaction.show(shoppingCart_page);
                       transaction.hide(myPage);
                       toolbar.setVisibility(View.GONE);
                       tvToolTitle.setText(getResources().getString(R.string.shoppcart));
                       transaction.commit();


                    break;
                case 3:
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        transaction.hide(homePage);
                        transaction.hide(typePage);
                        toolbar.setVisibility(View.VISIBLE);
                        transaction.hide(shoppingCart_page);
                        transaction.show(myPage);
                        tvToolTitle.setText(getResources().getString(R.string.personalcenter));
                        toolbar.setVisibility(View.GONE);
                        transaction.commit();

                    break;


            }
        }

        @Override
        public void onRepeat(int index) {

        }
    };


    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // DXApp.getInstance().hasNews = true;
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }else {
           // updataapk();
        }
        isForeground = true;


        //init();
    }

  /*  private void init() {
        JPushInterface.init(getApplicationContext());
        JPushInterface.resumePush(getApplicationContext());
        // 设置JPush别名
        new JPushInterface().setAliasAndTags(getApplicationContext(), JPushUtil.getImei(getApplicationContext()), null);
    }*/

    private void select(int i){
        navigationController.setSelect(i);
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED)
        {
            finish();
        }
    }


    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }


    private BaseTabItem newItem(int drawable, int checkedDrawable, String text){
        NormalItemView normalItemView =new NormalItemView(this);
        normalItemView.initialize(drawable,checkedDrawable,text);
        normalItemView.setTextDefaultColor(Color.GRAY);
        normalItemView.setTextCheckedColor(getResources().getColor(R.color.red));
        return  normalItemView;

    }



    private void address(){
        new HttpUtils().Post(Constant.APPURLS+"common/address","","",new HttpUtils.HttpCallback() {

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
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            try {
                switch (msg.what) {

                    case 1:
                        db = MainActivity.this.openOrCreateDatabase("city.db",MODE_PRIVATE,null);
                        db.execSQL(CREATETABLE);
                         db = getApplication().openOrCreateDatabase("city.db",MODE_PRIVATE,null);
                        ContentValues values = new ContentValues();
                        ContentValues values1 = new ContentValues();
                        ContentValues values2 = new ContentValues();
                        JSONArray jsonArray=new JSONArray(msg.obj.toString());
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.getJSONObject(i);
                            values.put("id",object.getInt("code"));
                            values.put("parent_id",1);
                            values.put("name",object.getString("value"));
                            JSONArray jsonArray1=object.getJSONArray("children");
                            for(int j=0;j<jsonArray1.length();j++){
                                JSONObject object1=jsonArray1.getJSONObject(j);
                                values1.put("id",object1.getInt("code"));
                                values1.put("parent_id",object.getInt("code"));
                                values1.put("name",object1.getString("value"));
                                db.insert(TABLENAME,null,values1);
                                JSONArray jsonArray2=object1.getJSONArray("children");
                                for(int d=0;d<jsonArray2.length();d++){
                                    JSONObject object2=jsonArray2.getJSONObject(d);
                                    values2.put("id",object2.getInt("code"));
                                    values2.put("parent_id",object1.getInt("code"));
                                    values2.put("name",object2.getString("value"));
                                    db.insert(TABLENAME,null,values2);
                                }
                            }
                            db.insert(TABLENAME,null,values);
                        }

                        db.close();
                        break;


                    case 2:
                        reslutJSONObject=new JSONObject(msg.obj.toString());
                        String result_code=reslutJSONObject.getString("code");
                        if(result_code.equals("200")){
                            //存储TOKEN信息
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            //系统用户
                            editor.putString("token",reslutJSONObject.getString("msg"));
                            editor.commit();

                        }
                        break;

                    default:
                        Toast.makeText(getApplicationContext(),"网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }



        }
    };
}
