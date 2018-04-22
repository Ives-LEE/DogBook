package com.example.leeicheng.dogbook;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bnvMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    void findViews(){
        bnvMain = findViewById(R.id.bnvMain);
        bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.navMyDog:
                        selectedFragment =  new MyDogFragment();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.flMain,selectedFragment).commit();
                return true;
            }
        });
    }
}
