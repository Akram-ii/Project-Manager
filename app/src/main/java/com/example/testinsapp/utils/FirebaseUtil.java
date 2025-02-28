package com.example.testinsapp.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.testinsapp.R;
import com.example.testinsapp.activities.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {
    public static String getCurrentUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static CollectionReference allUserCollectionRef(){
return FirebaseFirestore.getInstance().collection("Users");
    }
    public static CollectionReference allProjectCollectionRef(){
        return FirebaseFirestore.getInstance().collection("Project");
    }
    public static DocumentReference getChatroomRef(String chatroomId){
        return FirebaseFirestore.getInstance().collection("Chatrooms").document(chatroomId);
    }
    public  static CollectionReference getChatroomMsgRef(String chatroomId){
        return getChatroomRef(chatroomId).collection("chats");
    }
    public static String getChatroomId(String userid1,String userid2) {
        if (userid1.hashCode() < userid2.hashCode()) {
        return userid1+"_"+userid2;
        } else {
            return userid2  +"_"+userid1;
        }}
    public static CollectionReference allChatroomCollectionRef(){
return FirebaseFirestore.getInstance().collection("Chatrooms");
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){


        DocumentReference docRef;

        if (userIds.get(0).equals(FirebaseUtil.getCurrentUserId())) {
            docRef = allUserCollectionRef().document(userIds.get(1));
        } else {
            docRef = allUserCollectionRef().document(userIds.get(0));
        }

        Log.d("Chatroom", "Document Reference: " + docRef.getPath()); // Log the document reference
        return docRef;
    }
public static String timestampToString(Timestamp timestamp){
        String s= new SimpleDateFormat("yyyy/MM/dd HH:mm").format(timestamp.toDate());
    String monthOrDay = s.substring(5, 7);
    int month=Integer.parseInt(monthOrDay);
    String year=s.substring(0,4);
    String day=s.substring(8,10);
    return Util.getMonthFormat(month)+" "+day+" "+year+" "+s.substring(11,s.length());
}
    public static String timestampToStringNoDetail(Timestamp timestamp){
        String s= new SimpleDateFormat("yyyy/MM/dd").format(timestamp.toDate());
        String monthOrDay = s.substring(5, 7);
        int month=Integer.parseInt(monthOrDay);
        String year=s.substring(0,4);
        String day=s.substring(8,10);
        return Util.getMonthFormat(month)+" "+day+" "+year;
    }
public static void loadPfp(String userId, ImageView pfp){

    FirebaseUtil.allUserCollectionRef().document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                   String uriString = "https://"+documentSnapshot.getString("profilePictureUri");

                    if (documentSnapshot.getString("profilePictureUri")!=null && !documentSnapshot.getString("profilePictureUri").isEmpty()) {
                        Uri uri=Uri.parse(uriString);
                        Log.d("Firestore", "Profile picture URI: " + uri.toString());
                        Picasso.get().load(uri).transform(new CircleTransform()).into(pfp);
                    } else {
                        pfp.setImageResource(R.drawable.account_icon_gray);
                        Log.d("Firestore", "No profile picture");
                    }
                } else {
                    Log.d("Firestore", "User document does not exist.");
                }
            })
            .addOnFailureListener(e -> {
                Log.e("Firestore", "Error getting document: " + e.getMessage());
            });
    }

    public static void loadProjectPic(String userId, ImageView pfp){

        FirebaseUtil.allProjectCollectionRef().document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String uriString = "https://"+documentSnapshot.getString("profilePic");

                        if (documentSnapshot.getString("profilePic")!=null && !documentSnapshot.getString("profilePic").isEmpty()) {
                            Uri uri=Uri.parse(uriString);
                            Log.d("Firestore", "Profile picture URI: " + uri.toString());
                            Picasso.get().load(uri).transform(new CircleTransform()).into(pfp);
                        } else {
                            pfp.setImageResource(R.drawable.group_icon_main);
                            Log.d("Firestore", "No profile picture");
                        }
                    } else {
                        Log.d("Firestore", "User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting document: " + e.getMessage());
                });
    }
    public static Timestamp convertToTimestamp(String dateString) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

            Date date = sdf.parse(dateString);
            return new Timestamp(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}


