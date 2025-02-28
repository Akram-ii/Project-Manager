package com.example.testinsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testinsapp.R;
import com.example.testinsapp.model.ChatMsgModel;
import com.example.testinsapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMsgModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;


    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMsgModel> options,Context context) {
        super(options);
        this.context=context;

    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMsgModel model) {
if(Objects.equals(model.getSenderId(), FirebaseUtil.getCurrentUserId())){
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.pfp.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);
        holder.rightMsg.setText(model.getMessage());
}else{
    FirebaseUtil.loadPfp(model.getSenderId(), holder.pfp);
    holder.leftChatLayout.setVisibility(View.VISIBLE);
    holder.rightChatLayout.setVisibility(View.GONE);
    holder.leftMsg.setText(model.getMessage());

}

    }
    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.chatmsg_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
LinearLayout leftChatLayout,rightChatLayout;
ImageView pfp;
TextView leftMsg,rightMsg;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            pfp=itemView.findViewById(R.id.pfp);
            leftChatLayout=itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout=itemView.findViewById(R.id.right_chat_layout);
            leftMsg=itemView.findViewById(R.id.left_chat_textView);
            rightMsg=itemView.findViewById(R.id.right_chat_textView);

        }
    }

}

