package com.example.testinsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testinsapp.activities.ConvActivity;
import com.example.testinsapp.R;
import com.example.testinsapp.model.ProjectModel;
import com.example.testinsapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProjectAdapter extends FirestoreRecyclerAdapter<ProjectModel, ProjectAdapter.ProjectModelViewHolder> {
    Context context;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    public ProjectAdapter(@NonNull FirestoreRecyclerOptions<ProjectModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProjectModelViewHolder holder, int position, @NonNull ProjectModel model) {
FirebaseUtil.allProjectCollectionRef().document(model.getProjectId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
   if(task.isSuccessful()){
       ProjectModel project=task.getResult().toObject(ProjectModel.class);

       holder.name.setText(model.getName());
       FirebaseUtil.loadProjectPic(model.getProjectId(), holder.pfp);
   }



   holder.itemView.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {

       }
   });
    }
});

    }
    @NonNull
    @Override
    public ProjectModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.project_recycler_row,parent,false);
        return new ProjectModelViewHolder(view);
    }

    class ProjectModelViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView pfp;
        public ProjectModelViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.nameTextView);
            pfp=itemView.findViewById(R.id.profile_pic_image);
        }
    }

}

