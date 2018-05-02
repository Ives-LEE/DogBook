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
import com.example.leeicheng.dogbook.mydog.MyEventsActivity;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnvMain;
    Toolbar tbMain;
    TextView tvTitle;
    ImageView ivLeft, ivRight;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        findToolBarViews();
        findViews();
    }

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
                        selectedFragment = new ActivitiesFragment();
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
            ivRight.setImageResource(0);
            ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }

}
