package com.example.leeicheng.dogbook.owner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;


public class SignUpFragment extends Fragment {
    String TAG = "注測";
    RelativeLayout rlSignUp;
    EditText etEmail, etPassword;
    Button btnSignUp, btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        findViews(view);

        return view;
    }

    //找元件集合
    void findViews(View view) {
        rlSignUp = view.findViewById(R.id.rlSignUp);
        etEmail = view.findViewById(R.id.etEmailSignUp);
        etPassword = view.findViewById(R.id.etPasswordSignUp);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnCancel = view.findViewById(R.id.btnCancelSignUp);

        viewsControl();
    }

    //元件控制事件集合
    void viewsControl() {

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (isSignUp(email,password)){
                    Log.d(TAG,"成功");
                    Common.setPreferencesDogId(getActivity(),-1);
                    Common.setPreferencesIsLogin(getActivity(),true);
                    Log.d(TAG,Common.getPreferenceAll(getActivity()));
                    getActivity().finish();
                } else {
                    Log.d(TAG,"失敗");
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        rlSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    boolean isSignUp(String email,String password){
        boolean isSuccess = false;
        if (Common.isNetworkConnect(getActivity())) {
            String URL = Common.URL + "/OwnerServlet";
            Owner owner = new Owner(email,password);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status","signUp");
            jsonObject.addProperty("owner", new Gson().toJson(owner));

            GeneralTask generalTask = new GeneralTask(URL, jsonObject.toString());

            try {
                String JsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(JsonIn, JsonObject.class);
                Common.setPreferencesOwnerId(getActivity(),jsonObject.get("ownerId").getAsInt());
                isSuccess = jsonObject.get("isSignUp").getAsBoolean();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
        return isSuccess;
    }
}
