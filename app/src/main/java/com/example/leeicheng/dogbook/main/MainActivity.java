package com.example.leeicheng.dogbook.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.mydog.MyDogFragment;
import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.ActivitiesFragment;
import com.example.leeicheng.dogbook.articles.ArticlesFragment;
import com.example.leeicheng.dogbook.chats.ChatsFragment;
import com.example.leeicheng.dogbook.friends.FriendsFragment;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnvMain;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        findViews();
    }

    void findViews() {
        getSupportFragmentManager().beginTransaction().replace(R.id.flMain, new MyDogFragment()).commit();
        bnvMain = findViewById(R.id.bnvMain);
        bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navMyDog:
                        selectedFragment = new MyDogFragment();
                        break;
                    case R.id.navActivities:
                        selectedFragment = new ActivitiesFragment();
                        break;
                    case R.id.navFriends:
                        selectedFragment = new FriendsFragment();
                        break;
                    case R.id.navChats:
                        selectedFragment = new ChatsFragment();
                        break;
                    case R.id.navArticles:
                        selectedFragment = new ArticlesFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.flMain, selectedFragment).commit();
                return true;
            }
        });
    }
}
