package com.example.testinsapp.model;

public class UserModel {
        private String username;
        private String token;



    private String registerDate;
        private String id;

        private int availability;

        public UserModel() {
        }



    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public UserModel(String name, String date,String id) {
            this.username = name;
            this.registerDate = date;
            this.id=id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String name) {
            this.username = name;
        }

        public String getRegisterDate() {
            return registerDate;
        }


    }


