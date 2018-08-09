package com.example.leeicheng.dogbook.owner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnSignIn, btnSignUp;
    ImageView ivLogo;
    Common common;

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
                boolean isValid = true;
                hideKeyboard();
                if (email.equals("")) {
                    etEmail.setError("E-mail is empty");
                    isValid = false;
                }
                if (password.equals("")){
                    etPassword.setError("Password is empty");
                    isValid = false;
                }


                if (isValid && isLogin(email, password)) {
                    SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                            MODE_PRIVATE);
                    pref.edit()
                            .putString("email", email)
                            .putString("password", password)
                            .apply();
                    int dogId = Common.getPreferencesDogId(getApplicationContext());
                    Log.d("設定",pref.getAll().toString());
                    if (Common.getPreferencesDogId(getApplicationContext()) != -1){
                        Common.connectServer(getApplicationContext(), dogId);
                    }
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
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.flLogin, signUpFragment);
        fragmentTransaction.commit();
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    boolean isLogin(String email, String password) {
        boolean isLogin = false;
        int ownerId = -1;
        int dogId = -1;
        if (common.isNetworkConnect(this)) {
            String URL = common.URL + "/OwnerServlet";

            Owner owner = new Owner(email,password);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status","signIn");
            String jsonStr = new Gson().toJson(owner);
            jsonObject.addProperty("owner", jsonStr);

            GeneralTask generalTask = new GeneralTask(URL, jsonObject.toString());

            try {
                String JsonIn = generalTask.execute().get();
                Log.d("output","output = "+JsonIn);
                jsonObject = new Gson().fromJson(JsonIn, JsonObject.class);
                isLogin = jsonObject.get("isLogin").getAsBoolean();
                ownerId = jsonObject.get("ownerId").getAsInt();
                dogId = jsonObject.get("dogId").getAsInt();
                Common.setPreferencesIsLogin(this,isLogin);
                Common.setPreferencesOwnerId(this,ownerId);
                Common.setPreferencesDogId(this,dogId);

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
