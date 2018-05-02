package com.example.leeicheng.dogbook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leeicheng.dogbook.R;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class ActivitiesFragment extends Fragment {
    TabLayout tlActivities;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activities_fragment,container,false);
        findViews(view);
        return view;
    }

    void findViews(View view){
        tlActivities = view.findViewById(R.id.tlActivities);
        viewsControl();
    }
    void viewsControl(){
        tlActivities.addTab(tlActivities.newTab().setText("所有活動"));
        tlActivities.addTab(tlActivities.newTab().setText("我的活動"));
        tlActivities.addTab(tlActivities.newTab().setText("即將參與"));

        tlActivities.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    }
}
