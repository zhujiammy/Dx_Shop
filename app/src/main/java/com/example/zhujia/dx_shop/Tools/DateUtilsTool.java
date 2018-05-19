package com.example.zhujia.dx_shop.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DXSW5 on 2017/7/21.
 */

public class DateUtilsTool {

    public String dateCH;

    public String getDate(String date){
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d, yyyy HH:mm:ss",
                Locale.ENGLISH);
        try {
            dateCH = sdf1.format(sdf2.parse(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  dateCH;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

}
