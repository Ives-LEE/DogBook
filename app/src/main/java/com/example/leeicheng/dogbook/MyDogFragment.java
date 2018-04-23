package com.example.leeicheng.dogbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class MyDogFragment extends Fragment {
    Button button;
    Common common;
    String TAG = "我的狗";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mydog_fragment, container, false);
        button = view.findViewById(R.id.button);
        common = new Common(getActivity());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getActivity().getSharedPreferences(common.PREF_FILE,
                        MODE_PRIVATE);
                if (preferences.getBoolean("isLogin", true)) {
                    Log.d(TAG, "我登入過了");
                } else {
                    showLoginLayout();
                }
            }
        });
        return view;
    }

    void showLoginLayout() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}
