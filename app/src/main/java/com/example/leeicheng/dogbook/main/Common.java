package com.example.leeicheng.dogbook.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


import com.example.leeicheng.dogbook.chats.ChatWebSocketClient;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;


public class Common {
    public static final int REQ_TAKE_PICTURE = 0;
    public static final int REQ_CHOOSE_PROFILE_PICTURE = 1;
    public static final int REQ_CHOOSE_BACKGROUND_PICTURE = 2;
    public static final int REQ_CROP_PROFILE_PICTURE = 3;
    public static final int REQ_CROP_BACKGROUND_PICTURE = 4;
    public static final int REQ_CHOOSE_PICTURE = 5;
    final static int NOTIFICATION_ID = 0;

    public static final String PROFILE_PHOTO = "profilePhoto";
    public static final String BACKGROUND_PHOTO = "backgroundPhoto";
    public final static String URL = "http://10.0.2.2:8080/DogBookServlet";
//    public final static String URL = "http://192.168.43.68:8080/DogBookServlet";
//    private static String SERVER_URI = "ws://192.168.43.68:8080/DogBookServlet/ChatWebSocketServer/";
    private static String SERVER_URI = "ws://10.0.2.2:8080/DogBookServlet/ChatWebSocketServer/";
    public final static String PREF_FILE = "preference";
    public final static String INSERT = "insert";
    public final static String SELECT = "select";
    final static String GET_DOG_INFO = "getDogInfo";
    public final static String ADD_DOG = "addDog";
    public final static String GET_PROFILE_PHOTO = "getProfilePhoto";
    public final static String SET_PROFILE_PHOTO = "setProfilePhoto";
    public final static String GET_PROFILE_BACKGROUND_PHOTO = "getProfileBackgroundPhoto";
    public final static String SET_PROFILE_BACKGROUND_PHOTO = "setProfileBackgroundPhoto";
    public final static String SET_ARTICLE_PHOTO = "setArticlePhoto";
    public final static String CREATE_ARTICLE = "createArticle";
    public final static String GET_ARTICLES = "getArticles";
    public final static String GET_MY_ARTICLES = "getMyArticles";
    public final static String SHOW_ROOMS = "showRooms";
    public final static String GET_CHATS_RECODING = "getChatsRecoding";
    public final static String GET_LAST_CHAT = "getLastChat";
    public final static String GET_ROOM_DOGS = "getRoomDogs";
    public final static String ADD_LIKE = "addLike";
    public final static String DELETE_LIKE = "deleteLike";
    public final static String SELECT_LIKE = "selectLike";
    public final static String GET_LIKE_COUNT = "getLikeCount";
    public final static String ADD_METER = "addMeter";
    public final static String GET_METER = "getMeter";
    public final static String GET_EVENTS = "getEvents";
    public final static String ADD_EVENT = "addEvent";



    public static float mile = 0;
    public static int room = -1;
    public static ChatWebSocketClient chatWebSocketClient;


    public static void connectServer(Context context, int dogId) {
        URI uri = null;
        try {
            uri = new URI(SERVER_URI + dogId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (chatWebSocketClient == null) {
            chatWebSocketClient = new ChatWebSocketClient(uri, context);
            chatWebSocketClient.connect();
        }
    }

    public static void disconnectServer() {
        if (chatWebSocketClient != null) {
            chatWebSocketClient.close();
            chatWebSocketClient = null;
        }

    }

    // ben added below two showToast

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String getPreferenceAll(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        return pref.getAll().toString();
    }

    public static void setPreferenceClear(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }

    public static int getPreferencesDogId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        int id = pref.getInt("dogId", -1);
        return id;
    }

    public static void setPreferencesDogId(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        pref.edit().putInt("dogId", id).apply();
    }

    public static int getPreferencesOwnerId(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        int id = pref.getInt("ownerId", -1);
        return id;
    }

    public static void setPreferencesOwnerId(Context context, int id) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        pref.edit().putInt("ownerId", id).apply();
    }

    public static boolean getPreferencesIsLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        boolean isLogin = pref.getBoolean("isLogin", false);
        return isLogin;
    }

    public static void setPreferencesIsLogin(Context context, boolean isLogin) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        pref.edit().putBoolean("isLogin", isLogin).apply();
    }

    public static int getPreferencesRoom(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                context.MODE_PRIVATE);
        int roomId = pref.getInt("roomId", -1);
        return roomId;
    }

    public static void setPreferencesRoom(Context context, int roomId) {
        SharedPreferences pref = context.getSharedPreferences(Common.PREF_FILE,
                Context.MODE_PRIVATE);
        pref.edit().putInt("roomId", roomId).apply();
    }

}
