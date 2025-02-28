package com.example.testinsapp.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.auth.User;

import java.util.List;
import java.util.Map;

public class ProjectModel {
    String cat,name,desc;
   Map<String,String> roles;
   List<String> members;
    String createdBy;
    String projectId;
    String profilePic;

    Timestamp startDate;

    public String getProfilePic() {
        return profilePic;
    }

    public ProjectModel(){}
    public String getCat() {
        return cat;
    }

    public ProjectModel(String name, String desc, String cat, Map<String, String> roles, List<String> members, String createdBy, String projectId, Timestamp startDate,String profilePic) {
        this.cat = cat;
        this.name = name;
        this.desc = desc;
        this.roles = roles;
        this.members = members;
        this.createdBy = createdBy;
        this.projectId = projectId;
        this.profilePic = profilePic;
        this.startDate = startDate;
    }

    public ProjectModel(String name, String desc, String cat, Map<String, String> roles, List<String> members, String createdBy, String projectId, Timestamp startDate) {
        this.cat = cat;
        this.name = name;
        this.desc = desc;
        this.roles = roles;
        this.members = members;
        this.createdBy = createdBy;
        this.projectId = projectId;
        this.startDate = startDate;

    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Map<String, String> getRoles() {
        return roles;
    }

    public void setRoles(Map<String, String> roles) {
        this.roles = roles;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public com.google.firebase.Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

}
