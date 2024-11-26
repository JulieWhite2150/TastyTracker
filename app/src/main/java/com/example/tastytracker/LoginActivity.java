package com.example.tastytracker;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

//Activity that logs in a user that already has a registered account
public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText;
    Button loginButton, cancelButton;
    userInfoDBAdapter userInfoDB;
    public static String username;

       @Override
       protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
           setContentView(R.layout.activity_login);

           usernameEditText = findViewById(R.id.usernameEditText);
           passwordEditText = findViewById(R.id.passwordEditText);
           loginButton = findViewById(R.id.loginButton);

           userInfoDB = new userInfoDBAdapter(this);
           userInfoDB.open();

           //Button that takes in user input for username/password and calls the login method
           loginButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   username = usernameEditText.getText().toString();
                   String password = passwordEditText.getText().toString();
                   login(username, password);
               }
           });
       }


    //Method that attempts to login the user, but checks for various errors and handles them appropriately
    //If the username/password are correct, navigate the user to the inventory activity
    private void login(String enteredUsername, String enteredPassword) {
           User currentUser = userInfoDB.getUser(enteredUsername);

           //if user was not found in db, then username does not exist
           if (currentUser == null){
               showUsernameNotFoundAlert();
           }
           //if password entered does not match password in db
           else if (!currentUser.getPassword().equals(enteredPassword)){
               showPasswordIncorrectAlert();
           }
           //else username/password correct, navigate to inventory activity
           else{
               showLoginSuccessToast();
               userInfoDB.close();
               Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
               UserSession.init(currentUser); //Initialize user session
               intent.putExtra("HOUSEHOLD_ID", currentUser.getHouseholdID());
               startActivity(intent);
               finish();
           }
    }

    //Show the toast that the login was successful, toast is used just to inform the user-- no ack needed
    private void showLoginSuccessToast() {
        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
    }

    //Tell the user that username was not found, ack is needed so AlertDialog used
    private void showUsernameNotFoundAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username Not Found")
                .setMessage("The username you entered was not found.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that password was incorrect, ack is needed so AlertDialog used
    private void showPasswordIncorrectAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Incorrect")
                .setMessage("The password you entered is incorrect.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //used by the xml layout to move the user back to the initial screen
    public void goBack(View view) {
        Intent intent = new Intent(this, InitialActivity.class);
        startActivity(intent);
        finish();
    }
}