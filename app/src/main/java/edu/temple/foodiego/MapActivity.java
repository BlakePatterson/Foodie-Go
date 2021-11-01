package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity implements MapFragment.MapFragmentInterface, ForegroundLocationService.LocationServiceInterface {

    static int permissionRequestCode = 12345;

    public FoodieUser user;

    private MapFragment mapFragment;

    private SharedPreferences preferences;

    ForegroundLocationService locationService;

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
            String key = startIntent.getString(getString(R.string.key_bundle_key));
            Log.d(TAG, "onCreate: MapActivity launched with username: " + username + "; firstname: " + firstname + "; lastname: " + lastname + "key: " + key);
            user = new FoodieUser(username, firstname, lastname, key);
        }

        createNotificationChannel("Foodie Go Location Updates");

        // Check to see if location permissions have been granted before loading any of the actual content
        if (!hasGPSPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
        } else {
            loadFragments();
            startLocationService();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
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
        }else if(id == R.id.addFriendMenuItem){
            //add friend button was clicked
            return true;
        }
        return false;
    }

    private void createNotificationChannel(String channelName) {
        NotificationChannel notificationChannel = new NotificationChannel(
                getString(R.string.foodie_go_location_updates_notif_channel_id)
                , channelName
                , NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationChannel.setDescription("Notifications to display when Foodie Go is reading location data.");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void logout() {
        //null out the user field
        user = null;

        //remove the username from shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.stored_username_key), "");
        editor.putString(getString(R.string.stored_key_key), "");
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
            startLocationService();
        }
    }

    private void loadFragments() {
        if (!(getSupportFragmentManager().findFragmentById(R.id.mapFrameLayout) instanceof MapFragment)) {
            mapFragment = MapFragment.newInstance(user.getUsername(), user.getFirstname(), user.getLastname(), user.getKey());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mapFrameLayout, mapFragment)
                    .commit();
        }
        else {
            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrameLayout);
        }
    }


    //The following method(s) are for communicating information from the MapFragment
    @Override
    public void openLocationDetailView(FoodieLocation location) {

    }


    //The following are method(s) / object(s) for setting up the ForegroundLocationService
    private final ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationService = ((ForegroundLocationService.LocalBinder) service).getService();
            locationService.registerActivity(MapActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
        }
    };

    public void startLocationService() {
        if (locationService == null) {
            Intent serviceIntent = new Intent(this, ForegroundLocationService.class);
            startService(serviceIntent);
            bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void stopLocationService() {
        if (locationService != null) {
            Intent serviceIntent = new Intent(this, ForegroundLocationService.class);
            unbindService(locationServiceConnection);
            stopService(serviceIntent);
        }
    }

    private boolean isLocationServiceRunning(){
        boolean serviceRunning = false;
        String serviceName = ForegroundLocationService.class.getName();
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = i.next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;

                if(runningServiceInfo.foreground)
                {
                    //service run in foreground
                }
            }
        }
        return serviceRunning;
    }
    private void openAddFriendDialog(){
        new AlertDialog.Builder(MapActivity.this).setView(R.layout.dialog_add_friend)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog d = (Dialog) dialogInterface;
                        EditText inputUsernameField = d.findViewById(R.id.addFriendUsernameField);
                        String inputUsername = inputUsernameField.getText().toString();
                        if(inputUsername.equals("")){
                            Log.d(TAG, "openAddFriendDialog: no username entered");
                            Toast.makeText(MapActivity.this, "Please enter the username of your friend", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //get reference to the user's friends list
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = db.getReference("user");
                        final String[] friendKey = new String[1];
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    //iterate over users to find requested username?
                                    //super inefficient, but not sure how to avoid without significant restructuring
                                    try{
                                        JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));
                                        Iterator<String> keys = userData.keys();
                                        while(keys.hasNext()){
                                            String key = keys.next();
                                            if(userData.get(key) instanceof JSONObject){
                                                String dbUsername = (String) ((JSONObject) userData.get(key)).get("username");
                                                if(dbUsername.equals(inputUsername)){
                                                    friendKey[0] = key;
                                                    break;
                                                }
                                            }
                                        }
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    Log.d(TAG, "openAddFriendDialog: error getting user data");
                                    Toast.makeText(MapActivity.this, "Error contacting server. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        DatabaseReference friendsRef = userRef
                                .child(user.getKey())
                                .child("friends");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    //The following method(s) are for communicating information from the ForegroundLocationService
    @Override
    public void updateLocation(Location location) {
        Log.d(TAG, "updateLocation: location update received in MapActivity: " + location.toString());
        mapFragment.updateLocationWithMarker(location);
    }
}