package com.example.leeicheng.dogbook.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class Common {
    public static final int REQ_TAKE_PICTURE = 0;
    public static final int REQ_CHOOSE_PROFILE_PICTURE = 1;
    public static final int REQ_CHOOSE_BACKGROUND_PICTURE = 2;
    public static final int REQ_CROP_PROFILE_PICTURE = 3;
    public static final int REQ_CROP_BACKGROUND_PICTURE = 4;
    public static final String PROFILE_PHOTO = "profilePhoto";
    public static final String BACKGROUND_PHOTO = "backgroundPhoto";
    public final static String URL = "http://10.0.2.2:8080/DogBookServlet";
    public final static String PREF_FILE = "preference";
    public final static String INSERT = "insert";
    public final static String SELECT = "select";
    public final static String GET_DOG_INFO = "getDogInfo";
    public final static String ADD_DOG = "addDog";
    public final static String GET_PROFILE_PHOTO = "getProfilePhoto";
    public final static String SET_PROFILE_PHOTO = "setProfilePhoto";
    public final static String GET_PROFILE_BACKGROUND_PHOTO = "getProfileBackgroundPhoto";
    public final static String SET_PROFILE_BACKGROUND_PHOTO = "setProfileBackgroundPhoto";


    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static byte[] bitmapToPNG(Bitmap srcBitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        srcBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String getPreferenceAll(Context context){
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        return pref.getAll().toString();
    }

    public static void setPreferenceClear(Context context){
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }

    public static int getPreferencesDogId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        int id = pref.getInt("dogId", -1);
        return id;
    }

    public static void setPreferencesDogId(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        pref.edit().putInt("dogId", id).apply();
    }

    public static int getPreferencesOwnerId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        int id = pref.getInt("ownerId", -1);
        return id;
    }

    public static void setPreferencesOwnerId(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        pref.edit().putInt("ownerId", id).apply();
    }

    public static boolean getPreferencesIsLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        boolean isLogin = pref.getBoolean("isLogin", false);
        return isLogin;
    }

    public static void setPreferencesIsLogin(Context context, boolean isLogin) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        pref.edit().putBoolean("isLogin", isLogin).apply();
    }


}
