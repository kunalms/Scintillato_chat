package com.scintillato.scintillatochat;


public class Recommend_User_List {
    String name ,follower, id;

    public Recommend_User_List(String name, String follower, String id) {
        this.name = name;
        this.follower = follower;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
