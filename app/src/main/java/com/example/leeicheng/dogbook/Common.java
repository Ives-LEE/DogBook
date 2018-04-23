package com.example.leeicheng.dogbook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class Common {
    private Context context;
    final String URL = "http://10.0.2.2:8080/DogBookServlet";
    final String PREF_FILE = "preference";


    public Common(Context context) {
        this.context = context;
    }

    boolean isNetworkConnect() {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }
}
