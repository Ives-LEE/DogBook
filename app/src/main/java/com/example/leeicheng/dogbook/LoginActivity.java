package com.example.leeicheng.dogbook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnSignIn, btnSignUp;
    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        findViews();
    }

    void findViews() {
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnSignIn = findViewById(R.id.btnSignInLogin);
        btnSignUp = findViewById(R.id.btnSignUpLogin);
        ivLogo = findViewById(R.id.ivLogoLogin);

        viewsControl();
    }

    void viewsControl() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                hideKeyboard();

                if (isLogin(email, password)) {
                    finish();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignUpLayout();
                hideKeyboard();
                etEmail.setText("");
                etPassword.setText("");
            }
        });
    }

    //切換至注冊頁面
    void showSignUpLayout() {

    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    boolean isLogin(String email, String password) {
        Common common = new Common(this);
        boolean isLogin = false;
        if (common.isNetworkConnect()) {
            String URL = common.URL + "/";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("password", password);

            GeneralTask generalTask = new GeneralTask(URL, jsonObject.toString());

            try {
                String JsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(JsonIn, JsonObject.class);
                isLogin = jsonObject.get("isLogin").getAsBoolean();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return isLogin;
        }
        return isLogin;
    }
}
