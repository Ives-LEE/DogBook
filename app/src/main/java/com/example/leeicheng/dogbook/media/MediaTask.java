package com.example.leeicheng.dogbook.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.main.Common;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MediaTask extends AsyncTask<Object, Integer, Bitmap> {
    int CONNECT_SUCCESS = 200;
    private final static String TAG = "取得多媒體";
    private String url,status,kindOfId;
    private int id, imageSize;
    private WeakReference<ImageView> imageViewWeakReference;

    public MediaTask(String url, int id, int imageSize) {
        this.url = url;
        this.id = id;
        this.imageSize = imageSize;
        this.imageViewWeakReference = null;
    }

    public MediaTask(String url, int id, int imageSize, ImageView imageViewWeakReference , String status,String kindOfId) {
        this.url = url;
        this.id = id;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageViewWeakReference);
        this.status = status;
        this.kindOfId = kindOfId;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();

        if (status.equals(Common.GET_PROFILE_PHOTO)){
            jsonObject.addProperty("status", Common.GET_PROFILE_PHOTO);
        } else if(status.equals(Common.GET_PROFILE_BACKGROUND_PHOTO)) {
            jsonObject.addProperty("status", Common.GET_PROFILE_BACKGROUND_PHOTO);
        } else if (status.equals(Common.GET_ARTICLES)){
            jsonObject.addProperty("status", Common.GET_ARTICLES);
        } else if(status.equals(Common.GET_MY_ARTICLES)){
            jsonObject.addProperty("status", Common.GET_MY_ARTICLES);
        }

        if (kindOfId.equals("dog")){
            jsonObject.addProperty("dogId", id);
        } else if (kindOfId.equals("media")){
            jsonObject.addProperty("mediaId", id);
            
        }

        jsonObject.addProperty("imageSize", imageSize);
        return getRemoteMedia(url, jsonObject.toString());
    }

    private Bitmap getRemoteMedia(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");

            OutputStreamWriter outputWriter = new OutputStreamWriter(connection.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(outputWriter);
            bufferedWriter.write(jsonOut);
            Log.d(TAG, jsonOut.toString());
            bufferedWriter.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == CONNECT_SUCCESS) {
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
                // 存入暫存區
            } else {
                Log.d(TAG, "responseCode = " + responseCode);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }









}
