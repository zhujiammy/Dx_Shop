package com.example.zhujia.dx_shop.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.R;
import com.example.zhujia.dx_shop.Tools.ImageService;
import com.example.zhujia.dx_shop.Tools.Net.Constant;
import com.example.zhujia.dx_shop.Tools.Net.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

//个人信息界面
public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener {



    File outputImage;
    public Uri imageUri,mCutUri;
    public static final int HANDLE_MSG_LOAD_IMAGE = 10;
    public static final int HANDLE_MSG_LOAD_IMAGE2 = 12;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    public static final int  REQUEST_CODE=1001;
    public static final int  REQUEST_CODE2=1002;
    public static final int  REQUEST_CODE3=1003;
    public static final int CHOOSE_PHOTOS = 4;
    private  String timetype;
    private Bitmap photo;
    private Bitmap photo1;
    private File FilePath;
    private  String filename;
    private String mainImgUrl;
    private RelativeLayout profile_image_btn;
    private CircleImageView profile_image;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private String LoginState,TOKEN,loginUserId;
    private JSONObject header;
    private String iconUrlstr,nickNamestr,customerNamestr,sexstr,birthdaystr;
    private TextView customerName,nickName,sex,birthday;
    private LinearLayout customerName_lin,nickName_lin,sex_lin,birthday_lin;
    private Intent intent;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personaiinfo);
        sharedPreferences =getSharedPreferences("Session",
                Context.MODE_APPEND);
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


    }



    private void initUI(){

        TOKEN=sharedPreferences.getString("token","");
        loginUserId=sharedPreferences.getString("userId","");
        iconUrlstr=sharedPreferences.getString("iconUrl","");
        nickNamestr=sharedPreferences.getString("nickName","");
        customerNamestr=sharedPreferences.getString("customerName","");
        sexstr=sharedPreferences.getString("sex","");
        birthdaystr=sharedPreferences.getString("birthday","");
        profile_image_btn=(RelativeLayout)findViewById(R.id.profile_image_btn);
        profile_image_btn.setOnClickListener(this);
        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        customerName=(TextView)findViewById(R.id.customerName);
        nickName=(TextView)findViewById(R.id.nickName);
        sex=(TextView)findViewById(R.id.sex);
        birthday=(TextView)findViewById(R.id.birthday);
        customerName_lin=(LinearLayout)findViewById(R.id.customerName_lin);
        customerName_lin.setOnClickListener(this);
        nickName_lin=(LinearLayout)findViewById(R.id.nickName_lin);
        nickName_lin.setOnClickListener(this);
        sex_lin=(LinearLayout)findViewById(R.id.sex_lin);
        sex_lin.setOnClickListener(this);
        birthday_lin=(LinearLayout) findViewById(R.id.birthday_lin);
        birthday_lin.setOnClickListener(this);



        if(!iconUrlstr.equals("")){
            Glide.with(getApplicationContext()).load(iconUrlstr).into(profile_image);
        }else {
            profile_image.setImageDrawable(getResources().getDrawable(R.drawable.profile));
        }

        if(!customerNamestr.equals("")){
            customerName.setText(customerNamestr);
        }else {
            customerName.setText("");
        }

        if(!nickNamestr.equals("")){
            nickName.setText(nickNamestr);
        }else {
            nickName.setText("");
        }

        if(!sexstr.equals("")){
            if(sexstr.equals("M")){
                sex.setText("男");
            }
            if(sexstr.equals("F")){
                sex.setText("女");
            }
        }else {
            sex.setText("");
        }

        if(!birthdaystr.equals("")){
            birthday.setText(birthdaystr);
        }else {
            birthday.setText("");
        }

    }


    @Override
    public void onClick(View v) {

        if(v==profile_image_btn){
            //上传图片
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date(System.currentTimeMillis());
            filename = format.format(date);
            showPopwindows();
        }
        if(v==customerName_lin){

            intent=new Intent(getApplicationContext(),CustomerName.class);
            startActivityForResult(intent,REQUEST_CODE);
        }
        if(v==nickName_lin){
                intent=new Intent(getApplicationContext(),NickName.class);
            startActivityForResult(intent,REQUEST_CODE2);
        }
        if(v==sex_lin){
            intent=new Intent(getApplicationContext(),Sex.class);
            startActivityForResult(intent,REQUEST_CODE3);
        }
        if(v==birthday_lin){
            // 日期格式为yyyy-MM-dd
            final TimePickerView pvTime = new TimePickerView.Builder(PersonalInfoActivity.this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date2, View v) {//选中事件回调
                    timetype=getTime2(date2);
                    birthday.setText(timetype);
                    save("birthday",timetype,3);
                }
            })
                    .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                    .setCancelText("取消")//取消按钮文字
                    .setSubmitText("确定")//确认按钮文字
                    .setContentSize(20)//滚轮文字大小
                    .setTitleSize(20)//标题文字大小
