package com.example.leeicheng.dogbook.mydog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.Event;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyEventsActivity extends AppCompatActivity {
    Toolbar myEventToolbar;
    ImageButton ibBackMyEventToolbar, ibAddMyEventToolbar;
    RecyclerView rvMyEvents;
    CalendarView cvMyCalendar;
    List<Event> events;
    GeneralTask generalTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_activity);
        events = new ArrayList<>();
        setToolbar();
        findViews();
    }

    void setToolbar() {
        myEventToolbar = findViewById(R.id.myEventToolbar);
        setSupportActionBar(myEventToolbar);
        ibAddMyEventToolbar = findViewById(R.id.ibAddMyEventToolbar);
        ibBackMyEventToolbar = findViewById(R.id.ibBackMyEventToolbar);
        toolbarViewsControl();
    }

    void toolbarViewsControl() {
        ibBackMyEventToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibAddMyEventToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddEventActivity.class);
                startActivity(intent);
            }
        });
    }

    void findViews() {
        rvMyEvents = findViewById(R.id.rvMyEvents);
        cvMyCalendar = findViewById(R.id.cvMyCalendar);
        viewsControl();
    }

    void viewsControl() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        String date = ""+year+0+month+day;
        int selectDate = Integer.valueOf(date);
        events = getEvents(selectDate);

        cvMyCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                month = month + 1;
                String date = ""+year+0+month+day;
                int selectDate = Integer.valueOf(date);
                events = getEvents(selectDate);
                rvMyEvents.getAdapter().notifyDataSetChanged();
            }
        });
        rvMyEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMyEvents.setAdapter(new MyEventsAdapter());
    }


    private class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.EventsViewHolder> {

        @Override
        public EventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MyEventsActivity.this);
            View view = layoutInflater.inflate(R.layout.events_item, parent, false);
            return new EventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventsViewHolder holder, int position) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Event event = events.get(position);
            holder.tvEventTitle.setText(event.getTitle());
            holder.tvEventOverview.setText(event.getOverview());
            holder.tvEventDate.setText(sdf.format(event.getDate()));

            if (event.getType() != 1){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        public class EventsViewHolder extends RecyclerView.ViewHolder {
            TextView tvEventTitle, tvEventOverview, tvEventDate;

            public EventsViewHolder(View view) {
                super(view);
                tvEventTitle = view.findViewById(R.id.tvEventTitle);
                tvEventOverview = view.findViewById(R.id.tvEventOverview);
                tvEventDate = view.findViewById(R.id.tvEventDate);
            }
        }
    }

    List<Event> getEvents(int date) {
        int dogId = Common.getPreferencesDogId(this);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        List<Event> events = new ArrayList<>();

        if (Common.isNetworkConnect(this)) {
            String url = Common.URL + "/CalendarServlet";
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("status", Common.GET_EVENTS);
            jsonObject.addProperty("dogId", dogId);
            jsonObject.addProperty("date", date);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                Type type = new TypeToken<List<Event>>() {
                }.getType();
                events = gson.fromJson(jsonIn, type);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewsControl();
    }
}

