package com.example.tastytracker;

import android.os.Bundle;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText usernameEditText, passwordEditText, householdIDEditText;
    Button registerButton;
    userInfoDBAdapter userInfoDB;
    String enteredUsername, enteredPassword, permissions;
    int enteredHouseholdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        householdIDEditText = findViewById(R.id.householdIDEditText);
        ImageButton questionButton = findViewById(R.id.question);
        questionButton.setOnClickListener(v -> {
            // Create and display the AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("What is a Household ID?")
                    .setMessage("The Household ID links your account to your household's shared profile. \n\nIf you're the first member, don't enter one and we'll generate one for you. \n\nIf you're joining, ask another household member for the household ID and enter it below.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        registerButton = findViewById(R.id.registerButton);

        userInfoDB = new userInfoDBAdapter(this);
        userInfoDB.open();

        //Tries to register the new user but handles a series of errors
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredUsername = usernameEditText.getText().toString();
                enteredPassword = passwordEditText.getText().toString();
                enteredHouseholdID = getHouseholdIDFromString(householdIDEditText.getText().toString());

                //If user didn't enter text for username/password, show error
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    showUsernamePasswordEmptyAlert();
                    return;
                }

                //If first character of username isn't a letter, show error
                //User cannot use non-letter first char to make dbs consistent
                if(!Character.isLetter(enteredUsername.charAt(0))){
                    showUsernameFirstCharacterAlert();
                    return;
                }

                // Check if username already exists, if so, show error
                if (userInfoDB.getUser(enteredUsername) != null) {
                    showUsernameExistsAlert();
                    return;
                }

                //If user entered a household ID that is not all digits, show error
                if (enteredHouseholdID == -1){
                    showHouseholdIDHasNonDigitAlert();
                    return;
                }

                //If household ID entered does not exist, show error.
                //Currently no way to delete a household, so all household ID's would be less than the next smallest HHID
                if (enteredHouseholdID != 0 && enteredHouseholdID >= getNewHouseHoldID()){
                    showHouseholdIDDoesNotExistAlert();
                    return;
                }

                //If the user didn't enter a household ID, they need to have one generated
                if (enteredHouseholdID == 0){
                    enteredHouseholdID = getNewHouseHoldID();
                    permissions = "HH"; //the user is the first member of their house, they have head of household privileges
                }else{
                    permissions = "MWP"; //user is not first member of their house, they have MWP privileges.
                }

                // Register the user
                long result = userInfoDB.insertUser(enteredHouseholdID, enteredUsername, enteredPassword, permissions);
                //If registration successful, tell user and move to the inventory activity
                if (result != -1) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, InventoryActivity.class);
                    UserSession.init(new User(enteredUsername, enteredPassword, enteredHouseholdID, permissions));
                    intent.putExtra("HOUSEHOLD_ID", enteredHouseholdID);
                    startActivity(intent);
                    finish();
                }
                //else registration failed, show error
                else {
                    showRegistrationFailAlert();
                }
            }
        });
    }

    //Tell the user that registration into db failed, ack is needed so AlertDialog used
    private void showRegistrationFailAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Registration Failed")
                .setMessage("Registration failed, please try again.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that they need to enter username and password, ack is needed so AlertDialog used
    private void showUsernamePasswordEmptyAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username/Password Empty")
                .setMessage("You must submit a username and a password")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that the username must start with letter, ack is needed so AlertDialog used
    private void showUsernameFirstCharacterAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username must begin with letter")
                .setMessage("You must submit a username that begins with a letter of the alphabet.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that entered username is taken, ack is needed so AlertDialog used
    private void showUsernameExistsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Username Exists")
                .setMessage("Username submitted already exists. Either pick a new username or login.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that household ID must be digit, ack is needed so AlertDialog used
    private void showHouseholdIDHasNonDigitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Household ID Invalid")
                .setMessage("Household ID submitted has an invalid character. Household IDs consist of only digits.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that household id entered does not exist, ack is needed so AlertDialog used
    private void showHouseholdIDDoesNotExistAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Household ID Does Not Exist")
                .setMessage("The household ID you entered does not exist. Please double check and try again.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //If user needs a household ID (didn't give one) give user the next sequential, available HHID
    private int getNewHouseHoldID(){
        Cursor cursor = userInfoDB.getAllUsers();
        int IDIndex = cursor.getColumnIndex(userInfoDBAdapter.KEY_HOUSEHOLD_ID);
        int largestHHID = 0;

        //Move through the users and find the highest HH ID value
        if (cursor.moveToFirst()) {
            do {
                int storedID = cursor.getInt(IDIndex);
                if (storedID > largestHHID) {
                    largestHHID = storedID;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        //Set new user's HHID to largest current value + 1
        return largestHHID + 1;
    }

    //Method to take a string and determine if each char is a digit,
    // if it is all digits, add each to figure out its int value (accounting for tens, hundreds, etc.)
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

    //Used by the xml layout to move the user back to the initial screen
    public void goBack(View view) {
        Intent intent = new Intent(this, InitialActivity.class);
        //UserSession.init(enteredUsername);
        startActivity(intent);
        finish();
    }
}