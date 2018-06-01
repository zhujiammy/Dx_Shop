package com.example.zhujia.dx_shop.Tools.Net;

/**
 * Created by ZHUJIA on 2018/1/23.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.example.zhujia.dx_shop.Activity.LoginActivity;
import com.example.zhujia.dx_shop.MainActivity;
import com.example.zhujia.dx_shop.Tools.App;
import com.example.zhujia.dx_shop.Tools.DXApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * okhttp请求
 * @author Flyjun
 *
 */
public class HttpUtils{


    private Context mContext;
    private SharedPreferences sharedPreferences;
    public static String TAG="debug-okhttp";
    public static boolean isDebug=true;
    private OkHttpClient client;
    // 超时时间
    public static final int TIMEOUT = 1000 * 60;
    public static final MediaType FORM_CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    //json请求
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private Handler handler = new Handler(Looper.getMainLooper());

    @SuppressLint("WrongConstant")
    public HttpUtils() {
        // TODO Auto-generated constructor stub
        mContext= App.getContext();
        sharedPreferences =mContext.getSharedPreferences("Session",
                Context.MODE_APPEND);
        this.init();
    }

    @SuppressLint("WrongConstant")
    private void init() {

        client = new OkHttpClient();
        client.sslSocketFactory();

        // 设置超时时间
        client.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    /**
     * post请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void postJson(String url, String json,String token,String loginUserId,final HttpCallback callback) {
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("loginUserId",loginUserId).post(body).build();
        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }



    //获取短信验证码
    public void postJsoncode(String url, String json,final HttpCallback callback) {
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).post(body).build();
        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * post请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void LoginPost(String url, String json, final HttpCallback callback) {

        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }



    /**
     * post请求，json数据为body
     *
     * @param url
     * @param callback
     */
    public void LogoutPost(String url,String token,final HttpCallback callback) {

        RequestBody body = RequestBody.create(null,"");
        Request request = new Request.Builder().url(url).addHeader("token",token).post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     *
     *
     * @param url
     * @param callback
     */
    public void Post(String url,String token,String loginUserId,final HttpCallback callback) {

        RequestBody body = RequestBody.create(null,"");
        Request request = new Request.Builder().url(url).addHeader("loginUserId",loginUserId).addHeader("token",token).post(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }



    /**
     *
     *
     * @param url
     * @param callback
     */
    public void Get(String url,final HttpCallback callback) {

        Request request = new Request.Builder().url(url).get().build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    public void GetOrderStatu(String url,String token,String loginUserId,final HttpCallback callback) {

        Request request = new Request.Builder().url(url).addHeader("loginUserId",loginUserId).addHeader("token",token).get().build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * put请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void putJson(String url, String json,String token, final HttpCallback callback) {

        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).put(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * delete请求，json数据为body
     *
     * @param url
     * @param callback
     */
    public void deleteJson(String url,String token,final HttpCallback callback) {

        //RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).delete().build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * update请求，json数据为body
     *
     * @param url
     * @param callback
     */
    public void update(String url,String json,String token,final HttpCallback callback) {

        //RequestBody body = RequestBody.create(JSON,json);
        RequestBody body=RequestBody.create(JSON,json);
        Request request = new Request.Builder().url(url).addHeader("token",token).put(body).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                onError(callback, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    onSuccess(callback, response.body().string());
                } else {
                    onError(callback, response.message());
                }
            }

        });

    }

    /**
     * post请求 map为body
     *
     * @param url
     * @param map
     * @param callback
     */
    public void post(String url, Map<String, String> map,String token,final HttpCallback callback) {

       /* FormBody.Builder builder = new FormBody.Builder();
         FormBody body=new FormBody.Builder().add("key", "value").build();*/

        /**
         * 创建请求的参数body
         *
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 遍历key
         */
        if (null != map) {
            for ( Map.Entry<String, String> entry : map.entrySet() ) {
                builder.add( entry.getKey(), entry.getValue() );
            }
        }
        RequestBody formBody = builder.build();
        Request request = new Request.Builder().url(url).addHeader("token",token).post(formBody).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     *
     * put请求
     *
     *
     */


    public void doPut(String url, Map<String, String> map,final HttpCallback callback) {

        // FormBody.Builder builder = new FormBody.Builder();
        // FormBody body=new FormBody.Builder().add("key", "value").build();

        /**
         * 创建请求的参数body
         *
         */
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String,String> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));

            }
        }

        Request request = new Request.Builder().url(url).put(requestBody.build()).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     *
     * doDelete请求
     *
     *
     */


    public void doDelete(String url, Map<String, String> map,final HttpCallback callback) {

        // FormBody.Builder builder = new FormBody.Builder();
        // FormBody body=new FormBody.Builder().add("key", "value").build();

        /**
         * 创建请求的参数body
         *
         */
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String,String> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));

            }
        }

        Request request = new Request.Builder().url(url).delete(requestBody.build()).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }

        });

    }


    /**
     * get请求
     * @param url
     * @param callback
     */
    public void get(String url, final HttpCallback callback) {

        Request request = new Request.Builder().url(url).build();

        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }
        });

    }

    //文件上传
    public void postUpload(String url,String token,String loginUserId, File fileList, String newfilename,final HttpCallback callback) {
        /**
         * 创建请求的参数body
         */
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);


        RequestBody fileBody1 = RequestBody.create(MediaType.parse("image/png") , fileList);

        requestBody.addFormDataPart("file",newfilename,fileBody1);
        /* 下边的就和post一样了 */

        Request request = new Request.Builder().url(url).addHeader("token",token).addHeader("loginUserId",loginUserId).post(requestBody.build()).build();
        onStart(callback);

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                onError(callback, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    onSuccess(callback, response.body().string());

                } else {
                    onError(callback, response.message());
                }
            }


        });


    }

    /**
     * log信息打印
     * @param params
     */
    public void debug(String params){
        if(false == isDebug){
            return;
        }

        if(null == params){
            Log.d(TAG, "params is null");
        }else{
            Log.d(TAG, params);
            try {
                JSONObject object=new JSONObject(params);
                if(object.getString("code").equals("401")){
                    Looper.prepare();
                    Toast.makeText(mContext,object.getString("msg"),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext, LoginActivity.class);
                    intent.putExtra("page","x");
                    mContext.startActivity(intent);
                    Looper.loop();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void onStart(HttpCallback callback) {
        if (null != callback) {
            callback.onStart();
        }
    }

    private void onSuccess(final HttpCallback callback, final String data) {

        debug(data);

        if (null != callback) {
            handler.post(new Runnable() {
                public void run() {
                    // 需要在主线程的操作。
                    callback.onSuccess(data);
                }
            });
        }
    }

    private void onError(final HttpCallback callback,final String msg) {
        if (null != callback) {
            handler.post(new Runnable() {
                public void run() {
                    // 需要在主线程的操作。
                    callback.onError(msg);
                }
            });
        }
    }


    /**
     * http请求回调
     *
     * @author Flyjun
     *
     */
    public static abstract class HttpCallback {
        // 开始
        public void onStart() {};

        // 成功回调
        public abstract void onSuccess(String data);

        // 失败回调
        public void onError(String msg) {};
    }

}
