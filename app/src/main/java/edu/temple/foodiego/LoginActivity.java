package edu.temple.foodiego;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

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

        String storedUsername = preferences.getString(getString(R.string.stored_username_key), "");
        if (!storedUsername.equals("")) {
            //The user is still logged in, so read the database for their full name & launch the map activity

            //Get a reference to the user field of the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("user");

            //Launch a loading dialog before contacting database
            ProgressDialog loadingDialog;
            loadingDialog = new ProgressDialog(LoginActivity.this);
            loadingDialog.setMessage("Contacting Servers...");
            loadingDialog.setTitle("Logging In");
            loadingDialog.setIndeterminate(false);
            loadingDialog.show();

            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "login: Error getting data", task.getException());

                        try {
                            //Close the loading dialog
                            loadingDialog.dismiss();
                        } catch (final IllegalArgumentException e) {
                            // Handle or log or ignore
                        } finally {
                            Toast.makeText(LoginActivity.this, "Unable to Contact Servers, Please Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        boolean foundUser = false;
                        try {
                            JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));
                            Iterator<String> keys = userData.keys();
                            while(keys.hasNext()) {
                                String key = keys.next();
                                if (userData.get(key) instanceof JSONObject) {
                                    Log.d(TAG, "login: username: " + ((JSONObject) userData.get(key)).get("username"));

                                    //Read the username and password from the database
                                    String db_username = (String) ((JSONObject) userData.get(key)).get("username");
                                    String db_password = (String) ((JSONObject) userData.get(key)).get("password");

                                    //If the  username matches
                                    if (storedUsername.equals(db_username)) {

                                        foundUser = true;

                                        try {
                                            //Close the loading dialog
                                            loadingDialog.dismiss();
                                        } catch (final IllegalArgumentException e) {
                                            // Handle or log or ignore
                                        } finally {
                                            //Read the firstname and lastname from the database
                                            String db_firstname = (String) ((JSONObject) userData.get(key)).get("firstname");
                                            String db_lastname = (String) ((JSONObject) userData.get(key)).get("lastname");

                                            //Launch Map Activity
                                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                                            Bundle intentBundle = new Bundle();
                                            intentBundle.putString(getString(R.string.username_bundle_key), storedUsername);
                                            intentBundle.putString(getString(R.string.firstname_bundle_key), db_firstname);
                                            intentBundle.putString(getString(R.string.lastname_bundle_key), db_lastname);
                                            intentBundle.putString(getString(R.string.key_bundle_key), key);
                                            intent.putExtras(intentBundle);
                                            startActivity(intent);
                                        }


                                    }
                                }
                            }
                            //The username was not found, so notify the user
                            if (!foundUser) {
                                try {
                                    //Close the loading dialog
                                    loadingDialog.dismiss();
                                } catch (final IllegalArgumentException e) {
                                    // Handle or log or ignore
                                } finally {
                                    Toast.makeText(LoginActivity.this, "Error Finding User Data, Please Try Again", Toast.LENGTH_LONG).show();
                                }
                            }

                        } catch (JSONException e) {
                            try {
                                //Close the loading dialog
                                loadingDialog.dismiss();
                            } catch (final IllegalArgumentException e2) {
                                // Handle or log or ignore
                            } finally {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Error Finding User Data, Please Try Again", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }
            });
        }

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

        //Get a reference to the user field of the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user");

        //Launch a loading dialog before contacting database
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(LoginActivity.this);
        loadingDialog.setMessage("Contacting Servers...");
        loadingDialog.setTitle("Logging In");
        loadingDialog.setIndeterminate(false);
        loadingDialog.show();

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "login: Error getting data", task.getException());

                    try {
                        //Close the loading dialog
                        loadingDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } finally {
                        Toast.makeText(LoginActivity.this, "Unable to Contact Servers, Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
//                    Log.d(TAG, "login: " + String.valueOf(task.getResult().getValue()));

                    boolean foundUser = false;

                    try {
                        JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));
                        Iterator<String> keys = userData.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            if (userData.get(key) instanceof JSONObject) {
                                Log.d(TAG, "login: username: " + ((JSONObject) userData.get(key)).get("username"));

                                //Read the username and password from the database
                                String db_username = (String) ((JSONObject) userData.get(key)).get("username");
                                String db_password = (String) ((JSONObject) userData.get(key)).get("password");

                                //If the  username matches
                                if (username.equals(db_username)) {

                                    foundUser = true;
                                    boolean incorrectPassword = false;

                                    try {
                                        //Close the loading dialog
                                        loadingDialog.dismiss();
                                    } catch (final IllegalArgumentException e) {
                                        // Handle or log or ignore
                                    } finally {
                                        if (password.equals(db_password)) {
                                            //The correct credentials have been provided so proceed to login

                                            //Read the firstname and lastname from the database
                                            String db_firstname = (String) ((JSONObject) userData.get(key)).get("firstname");
                                            String db_lastname = (String) ((JSONObject) userData.get(key)).get("lastname");

                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString(getString(R.string.stored_username_key), username);
                                            editor.putString(username + getString(R.string.stored_firstname_key), db_firstname);
                                            editor.putString(username + getString(R.string.stored_lastname_key), db_lastname);
                                            editor.putString(getString(R.string.stored_key_key), key);
                                            editor.apply();

                                            Log.d(TAG, "onComplete: successfully logged in with username: " + db_username + "; password: " + password);

                                            //Launch Map Activity
                                            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                                            Bundle intentBundle = new Bundle();
                                            intentBundle.putString(getString(R.string.username_bundle_key), username);
                                            intentBundle.putString(getString(R.string.firstname_bundle_key), db_firstname);
                                            intentBundle.putString(getString(R.string.lastname_bundle_key), db_lastname);
                                            intentBundle.putString(getString(R.string.key_bundle_key), key);
                                            intent.putExtras(intentBundle);
                                            startActivity(intent);

                                        } else {
                                            //The user exists but the password provided was incorrect, so notify the user
                                            Toast.makeText(LoginActivity.this, "Incorrect Password, Try Again", Toast.LENGTH_LONG).show();
                                            incorrectPassword = true;
                                        }
                                    }

                                }
                            }
                        }

                        //The username was not found, so notify the user
                        if (!foundUser) {
                            try {
                                //Close the loading dialog
                                loadingDialog.dismiss();
                            } catch (final IllegalArgumentException e) {
                                // Handle or log or ignore
                            } finally {
                                Toast.makeText(LoginActivity.this, "Username Does Not Exist, Please Create An Account", Toast.LENGTH_LONG).show();
                            }

                        }

                    } catch (JSONException e) {
                        try {
                            //Close the loading dialog
                            loadingDialog.dismiss();
                        } catch (final IllegalArgumentException e2) {
                            // Handle or log or ignore
                        } finally {
                            e.printStackTrace();

                            Toast.makeText(LoginActivity.this, "Error Finding User Data, Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    }

                }
            }
        });


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
                        EditText confirmPasswordEditText = d.findViewById(R.id.confirmPasswordCreateAccountEditText);
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
                        } else if (username.contains(";") || password.contains(";") || firstname.contains(";") || lastname.contains(";")) {
                            Toast.makeText(LoginActivity.this, "Invalid Characters Used, Please Try Again", Toast.LENGTH_LONG).show();
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

        //Launch a loading dialog before contacting database
        ProgressDialog loadingDialog;
        loadingDialog = new ProgressDialog(LoginActivity.this);
        loadingDialog.setMessage("Contacting Servers...");
        loadingDialog.setTitle("Creating Account");
        loadingDialog.setIndeterminate(false);
        loadingDialog.show();

        //Get a reference to the user field of the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user");

        //Add a new entry to the user list and get the auto generated key for it
        DatabaseReference newUserRef = userRef.push();
        String key = newUserRef.getKey();

        //read from the database to see if the username is already taken
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "createAccount: Error getting data", task.getException());

                    try {
                        //Close the loading dialog
                        loadingDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        // Handle or log or ignore
                    } finally {
                        Toast.makeText(LoginActivity.this, "Unable to contact servers, try again later", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    boolean foundUser = false;

                    try {
                        JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));
                        Iterator<String> keys = userData.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            if (userData.get(key) instanceof JSONObject) {
                                Log.d(TAG, "createAccount: db_username: " + ((JSONObject) userData.get(key)).get("username"));

                                //Read the username and password from the database
                                String db_username = (String) ((JSONObject) userData.get(key)).get("username");

                                //If the  username matches
                                if (username.equals(db_username)) {
                                    foundUser = true;
                                    break;
                                }
                            }
                        }

                        if (foundUser) {
                            //The username already exists, so notify the user to use a different username

                            try {
                                //Close the loading dialog
                                loadingDialog.dismiss();
                            } catch (final IllegalArgumentException e) {
                                // Handle or log or ignore
                            } finally {
                                Toast.makeText(LoginActivity.this, "Username Already Exists, Please Try Another Username", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            //The username does not exist yet, so proceed to create the account

                            //Put the user data in a hash map for easy insertion
                            HashMap<String, String> userDataMap = new HashMap<>();
                            userDataMap.put("username", username);
                            userDataMap.put("password", password);
                            userDataMap.put("firstname", firstname);
                            userDataMap.put("lastname", lastname);

                            //Save the user data on the database
                            userRef.child(key).setValue(userDataMap);

                            //Save the user data in shared preferences
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(getString(R.string.stored_username_key), username);
                            editor.putString(username + getString(R.string.stored_firstname_key), firstname);
                            editor.putString(username + getString(R.string.stored_lastname_key), lastname);
                            editor.apply();

                            try {
                                //Close the loading dialog
                                loadingDialog.dismiss();
                            } catch (final IllegalArgumentException e) {
                                // Handle or log or ignore
                            } finally {
                                //Launch Map Activity
                                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                                Bundle intentBundle = new Bundle();
                                intentBundle.putString(getString(R.string.username_bundle_key), username);
                                intentBundle.putString(getString(R.string.firstname_bundle_key), firstname);
                                intentBundle.putString(getString(R.string.lastname_bundle_key), lastname);
                                intent.putExtras(intentBundle);
                                startActivity(intent);
                            }

                        }

                    } catch (JSONException e) {
                        try {
                            //Close the loading dialog
                            loadingDialog.dismiss();
                        } catch (final IllegalArgumentException e2) {
                            // Handle or log or ignore
                        } finally {
                            e.printStackTrace();

                            Toast.makeText(LoginActivity.this, "Error Finding User Data, Please Try Again", Toast.LENGTH_LONG).show();
                        }

                    }

                }
            }
        });

    }

}