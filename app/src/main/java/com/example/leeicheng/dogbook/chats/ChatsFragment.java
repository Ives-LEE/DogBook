package com.example.leeicheng.dogbook.chats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;

public class ChatsFragment extends Fragment {
    RecyclerView rvChatRoom;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment,container,false);
        findViews(view);
        return view;
    }
    void findViews(View view){
        rvChatRoom = view.findViewById(R.id.rvChatRoom);
        viewsControl();
    }
    void viewsControl(){
        rvChatRoom.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rvChatRoom.setAdapter(new ChatRoomAdapter());
    }



    private class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatItemViewHolder> {
        @Override
        public ChatItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.chatsroom_item,parent,false);
            return new ChatItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatItemViewHolder holder, int position) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),ChatroomActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        public class ChatItemViewHolder extends RecyclerView.ViewHolder {
            ImageView civFriendProfilePhoto;
            TextView tvChatsTitle,tvLastChat;
            public ChatItemViewHolder(View view) {
                super(view);
                civFriendProfilePhoto = view.findViewById(R.id.civFriendProfilePhoto);
                tvChatsTitle = view.findViewById(R.id.tvChatsTitle);
                tvLastChat = view.findViewById(R.id.tvLastChat);
            }
        }
    }
}
