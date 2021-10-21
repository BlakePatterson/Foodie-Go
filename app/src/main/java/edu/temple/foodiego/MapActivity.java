package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity {

    public FoodieUser user;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        preferences = getSharedPreferences(getString(R.string.credentials_preferences), MODE_PRIVATE);

        Bundle startIntent = getIntent().getExtras();
        if(startIntent != null) {
            String username = startIntent.getString(getString(R.string.username_bundle_key));
            String firstname = startIntent.getString(getString(R.string.firstname_bundle_key));
            String lastname = startIntent.getString(getString(R.string.lastname_bundle_key));
            Log.d(TAG, "onCreate: MapActivity launched with username: " + username + "; firstname: " + firstname + "; lastname: " + lastname);
            user = new FoodieUser(username, firstname, lastname);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add the convoy options menu to the convoy activity screen
        getMenuInflater().inflate(R.menu.map_activity_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logoutMenuItem) {
            //logout button was clicked
            logout();
            return true;
        }
        return false;
    }

    private void logout() {
        //null out the user field
        user = null;

        //remove the username from shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.stored_username_key), "");
        editor.apply();

        //return to the login activity
        finish();
    }

}