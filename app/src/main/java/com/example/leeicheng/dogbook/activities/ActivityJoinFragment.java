package com.example.leeicheng.dogbook.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.task.ImageTask;

public class ActivityJoinFragment extends Fragment {

    private static final String TAG = "ActivityJoin即將參與的活動";


    private Activity activity;
    private ImageTask activityImageTask;
    private int imageSize;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.fragment_activity_join,container,false);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvActDate = view.findViewById(R.id.tvActDate);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        ImageView imageView = view.findViewById(R.id.ivActivity);
        String url = Common.URL + "/ActivitiesServlet";
        Bundle bundle = getArguments();

        activity = (Activity) bundle.getSerializable("activity");
        String name = activity.getName();
        String date = activity.getActivity_date();
        String address = activity.getLocation_address();

        tvName.setText("活動名稱：" + name);
        tvActDate.setText("日期：" + date);
        tvAddress.setText("地址：" + address);

        int id = activity.getId();
        Bitmap bitmap = null;
        try {
            activityImageTask = new ImageTask(url, id, imageSize);
            bitmap = activityImageTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (activityImageTask != null) {
            activityImageTask.cancel(true);
        }
    }

}









