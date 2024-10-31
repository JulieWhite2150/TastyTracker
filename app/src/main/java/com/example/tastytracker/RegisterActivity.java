package com.example.tastytracker;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText, householdIDEditText;
    Button registerButton, cancelButton;
    userInfoDBAdapter userInfoDB;
    public static String username;
    int householdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        householdIDEditText = findViewById(R.id.householdIDEditText);
        registerButton = findViewById(R.id.registerButton);

        userInfoDB = new userInfoDBAdapter(this);
        userInfoDB.open();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                householdID = getHouseholdIDFromString(householdIDEditText.getText().toString());


                if (username.isEmpty() || password.isEmpty()) {
                    showUsernamePasswordEmptyAlert();
                    return;
                }

                if(!Character.isLetter(username.charAt(0))){
                    showUsernameFirstCharacterAlert();
                    return;
                }

                // Check if username already exists
                if (ifUsernameExists(username)) {
                    showUsernameExistsAlert();
                    return;
                }

                if (householdID == -1){
                    showHouseholdIDHasNonDigitAlert();
                    return;
                }

                if (householdID != 0 && !householdIDExists(householdID)){
                    showHouseholdIDDoesNotExistAlert();
                    return;
                }

                if (householdID == 0){
                    householdID = getNewHouseHoldID();
                }


                // Register the user
                long result = userInfoDB.insertUser(householdID, username, password);
                if (result != -1) {

                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    openSecondActivity(username);
                } else {
                    showRegistrationFailAlert();
                }
            }
        });
    }

    private void showRegistrationFailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registration Failed")
                .setMessage("Registration failed, please try again.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
    private void showUsernamePasswordEmptyAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username/Password Empty")
                .setMessage("You must submit a username and a password")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void showUsernameFirstCharacterAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username must begin with letter")
                .setMessage("You must submit a username that begins with a letter of the alphabet.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void showUsernameExistsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username Exists")
                .setMessage("Username submitted already exists. Either pick a new username or login.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void showHouseholdIDHasNonDigitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Household ID Invalid")
                .setMessage("Household ID submitted has an invalid character. Household IDs consist of only digits.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void showHouseholdIDDoesNotExistAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Household ID Does Not Exist")
                .setMessage("The household ID you entered does not exist. Please double check and try again.")
                .setPositiveButton("OK", null)
                .create()
                .show();
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

    private int getNewHouseHoldID(){
        Cursor cursor = userInfoDB.getAllUsers();
        int IDIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_HOUSEHOLD_ID);
        int largestHHID = 0;

        if (cursor.moveToFirst()) {
            do {
                int storedID = cursor.getInt(IDIndex);
                if (storedID > largestHHID) {
                    largestHHID = storedID;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return largestHHID + 1;
    }

    private boolean householdIDExists(int enteredHouseholdID){
        Cursor cursor = userInfoDB.getAllUsers();
        int IDIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_HOUSEHOLD_ID);

        if (cursor.moveToFirst()) {
            do {
                int storedID = cursor.getInt(IDIndex);
                if (storedID == enteredHouseholdID) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }
    private int getHouseholdIDFromString(String householdIDString){
        if (householdIDString.isEmpty())
                return 0;

        int householdID = 0;

        for (int i = 0; i < householdIDString.length(); i++){
            if (!Character.isDigit(householdIDString.charAt(i))){
                return -1;
            }
        }

        return Integer.parseInt(householdIDString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfoDB.close();
    }

    private void openSecondActivity(String username) {
        Intent intent = new Intent(RegisterActivity.this, InventoryActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("HOUSEHOLD_ID", householdID);
        startActivity(intent);
        finish();
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, InitialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public static String getUsername(){
        return username;
    }

}