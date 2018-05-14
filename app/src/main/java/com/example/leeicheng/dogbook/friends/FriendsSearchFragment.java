package com.example.leeicheng.dogbook.friends;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class FriendsSearchFragment extends Fragment {

    private static final String TAG = "FrinedSearchFragment";

    private RecyclerView rvSearch;
    private GeneralTask searchGetAllTask;

    private ImageView btSearch;
    private Button btAddFriend;
    private EditText etSearch;
    List<Dog> searchResult;
    FriendSearchAdapter friendSearchAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_search_fragment, container, false);
        findViews(view);

        return view;
    }

    private void findViews(View view) {
        rvSearch = view.findViewById(R.id.rvSearch);
        etSearch = view.findViewById(R.id.etSearch);
        btSearch = view.findViewById(R.id.btSearch);

        viewControl();
    }

    private void viewControl() {
        searchResult = new ArrayList<>();
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchName = etSearch.getText().toString().trim();
                searchResult = searchDogs(searchName);
                friendSearchAdapter.notifyDataSetChanged();
            }
        });
//

        rvSearch.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        friendSearchAdapter = new FriendSearchAdapter();
        rvSearch.setAdapter(friendSearchAdapter);

    }

    List<Dog> searchDogs(String dogName) {
        List<Dog> search = null;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/FriendServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "searchDogs");
            jsonObject.addProperty("dogName", dogName);

            String jsonOut = jsonObject.toString();
            searchGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = searchGetAllTask.execute().get();
                Type listType = new TypeToken<List<Dog>>() {
                }.getType();
                search = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
        return search;
    }

    private void showSearchResult() {
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/FriendServlet";
            List<Friend> search = null;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            searchGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = searchGetAllTask.execute().get();
                Type listType = new TypeToken<List<Friend>>() {
                }.getType();
                search = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (search == null || search.isEmpty()) {
                Common.showToast(getActivity(), R.string.msg_NoSpotsFound);
            } else {
//                rvSearch.setAdapter(new FriendsSearchFragment(getActivity(), search));
            }
        } else {
            Common.showToast(getActivity(), R.string.msg_NoNetwork);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        showSearchResult();
    }

    private class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView ivPhoto;
            Button btAdd;

            MyViewHolder(View itemView) {
                super(itemView);
                btAdd = itemView.findViewById(R.id.btAddFriend);
                tvName = itemView.findViewById(R.id.tvFriendName);
                ivPhoto = itemView.findViewById(R.id.ivPhoto);

            }
        }

        @Override
        public int getItemCount() {
            return searchResult.size();
        }

        @NonNull
        @Override
        public FriendSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View itemView = layoutInflater.inflate(R.layout.friends_search_item_view, parent, false);
            return new FriendSearchAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendSearchAdapter.MyViewHolder myViewHolder, int position) {
            final Dog search = searchResult.get(position);

            myViewHolder.tvName.setText(search.getName());
            Log.d("", "" + search.getDogId());
            CommonRemote.getProfilePhoto(search.getDogId(), myViewHolder.ivPhoto, getActivity());

            myViewHolder.btAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFriend(search.getDogId());
                }
            });


            myViewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int dogId = Common.getPreferencesDogId(getActivity());
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
                    tvName.setText(search.getName());
                    String info = "品種：" + search.getVariety() + "\n"
                            + "性別：" + search.getGender() + "\n"
                            + "年紀：" + search.getAge() + "\n"
                            + "生日：" + dateFormat.format(search.getBirthday());

                    tvInfo.setText(info);

                    dialogBuilder.setView(dialogView);
                    final AlertDialog myDogDialog = dialogBuilder.create();
                    myDogDialog.show();

                    changeProfilePhoto.setVisibility(View.GONE);

                    signOut.setVisibility(View.GONE);
                }
            });
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


        void addFriend(int friendId) {
            int myDogId = Common.getPreferencesDogId(getActivity());
            if (Common.isNetworkConnect(getActivity())) {
                String url = Common.URL + "/FriendServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "addFriend");
                jsonObject.addProperty("myDogId", myDogId);
                jsonObject.addProperty("friendId", friendId);

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