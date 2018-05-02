package com.example.leeicheng.dogbook.main;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.mydog.MyDogFragment;
import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.ActivitiesFragment;
import com.example.leeicheng.dogbook.articles.ArticlesFragment;
import com.example.leeicheng.dogbook.chats.ChatsFragment;
import com.example.leeicheng.dogbook.friends.FriendsFragment;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnvMain;
    TabLayout tlMain;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        findViews();
    }
    //...

    void findViews() {
        getSupportFragmentManager().beginTransaction().replace(R.id.flMain, new MyDogFragment()).commit();
        tlMain = findViewById(R.id.tlMain);
        bnvMain = findViewById(R.id.bnvMain);
        disableShiftMode(bnvMain);

        tlMain.addTab(tlMain.newTab().setText("所有活動"));
        tlMain.addTab(tlMain.newTab().setText("我的活動"));
        tlMain.addTab(tlMain.newTab().setText("即將參與"));

        tlMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navMyDog:
                        tlMain.setVisibility(View.GONE);
                        selectedFragment = new MyDogFragment();
                        break;
                    case R.id.navActivities:
                        tlMain.setVisibility(View.VISIBLE);
                        selectedFragment = new ActivitiesFragment();
                        break;
                    case R.id.navFriends:
                        tlMain.setVisibility(View.GONE);
                        selectedFragment = new FriendsFragment();
                        break;
                    case R.id.navChats:
                        tlMain.setVisibility(View.GONE);
                        selectedFragment = new ChatsFragment();
                        break;
                    case R.id.navArticles:
                        tlMain.setVisibility(View.GONE);
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
}
