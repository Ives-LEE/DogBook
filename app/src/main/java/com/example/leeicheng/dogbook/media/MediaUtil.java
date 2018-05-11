package com.example.leeicheng.dogbook.media;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import com.example.leeicheng.dogbook.main.Common;

import java.io.FileNotFoundException;

public class MediaUtil {
    final static float heightRatio = 0.5f;
    final static float widthRatio = 0.5f;
    public static Bitmap reduceSize(Bitmap bitmap) throws FileNotFoundException {
        Log.d("大小","前："+ bitmap.getByteCount());
        Matrix matrix = new Matrix();
        matrix.setScale(heightRatio,widthRatio);
        Bitmap result = Bitmap.createBitmap(bitmap,0,0,bitmap.getHeight(),bitmap.getWidth(),matrix,true);
        Log.d("大小","後："+ result.getByteCount());
        return result;
    }

    public static Bitmap restoreSize(Bitmap bitmap){
//        Matrix matrix = new Matrix();
//        matrix.setScale(heightRatio,widthRatio);
//        Bitmap result = Bitmap.createBitmap(bitmap,0,0,bitmap.getHeight(),bitmap.getWidth(),matrix,true);

        return null;
    }


    public static int getHeight(Bitmap bitmap){
        int height = bitmap.getHeight();
        return height;
    }
    public static int getWidth(Bitmap bitmap){
        int width = bitmap.getWidth();
        return width;
    }
}
