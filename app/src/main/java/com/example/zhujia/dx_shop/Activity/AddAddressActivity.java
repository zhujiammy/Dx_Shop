package com.example.zhujia.dx_shop.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.AddressPickerView;
import com.example.zhujia.dx_shop.Tools.CitySelect1Activity;
import com.example.zhujia.dx_shop.Tools.ClearEditText;
import com.example.zhujia.dx_shop.Tools.CustomDialog;
import com.example.zhujia.dx_shop.Tools.IEditTextChangeListener;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;
import com.example.zhujia.dx_shop.Tools.WorksSizeCheckUtil;
import com.hmy.popwindow.PopWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.zhujia.dx_shop.Tools.insertComma.isMobile;

//新建收货人
public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {



    private PopWindow popWindow;
    private View customView;
    private Toolbar toolbar;
    private Intent intent;
    private String iconUrl,nickNamestr,customerNamestr;
    private SharedPreferences sharedPreferences;
    private Button add_address_btn;
    private RelativeLayout select_contact;
    private ClearEditText et_contact,et_contact_phone,xxet;
    private String LoginState,TOKEN,loginUserId;
    private TextView select_address,title;
    private String receiveProvince,receiveCity,address,receiveDistrict;
    private CitySelect1Activity citySelect1Activity;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addaddress);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        intent=getIntent();
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


    }



    @SuppressLint("SetTextI18n")
    private void initUI(){
        title=(TextView)findViewById(R.id.title);
        add_address_btn=(Button)findViewById(R.id.add_address_btn);
        add_address_btn.setOnClickListener(this);
        select_contact=(RelativeLayout)findViewById(R.id.select_contact);
        et_contact=(ClearEditText) findViewById(R.id.et_contact);
        et_contact_phone=(ClearEditText)findViewById(R.id.et_contact_phone);
        select_address=(TextView)findViewById(R.id.select_address);
        xxet=(ClearEditText) findViewById(R.id.xxet);
        select_contact.setOnClickListener(this);
        et_contact_phone.setOnClickListener(this);
        et_contact.setOnClickListener(this);
        select_address.setOnClickListener(this);
        customView = View.inflate(AddAddressActivity.this, R.layout.address_select, null);
        popWindow = new PopWindow.Builder(AddAddressActivity.this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setView(customView)
                .create();

        citySelect1Activity=(CitySelect1Activity) customView.findViewById(R.id.apvAddress);
        citySelect1Activity.setOnAddressPickerSure(new CitySelect1Activity.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String Province, String City, String District, String ProvinceCode, String CityCode, String DistrictCode) {
                select_address.setText(Province+" "+City+" "+District);
                receiveProvince=Province;
                receiveCity=City;
                receiveDistrict=District;
                popWindow.dismiss();
            }
        });


       if(intent.getStringExtra("type").equals("1")){
           title.setText("新建收货人");
       }else {
           title.setText("编辑收货人");
           et_contact.setText(intent.getStringExtra("person"));
           et_contact_phone.setText(intent.getStringExtra("phone"));
           select_address.setText(intent.getStringExtra("receiveProvince")+" "+intent.getStringExtra("receiveCity")+" "+intent.getStringExtra("receiveDistrict"));
           xxet.setText(intent.getStringExtra("address"));

       }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.add_address_btn:

                JSONObject object = new JSONObject();
                try {
                    object.put("person",et_contact.getText().toString());
                    object.put("phone",et_contact_phone.getText().toString());
                        object.put("receiveProvince",receiveProvince);
                        object.put("receiveCity",receiveCity);
                        object.put("receiveDistrict",receiveDistrict);
                    object.put("address",xxet.getText().toString());
                    final String params=object.toString();

                    if(intent.getStringExtra("type").equals("1")){

                        //新增
                        new HttpUtils().postJson(Constant.APPURLS+"address/insert",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
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

                    }else {

                        //修改

                        new HttpUtils().postJson(Constant.APPURLS+"address/update?id="+intent.getStringExtra("id")+"",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
                            @Override
                            public void onSuccess(String data) {
                                // TODO Auto-generated method stub
                                Log.e("TAG", "onSuccess: "+params );
                                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                                Message msg= Message.obtain(
                                        mHandler,1,data
                                );
                                mHandler.sendMessage(msg);
                            }

                        });

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
            case R.id.select_contact: //跳转到联系人列表
                Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,0);
                break;
            case R.id.select_address:
                popWindow.show();
                break;

        }

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {
                    case 1:
                        JSONObject header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){

                            if(intent.getStringExtra("type").equals("1")){
                                Intent intent=new Intent();
                                intent.putExtra("statue","save");
                                setResult(RESULT_OK,intent);
                                finish();
                                Toast.makeText(getApplicationContext(),"新建成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Intent intent=new Intent();
                                intent.putExtra("statue","save");
                                setResult(RESULT_OK,intent);
                                finish();
                                Toast.makeText(getApplicationContext(),"编辑成功",Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(data==null){
                    return;
                }
                //此处返回的data，获取选择的联系人信息
                Uri uri=data.getData();
                String[]contacts=getPhoneContacts(uri);
                et_contact.setText(contacts[0]);
                et_contact_phone.setText(contacts[1].replace(" ", ""));
                break;



        }
    }

    private String[] getPhoneContacts(Uri uri) {

        String[]contact=new String[2];
        //得到ContentResolver对象
        ContentResolver cr=getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor=cr.query(uri,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFiledColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0]=cursor.getString(nameFiledColumnIndex);
            //取得电话号码
            String ContactId=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone=cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+ContactId,null,null);
            if(phone!=null){
                phone.moveToFirst();
                contact[1]=phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            }
            phone.close();
            cursor.close();
        }else {
            return  null;
        }
        return contact;
    }




}
