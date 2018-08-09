package com.example.leeicheng.dogbook.articles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.chats.Chat;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MessageBoardActivity extends AppCompatActivity {

    Toolbar tbMessageBoard;
    TextView tvTitleMessageBoard;
    ImageView ivLeftToolbarMessageBoard;
    RecyclerView rvMessageBoard;
    ImageButton ibSubmitMessageBoard;
    EditText etInputMessageBoard;
    List<Message> messages;
    int articleId;
    GeneralTask generalTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_board_activity);
        findToolBarViews();
        findViews();
    }

    void findToolBarViews() {
        tvTitleMessageBoard = findViewById(R.id.tvTitleMessageBoard);
        ivLeftToolbarMessageBoard = findViewById(R.id.ivLeftToolbarMessageBoard);
        toolbarViewControl();
    }

    void toolbarViewControl() {
        tvTitleMessageBoard.setText(R.string.messageBoard);
        ivLeftToolbarMessageBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void findViews() {
        rvMessageBoard = findViewById(R.id.rvMessageBoard);
        ibSubmitMessageBoard = findViewById(R.id.ibSubmitMessageBoard);
        etInputMessageBoard = findViewById(R.id.etInputMessageBoard);
        viewsControl();
    }

    void viewsControl() {
        ibSubmitMessageBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                etInputMessageBoard.setText("");
                refresh();
                rvMessageBoard.scrollToPosition(messages.size()-1);
            }
        });
        refresh();
    }

    void refresh(){
        messages = getMessages();
        rvMessageBoard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMessageBoard.setAdapter(new MessageAdapter(this));
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageItemViewHolder> {

        Context context;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public MessageItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.message_board_item, parent, false);
            return new MessageItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageItemViewHolder holder, int position) {
            if (!messages.isEmpty()){
                Message message = messages.get(position);
                Dog dog = CommonRemote.getDogInfo(message.getDogId(),context);
                if (dog != null){
                    CommonRemote.getProfilePhoto(message.getDogId(),holder.civProfilePhotoMessageBoard,context);
                    holder.tvMessagePoster.setText(dog.getName());
                    holder.tvMessage.setText(message.getContent());
                }
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public class MessageItemViewHolder extends RecyclerView.ViewHolder {
            ImageView civProfilePhotoMessageBoard;
            TextView tvMessagePoster,tvMessage;
            public MessageItemViewHolder(View view) {
                super(view);
                civProfilePhotoMessageBoard = view.findViewById(R.id.civProfilePhotoMessageBoard);
                tvMessagePoster = view.findViewById(R.id.tvMessagePoster);
                tvMessage = view.findViewById(R.id.tvMessage);
            }
        }
    }

    void getArticleId(){
        Bundle bundle = getIntent().getExtras();
        articleId = bundle.getInt("articleId");
    }

    void sendMessage(){
        int dogId = Common.getPreferencesDogId(this);
        String content = etInputMessageBoard.getText().toString().trim();
        Message message = new Message(dogId,articleId,content);

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

    List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        getArticleId();

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
}
