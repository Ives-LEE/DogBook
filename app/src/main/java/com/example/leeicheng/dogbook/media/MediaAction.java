package com.example.leeicheng.dogbook.media;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.example.leeicheng.dogbook.main.Common;

import java.io.File;
import java.util.List;

/**
 * Created by leeicheng on 2018/5/9.
 */

public class MediaAction {

    public static Intent crop(Context context,Uri srcImageUri, String action) {
        Intent intent = null;
        Uri croppedImageUri;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo_crop.jpg");

        int width = 0;
        int height = 0;

        if (action.equals(Common.PROFILE_PHOTO)) {
            width = 150;
            height = 150;
        } else if (action.equals(Common.BACKGROUND_PHOTO)) {
            width = 160;
            height = 90;
        }

        croppedImageUri = Uri.fromFile(file);
        try {
            intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(srcImageUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            intent.putExtra("return-data", true);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
        return intent;
    }

    public static Uri takePicture(Context context) {
        Uri contentUri;
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo.jpg");
        contentUri = FileProvider.getUriForFile(context,context.getPackageName() + ".provider", file);
        return contentUri;
    }

    public static boolean isIntentAvailable(Intent intent,Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent
                , packageManager.MATCH_DEFAULT_ONLY);
        return resolveInfos.size() > 0;
    }
}
