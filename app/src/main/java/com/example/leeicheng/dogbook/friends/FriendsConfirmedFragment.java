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

/**
 * Created by apple on 2018/8/1.
 */

public class FriendsConfirmedFragment extends Fragment {


    private static final String TAG = "FriendConfirmedFragment";

    private RecyclerView rvCheck;
    private GeneralTask getFriendListFromCheckListTask;
    private GridLayoutManager gridLayoutManager;
    List<Dog> checkList;

    Dog dog;
    private Button btAddFriend;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_confirmed_fragment, container, false);

        checkList = getCheckList();



        findViews(view);

        return view;
    }


    private void findViews(View view) {

        gridLayoutManager = (new GridLayoutManager(getActivity(), 1));

        rvCheck = view.findViewById(R.id.rvCheck);

        viewControl();
    }

    private void viewControl() {


        rvCheck.setLayoutManager(gridLayoutManager);
        rvCheck.setAdapter(new FriendCheckAdapter());

    }



//Q1
private List<Dog> getCheckList() {
    List<Dog> checkList = null;
    if (Common.isNetworkConnect(getActivity())) {
        String url = Common.URL + "/FriendServlet";
        int inviteDogId = Common.getPreferencesDogId(getActivity());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", Common.GET_FRIENDID_FROM_CHECKLIST);
        jsonObject.addProperty("inviteDogId", inviteDogId);
        String jsonOut = jsonObject.toString();
        getFriendListFromCheckListTask = new GeneralTask(url, jsonOut);
        try {
            String jsonIn = getFriendListFromCheckListTask.execute().get();
            Type listType = new TypeToken<List<Dog>>() {
            }.getType();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            checkList = gson.fromJson(jsonIn, listType);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }
    return checkList;
}













    @Override
    public void onStart() {
        super.onStart();

    }

    private class FriendCheckAdapter extends RecyclerView.Adapter<FriendCheckAdapter.MyViewHolder> {

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
            Iterator<Dog> iter = checkList.iterator();

            while (iter.hasNext()){
                Dog dog = iter.next();
                if (dog.getDogId() == Common.getPreferencesDogId(getActivity())){
                    iter.remove();
                }
            }

            return checkList.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View itemView = layoutInflater.inflate(R.layout.friends_confirmed_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final Dog stranger = checkList.get(position);

            myViewHolder.tvName.setText(stranger.getName());


            Log.d("", "" + stranger.getDogId());
            CommonRemote.getProfilePhoto(stranger.getDogId(), myViewHolder.ivPhoto, getActivity());

            myViewHolder.btAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFromCheckList(stranger.getDogId());
                    addFriendConfirmed(stranger.getDogId());
                    myViewHolder.btAdd.setText("已接受");
                    view.setEnabled(false);
                }
            });


            myViewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int dogId = stranger.getDogId();
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
                    tvName.setText(stranger.getName());
                    String info = "品種：" + stranger.getVariety() + "\n"
                            + "性別：" + stranger.getGender() + "\n"
                            + "年紀：" + stranger.getAge() + "\n"
                            + "生日：" + dateFormat.format(stranger.getBirthday());

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
            int dogId = stranger.getDogId();
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


//Q2
    private void deleteFromCheckList(int dogId) {
        ;
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/FriendServlet";

            int inviteDogId = Common.getPreferencesDogId(getActivity());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", Common.DELETE_FRIEND);
            jsonObject.addProperty("dogId", dogId);
            jsonObject.addProperty("inviteDogId", inviteDogId);
            String jsonOut = jsonObject.toString();
            getFriendListFromCheckListTask = new GeneralTask(url, jsonOut);
            try {
                String jsonIn = getFriendListFromCheckListTask.execute().get();
                Type listType = new TypeToken<List<Dog>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                checkList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }

    }


    //Q3

    void addFriendConfirmed(int friendId) {
        int myDogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/FriendServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "addFriend");
            jsonObject.addProperty("myDogId", myDogId);
            jsonObject.addProperty("friendId", friendId);

            String jsonOut = jsonObject.toString();
            getFriendListFromCheckListTask = new GeneralTask(url, jsonOut);
            try {
                getFriendListFromCheckListTask.execute().get();

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
        if (getFriendListFromCheckListTask != null) {
            getFriendListFromCheckListTask.cancel(true);
        }


    }

}