//                        .setTitleText("请选择时间")//标题文字
                    .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                    .isCyclic(true)//是否循环滚动
                    .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                    .setCancelColor(Color.BLUE)//取消按钮文字颜色
//                        .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
//                        .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
//                        .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
//                        .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
//                        .setRangDate(startDate,endDate)//起始终止年月日设定
//                        .setLabel("年","月","日","时","分","秒")
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                        .isDialog(true)//是否显示为对话框样式
                    .build();
            Calendar calendar=new GregorianCalendar();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date daystart = df.parse(birthdaystr);
                calendar.setTime(daystart);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(!birthdaystr.equals("")){
                pvTime.setDate(calendar);//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
            }else {
                pvTime.setDate(Calendar.getInstance());
            }

            pvTime.show();

        }

    }






    //打开相机
    private void showPopwindows(){
        View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        View popView = View.inflate(this, R.layout.camera_pop_menu, null);

        Button btnCamera = (Button) popView.findViewById(R.id.btn_camera_pop_camera);
        Button btnAlbum = (Button) popView.findViewById(R.id.btn_camera_pop_album);
        Button btnCancel = (Button) popView.findViewById(R.id.btn_camera_pop_cancel);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        final PopupWindow popWindow = new PopupWindow(popView, width, height);
        popWindow.setAnimationStyle(R.style.AnimBottom);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()) {
                    case R.id.btn_camera_pop_camera:

                        //创建file对象，用于存储拍照后的图片
                        outputImage = new File(Environment.getExternalStorageDirectory(), "output_Image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }

                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int permissionCheck = ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                Manifest.permission.CAMERA);
                        //存储权限
                        int permissionCheck_storage = ContextCompat.checkSelfPermission(PersonalInfoActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (permissionCheck_storage == PackageManager.PERMISSION_GRANTED) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.zhujia.dx_shop.fileProvider",outputImage);
                                }else{
                                    imageUri = Uri.fromFile(outputImage);
                                }
                                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                                //启动相机程序
                                startActivityForResult(intent, TAKE_PHOTO);
                            } else {
                                // 没有权限，跳到设置界面，调用Android系统“应用程序信息（Application Info）”界面
                                new AlertDialog.Builder(PersonalInfoActivity.this)
                                        .setMessage("app需要读取存储权限")
                                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create()
                                        .show();
                            }

                        } else {

                            // 没有权限，跳到设置界面，调用Android系统“应用程序信息（Application Info）”界面
                            new AlertDialog.Builder(PersonalInfoActivity.this)
                                    .setMessage("app需要开启相机权限")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();

                        }


                        break;

                    case R.id.btn_camera_pop_album:
                        //存储权限
                        int permissionCheck_storage_xc = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (permissionCheck_storage_xc == PackageManager.PERMISSION_GRANTED) {
                            Intent intents = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intents, CHOOSE_PHOTOS);

                        } else {
                            // 没有权限，跳到设置界面，调用Android系统“应用程序信息（Application Info）”界面
                            new AlertDialog.Builder(PersonalInfoActivity.this)
                                    .setMessage("app需要读取存储权限")
                                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create()
                                    .show();
                        }

                        break;
                    case R.id.btn_camera_pop_cancel:

                        break;

                }
                popWindow.dismiss();
            }
        };

        btnCamera.setOnClickListener(listener);
        btnAlbum.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);

        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK) {
                        this.startPhotoZoom(imageUri);
                    }
                    break;

                case CHOOSE_PHOTO:
                    photo=null;
                        photo1=null;
                        photo= ImageService.loadImgFromLocal(PersonalInfoActivity.this,mCutUri);
                        photo1=ImageService.loadImgFromLocal(PersonalInfoActivity.this,mCutUri);
                        if(photo!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE, photo
                            );
                            mHandler.sendMessage(msg);
                        }
                        if(photo1!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE2, photo1
                            );
                            mHandler.sendMessage(msg);
                        }
                    break;
                case CHOOSE_PHOTOS:
                    if (resultCode == RESULT_OK) {

                        //启动裁剪
                        startActivityForResult(CutForPhoto(data.getData()),CHOOSE_PHOTO);

                    }
                    break;
                case CROP_PHOTO:
                    if (resultCode == RESULT_OK) {

                        photo1=null;
                        photo=null;
                        photo1=photo;
                        photo= ImageService.loadImgFromLocal(outputImage.getAbsolutePath());
                        photo1= ImageService.loadImgFromLocal(outputImage.getAbsolutePath());
                        if(photo!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE, photo
                            );
                            mHandler.sendMessage(msg);
                        }
                        if(photo1!=null){
                            Message msg = Message.obtain(
                                    mHandler, HANDLE_MSG_LOAD_IMAGE2, photo1
                            );
                            mHandler.sendMessage(msg);
                        }



                    }
                    break;


                case REQUEST_CODE:
                    customerName.setText(data.getStringExtra("customerName"));
                    break;
                case REQUEST_CODE2:
                    nickName.setText(data.getStringExtra("nickName"));
                    break;
                case REQUEST_CODE3:
                    if(data.getStringExtra("sex").equals("M")){
                        sex.setText("男");
                    }
                    if(data.getStringExtra("sex").equals("F")){
                        sex.setText("女");
                    }

                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startPhotoZoom(Uri uri) {
        //imageUri=FileProvider.getUriForFile(getApplicationContext(), "com.uroad.cargo.alpha.fileprovider", new File(ImageService.getImageAbsolutePath(getApplicationContext(), uri)));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // aspectX aspectY 是宽高的比例
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        //intent.putExtra("outputX", 800);
        //intent.putExtra("outputY", 600);
        // 启动裁剪程序
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_PHOTO);
    }


    /**
     * 图片裁剪
     * @param uri
     * @return
     */
    private Intent CutForPhoto(Uri uri) {
        try {
            //直接裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            //设置裁剪之后的图片路径文件
            File cutfile = new File(Environment.getExternalStorageDirectory().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = uri; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Log.d("TAG", "CutForPhoto: "+cutfile);
            outputUri = Uri.fromFile(cutfile);
            mCutUri = outputUri;
            Log.d("TAG", "mCameraUri: "+mCutUri);
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop",true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            //设置要裁剪的宽高
            intent.putExtra("outputX",200); //200dp
            intent.putExtra("outputY",200);
            intent.putExtra("scale",true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data",false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what) {


                    case 1:
                        header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){
                            mainImgUrl=header.getString("msg");
                            save("iconUrl",Constant.loadimag+mainImgUrl,2);
                        }
                        break;


                    case 2:
                         header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){
                            SharedPreferences.Editor editors=sharedPreferences.edit();
                            Log.e("TAG", "handleMessage: "+Constant.loadimag+mainImgUrl );
                            editors.putString("iconUrl",Constant.loadimag+mainImgUrl);
                            editors.commit();
                        }
                        break;

                    case 3:
                        header=new JSONObject(msg.obj.toString());
                        if(header.getString("code").equals("200")){
                            SharedPreferences.Editor editors=sharedPreferences.edit();
                            Log.e("TAG", "birthday: "+timetype);
                            editors.putString("birthday",timetype);
                            editors.commit();
                        }
                        break;

                    case HANDLE_MSG_LOAD_IMAGE:
                        FilePath = ImageService.compressImage1(photo,filename);
                        upload();
                        break;

                    case HANDLE_MSG_LOAD_IMAGE2:
                        profile_image.setImageBitmap(photo1);
                        break;
                    default:
                        Toast.makeText(PersonalInfoActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        break;
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
    };


    private void save(String parmsname, String parms, final int i){

        JSONObject object = new JSONObject();
        try {
            object.put(parmsname,parms);


        String params=object.toString();
        new HttpUtils().postJson(Constant.APPURLS+"account/update",params,TOKEN,loginUserId,new HttpUtils.HttpCallback() {
            @Override
            public void onSuccess(String data) {
                // TODO Auto-generated method stub
                com.example.zhujia.dx_shop.Tools.Log.printJson("tag",data,"header");

                Message msg= Message.obtain(
                        mHandler,i,data
                );
                mHandler.sendMessage(msg);
            }

        });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void upload(){

        new HttpUtils().postUpload(Constant.APPURLS+"upload/upload?path=account",TOKEN,loginUserId,FilePath, filename + ".png",new HttpUtils.HttpCallback() {
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



    public String getTime2(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
