package com.example.leeicheng.dogbook.mydog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.articles.Article;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaAction;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyArticleActivity extends AppCompatActivity {
    ImageView civProfilePhoto, ivArticlePhoto;
    CheckBox cbLike;
    TextView tvPosterName, likesCount, tvArticleContent;
    Dog dog;
    GeneralTask generalTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myarticle_activity);
        findViews();
    }

    void findViews() {
        civProfilePhoto = findViewById(R.id.civProfilePhoto);
        cbLike = findViewById(R.id.cbLike);
        ivArticlePhoto = findViewById(R.id.ivArticlePhoto);
        tvArticleContent = findViewById(R.id.tvArticleContent);
        likesCount = findViewById(R.id.likesCount);
        tvPosterName = findViewById(R.id.tvPosterName);
        viewsControl();
    }

    void viewsControl() {
        Bundle bundle = getIntent().getExtras();

        final Article article = (Article) bundle.getSerializable("article");
        Drawable drawable;
        int likeCount;
        if ((dog = CommonRemote.getDogInfo(article.getDogId(), this)) != null) {
            tvPosterName.setText(dog.getName());
        }
        if (article != null) {
            tvArticleContent.setText(article.getContent());
            CommonRemote.getProfilePhoto(article.getDogId(), civProfilePhoto,this);
            CommonRemote.getMedia(article.getMediaId(), ivArticlePhoto, this);
        }

        cbLike.setOnCheckedChangeListener(null);

        if (isLike(article.getArticleId())) {
            drawable = getResources().getDrawable(R.drawable.ic_favorite_24dp);
            cbLike.setBackground(drawable);
            cbLike.setChecked(true);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
            cbLike.setBackground(drawable);
            cbLike.setChecked(false);
        }


        cbLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Drawable drawable;
            int articleId = article.getArticleId();

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
                if (selected) {
                    addLike(articleId);
                    drawable = getResources().getDrawable(R.drawable.ic_favorite_24dp);
                    cbLike.setBackground(drawable);
                    cbLike.setChecked(selected);
                } else {
                    deleteLike(articleId);
                    drawable = getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
                    cbLike.setBackground(drawable);
                    cbLike.setChecked(selected);
                }
            }
        });

        likeCount = getLikeCount(article.getArticleId());
        likesCount.setText(likeCount + " likes");

    }


    boolean isLike(int articleId) {
        boolean isLike = false;
        int dogId = Common.getPreferencesDogId(this);
        if (Common.isNetworkConnect(this)) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SELECT_LIKE);
            jsonObject.addProperty("articleId", articleId);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(jsonIn, JsonObject.class);
                isLike = jsonObject.get("isLike").getAsBoolean();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return isLike;
    }

    void addLike(int articleId) {
        int dogId = Common.getPreferencesDogId(this);
        if (Common.isNetworkConnect(this)) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.ADD_LIKE);
            jsonObject.addProperty("articleId", articleId);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                generalTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    void deleteLike(int articleId) {
        int dogId = Common.getPreferencesDogId(this);
        if (Common.isNetworkConnect(this)) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.DELETE_LIKE);
            jsonObject.addProperty("articleId", articleId);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                generalTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

    }

    int getLikeCount(int articleId) {
        int likeCount = 0;
        if (Common.isNetworkConnect(this)) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_LIKE_COUNT);
            jsonObject.addProperty("articleId", articleId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(jsonIn, JsonObject.class);
                likeCount = jsonObject.get("likeCount").getAsInt();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return likeCount;
    }

}
