package com.example.tastytracker;

//Object representing the current user
public class User {
    private String username;
    private String password;
    private int householdID;
    private String permissions;

    //Constructor
    User(String username, String password, int householdID, String permissions){
        this.username = username;
        this.password = password;
        this.householdID = householdID;
        this.permissions = permissions;
    }

    //Getter method for password
    public String getPassword(){
        return password;
    }

    //Getter method for username
    public String getUsername(){
        return username;
    }

    //Getter method for household id
    public int getHouseholdID(){
        return householdID;
    }

    public String getPermissions(){
        return permissions;
    }

    public void changePermissions(String newPermissions){
        this.permissions = newPermissions;
    }
}
