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

        if (cursor.moveToFirst()) {
            do {
                String storedUsername = cursor.getString(usernameIndex);
                String storedPassword = cursor.getString(passwordIndex);

                if (storedUsername != null && storedUsername.equals(username)) {
                    isUsernameFound = true;
                    if (storedPassword != null && storedPassword.equals(password)) {
                        // Password is correct
                        showLoginSuccessToast();
                        Log.d("END OF MAIN", "~~~~~~~~~~~~~~~~~~~~~~~~~");
                        userInfoDB.close();
                        cursor.close();
                        openSecondActivity(username);
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
            showUsernameNotFoundToast();
        }
    }



    private void showLoginSuccessToast() {
        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
    }

    private void showUsernameNotFoundToast() {
        Toast.makeText(LoginActivity.this, "Username not found!", Toast.LENGTH_SHORT).show();
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

    private void openSecondActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }

    /*public static String getUsername(){
        return username;
    }*/
}