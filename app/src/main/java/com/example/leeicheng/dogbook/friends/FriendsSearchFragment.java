package com.example.leeicheng.dogbook.friends;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class FriendsSearchFragment extends Fragment {

    private static final String TAG = "FrinedsSearchFragment";

    private RecyclerView rvSearch;
    private GeneralTask searchGetAllTask;

    private Button btAddFriend;
    private EditText etSearch;
    List<Dog> allUsersList;
    List<Dog> friendList;


    FriendSearchAdapter friendSearchAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_search_fragment, container, false);

        allUsersList = getAllUsers();

        friendList = getAllFriends();

        deleteFriendFromAll();

        findViews(view);


        return view;
    }

    private void findViews(View view) {
        rvSearch = view.findViewById(R.id.rvSearch);
        etSearch = view.findViewById(R.id.etSearch);

        btAddFriend = view.findViewById(R.id.btAddFriend);
        friendSearchAdapter = new FriendSearchAdapter(allUsersList);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString());
            }
        });

        viewControl();
    }

    private void viewControl() {
        rvSearch.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvSearch.setAdapter(friendSearchAdapter);

    }



    private void filter(String text){
        List<Dog> filteredList = new ArrayList<>();

        for (Dog item: allUsersList){
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        friendSearchAdapter.setAll(filteredList);
        friendSearchAdapter.notifyDataSetChanged();
//        friendSearchAdapter.filterList(filteredList);
    }




    private List<Dog> getAllUsers() {
        List<Dog> allUsersList = null;
        Gson gson = new Gson();
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/DogServlet";
            int dogId = Common.getPreferencesDogId(getActivity());
            Dog dog = new Dog(dogId);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_All_Dog);
            jsonObject.addProperty("dog", gson.toJson(dog));
            String jsonOut = jsonObject.toString();
            searchGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = searchGetAllTask.execute().get();
                Type listType = new TypeToken<List<Dog>>() {
                }.getType();
                gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                allUsersList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return allUsersList;
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
            searchGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = searchGetAllTask.execute().get();
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
        Dog k;
        Dog n;
        for (int i = 0; i < friendList.size(); i++ ) {
           for (int j = 0; j < allUsersList.size(); j++ ){
               k = friendList.get(i);
               n = allUsersList.get(j);

               if (k.getDogId() == n.getDogId()){
                   allUsersList.remove(j);
               }

           }
//            Log.d("這djdnunudnd","後來"+allUsersList.size());
        }

    }





    @Override
    public void onStart() {
        super.onStart();
    }

    private class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.MyViewHolder> {

        private List<Dog> all = new ArrayList<>();

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvAge;
            TextView tvGender;
            TextView tvVariety;
            ImageView ivPhoto;
            Button btAddFriend;


            MyViewHolder(View itemView) {
                super(itemView);
                btAddFriend = itemView.findViewById(R.id.btAddFriend);
                tvName = itemView.findViewById(R.id.tvFriendName);
                ivPhoto = itemView.findViewById(R.id.ivPhoto);
                tvGender = itemView.findViewById(R.id.tvGender);
                tvAge = itemView.findViewById(R.id.tvAge);
                tvVariety = itemView.findViewById(R.id.tvVariety);

            }
        }

        public FriendSearchAdapter(List<Dog> all) {
            this.all = all;
        }

        public void setAll(List<Dog> all){
            this.all = all;
        }

        @Override
        public int getItemCount() {
            Iterator<Dog> iter = all.iterator();

            while (iter.hasNext()){
                Dog dog = iter.next();
                if (dog.getDogId() == Common.getPreferencesDogId(getActivity())){
                    iter.remove();
                }
            }

            return all.size();
        }



        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View itemView = layoutInflater.inflate(R.layout.friends_search_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final Dog Stranger = all.get(position);


            myViewHolder.tvName.setText(Stranger.getName());
            myViewHolder.tvGender.setText(Stranger.getGender());
            myViewHolder.tvVariety.setText(Stranger.getVariety());
            myViewHolder.tvAge.setText(String.valueOf(Stranger.getAge()) + "歲");

            myViewHolder.btAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                addFriend(Stranger.getDogId());
                myViewHolder.btAddFriend.setText("已邀請");
                view.setEnabled(false);

                }
            });


            Log.d("", "" + Stranger.getDogId());
            CommonRemote.getProfilePhoto(Stranger.getDogId(),myViewHolder.ivPhoto, getActivity());



            myViewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int dogId = Stranger.getDogId();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.mydog_dialog, null);
                    final ImageView background = dialogView.findViewById(R.id.ivBackgroundDialog);
                    ImageView ProfilePhoto = dialogView.findViewById(R.id.ivProfilePhotoDialog);
                    TextView tvName = dialogView.findViewById(R.id.tvNameDialog);
                    TextView tvInfo = dialogView.findViewById(R.id.tvInfoDialog);
                    Button changeProfilePhoto = dialogView.findViewById(R.id.changeProfilePhoto);
                    Button signOut = dialogView.findViewById(R.id.signOut);
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

                    getBackgroundPhoto(background);
                    CommonRemote.getProfilePhoto(dogId, ProfilePhoto, getActivity());
                    tvName.setText(Stranger.getName());
                    String info = "品種：" + Stranger.getVariety() + "\n"
                            + "性別：" + Stranger.getGender() + "\n"
                            + "年紀：" + Stranger.getAge() + "\n"
                            + "生日：" + dateFormat.format(Stranger.getBirthday());

                    tvInfo.setText(info);

                    dialogBuilder.setView(dialogView);
                    final AlertDialog myDogDialog = dialogBuilder.create();
                    myDogDialog.show();

                    changeProfilePhoto.setVisibility(View.GONE);

                    signOut.setVisibility(View.GONE);
                }


                void getBackgroundPhoto(ImageView imageView) {
                    MediaTask mediaTask;
                    int photoSize = getActivity().getResources().getDisplayMetrics().widthPixels;
                    int dogId = Stranger.getDogId();
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
            });



        }




    }

    void addFriend(int friendId) {
        int myDogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/FriendServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", Common.ADD_FRIEND_TO_CHECKLIST);
            jsonObject.addProperty("dogId", myDogId);
            jsonObject.addProperty("inviteDogId", friendId);

            String jsonOut = jsonObject.toString();
            searchGetAllTask = new GeneralTask(url, jsonOut);
            try {
                searchGetAllTask.execute().get();

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
        if (searchGetAllTask != null) {
            searchGetAllTask.cancel(true);
        }


    }


}