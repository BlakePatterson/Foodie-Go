package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements MapFragment.MapFragmentInterface, ForegroundLocationService.LocationServiceInterface,
        FirebaseHelper.GetFriendsResponse, FirebaseHelper.GetFriendsReviewsResponse {

    static int permissionRequestCode = 12345;

    public FoodieUser user;

    private MapFragment mapFragment;

    private SharedPreferences preferences;

    ForegroundLocationService locationService;

    Location userLocation;
    Button recommendationButton;
    ArrayList<FoodieUser> friends; //index 0 is the user, friends start at 1
    ArrayList<ArrayList<FoodieReview>> friendsReviews;
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
            Log.d(TAG, "onCreate: MapActivity launched with username: " + username + "; firstname: " + firstname + "; lastname: " + lastname + "; key: " + key);
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
        if(user != null){
            FirebaseHelper helper = FirebaseHelper.getInstance(this);
            helper.getFriends(user, MapActivity.this);
            recommendationButton = findViewById(R.id.recommendButton);
            recommendationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getRecommendation();
                }
            });
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
        } else if (id == R.id.addFriendMenuItem) {
            //add friend button was clicked
            openAddFriendDialog();
            return true;
        } else if (id == R.id.socialFeedMenuItem) {
            //Social Feed Activity Button was clicked
            openSocialFeedView();
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
        Intent intent = new Intent(MapActivity.this, LocationDetailActivity.class);
        intent.putExtra(getString(R.string.locationDetailNameKey), location.getName());
        intent.putExtra(getString(R.string.locationDetailLatKey), location.getLocation().getLatitude());
        intent.putExtra(getString(R.string.locationDetailLongKey), location.getLocation().getLongitude());
        intent.putExtra(getString(R.string.locationDetailRatingKey), location.getRating());
        intent.putExtra("location_key", location.getKey());

        intent.putExtra(getString(R.string.username_bundle_key), user.getUsername());
        intent.putExtra(getString(R.string.firstname_bundle_key), user.getFirstname());
        intent.putExtra(getString(R.string.lastname_bundle_key), user.getLastname());
        intent.putExtra(getString(R.string.key_bundle_key), user.getKey());

        if(userLocation!=null)
        intent.putExtra("userLocation",userLocation);

        startActivity(intent);
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

    public void openAddFriendDialog(){
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
                        addFriend(new FoodieUser(inputUsername, null, null, null));
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

    public void addFriend(FoodieUser user){
        FirebaseHelper helper = FirebaseHelper.getInstance(this);
        helper.addFriend(user);
    }

    public void openSocialFeedView() {
        Intent intent = new Intent(MapActivity.this, SocialFeedActivity.class);

        intent.putExtra(getString(R.string.username_bundle_key), user.getUsername());
        intent.putExtra(getString(R.string.firstname_bundle_key), user.getFirstname());
        intent.putExtra(getString(R.string.lastname_bundle_key), user.getLastname());
        intent.putExtra(getString(R.string.key_bundle_key), user.getKey());

        startActivity(intent);
    }

    //The following method(s) are for communicating information from the ForegroundLocationService
    @Override
    public void updateLocation(Location location) {
        Log.d(TAG, "updateLocation: location update received in MapActivity: " + location.toString());
        mapFragment.updateLocationWithMarker(location);
        //send user location to detailActivity
        userLocation = location;
        Intent intent = new Intent();
        intent.setAction("edu.temple.foodiego.userlocation");
        intent.putExtra("userLocation",location);
        sendBroadcast(intent);
    }
    public void getRecommendation(){
        if(friendsReviews == null){
            Toast.makeText(MapActivity.this, "Still preparing database data, please try again in a moment.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "getRecommendation: friends' reviews not received yet");
        }else{
            Log.d(TAG, "getRecommendation: getting recommendation");
            ArrayList<FoodieReview> userReviews = friendsReviews.get(0);
            if(userReviews.size() == 0){
                Toast.makeText(MapActivity.this, "Please leave a review before asking for a recommendation", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<Double> friendSimilarities = new ArrayList<>();
            for(int i = 1; i < friendsReviews.size(); i++){
                //for each friend, find out which locations they have in common
                ArrayList<FoodieReview> currentFriendReviews = friendsReviews.get(i);
                if(currentFriendReviews.size() == 0){
                    //current friend has no reviews, so there's no similarity
                    friendSimilarities.add(0.0);
                    continue;
                }
                ArrayList<Double> userMatch = new ArrayList<>();
                ArrayList<Double> friendMatch = new ArrayList<>();
                for(int j = 0; j < userReviews.size(); i++){
                    String currentUserReviewLocation = userReviews.get(j).getLocation().getName();
                    for(int k = 0; k < currentFriendReviews.size(); k++){
                        if(currentUserReviewLocation.equals(currentFriendReviews.get(k).getLocation().getName())){
                            userMatch.add(userReviews.get(j).getRating());
                            friendMatch.add(currentFriendReviews.get(k).getRating());
                            break;
                        }
                    }
                }
                if(userMatch.size() == 0){
                    //current friend has no overlapping reviews, so no similarity
                    friendSimilarities.add(0.0);
                    continue;
                }
                //perform cosine similarity
                friendSimilarities.add(cosineSimilarity((Double[]) userMatch.toArray(), (Double[]) friendMatch.toArray()));
            }
            //launch LocationDetailActivity for the top recommended location
        }
    }
    public double cosineSimilarity(Double[] vectorA, Double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    @Override
    public void getFriendsResult(ArrayList<FoodieUser> friends) {
        this.friends = new ArrayList<>();
        this.friends.add(user);
        this.friends.addAll(friends);
        FirebaseHelper helper = FirebaseHelper.getInstance(MapActivity.this);
        helper.getFriendsReviews(this.friends, MapActivity.this);
    }
    @Override
    public void getFriendsReviewsResult(ArrayList<ArrayList<FoodieReview>> reviews) {
        this.friendsReviews = reviews;
    }
}