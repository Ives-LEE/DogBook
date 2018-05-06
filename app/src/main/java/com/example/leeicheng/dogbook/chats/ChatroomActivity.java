package com.example.leeicheng.dogbook.chats;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.articles.Article;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.util.List;

public class ChatroomActivity extends AppCompatActivity {
    TextView tvTitleChatroom;
    ImageView ivLeftToolbar;
    RecyclerView rvChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatsroom_activity);
        findViews();
    }

    void findViews() {
        tvTitleChatroom = findViewById(R.id.tvTitleChatroom);
        ivLeftToolbar = findViewById(R.id.ivLeftToolbarChatroom);
        rvChats = findViewById(R.id.rvChats);
        viewsControl();
    }

    void viewsControl() {
        rvChats.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvChats.setAdapter(new ChatsAdapter());
        rvChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        ivLeftToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private class ChatsAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.chats_item,parent,false);
            return new ChatsItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }

        private class ChatsItemViewHolder extends RecyclerView.ViewHolder {
            public ChatsItemViewHolder(View view) {
                super(view);
            }
        }
    }
}
