package com.example.tastytracker;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
           //userInfoDB.clearAllUsers();

           loginButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   username = usernameEditText.getText().toString();
                   String password = passwordEditText.getText().toString();
                   login(username, password);
               }
           });
       }

    private void login(String username, String password) {
        Cursor cursor = userInfoDB.getAllUsers();
        boolean isUsernameFound = false;
        int usernameIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_USERNAME);
        int passwordIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_PASSWORD);
        int householdIDIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_HOUSEHOLD_ID);

        if (cursor.moveToFirst()) {
            do {
                String storedUsername = cursor.getString(usernameIndex);
                String storedPassword = cursor.getString(passwordIndex);
                int householdID = cursor.getInt(householdIDIndex);


                if (storedUsername != null && storedUsername.equals(username)) {
                    isUsernameFound = true;
                    if (storedPassword != null && storedPassword.equals(password)) {
                        // Password is correct
                        showLoginSuccessToast();
                        Log.d("END OF MAIN", "~~~~~~~~~~~~~~~~~~~~~~~~~");
                        userInfoDB.close();
                        cursor.close();
                        openSecondActivity(username, householdID);
                        return;
                    } else {
                        showPasswordIncorrectAlert();
                        return;
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (!isUsernameFound) {
            showUsernameNotFoundAlert();
        }
    }

    private void showLoginSuccessToast() {
        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
    }

    private void showUsernameNotFoundAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username Not Found")
                .setMessage("The username you entered was not found.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void showPasswordIncorrectAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Incorrect")
                .setMessage("The password you entered is incorrect.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfoDB.close();
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, InitialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void openSecondActivity(String username, int householdID) {
        Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("HOUSEHOLD_ID", householdID);
        startActivity(intent);
        finish();
    }

    /*public static String getUsername(){
        return username;
    }*/
}