package com.amsu.intelligentinsole.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;


import com.amsu.intelligentinsole.application.MyApplication;
import com.lidroid.xutils.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 10/25/16.
 */

public class MyUtil {
    private static ProgressDialog dialog;

    public static void showDialog(String message,Context context){
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(true);
            }
            dialog.setMessage(message);
            dialog.show();
        } catch (Exception e) {
            // 在其他线程调用dialog会报错
        }
    }

    public static void hideDialog() {
        if (dialog != null && dialog.isShowing())
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
    }

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static float getScreeenWidth(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽
        return width;
    }

    public static float getScreeenHeight(Activity activity){
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;  //屏幕高
        return height;
    }

    public static void showToask(Context context ,String text){
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    public static String getStringValueFromSP(String key){
        return MyApplication.sharedPreferences.getString(key,"");
    }

    public static boolean getBooleanValueFromSP(String key){
        return MyApplication.sharedPreferences.getBoolean(key,false);
    }

    public static int getIntValueFromSP(String key){
        return MyApplication.sharedPreferences.getInt(key,-1);
    }


    public static void putStringValueFromSP(String key,String value){
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putString(key,value).apply();
    }

    public static void putBooleanValueFromSP(String key,Boolean value){
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putBoolean(key,value).apply();
    }

    public static void putIntValueFromSP(String key,int value){
        SharedPreferences.Editor edit = MyApplication.sharedPreferences.edit();
        edit.putInt(key,value).apply();
    }

    public static boolean isEmpty(String text){
        if (text==null || text.equals("")){
            return true;
        }
        else {
            return false;
        }
    }

    //添加Cookie
    public static void addCookieForHttp(RequestParams requestParams){
        String cookie = getStringValueFromSP("Cookie");
        if (!MyUtil.isEmpty(cookie)){
            requestParams.addHeader("Cookie",cookie);
        }
        else {
            Log.e("com.amsu.healthy","Cookie is null");
        }
    }

    public static String getFormatTime(Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }

    public static String getSpecialFormatTime(String stringFormat,Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat(stringFormat, Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }


    public static String getECGFileNameDependFormatTime(Date date){
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);  //07-12 15:10
        return format.format(date);
    }

    public static String getCueMapDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }


    //获取app的版本名称
    public static String getVersionName(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取app的版本号
    public static int getVersionCode(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static float getDimen(Context context,int resource){
        float dimension = context.getResources().getDimension(resource);
        return dimension;
    }

}
