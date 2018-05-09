package com.example.leeicheng.dogbook.main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ChatService extends Service {
    private final IBinder binder = new ServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"onbind",Toast.LENGTH_SHORT).show();
        return binder;
    }

    public class ServiceBinder extends Binder {
        ChatService getService(){
            return ChatService.this;
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "onUnbind", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
