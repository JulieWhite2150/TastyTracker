package com.example.tastytracker;

public class UserSession {
    private static UserSession instance;
    private final User currentUser;

    //Constructor
    private UserSession(User currentUser) {
        this.currentUser = currentUser;
    }

    //Initialize the user session and store the username through the constructor
    public static void init(User currentUser) {
            instance = new UserSession(currentUser);
    }

    //Get instance and throw exception if no user session has been created
    public static UserSession getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserSession not initialized. Call init() after login.");
        }
        return instance;
    }

    //Getter method to get the username for the instance
    public String getUsername() {
        return currentUser.getUsername();
    }

    public String getPermissions(){
        return currentUser.getPermissions();
    }

    public void setPermissions(String newPermissions){
        currentUser.changePermissions(newPermissions);
    }

    public int getHouseholdID(){
        return currentUser.getHouseholdID();
    }
}

