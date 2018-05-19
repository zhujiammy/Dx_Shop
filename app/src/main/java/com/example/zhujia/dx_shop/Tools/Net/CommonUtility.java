package com.example.zhujia.dx_shop.Tools.Net;



import android.app.AlertDialog;
import android.content.Context;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;


/**
 * 通用方法类
 * @author lim
 *
 */
public class CommonUtility {
	
	/**
	 * 整数(秒数)转换为时分秒格式(xx:xx:xx)
	 * @param time
	 * @return
	 */
	public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;  
        int minute = 0;  
        int second = 0;  
        if (time <= 0)  
            return "00:00:00";  
        else {  
            minute = time / 60;  
            if (minute < 60) {  
                second = time % 60;  
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);  
            } else {  
                hour = minute / 60;  
                if (hour > 99)  
                    return "99:59:59";  
                minute = minute % 60;  
                second = time - hour * 3600 - minute * 60;  
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);  
            }
        }
        return timeStr;  
    }  
  
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)  
            retStr = "0" + Integer.toString(i);
        else  
            retStr = "" + i;  
        return retStr;  
    }  
    





	

	/**
	 * 弹出对话框
	 * @param message
	 */
	public static void Alert(Context context, String message){
		new AlertDialog.Builder(context)
			.setTitle("系统提示")
			.setMessage(message)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton("确定", null)
			.show();
	}
	
	/**
	 * 根据值, 设置spinner默认选中:
	 * @param spinner
	 * @param value
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner, String value){
	    SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
	    int k= apsAdapter.getCount();
	    for(int i=0;i<k;i++){
	        if(value.equals(apsAdapter.getItem(i).toString())){
	            spinner.setSelection(i,true);// 默认选中项
	            break;
	        }
	    }
	} 
	
}
