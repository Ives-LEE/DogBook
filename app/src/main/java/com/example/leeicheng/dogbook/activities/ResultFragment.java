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

public class ResultFragment extends Fragment {
    private static final String TAG = "ResultFragment 活動詳細內容頁面";

    private Activity activity;
    private ImageTask activityImageTask;
    private int imageSize;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){


        View view = inflater.inflate(R.layout.fragment_result,container,false);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvActDate = view.findViewById(R.id.tvActDate);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvContent = view.findViewById(R.id.tvContent);

        ImageView imageView = view.findViewById(R.id.ivActivity);


        String url = Common.URL + "/ActivitiesServlet";
        Bundle bundle = getArguments();
        activity = (Activity) bundle.getSerializable("activity");
        String name = activity.getName();
        String date = activity.getActivity_date();
        String address = activity.getLocation_address();
        String content = activity.getContent();

        tvName.setText("活動名稱：" + name);
        tvActDate.setText("時間：" + date);
        tvAddress.setText("地址：" + address);
        tvContent.setText("活動內容：" + content);

        //監聽 “我要參加” 按鈕
//        Button btnJoin = view.findViewById(R.id.btnJoin);
//        btnJoin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Fragment fragment = new ActivityJoinFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("activity", activity);
//                fragment.setArguments(bundle);
//                switchFragment(fragment);


         //   }






//            private void switchFragment(Fragment fragment) {
//                if (getFragmentManager() != null) {
//                    getFragmentManager().beginTransaction().
//                            replace(R.id.flMain, fragment).addToBackStack(null).commit();
//                }
//            }
//
//        });

        //google map 按鈕監聽
        ImageView ivMarker = view.findViewById(R.id.ivMarker);
        ivMarker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Fragment fragment = new ActivityDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("activity", activity);
                fragment.setArguments(bundle);
                switchFragment(fragment);

            }
                private void switchFragment(Fragment fragment) {
                    if (getFragmentManager() != null) {
                        getFragmentManager().beginTransaction().
                                replace(R.id.flMain, fragment).addToBackStack(null).commit();
                    }
                }

            });




        int id = activity.getId();
        Bitmap bitmap = null;
        try {
            activityImageTask = new ImageTask(url, id, imageSize);
            // passing null and calling get() means not to run FindImageByIdTask.onPostExecute()
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

