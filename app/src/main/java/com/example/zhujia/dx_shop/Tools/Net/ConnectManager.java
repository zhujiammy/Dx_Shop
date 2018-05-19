package com.example.zhujia.dx_shop.Tools.Net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by yeshaokun on 2017/1/11.
 */

public class ConnectManager {
    public  static boolean isNetworkAlive(Context ctx){
        ConnectivityManager manager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(Constant.LOGTAG, "CONNECTIVITY_ACTION");

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

                    Log.e(Constant.LOGTAG, "当前WiFi连接可用 ");
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
//                    UroadApp.getInstance().setMobile(true);
                    Log.e(Constant.LOGTAG, "当前移动网络连接可用 ");
                }
            } else {
                Log.e(Constant.LOGTAG, "当前没有网络连接，请确保你已经打开网络 ");
            }
            return  true;
        } else {   // not connected to the internet
            Log.e(Constant.LOGTAG, "当前没有网络连接，请确保你已经打开网络 ");
            /*UroadApp.getInstance().setWifi(false);
            UroadApp.getInstance().setMobile(false);
            UroadApp.getInstance().setConnected(false);*/
            return  false;

        }
    }

}
