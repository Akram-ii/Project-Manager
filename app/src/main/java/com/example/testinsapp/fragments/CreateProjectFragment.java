package com.example.testinsapp.fragments;

import static android.content.ContentValues.TAG;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.testinsapp.R;
import com.example.testinsapp.model.ProjectModel;
import com.example.testinsapp.utils.CircleTransform;
import com.example.testinsapp.utils.FirebaseUtil;
import com.example.testinsapp.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateProjectFragment extends Fragment {
    ImageView projectPic;
    TextInputEditText name, desc, cat;
    TextView showStart;

    DatePickerDialog datePickerDialog;
    private static final int GALLERY_REQUEST_CODE = 105;
    String pId,imageUri;
Uri imagePath;
    Button create;
Timestamp t;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_project, container, false);

        projectPic = rootView.findViewById(R.id.pfp_Image_View);
        initDatePicker();
        name = rootView.findViewById(R.id.name_EditText);
        desc = rootView.findViewById(R.id.desc_EditText);

        cat = rootView.findViewById(R.id.cat_EditText);
        showStart = rootView.findViewById(R.id.startTextView);

        create = rootView.findViewById(R.id.create_button);

showStart.setOnClickListener(v->{
    openDatePicker(showStart);
});
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtName = name.getText().toString();
                String txtDesc = desc.getText().toString();
                String txtCat = cat.getText().toString();
                if(txtName.length()<3){
                    name.setError("Too short");
                } else if (txtName.length()>20) {
                    name.setError("Too long");
                } else if (txtDesc.isEmpty()) {
                    desc.setError("Empty description");
                } else if (txtCat.isEmpty()) {
                    cat.setError("No category");
                } else if (t==null) {
                    showStart.setError("Enter ");
                }else{ DocumentReference newP = FirebaseFirestore.getInstance().collection("Project").document();
                    pId = newP.getId();
                    Map<String, String> roles = new HashMap<>();
                    roles.put(FirebaseUtil.getCurrentUserId(), "Owner");
                    List<String> members = new ArrayList<>();
                    members.add(FirebaseUtil.getCurrentUserId());
                    ProjectModel project;

                        if(imageUri==""){
                            Log.d(TAG, "projectpic no  "+imageUri);
                            project = new ProjectModel(txtName, txtDesc, txtCat, roles, members, FirebaseUtil.getCurrentUserId(), pId,t,"");
                        }else{
                            Log.d(TAG, "projectpic yes "+imageUri.toString().substring(8));
                            project = new ProjectModel(txtName, txtDesc, txtCat, roles, members, FirebaseUtil.getCurrentUserId(), pId,t,imageUri.toString().substring(8));
                        }


                addProject(project);
             }
            }
        });
        projectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,GALLERY_REQUEST_CODE);
            }
        });
        return rootView;
    }

    private void addProject(ProjectModel pr) {

        DocumentReference newP = FirebaseFirestore.getInstance().collection("Project").document(pr.getProjectId());
        newP.set(pr).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Project Created Successfully", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error Creating new project", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK && data!=null){
                imagePath=data.getData();
                Picasso.get().load(imagePath).transform(new CircleTransform()).into(projectPic);
                uploadImagePathToCloud(imagePath);
            }}

    }

    private void uploadImagePathToCloud(Uri imagePath) {
        MediaManager.get().upload(imagePath)
                .option("public_id", "project_pictures/" + FirebaseUtil.getCurrentUserId()) // Folder path + user ID
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
                         imageUri = resultData.get("secure_url").toString();
                        Log.d(TAG, "projectp"+imageUri);

                        Picasso.get().load(imagePath).transform(new CircleTransform()).into(projectPic);
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
                .dispatch();
    }


    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
          month++;
                String date = Util.getMonthFormat(month) + " " + dayOfMonth + " " + year;
                t=createTimestamp(year,month,dayOfMonth);
                showStart.setText(date);
            }
        };
        Calendar cal=Calendar.getInstance();
        final int y = cal.get(Calendar.YEAR);
        final int m = cal.get(Calendar.MONTH);
        final int d = cal.get(Calendar.DAY_OF_MONTH);
        int style=AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(getContext(),style, dateSetListener, y, m, d);
    }


public void openDatePicker(View view){
        datePickerDialog.show();
}
    public Timestamp createTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0); // month - 1 because Calendar months are 0-based
        Date date = calendar.getTime();
        return new Timestamp(date);
    }
}

