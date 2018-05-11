package com.example.leeicheng.dogbook.articles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaAction;
import com.example.leeicheng.dogbook.media.MediaUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;

public class AddArticleActivity extends AppCompatActivity {
    ImageView ivBack, ivArticlePhoto;
    Button btnSend, btnGetPicture;
    EditText etContent;
    String content,location;
    Bitmap photo;
    GeneralTask generalTask;
    String TAG = "新增文章";
    Article article;
    int dogId,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_articles_activity);
        findViews();
    }

    void findViews() {
        ivBack = findViewById(R.id.ivLeftToolbar);
        btnSend = findViewById(R.id.btnSend);
        etContent = findViewById(R.id.etContentAddArticle);
        btnGetPicture = findViewById(R.id.btnGetPicture);
        ivArticlePhoto = findViewById(R.id.ivArticlePhotoAdd);
        dogId = Common.getPreferencesDogId(this);
        viewsControl();
    }

    void viewsControl() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                content = etContent.getText().toString();
                location = "here";
                status = 1;
                article = new Article(dogId,content,location,status);
                sendContent(article);
                finish();
            }
        });

        btnGetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

    }

    public void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (MediaAction.isIntentAvailable(intent,this)) {
            startActivityForResult(intent, Common.REQ_CHOOSE_PICTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Common.REQ_CHOOSE_PICTURE) {
                Uri uri = data.getData();
                try {
                    photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    ivArticlePhoto.setImageBitmap(photo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void sendContent(Article article) {
        if (Common.isNetworkConnect(this)) {
            Gson gson = new Gson();
            String url = Common.URL + "/ArticleServlet";
            byte[] image = Common.bitmapToPNG(photo);
//            Log.d("大小","前"+image.length);
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
//            BitmapFactory.decodeByteArray(image,0,image.length);
//            int height = options.outHeight;
//            int width= options.outWidth;
//            int inSampleSize = 10; // 默认像素压缩比例，压缩为原图的1/2
//            int minLen = Math.min(height, width); // 原图的最小边长
//            if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
//                float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
//                inSampleSize = (int)ratio;
//            }
//            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
//            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
//            Bitmap bm = BitmapFactory.decodeByteArray(image,0,image.length,options);
//
//            image = Common.bitmapToPNG(bm);
//            Log.d("大小","後"+image.length);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.CREATE_ARTICLE);
            jsonObject.addProperty("article",gson.toJson(article));
            jsonObject.addProperty("media", Base64.encodeToString(image, Base64.DEFAULT));
            jsonObject.addProperty("type", 1);

            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                generalTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
