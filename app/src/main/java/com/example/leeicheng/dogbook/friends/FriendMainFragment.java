package com.example.leeicheng.dogbook.friends;

/**
 * Created by apple on 2018/8/5.
 */

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.articles.ArticlesFragment;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.example.leeicheng.dogbook.mydog.MyDogFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FriendMainFragment extends Fragment{

    private static final String TAG = "FriendMainFragment";

    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    Fragment selectedFragment = new FriendsFragment();



    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_main_fragment, container, false);


        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();

        findViews(view);
        return view;
    }

    private void findViews(View view) {



        tabLayout = view.findViewById(R.id.tabLayout);
        frameLayout =  view.findViewById(R.id.frameLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {


            @Override

            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0){
                    selectedFragment = new FriendsFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();


                }else if (tab.getPosition() == 1){
                    selectedFragment = new FriendsSearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();

                }else if (tab.getPosition() == 2){
                    selectedFragment = new FriendsPairFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
                }else if (tab.getPosition() == 3) {
                    selectedFragment = new FriendsConfirmedFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();
                }


                }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        viewControl();
    }


    void viewControl() {


    }




}