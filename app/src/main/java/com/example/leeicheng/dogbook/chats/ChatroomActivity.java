package com.example.leeicheng.dogbook.chats;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatroomActivity extends AppCompatActivity {
    TextView tvTitleChatroom;
    EditText etInput;
    ImageView ivLeftToolbar;
    RecyclerView rvChats;
    ImageButton ibSubmit;
    Toolbar tbChatroom;
    int friendId, roomId;
    GeneralTask generalTask;
    Dog dog;
    MediaTask mediaTask;
    String TAG = "聊天室";
    LocalBroadcastManager broadcastManager;
    public static Bitmap friendPhoto;

    List<Chat> chats;
    ChatsAdapter chatsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatsroom_activity);

        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        registerChatReceiver();

        getFriend();
        chats = getChatsRecoding(roomId);
        findToolBarViews();
        findViews();
    }

    void getFriend() {
        Bundle bundle = getIntent().getExtras();
        friendId = bundle.getInt("friendId");
        roomId = bundle.getInt("roomId");
        Common.setPreferencesRoom(this,roomId);
        Common.room = roomId;
        dog = CommonRemote.getDogInfo(friendId,this);
    }

    void findToolBarViews() {
        tvTitleChatroom = findViewById(R.id.tvTitleChatroom);
        ivLeftToolbar = findViewById(R.id.ivLeftToolbarChatroom);
        toolbarViewControl();
    }

    void toolbarViewControl() {
        if (dog != null) {
            tvTitleChatroom.setText(dog.getName());
        }
        ivLeftToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void findViews() {
        rvChats = findViewById(R.id.rvChats);
        ibSubmit = findViewById(R.id.ibSubmit);
        etInput = findViewById(R.id.etInput);
        viewsControl();
    }

    void viewsControl() {

        rvChats.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatsAdapter = new ChatsAdapter();
        rvChats.setAdapter(chatsAdapter);
        rvChats.scrollToPosition(chatsAdapter.getItemCount() - 1);
        rvChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
        etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                rvChats.scrollToPosition(chatsAdapter.getItemCount()-1);
            }
        });
        ibSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("chat");
        ChatReceiver chatReceiver = new ChatReceiver();
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
    }

    private class ChatReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Chat chat = new Gson().fromJson(message, Chat.class);
            int sender = chat.getSenderId();

            if (sender == friendId) {
                chats.add(chat);
                chatsAdapter.notifyDataSetChanged();
                rvChats.scrollToPosition(chatsAdapter.getItemCount() - 1);
            }
        }
    }

    void submit() {

        int dogId = Common.getPreferencesDogId(this);
        String message = etInput.getText().toString();
        if (message.trim().isEmpty()) {
            Toast.makeText(this, R.string.messageEmpty, Toast.LENGTH_SHORT).show();
            return;
        }
        etInput.setText(null);
        Chat chat = new Chat(dogId, roomId, message, "chat");
        String chatJson = new Gson().toJson(chat);
        Common.chatWebSocketClient.send(chatJson);
        Log.d(TAG, "output: " + chatJson);
        chats.add(chat);
        chatsAdapter.notifyDataSetChanged();
        rvChats.scrollToPosition(chatsAdapter.getItemCount() - 1);

    }


    private class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsItemViewHolder> {
        View view;

        @Override
        public ChatsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.chats_item, parent, false);
            return new ChatsItemViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ChatsItemViewHolder holder, int position) {
            Chat chat = chats.get(position);
            int sender = chat.getSenderId();

            if (sender != friendId) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) holder.cvChatBubble.getLayoutParams();
                layoutParams.removeRule(RelativeLayout.END_OF);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                holder.cvChatBubble.setLayoutParams(layoutParams);
                holder.civFriendProfilePhoto.setVisibility(View.INVISIBLE);
                holder.tvChat.setText(chat.getMessage());
            } else {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) holder.cvChatBubble.getLayoutParams();
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.END_OF, R.id.civFriendProfilePhotoChat);
                holder.cvChatBubble.setLayoutParams(layoutParams);
                holder.civFriendProfilePhoto.setVisibility(View.VISIBLE);
                if (friendPhoto ==null){
                    friendPhoto = CommonRemote.getProfilePhoto(friendId, holder.civFriendProfilePhoto,getApplicationContext());
                }
                holder.civFriendProfilePhoto.setImageBitmap(friendPhoto);
                holder.tvChat.setText(chat.getMessage());
            }

        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        public class ChatsItemViewHolder extends RecyclerView.ViewHolder {
            ImageView civFriendProfilePhoto;
            TextView tvChat;
            RelativeLayout rlChats;
            CardView cvChatBubble;

            public ChatsItemViewHolder(View view) {
                super(view);
                rlChats = view.findViewById(R.id.rlChats);
                civFriendProfilePhoto = view.findViewById(R.id.civFriendProfilePhotoChat);
                tvChat = view.findViewById(R.id.tvChat);
                cvChatBubble = view.findViewById(R.id.cvChatBubble);
            }
        }
    }


    List<Chat> getChatsRecoding(int roomId) {
        List<Chat> chats = null;
        if (Common.isNetworkConnect(getApplicationContext())) {
            String url = Common.URL + "/ChatServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_CHATS_RECODING);
            jsonObject.addProperty("roomId", roomId);
            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                Type type = new TypeToken<List<Chat>>() {
                }.getType();
                chats = new Gson().fromJson(jsonIn, type);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return chats;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chats.clear();
        friendPhoto = null;
        Common.room = -1;
        Common.setPreferencesRoom(this,-1);

    }
}
