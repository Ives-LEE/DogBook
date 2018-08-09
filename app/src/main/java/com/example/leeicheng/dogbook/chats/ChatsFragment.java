package com.example.leeicheng.dogbook.chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatsFragment extends Fragment {
    RecyclerView rvChatRoom;
    List<Room> rooms;
    GeneralTask generalTask;
    int friendId;
    Room room;
    List<Integer> friends;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        rooms = getRooms();
        friends = new ArrayList<>();
        findViews(view);

        return view;
    }

    void findViews(View view) {
        rvChatRoom = view.findViewById(R.id.rvChatRoom);
        viewsControl();
    }

    void viewsControl() {
        rvChatRoom.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvChatRoom.setAdapter(new ChatRoomAdapter());
    }

    List<Room> getRooms() {
        List<Room> rooms = null;
        int dogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/ChatServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SHOW_ROOMS);
            jsonObject.addProperty("dogId", dogId);
            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                Type type = new TypeToken<List<Room>>() {
                }.getType();
                rooms = new Gson().fromJson(jsonIn, type);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return rooms;
    }

    private class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatItemViewHolder> {

        @Override
        public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.chatsroom_item, parent, false);

            return new ChatItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ChatItemViewHolder holder, final int position) {
            room = rooms.get(position);
            final int roomId = room.getRoomId();
            Chat lastChat = getLastChat(roomId);

            if (Common.getPreferencesDogId(getActivity()) == room.getDogOne()) {
                friendId = room.getDogTwo();
            } else if (Common.getPreferencesDogId(getActivity()) == room.getDogTwo()) {
                friendId = room.getDogOne();
            }
            Dog dog = CommonRemote.getDogInfo(friendId,getActivity());
            if (dog != null) {
                holder.tvChatsFriend.setText(dog.getName());
            }

            CommonRemote.getProfilePhoto(friendId, holder.civFriendProfilePhoto,getActivity());

            if (lastChat != null) {
                holder.tvLastChat.setText(lastChat.getMessage());
            }

            friends.add(friendId);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("friendId", friends.get(position));
                    bundle.putInt("roomId", roomId);
                    Intent intent = new Intent(getActivity(), ChatroomActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return rooms.size();
        }

        public class ChatItemViewHolder extends RecyclerView.ViewHolder {
            ImageView civFriendProfilePhoto;
            TextView tvChatsFriend, tvLastChat;

            public ChatItemViewHolder(View view) {
                super(view);
                civFriendProfilePhoto = view.findViewById(R.id.civFriendProfilePhoto);
                tvChatsFriend = view.findViewById(R.id.tvChatsFriend);
                tvLastChat = view.findViewById(R.id.tvLastChat);
            }
        }

        Chat getLastChat(int roomId) {
            Gson gson = new Gson();
            Chat chat = null;
            if (Common.isNetworkConnect(getActivity())) {
                String url = Common.URL + "/ChatServlet";
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("status", Common.GET_LAST_CHAT);
                jsonObject.addProperty("roomId", roomId);

                generalTask = new GeneralTask(url, jsonObject.toString());

                try {
                    String chatJson = generalTask.execute().get();
                    chat = gson.fromJson(chatJson, Chat.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return chat;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewsControl();
    }
}
