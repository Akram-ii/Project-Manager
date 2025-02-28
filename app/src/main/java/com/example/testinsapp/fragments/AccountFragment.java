package com.example.testinsapp.fragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.testinsapp.R;
import com.example.testinsapp.activities.LoginActivity;
import com.example.testinsapp.activities.MainActivity;
import com.example.testinsapp.utils.CircleTransform;
import com.example.testinsapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class AccountFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 105;
    TextView textViewUsername, textViewEmail, textViewPass, textViewWelcome,textViewDate;
    ImageButton editUsername, editEmail, editPassword;
    ImageView pfp;
    Uri imagePath;
    ProgressBar progressBar;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userId;
    Button deleteUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        textViewDate=rootView.findViewById(R.id.dateTextView);
        deleteUser=rootView.findViewById(R.id.deleteButton);
        editUsername = rootView.findViewById(R.id.usernameEdit);
        editEmail = rootView.findViewById(R.id.emailEdit);
        editPassword = rootView.findViewById(R.id.passwordEdit);
        textViewUsername = rootView.findViewById(R.id.usernameTextView);
        textViewEmail = rootView.findViewById(R.id.emailTextView);
        textViewWelcome = rootView.findViewById(R.id.welcomeTextView);
        textViewPass = rootView.findViewById(R.id.passwordTextView);
        pfp = rootView.findViewById(R.id.pfpImageView);
        progressBar = rootView.findViewById(R.id.progressbar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "something went wrong User's details not found", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserInfo(user);
        }

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure ?");
            builder.setMessage("Deleting your account will result in completely removing your data from the app");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseUtil.allUserCollectionRef().document(FirebaseUtil.getCurrentUserId()).delete();
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       Toast.makeText(getContext(),"Account deleted",Toast.LENGTH_SHORT).show();
                       Intent intent=new Intent(getContext(),LoginActivity.class);
                       startActivity(intent);
                       getActivity().finish();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getContext(),"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();

                   }
               });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
                }
            });
                AlertDialog dialog=builder.create();
                dialog.show();
            }

        });
        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEditText = new EditText(v.getContext());

                AlertDialog.Builder updateUsernameDialog = new AlertDialog.Builder(requireActivity());
                updateUsernameDialog.setTitle("Change username?");
                updateUsernameDialog.setMessage("Enter your new username");
                updateUsernameDialog.setView(usernameEditText);
                updateUsernameDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = usernameEditText.getText().toString();
                        if (newUsername.length() < 3) {
                            Toast.makeText(getContext(), "Username too short", Toast.LENGTH_SHORT).show();
                        } else if (newUsername.length() > 15) {
                            Toast.makeText(getContext(), "Username too long", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Username successfully updated", Toast.LENGTH_SHORT).show();
                            userId=user.getUid();
                            db.collection("Users").document(userId).update("username", newUsername);
                            progressBar.setVisibility(View.VISIBLE);
                            showUserInfo(user);
                            if(getActivity() != null && isAdded()){
                            ((MainActivity) getActivity()).refreshUser(newUsername);
                            }
                        }
                    }
                });
                updateUsernameDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = updateUsernameDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                showUserInfo(user);
            }
        });
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailEditText = new EditText(v.getContext());

                AlertDialog.Builder updateEmailDialog = new AlertDialog.Builder(v.getContext());
                updateEmailDialog.setTitle("Change E-mail?");
                updateEmailDialog.setMessage("Enter your new E-mail");
                updateEmailDialog.setView(emailEditText);
                updateEmailDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newEmail = emailEditText.getText().toString();
                        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                            Toast.makeText(getContext(), "Not a valid email", Toast.LENGTH_SHORT).show();
                        } else if (newEmail == user.getEmail()) {
                            Toast.makeText(getContext(), "Already using " + newEmail, Toast.LENGTH_SHORT).show();
                        } else {
                            user.verifyBeforeUpdateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Verification email sent to " + newEmail, Toast.LENGTH_LONG).show();
                                    auth.signOut();
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                    getActivity().finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error sending verification email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                });
                updateEmailDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = updateEmailDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                showUserInfo(user);
            }
        });
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordEditText = new EditText(v.getContext());

                AlertDialog.Builder updatePasswordDialog = new AlertDialog.Builder(v.getContext());
                updatePasswordDialog.setTitle("Change password?");
                updatePasswordDialog.setMessage("Enter your new password");
                updatePasswordDialog.setView(passwordEditText);
                updatePasswordDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = passwordEditText.getText().toString();
                        if (newPassword.length() < 6) {
                            Toast.makeText(getContext(), "Password too short", Toast.LENGTH_SHORT).show();
                        } else {
                            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Password successfully updated", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.VISIBLE);
                                    showUserInfo(user);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                });
                updatePasswordDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = updatePasswordDialog.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                showUserInfo(user);
            }
        });
        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,GALLERY_REQUEST_CODE);
            }
        });

        return rootView;
    }



    @Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
if(requestCode==GALLERY_REQUEST_CODE){
    if(resultCode== Activity.RESULT_OK && data!=null){
        imagePath=data.getData();
        Picasso.get().load(imagePath).transform(new CircleTransform()).into(pfp);
        MainActivity mainActivity=(MainActivity) getActivity();
        mainActivity.refreshPfp(imagePath);
        uploadImagePathToCloud(imagePath);
}}

}

    private void uploadImagePathToCloud(Uri imagePath) {
        MediaManager.get().upload(imagePath)
                .option("public_id", "profile_pictures/" + FirebaseUtil.getCurrentUserId()) // Folder path + user ID
                .option("resource_type", "image")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(getContext(), "Upload started...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUri = resultData.get("secure_url").toString();
                        uploadUriToDB(imageUri);

                        Picasso.get().load(imagePath).transform(new CircleTransform()).into(pfp);
                        Toast.makeText(getContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onError: "+error.getDescription());
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                })
                .dispatch();}

    private void uploadUriToDB(String imageUrl) {


        FirebaseUtil.allUserCollectionRef().document(FirebaseUtil.getCurrentUserId()).update("profilePictureUri",imageUrl.toString().substring(8))
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "uploadUriToDB: "+ imageUrl.toString().substring(8));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "add pfp to db " + e.getMessage());
                });
    }


    public void onDestroyView() {
        super.onDestroyView();
        progressBar.setVisibility(View.GONE);
    }


    private void showUserInfo(FirebaseUser CUser) {
if(user!=null) {
    userId=user.getUid();
    DocumentReference userDocRef = db.collection("Users").document(userId);
    userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()) {
                String date = documentSnapshot.getString("registerDate");
                String username = documentSnapshot.getString("username");
                textViewDate.setText("Member since " + date);
                textViewEmail.setText(user.getEmail());
                FirebaseUtil.loadPfp(FirebaseUtil.getCurrentUserId(),pfp);
                textViewUsername.setText(username);
                textViewPass.setText("123456");
                textViewWelcome.setText("Hi " + username);
progressBar.setVisibility(View.GONE);
            }
        }

            });}
    }




}