package com.example.leeicheng.dogbook.mydog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.ActivitiesFragment;
import com.example.leeicheng.dogbook.articles.ArticlesFragment;
import com.example.leeicheng.dogbook.chats.ChatsFragment;
import com.example.leeicheng.dogbook.friends.FriendsFragment;
import com.example.leeicheng.dogbook.main.Event;

import java.lang.reflect.Field;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {
    Toolbar myEventToolbar;
    ImageButton ibBackMyEventToolbar,ibAddMyEventToolbar;
    RecyclerView rvMyEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);
        setToolbar();
        findViews();
    }
    void setToolbar(){
        myEventToolbar = findViewById(R.id.myEventToolbar);
        setSupportActionBar(myEventToolbar);
        ibAddMyEventToolbar = findViewById(R.id.ibAddMyEventToolbar);
        ibBackMyEventToolbar = findViewById(R.id.ibBackMyEventToolbar);
        toolbarViewsControl();
    }

    void toolbarViewsControl(){
        ibBackMyEventToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibAddMyEventToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    void findViews() {
        rvMyEvents = findViewById(R.id.rvMyEvents);
        viewsControl();
    }
    void viewsControl(){
        rvMyEvents.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvMyEvents.setAdapter(new MyEventsAdapter());
    }


    private class MyEventsAdapter extends RecyclerView.Adapter {
        List<Event> events;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MyEventsActivity.this);
            View view = layoutInflater.inflate(R.layout.events_item,parent,false);
            return new EventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        private class EventsViewHolder extends RecyclerView.ViewHolder {

            public EventsViewHolder(View view) {
                super(view);

            }
        }
    }
}

