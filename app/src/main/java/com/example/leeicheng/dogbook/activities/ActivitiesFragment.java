package com.example.leeicheng.dogbook.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leeicheng.dogbook.R;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class ActivitiesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activities_fragment,container,false);
        return view;
    }
}
