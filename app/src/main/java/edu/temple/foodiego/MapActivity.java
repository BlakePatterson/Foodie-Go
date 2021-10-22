package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity {

    static int permissionRequestCode = 12345;

    public FoodieUser user;

    private MapFragment mapFragment;

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

        // Check to see if location permissions have been granted before loading any of the actual content
        if (!hasGPSPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
        } else {
            loadFragments();
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

    public boolean hasGPSPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadFragments();
        }
    }

    private void loadFragments() {
        if (!(getSupportFragmentManager().findFragmentById(R.id.mapFrameLayout) instanceof MapFragment)) {
            mapFragment = MapFragment.newInstance(user.getUsername(), user.getFirstname(), user.getLastname());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFrameLayout, mapFragment)
                    .commit();
        }
        else {
            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrameLayout);
        }
    }

}