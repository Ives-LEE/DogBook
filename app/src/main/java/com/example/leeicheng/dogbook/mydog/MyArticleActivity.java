package com.example.leeicheng.dogbook.mydog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.articles.Article;
import com.example.leeicheng.dogbook.articles.Message;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyArticleActivity extends AppCompatActivity {

    GeneralTask generalTask;
    RecyclerView rvMyArticle;
    MyArticleBoard myArticleBoard ;
    ImageButton ibSendMessage;
    EditText etMessageInput;
    List<Message> messages;
    Article article;
    Dog dog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myarticle_activity);
        messages = new ArrayList<>();
        findViews();
    }

    void findViews() {
        Bundle bundle = getIntent().getExtras();
        article = (Article) bundle.getSerializable("article");

        rvMyArticle = findViewById(R.id.rvMyArticle);
        etMessageInput = findViewById(R.id.etMessageInput);
        ibSendMessage = findViewById(R.id.ibSendMessage);
        myArticleBoard = new MyArticleBoard(this,article);
        rvMyArticle.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvMyArticle.setAdapter(myArticleBoard);
        viewsControl();
    }


    void viewsControl() {
        messages = getMessages(article.getArticleId());

        ibSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(article.getArticleId());
                etMessageInput.setText("");
                messages = getMessages(article.getArticleId());
                myArticleBoard.notifyDataSetChanged();

            }
        });
    }


    void sendMessage(int artocleId){
        int dogId = Common.getPreferencesDogId(this);

        String content = etMessageInput.getText().toString().trim();
        Message message = new Message(dogId,artocleId,content);

        if (Common.isNetworkConnect(getApplicationContext())) {
            Gson gson = new Gson();
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SEND_MESSAGE);
            jsonObject.addProperty("message", gson.toJson(message));
            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                generalTask.execute().get();

            } catch (InterruptedException | ExecutionException e) {
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

    List<Message> getMessages(int articleId) {
        List<Message> messages = new ArrayList<>();

        if (Common.isNetworkConnect(getApplicationContext())) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_MESSAGE_BOARD);
            jsonObject.addProperty("articleId", articleId);
            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                Type type = new TypeToken<List<Message>>() {
                }.getType();
                messages = gson.fromJson(jsonIn, type);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    private class MyArticleBoard extends RecyclerView.Adapter {
        Context context;
        Article article;
        private class MyMessageViewHolder extends RecyclerView.ViewHolder {
            CircleImageView civProfilePhotoMessageBoard;
            TextView tvMessagePoster,tvMessage;

            public MyMessageViewHolder(View view) {
                super(view);
                civProfilePhotoMessageBoard = view.findViewById(R.id.civProfilePhotoMessageBoard);
                tvMessagePoster = view.findViewById(R.id.tvMessagePoster);
                tvMessage = view.findViewById(R.id.tvMessagePoster);
            }
        }

        private class MyArticleViewHolder extends RecyclerView.ViewHolder {
            CircleImageView civMyArticleAuthorPhoto;
            TextView tvMyArticleName,tvMyArticleContent,tvLikeCount;
            ImageView ivMyArticlePhoto;
            public MyArticleViewHolder(View view) {
                super(view);
                civMyArticleAuthorPhoto = view.findViewById(R.id.civMyArticleAuthorPhoto);
                tvMyArticleName = view.findViewById(R.id.tvMyArticleName);
                tvMyArticleContent = view.findViewById(R.id.tvMyArticleContent);
                tvLikeCount = view.findViewById(R.id.tvLikeCount);
                ivMyArticlePhoto = view.findViewById(R.id.ivMyArticlePhoto);
            }

        }

        public MyArticleBoard(Context context,Article article) {
            this.context = context;
            this.article = article;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            if (viewType == 0) {
                View itemView = layoutInflater.inflate(R.layout.mydog_myarticle_item, parent, false);
                return new MyArticleViewHolder(itemView);
            } else {
                View itemView = layoutInflater.inflate(R.layout.message_board_item, parent, false);
                return new MyMessageViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0){
                MyArticleViewHolder myArticleViewHolder = (MyArticleViewHolder) holder;

                int likeCount = getLikeCount(article.getArticleId());
                if ((dog = CommonRemote.getDogInfo(article.getDogId(), context)) != null) {
                    myArticleViewHolder.tvMyArticleName.setText(dog.getName());
                }
                if (article != null) {
                    myArticleViewHolder.tvMyArticleContent.setText(article.getContent());
                    CommonRemote.getProfilePhoto(article.getDogId(), myArticleViewHolder.civMyArticleAuthorPhoto,context);
                    CommonRemote.getMedia(article.getMediaId(), myArticleViewHolder.ivMyArticlePhoto, context);
                }
                myArticleViewHolder.tvLikeCount.setText(likeCount + " likes");

                return;
            }
            MyMessageViewHolder myMessageViewHolder = (MyMessageViewHolder) holder;
            Message message = messages.get(position-1);
            Dog dog = CommonRemote.getDogInfo(message.getDogId(),context);
            if (dog != null){
                CommonRemote.getProfilePhoto(message.getDogId(),myMessageViewHolder.civProfilePhotoMessageBoard,context);
                myMessageViewHolder.tvMessagePoster.setText(dog.getName());
                myMessageViewHolder.tvMessage.setText(message.getContent());
            }
        }

        @Override
        public int getItemCount() {
            return messages.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        }


    }
}
