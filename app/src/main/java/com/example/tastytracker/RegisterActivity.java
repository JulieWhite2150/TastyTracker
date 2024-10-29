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

public class RegisterActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText, HouseholdIDEditText;
    Button registerButton, cancelButton;
    userInfoDBAdapter userInfoDB;
    public static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        userInfoDB = new userInfoDBAdapter(this);
        userInfoDB.open();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Username and password cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Character.isLetter(username.charAt(0))){
                    Toast.makeText(RegisterActivity.this, "Username must start with a letter", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if username already exists
                if (ifUsernameExists(username)) {
                    Toast.makeText(RegisterActivity.this, "Username already exists. Please choose a different username.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register the user
                long result = userInfoDB.insertUser(username, password);
                if (result != -1) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    openSecondActivity(username);
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to register user!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean ifUsernameExists(String username) {
        Cursor cursor = userInfoDB.getAllUsers();
        boolean exists = false;
        int usernameIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_USERNAME);

        if (cursor.moveToFirst()) {
            do {
                String storedUsername = cursor.getString(usernameIndex);
                if (storedUsername != null && storedUsername.equals(username)) {
                    exists = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exists;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfoDB.close();
    }

    private void openSecondActivity(String username) {
        Intent intent = new Intent(RegisterActivity.this, InventoryActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }

    public static String getUsername(){
        return username;
    }

}