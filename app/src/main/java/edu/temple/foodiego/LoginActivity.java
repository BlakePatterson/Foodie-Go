package edu.temple.foodiego;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    public static String defaultName = "";
    public static String defaultUsername = "";

    private EditText usernameEditText;
    private EditText passwordEditText;

    private String username;
    private String password;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        preferences = getSharedPreferences(getString(R.string.credentials_preferences), MODE_PRIVATE);

//        String storedUsername = preferences.getString(getString(R.string.stored_username_key), defaultUsername);
//        // If the username is stored, meaning the user is still logged in
//        if (!storedUsername.equals(defaultUsername)) {
//            String storedFirstname = preferences.getString(storedUsername + getString(R.string.stored_firstname_key), defaultName);
//            String storedLastname = preferences.getString(storedUsername + getString(R.string.stored_lastname_key), defaultName);
//
//            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
//            Bundle intentBundle = new Bundle();
//            intentBundle.putString(getString(R.string.firstNameBundleKey), storedFirstName);
//            intentBundle.putString(getString(R.string.lastNameBundleKey), storedLastName);
//            intentBundle.putString(getString(R.string.username_bundle_key), storedUsername);
//            intentBundle.putString(getString(R.string.session_key_bundle_key), storedSessionKey);
//            intent.putExtras(intentBundle);
//            startActivity(intent);
//        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                login(username, password);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateAccountDialog();
            }
        });
    }

    private void login(String username, String password) {

        Log.d(TAG, "login: logging in with username: " + username + "; password: " + password);

        //TODO:
        // 1. Contact Firebase to login here
        // 2. If firstname & lastname are already stored, just store username in shared preferences
        // 2.5. Otherwise, read firstname and lastname from Firebase and store all 3 fields in shared preferences
        // 3. Launch mp activity

    }

    private void openCreateAccountDialog() {
        Log.d(TAG, "openCreateAccountDialog: opening create account dialog");

        new AlertDialog.Builder(LoginActivity.this)
                .setView(R.layout.create_account_dialog)
                .setTitle("Create Account")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Dialog d = (Dialog) dialogInterface;

                        EditText usernameEditText = d.findViewById(R.id.usernameCreateAccountEditText);
                        EditText passwordEditText = d.findViewById(R.id.passwordCreateAccountEditText);
                        EditText confirmPasswordEditText = d.findViewById(R.id.passwordCreateAccountEditText);
                        EditText firstnameEditText = d.findViewById(R.id.firstNameCreateAccountEditText);
                        EditText lastnameEditText = d.findViewById(R.id.lastNameCreateAccountEditText);

                        username = usernameEditText.getText().toString();
                        password = passwordEditText.getText().toString();
                        String confirmPassword = confirmPasswordEditText.getText().toString();
                        String firstname = firstnameEditText.getText().toString();
                        String lastname = lastnameEditText.getText().toString();

                        if (password.equals(confirmPassword)) {
                            //Create account
                            createAccount(username, password, firstname, lastname);
                        } else {
                            Toast.makeText(LoginActivity.this, "Password & Confirm Password Must Match, Please Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close dialog
                    }
                }).show();
    }

    private void createAccount(String username, String password, String firstname, String lastname) {

        Log.d(TAG, "createAccount: creating a new account with username: " + username + "; password: " + password + "; fistname: " + firstname + "; lastname: " + lastname);

        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(LoginActivity.this);
        loadingDialog.setMessage("Contacting Servers...");
        loadingDialog.setTitle("Creating Account");
        loadingDialog.setIndeterminate(false);
        loadingDialog.show();


        //TODO:
        // 1. Contact Firebase to create account here
        // 2. Save username, firstname, and lastname in shared preferences
        // 3. Launch map activity



        loadingDialog.dismiss();

    }

}