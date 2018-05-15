package com.example.leeicheng.dogbook.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.activities.task.CommonTask;
import com.example.leeicheng.dogbook.activities.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class ActivityFragment extends Fragment {
    private static final String TAG = "ActivityFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvActivities;
    private CommonTask activityGetAllTask, activityDeleteTask;
    private ImageTask activityImageTask;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_activity_list, container, false);
        swipeRefreshLayout =
                view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAllActivities();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rvActivities = view.findViewById(R.id.rvNews);
        rvActivities.setLayoutManager(new LinearLayoutManager(getActivity()));
        FloatingActionButton btAdd = view.findViewById(R.id.btAdd);

        //監聽新增活動按鈕
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ActivityInsertFragment();
                switchFragment(fragment);
            }
        });
        return view;
    }

    private void showAllActivities() {
        if (Common.networkConnected(getActivity())) {
            String url = Common.URL + "/ActivitiesServlet";
            List<Activity> activities = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            activityGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = activityGetAllTask.execute().get();
                Type listType = new TypeToken<List<Activity>>() {
                }.getType();
                activities = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (activities == null || activities.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoSpotsFound);
            } else {
                rvActivities.setAdapter(new ActivitiesRecyclerViewAdapter(getActivity(), activities));
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllActivities();
    }

    private class ActivitiesRecyclerViewAdapter extends RecyclerView.Adapter<ActivitiesRecyclerViewAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Activity> activities;
        private int imageSize;

        ActivitiesRecyclerViewAdapter(Context context, List<Activity> activities) {
            layoutInflater = LayoutInflater.from(context);
            this.activities = activities;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvName, tvActDate, tvAddress;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivActivity);
                tvName = itemView.findViewById(R.id.tvName);
                tvActDate = itemView.findViewById(R.id.tvActDate);
                tvAddress = itemView.findViewById(R.id.tvAddress);
            }
        }

        @Override
        public int getItemCount() {
            return activities.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_activity, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Activity activity = activities.get(position);
            String url = Common.URL + "/ActivitiesServlet";
            int id = activity.getId();
            activityImageTask = new ImageTask(url, id, imageSize, myViewHolder.imageView);
            activityImageTask.execute();
            myViewHolder.tvName.setText(activity.getName());
            myViewHolder.tvActDate.setText(activity.getActivity_date());
            myViewHolder.tvAddress.setText(activity.getLocation_address());

            //監聽按下itemView 進入詳細活動內容頁面, ResultFragment.
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new ResultFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("activity", activity);
                    fragment.setArguments(bundle);
                    switchFragment(fragment);

                }
            });



            myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(ActivityFragment.this.getActivity(), view, Gravity.END);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    Fragment fragment = new ActivityUpdateFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("activity", activity);
                                    fragment.setArguments(bundle);
                                    switchFragment(fragment);
                                    break;
                                case R.id.delete:
                                    if (Common.networkConnected(ActivityFragment.this.getActivity())) {
                                        String url = Common.URL + "/ActivitiesServlet";
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "activityDelete");
                                        jsonObject.addProperty("activity", new Gson().toJson(activity));
                                        int count = 0;
                                        try {
                                            activityDeleteTask = new CommonTask(url, jsonObject.toString());
                                            String result = activityDeleteTask.execute().get();
                                            count = Integer.valueOf(result);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(ActivityFragment.this.getActivity(), R.string.msg_DeleteFail);
                                        } else {
                                            activities.remove(activity);
                                            ActivitiesRecyclerViewAdapter.this.notifyDataSetChanged();
                                            Common.showToast(ActivityFragment.this.getActivity(), R.string.msg_DeleteSuccess);
                                        }
                                    } else {
                                        Common.showToast(ActivityFragment.this.getActivity(), R.string.msg_NoNetwork);
                                    }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }

    }

    private void switchFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().
                    replace(R.id.flMain, fragment).addToBackStack(null).commit();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (activityGetAllTask != null) {
            activityGetAllTask.cancel(true);
        }

        if (activityImageTask != null) {
            activityImageTask.cancel(true);
        }

        if (activityDeleteTask != null) {
            activityDeleteTask.cancel(true);
        }
    }
}
