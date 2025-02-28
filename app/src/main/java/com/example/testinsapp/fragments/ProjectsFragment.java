package com.example.testinsapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.testinsapp.R;
import com.example.testinsapp.activities.ConvActivity;
import com.example.testinsapp.adapter.ProjectAdapter;
import com.example.testinsapp.adapter.RecentChatRecyclerAdapter;
import com.example.testinsapp.adapter.SearchUserRecyclerAdapter;
import com.example.testinsapp.model.ChatroomModel;
import com.example.testinsapp.model.ProjectModel;
import com.example.testinsapp.model.UserModel;
import com.example.testinsapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ProjectsFragment extends Fragment {
RelativeLayout addProject;
ProjectAdapter adapter;
RecyclerView projectsRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_projects, container, false);
addProject=rootView.findViewById(R.id.addProject);
projectsRecyclerView=rootView.findViewById(R.id.projects_recyclerView);
addProject.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Find existing CreateProjectFragment
        Fragment existingFragment = fragmentManager.findFragmentByTag(CreateProjectFragment.class.getSimpleName());
        if (existingFragment != null) {
            fragmentManager.beginTransaction().remove(existingFragment).commit();
        }

        // Replace with new CreateProjectFragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        CreateProjectFragment CreateProjectFragment = new CreateProjectFragment();
        transaction.replace(R.id.fragment_container, CreateProjectFragment, CreateProjectFragment.class.getSimpleName());
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
});
setupRecyclerView(projectsRecyclerView);
    return rootView;
    }

    private void setupRecyclerView(RecyclerView projectsRecyclerView) {

        Query query= FirebaseUtil.allProjectCollectionRef().whereArrayContains("members",FirebaseUtil.getCurrentUserId());
        Log.d("FirestoreData", "Snapshot Data: " + query);
        FirestoreRecyclerOptions<ProjectModel> options=new FirestoreRecyclerOptions.Builder<ProjectModel>().setQuery(query,ProjectModel.class).build();
        adapter=new ProjectAdapter(options,requireContext());
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        projectsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}