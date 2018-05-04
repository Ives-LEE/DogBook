package com.example.leeicheng.dogbook.articles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.example.leeicheng.dogbook.owner.Owner;
import com.example.leeicheng.dogbook.owner.SignUpFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

//    public void takePicture() {
//        Intent intent = new Intent();
//        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        file = new File(file, "photo.jpg");
//        contentUri = FileProvider.getUriForFile(this,getPackageName() + ".provider", file);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//        if (isIntentAvailable(intent)) {
//            startActivityForResult(intent, Common.REQ_TAKE_PICTURE);
//        }
//    }

    public void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (isIntentAvailable(intent)) {
            startActivityForResult(intent, Common.REQ_CHOOSE_PICTURE);
        }
    }

    public boolean isIntentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent
                , packageManager.MATCH_DEFAULT_ONLY);
        return resolveInfos.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Common.REQ_CHOOSE_PICTURE) {
                try {
                    Uri uri = data.getData();
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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.CREATE_ARTICLE);
            jsonObject.addProperty("article",gson.toJson(article));
            jsonObject.addProperty("media", Base64.encodeToString(image, Base64.DEFAULT));
            jsonObject.addProperty("type", 1);

            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                generalTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
