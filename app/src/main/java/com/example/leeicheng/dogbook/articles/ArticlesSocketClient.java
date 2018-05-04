package com.example.leeicheng.dogbook.articles;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;



public class ArticlesSocketClient extends WebSocketClient {
    String TAG = "";
    private final Gson gson;

    public ArticlesSocketClient(URI serverURI , Context context) {
        // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
        super(serverURI , new Draft_17());
        gson = new Gson();
    }

    @Override
    public void onOpen(ServerHandshake data) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError: exception = " + ex.toString());
    }
}
