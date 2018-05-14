package com.example.leeicheng.dogbook.main;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class CommonRemote {



    public static String TAG = "CommonRemote";

    public static Dog getDogInfo(int dogId, Context context) {
        Gson gson = new GsonBuilder().create();
        Dog dog = null;
        GeneralTask generalTask;
        if (Common.isNetworkConnect(context)) {
            String url = Common.URL + "/DogServlet";
            JsonObject jsonObject = new JsonObject();
            dog = new Dog(dogId);

            jsonObject.addProperty("status", Common.GET_DOG_INFO);
            jsonObject.addProperty("dog", gson.toJson(dog));

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String info = generalTask.execute().get();
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                dog = gson.fromJson(info, Dog.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return dog;
    }

    public static Bitmap getProfilePhoto(int dogId, ImageView imageView,Context context) {
        Bitmap bitmap = null;
        MediaTask mediaTask;
        int photoSize = context.getResources().getDisplayMetrics().widthPixels / 4;
        if (Common.isNetworkConnect(context)) {
            String url = Common.URL + "/MediaServlet";
            mediaTask = new MediaTask(url, dogId, photoSize, imageView, Common.GET_PROFILE_PHOTO, "dog");
            try {
                bitmap = mediaTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return bitmap;
    }


    public static void sendMedia(Bitmap photo, Context context) {
        GeneralTask generalTask;
        String TAG = "送";
        if (Common.isNetworkConnect(context)) {
            String url = Common.URL + "/MediaServlet";
            byte[] image = Common.bitmapToPNG(photo);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SET_PROFILE_PHOTO);
            jsonObject.addProperty("dogId", Common.getPreferencesDogId(context));
            jsonObject.addProperty("media", Base64.encodeToString(image, Base64.DEFAULT));
            jsonObject.addProperty("type", 1);
            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                String jsonIn = generalTask.execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);

                Log.d(TAG, "成功 = " + jObject.get("isSuccess").getAsString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public static void getMedia(int MediaId, ImageView imageView,Context context) {
        MediaTask mediaTask;
        int photoSize = context.getResources().getDisplayMetrics().widthPixels;
        if (Common.isNetworkConnect(context)) {
            String url = Common.URL + "/MediaServlet";
            mediaTask = new MediaTask(url, MediaId, photoSize, imageView, Common.GET_ARTICLES, "media");
            try {
                mediaTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public static float getMeter(int dogId, Context context) {
        Gson gson = new GsonBuilder().create();
        float meter = 0;

        GeneralTask generalTask;
        if (Common.isNetworkConnect(context)) {
            String url = Common.URL + "/DogServlet";
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("status", Common.GET_METER);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {

                String JsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(JsonIn, JsonObject.class);
                meter = jsonObject.get("meter").getAsFloat();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return meter;
    }
}
