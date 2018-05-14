package com.example.leeicheng.dogbook.mydog;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.Event;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {
    RelativeLayout rlAddEvent;
    EditText eventName, eventDate, eventLocation, eventOverview;
    ImageButton AddToolbar, BackToolbar;
    GeneralTask generalTask;
    String TAG = "新增事件";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_activity);
        findViews();
    }

    void findViews() {
        rlAddEvent = findViewById(R.id.rlAddEvent);
        AddToolbar = findViewById(R.id.ibAddEventToolbar);
        BackToolbar = findViewById(R.id.ibBackAddEventToolbar);
        eventName = findViewById(R.id.etEventName);
        eventDate = findViewById(R.id.etEventDate);
        eventLocation = findViewById(R.id.etEventLocation);
        eventOverview = findViewById(R.id.etEventOverview);
        viewsControl();
    }

    void viewsControl() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        rlAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                showDatePicker();
            }
        });

        AddToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = eventName.getText().toString().trim();
                Date date = null;
                try {
                    date = format.parse(eventDate.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String location = eventLocation.getText().toString().trim();
                String overview = eventOverview.getText().toString().trim();

                Event event = new Event(name,overview,location,date);
                sendEvnet(event);

                finish();
            }
        });

        BackToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //顯示月曆對話
    void showDatePicker() {
        int year, month, day;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                eventDate.setText(year + "-" + month + "-" + day);
            }
        }, year, month, day).show();
    }

    void sendEvnet(Event event){
        int dogId = Common.getPreferencesDogId(this);
        if (Common.isNetworkConnect(this)) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String url = Common.URL + "/CalendarServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.ADD_EVENT);
            jsonObject.addProperty("dogId", dogId);
            jsonObject.addProperty("event", gson.toJson(event));

            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                generalTask.execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

}
