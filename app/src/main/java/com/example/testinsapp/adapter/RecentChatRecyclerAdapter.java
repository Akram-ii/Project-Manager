package com.example.testinsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testinsapp.activities.ConvActivity;
import com.example.testinsapp.R;
import com.example.testinsapp.model.ChatroomModel;
import com.example.testinsapp.model.UserModel;
import com.example.testinsapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    Context context;



    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options,Context context) {
        super(options);
        this.context=context;

    }

    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        Log.d("Chatroom", "userIds: " + model.getUserIds().toString());
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    UserModel otherUser = task.getResult().toObject(UserModel.class);

                    Boolean lastMsgSentByMe = model.getLastMsgSenderId().equals(FirebaseUtil.getCurrentUserId());


                    holder.username.setText(otherUser.getUsername());

                    if (lastMsgSentByMe) {
                        holder.mostRecentMsg.setText("Me: " + model.getLastMsgSent());
                    } else {
                        holder.mostRecentMsg.setText(otherUser.getUsername() + ": " + model.getLastMsgSent());
                    }
                    holder.timestampRecentMsg.setText(FirebaseUtil.timestampToString(model.getLastMsgTimeStamp()));
                    FirebaseUtil.loadPfp(otherUser.getId(), holder.pfp);


                    if(model.getLastMsgSenderId().equals(FirebaseUtil.getCurrentUserId()) || model.getUnseenMsg()==0){
                        holder.unseenMsg.setVisibility(View.GONE);
                    }else {
                        holder.mostRecentMsg.setTextColor(Color.parseColor("#8578A8"));
                        holder.mostRecentMsg.setTypeface(null, Typeface.BOLD);
                        holder.unseenMsg.setVisibility(View.VISIBLE);
                    }

                    FirebaseUtil.allUserCollectionRef().document(otherUser.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.e("verif", "Error fetching document: " + error.getMessage());
                                return;
                            }
                            if (value != null && value.exists()) {
                                Long availabilityLong = value.getLong("availability");
                                if (availabilityLong != null) {
                                    int a = availabilityLong.intValue();
                                    if (a == 1) {
                                        holder.activity.setVisibility(View.VISIBLE);
                                        holder.activity.setImageResource(R.drawable.online_user);
                                    } else {
                                        holder.activity.setVisibility(View.VISIBLE);
                                        holder.activity.setImageResource(R.drawable.offline_user);
                                    }
                                } else {
                                }
                            } else {
                            }
                        }
                    });

                    holder.unseenMsg.setText("+ "+model.getUnseenMsg());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(model.getLastMsgSenderId()!=FirebaseUtil.getCurrentUserId()){
                                Log.d( "unseenMsg: ","");
                                model.setUnseenMsg(0);
                            }
                            Intent intent = new Intent(context.getApplicationContext(), ConvActivity.class);
                            intent.putExtra("username", otherUser.getUsername());
                            intent.putExtra("userId", otherUser.getId());
                            intent.putExtra("availability",otherUser.getAvailability());
                            intent.putExtra("token",otherUser.getToken());
                            intent.putExtra("lastMsgId",model.getLastMsgSenderId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                } else {
                    FirebaseUtil.allChatroomCollectionRef().document(model.getChatroomId()).delete();
                    Log.e("Chatroom", "Error: Document not found or task unsuccessful");
                }
            }
        });
    }
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.recent_chat_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView username,timestampRecentMsg,mostRecentMsg,unseenMsg;
        ImageView pfp;
        ImageView activity;
        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.usernameTextView);
            activity=itemView.findViewById(R.id.activity);
            timestampRecentMsg=itemView.findViewById(R.id.lastMsg_timestampTextView);
            mostRecentMsg=itemView.findViewById(R.id.lastMsgSentTextView);
            unseenMsg=itemView.findViewById(R.id.unseenMsg);
            pfp=itemView.findViewById(R.id.profile_pic_image);
        }

    }
    public interface OnDataLoadedListener {
        void onDataLoaded();
    }
}

