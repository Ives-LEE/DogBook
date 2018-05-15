package com.example.leeicheng.dogbook.main;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.ActivityFragment;
import com.example.leeicheng.dogbook.articles.AddArticleActivity;
import com.example.leeicheng.dogbook.articles.ArticlesFragment;
import com.example.leeicheng.dogbook.chats.Chat;
import com.example.leeicheng.dogbook.chats.ChatroomActivity;
import com.example.leeicheng.dogbook.chats.ChatsFragment;
import com.example.leeicheng.dogbook.friends.FriendsFragment;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.example.leeicheng.dogbook.mydog.MyDogFragment;
import com.example.leeicheng.dogbook.mydog.MyEventsActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnvMain;
    Toolbar tbMain;
    TextView tvTitle;
    ImageView ivLeft, ivRight;
    ChatService chatService;
    LocalBroadcastManager broadcastManager;
    NotificationManager notificationManager;
    NotificationChannel chatChannel;
    final static String CHAT_CHINNEL = "chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatNotificationManager();
        findToolBarViews();
        findViews();
        startService();
        int dogId = Common.getPreferencesDogId(getApplicationContext());
        if ( dogId != -1){
            Common.connectServer(getApplicationContext(), dogId);
        }
    }

    void chatNotificationManager() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chatChannel = new NotificationChannel(CHAT_CHINNEL, "message", NotificationManager.IMPORTANCE_HIGH);
            chatChannel.setDescription("message");
            chatChannel.enableLights(true);
            chatChannel.enableVibration(true);
            notificationManager.createNotificationChannel(chatChannel);
        }

    }

    void startService() {
        Intent intent = new Intent(this, ChatService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        Context context;
        int dogId;

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (Common.getPreferencesIsLogin(getApplicationContext())) {
                context = getApplicationContext();
                broadcastManager = LocalBroadcastManager.getInstance(context);
                chatService = ((ChatService.ServiceBinder) iBinder).getService();
                dogId = Common.getPreferencesDogId(context);

                registerChatReceiver();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            chatService = null;
        }

        void registerChatReceiver() {
            IntentFilter chatFilter = new IntentFilter("chat");
            ChatReceiver chatReceiver = new ChatReceiver();
            broadcastManager.registerReceiver(chatReceiver, chatFilter);
        }

        class ChatReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("房", "我在這" + Common.room);
                int inRoom = Common.getPreferencesRoom(getApplicationContext());
                //我沒在房間時 || 我在房間
                if (Common.room == -1 || Common.room == inRoom) {
                    String message = intent.getStringExtra("message");
                    Chat chat = new Gson().fromJson(message, Chat.class);

                    int room = chat.getChatroomId();
                    int receiver = getReceiver(room, chat.getSenderId());
                    //我在別的房間時
                    if (receiver == Common.getPreferencesDogId(context) && Common.room != chat.getChatroomId()) {
                        sendNotification(chat);
                    }
                }

            }
        }

        void sendNotification(Chat chat) {

            Bundle bundle = new Bundle();
            bundle.putInt("friendId", chat.getSenderId());
            bundle.putInt("roomId", chat.getChatroomId());
            Intent intent = new Intent(getApplicationContext(), ChatroomActivity.class);
            intent.putExtras(bundle);

            Dog dog = CommonRemote.getDogInfo(chat.getSenderId(), getApplicationContext());
            PendingIntent pendingIntent =
                        PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


            Notification notification ;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification = new Notification.Builder(getApplicationContext(), CHAT_CHINNEL)
                        .setContentTitle(dog.getName())
                        .setContentText(chat.getMessage())
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setAutoCancel(true)
                        .setVisibility(Notification.VISIBILITY_SECRET)
                        .setContentIntent(pendingIntent)
                        .build();
            } else {
                notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(dog.getName())
                        .setContentText(chat.getMessage())
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();
            }

            notificationManager.notify(Common.NOTIFICATION_ID, notification);
        }

        int getReceiver(int roomId, int senderId) {
            GeneralTask generalTask;
            int receiver = 0;
            List<Integer> dogsId = null;
            if (Common.isNetworkConnect(getApplicationContext())) {
                String url = Common.URL + "/ChatServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", Common.GET_ROOM_DOGS);
                jsonObject.addProperty("roomId", roomId);
                generalTask = new GeneralTask(url, jsonObject.toString());

                try {
                    String jsonIn = generalTask.execute().get();
                    Type type = new TypeToken<List<Integer>>() {
                    }.getType();
                    dogsId = new Gson().fromJson(jsonIn, type);
                    if (senderId == dogsId.get(0)) {
                        receiver = dogsId.get(1);
                    } else {
                        receiver = dogsId.get(0);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return receiver;
        }

    };

    void findToolBarViews() {
        tbMain = findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);
        tvTitle = findViewById(R.id.tvTitle);
        ivLeft = findViewById(R.id.ivLeftToolbar);
        ivRight = findViewById(R.id.ivRightToolbar);
    }

    void findViews() {
        getSupportFragmentManager().beginTransaction().replace(R.id.flMain, new MyDogFragment()).commit();
        setToolbar(R.id.navMyDog);
        bnvMain = findViewById(R.id.bnvMain);
        disableShiftMode(bnvMain);

        bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navMyDog:
                        setToolbar(R.id.navMyDog);
                        selectedFragment = new MyDogFragment();
                        break;
                    case R.id.navActivities:
                        setToolbar(R.id.navActivities);
                        selectedFragment = new ActivityFragment();
                        break;
                    case R.id.navFriends:
                        setToolbar(R.id.navFriends);
                        selectedFragment = new FriendsFragment();
                        break;
                    case R.id.navChats:
                        setToolbar(R.id.navChats);
                        selectedFragment = new ChatsFragment();
                        break;
                    case R.id.navArticles:
                        setToolbar(R.id.navArticles);
                        selectedFragment = new ArticlesFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.flMain, selectedFragment).commit();
                return true;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(android.support.design.widget.BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    void setToolbar(int itemId) {
        if (itemId == R.id.navMyDog) {
            tvTitle.setText(R.string.myDog);
            ivRight.setImageResource(R.drawable.ic_event_available_black_24dp);
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MyEventsActivity.class);
                    startActivity(intent);
                }
            });
        } else if (itemId == R.id.navActivities) {
            tvTitle.setText(R.string.activities);
            ivRight.setImageResource(0);
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        } else if (itemId == R.id.navFriends) {
            tvTitle.setText(R.string.friends);
            ivRight.setImageResource(0);
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else if (itemId == R.id.navChats) {
            tvTitle.setText(R.string.chats);
            ivRight.setImageResource(0);
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else if (itemId == R.id.navArticles) {
            tvTitle.setText(R.string.article);
            ivRight.setImageResource(R.drawable.ic_photo_filter_black_24dp);
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), AddArticleActivity.class);
                    startActivity(intent);
                }
            });

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(serviceConnection);
        //TODO
//        Common.disconnectServer();
    }
}
