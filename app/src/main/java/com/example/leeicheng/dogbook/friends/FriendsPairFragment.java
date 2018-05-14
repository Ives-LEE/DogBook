package com.example.leeicheng.dogbook.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by leeicheng on 2018/4/22.
 */

public class FriendsPairFragment extends Fragment {
    private static final String TAG = "FriendsPairFragment";
    private GeneralTask memberGetAllTask;
    private Friend member;
    private ImageView ivPair1;
    private Button btLike, btDislike, btSearch;
    private ViewPager vpPair;
    List<Dog> members;



    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_pair_fragment, container, false);
        members = getAllMembers();
        findViews(view);
        return view;
    }

    private void findViews(View view) {

        vpPair = view.findViewById(R.id.vpPair);
//        gridLayoutManager = (new GridLayoutManager(getActivity(), 3));

        btLike = view.findViewById(R.id.btLike);
        btDislike = view.findViewById(R.id.btDislike);

        viewControl();
    }

    void viewControl() {


        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        btDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

//        vpPair.setAdapter(new MemberAdapter());
    }

    private List<Dog> getAllMembers() {
        List<Dog> memberList = null;
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
                memberList = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }
        return memberList;
    }

//    private class MemberAdapter extends FragmentStatePagerAdapter<MemberAdapter.MyViewHolder> {
//
//        public class MyViewHolder extends RecyclerView.ViewHolder {
//            TextView tvName;
//            ImageView ivPhoto;
//
//            MyViewHolder(View itemView) {
//                super(itemView);
//
//                tvName = itemView.findViewById(R.id.tvName);
//                ivPhoto = itemView.findViewById(R.id.ivPhoto);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return members.size();
//        }
//
//        @NonNull
//        @Override
//        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//            View itemView = layoutInflater.inflate(R.layout.friends_pair_item_view, parent, false);
//            return new MemberAdapter().MyViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
//            final Dog member = members.get(position);
//            myViewHolder.tvName.setText(member.getName());
//            //get media
//            CommonRemote.getProfilePhoto(member.getDogId(), myViewHolder.ivPhoto, getActivity());
//
//            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Fragment fragment = new MyDogFragment();
////                    Bundle bundle = new Bundle();
////                    bundle.putSerializable("friend", (Serializable) friend);
////                    fragment.setArguments(bundle);
////                    switchFragment(fragment);
//
//                }
//            });
//
//
//        }
//
//    }


    @Override
    public void onStop() {
        super.onStop();
        if (memberGetAllTask != null) {
            memberGetAllTask.cancel(true);
        }


    }
}