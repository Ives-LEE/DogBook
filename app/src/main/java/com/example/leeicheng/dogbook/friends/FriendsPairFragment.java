package com.example.leeicheng.dogbook.friends;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.List;
import java.util.Random;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class FriendsPairFragment extends Fragment {
    private static final String TAG = "FriendsPairFragment";
    private GeneralTask memberGetAllTask;
//    private Friend member;
    private ImageView ivPair;
    private Button btLike, btDislike;
    private TextView tvName,tvAge,tvGender,tvVariety;

    Random rand = new Random();
    Dog newRandom;
    List<Dog> members;

    List<Dog> friends;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_pair_fragment, container, false);
        members = getAllMembers();
        friends = getAllFriends();

        deleteFriendFromAll();



        findViews(view);
        return view;
    }

    private void findViews(View view) {

        ivPair = view.findViewById(R.id.ivPair);
        btLike = view.findViewById(R.id.btLike);
        btDislike = view.findViewById(R.id.btDislike);
        tvName = view.findViewById(R.id.tvName);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvVariety = view.findViewById(R.id.tvVariety);




        viewControl();
    }


    void viewControl() {

        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                addFriend(newRandom.getDogId());
                btLike.setEnabled(false);
                btLike.setText("已送出");



            }
        });
        btDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newRandom = members.get(rand.nextInt(members.size()));

                Log.d("隨機","狗"+newRandom.getVariety());

//                String a = newRandom.getVariety();
                tvName.setText(newRandom.getName());
                tvAge.setText(String.valueOf(newRandom.getAge()) + "歲/ ");
                tvGender.setText(newRandom.getGender() + "/ ");
                tvVariety.setText(newRandom.getVariety());

                getProfilePhoto(newRandom.getDogId() ,ivPair);

                btLike.setText("送出邀請");
                btLike.setEnabled(true);


            }
        });


    }

    private List<Dog> getAllMembers() {
        List<Dog> memberList = null;
        Gson gson = new Gson();
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/DogServlet";
            int dogId = Common.getPreferencesDogId(getActivity());
            Dog dog = new Dog(dogId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_All_Dog);
            jsonObject.addProperty("dog", gson.toJson(dog));
            String jsonOut = jsonObject.toString();
            memberGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = memberGetAllTask.execute().get();
                Type listType = new TypeToken<List<Dog>>() {
                }.getType();
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                memberList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }
        return memberList;
    }


    private List<Dog> getAllFriends() {
        List<Dog> friendList = null;
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/FriendServlet";
            int dogId = Common.getPreferencesDogId(getActivity());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("dogId", dogId);
            String jsonOut = jsonObject.toString();
            memberGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = memberGetAllTask.execute().get();
                Type listType = new TypeToken<List<Dog>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                friendList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return friendList;
    }


    void deleteFriendFromAll() {

//        Iterator<Dog> iterAll = allUsersList.iterator();
//
//        Iterator<Dog> iterFriend = friendList.iterator();
        Dog k;
        Dog n;
        for (int i = 0; i < friends.size(); i++ ) {
            for (int j = 0; j < members.size(); j++ ){
                k = friends.get(i);
                n = members.get(j);

                if (k.getDogId() == n.getDogId()){
                    members.remove(j);
                }
            }
            Log.d("這djdnunudnd","後來"+members.size());
        }
    }







        void getProfilePhoto(int dogId ,ImageView imageView) {
            MediaTask mediaTask;
            int photoSize = getActivity().getResources().getDisplayMetrics().widthPixels;
            if (Common.isNetworkConnect(getActivity())) {
                String url = Common.URL + "/MediaServlet";
                mediaTask = new MediaTask(url, dogId, photoSize, imageView, Common.GET_PROFILE_PHOTO, "dog");
                try {
                    mediaTask.execute();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }


        void getBackgroundPhoto(ImageView imageView) {
            MediaTask mediaTask;
            int photoSize = getActivity().getResources().getDisplayMetrics().widthPixels;
            int dogId = Common.getPreferencesDogId(getActivity());
            if (Common.isNetworkConnect(getActivity())) {
                String url = Common.URL + "/MediaServlet";
                mediaTask = new MediaTask(url, dogId, photoSize, imageView, Common.GET_PROFILE_BACKGROUND_PHOTO, "dog");
                try {
                    mediaTask.execute();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }




        //Q1
        void addFriend(int friendId) {
            int myDogId = Common.getPreferencesDogId(getActivity());
            if (Common.isNetworkConnect(getActivity())) {
                String url = Common.URL + "/FriendServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", Common.ADD_FRIEND_TO_CHECKLIST);
                jsonObject.addProperty("dogId", myDogId);
                jsonObject.addProperty("inviteDogId", friendId);

                String jsonOut = jsonObject.toString();
                memberGetAllTask = new GeneralTask(url, jsonOut);
                try {
                    memberGetAllTask.execute().get();

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

            } else {
                Common.showToast(getActivity(), R.string.msg_NoNetwork);
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
        if (memberGetAllTask != null) {
            memberGetAllTask.cancel(true);
        }


    }


}