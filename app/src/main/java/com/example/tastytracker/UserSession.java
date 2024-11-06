package com.example.tastytracker;

public class UserSession {
    private static UserSession instance;
    private final String username;

    //Constructor
    private UserSession(String username) {
        this.username = username;
    }

    //Initialize the user session and store the username through the constructor
    public static void init(String username) {
            instance = new UserSession(username);
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
        return username;
    }
}

