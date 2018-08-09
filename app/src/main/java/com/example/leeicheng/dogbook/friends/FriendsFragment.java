package com.example.leeicheng.dogbook.friends;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.Iterator;
import java.util.List;

public class FriendsFragment extends Fragment {
    private static final String TAG = "FriendsFragment";

    private RecyclerView rvMember;
    private GridLayoutManager gridLayoutManager;
    private GeneralTask friendGetAllTask;
    private ImageView btSearch;
    private ImageView btPair;
    private  ImageView btConfirm;
    List<Dog> friendList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_fragment, container, false);

        friendList = getAllFriends();

        findViews(view);

        return view;
    }

    private void findViews(View view) {

        rvMember = view.findViewById(R.id.rvMember);
        gridLayoutManager = (new GridLayoutManager(getActivity(), 1));
        btSearch = view.findViewById(R.id.btSearch);

        viewControl();
    }

    void viewControl() {
        //搜尋


        rvMember.setLayoutManager(gridLayoutManager);
        rvMember.setAdapter(new FriendAdapter());
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
            friendGetAllTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = friendGetAllTask.execute().get();
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

    private class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            TextView tvAge;
            TextView tvGender;
            TextView tvVariety;
            ImageView ivPhoto;

            MyViewHolder(View itemView) {
                super(itemView);

                tvName =  itemView.findViewById(R.id.tvName);
                tvAge =  itemView.findViewById(R.id.tvAge);
                tvGender =  itemView.findViewById(R.id.tvGender);
                tvVariety =  itemView.findViewById(R.id.tvVariety);
                ivPhoto = itemView.findViewById(R.id.ivPhoto);
            }
        }

        @Override
        public int getItemCount() {
            Iterator<Dog> iter = friendList.iterator();

            while (iter.hasNext()){
                Dog dog = iter.next();
                if (dog.getDogId() == Common.getPreferencesDogId(getActivity())){
                    iter.remove();
                }
            }

            return friendList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View itemView = layoutInflater.inflate(R.layout.friends_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Dog friend = friendList.get(position);


            myViewHolder.tvName.setText(friend.getName());


            myViewHolder.tvAge.setText(String.valueOf(friend.getAge()) + "歲");
            myViewHolder.tvGender.setText(friend.getGender());
            myViewHolder.tvVariety.setText(friend.getVariety());
            //get media
            CommonRemote.getProfilePhoto(friend.getDogId(),myViewHolder.ivPhoto,getActivity());

            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int dogId = friend.getDogId();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.mydog_dialog, null);
                    final ImageView background = dialogView.findViewById(R.id.ivBackgroundDialog);
                    ImageView ProfilePhoto = dialogView.findViewById(R.id.ivProfilePhotoDialog);
                    TextView tvName = dialogView.findViewById(R.id.tvNameDialog);
                    TextView tvInfo = dialogView.findViewById(R.id.tvInfoDialog);
                    Button changeProfilePhoto = dialogView.findViewById(R.id.changeProfilePhoto);
                    Button signOut = dialogView.findViewById(R.id.signOut);
                    final Button goOut = dialogView.findViewById(R.id.goOut);

                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

                    getBackgroundPhoto(background);
                    CommonRemote.getProfilePhoto(dogId, ProfilePhoto, getActivity());
                    tvName.setText(friend.getName());
                    float meterSum = CommonRemote.getMeter(dogId, getActivity());
                    String info = String.format("%.1f", meterSum) + " 公尺" + "\n"
                            + "品種：" + friend.getVariety() + "\n"
                            + "性別：" + friend.getGender() + "\n"
                            + "年紀：" + friend.getAge() + "\n"
                            + "生日：" + dateFormat.format(friend.getBirthday());
                    tvInfo.setText(info);

                    dialogBuilder.setView(dialogView);
                    final AlertDialog myDogDialog = dialogBuilder.create();
                    myDogDialog.show();

                    changeProfilePhoto.setVisibility(View.GONE);
                    signOut.setVisibility(View.GONE);
                    goOut.setVisibility(View.GONE);
                }


                void getBackgroundPhoto(ImageView imageView) {

                    MediaTask mediaTask;
                    int photoSize = getActivity().getResources().getDisplayMetrics().widthPixels;
                    int dogId = friend.getDogId();
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

    private void switchFragment(Fragment fragment) {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().
                    replace(R.id.flMain, fragment).addToBackStack(null).commit();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (friendGetAllTask != null) {
            friendGetAllTask.cancel(true);
        }


    }
}
