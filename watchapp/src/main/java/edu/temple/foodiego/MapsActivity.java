package edu.temple.foodiego;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.wear.widget.SwipeDismissFrameLayout;

import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.helpers.LocatorImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends WearableActivity implements OnMapReadyCallback {

    /**
     * Map is initialized when it"s fully loaded and ready to be used.
     *
     * @see #onMapReady(com.google.android.gms.maps.GoogleMap)
     */
    private GoogleMap map;

    static int permissionRequestCode = 12345;

    private Marker userMarker;

    private ArrayList<FoodieLocation> foodieLocations;
    private int updateCountDown = 0;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference locationRef = fbdb.getReference("location");
    private boolean listenerAdded = false;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Location userLocation = new Location("");
            if(userMarker != null)
            {
                userLocation.setLatitude(userMarker.getPosition().latitude);
                userLocation.setLongitude(userMarker.getPosition().longitude);
            }

            if(foodieLocations.size()>0)
            {
                for (FoodieLocation f: foodieLocations) {
                    f.getMarker().remove();
                }
                foodieLocations.clear();
            }

            String data = String.valueOf(snapshot.getValue());
            if(data.equals("null"))
            {
                Log.e("Locations", "" +data);
            }
            else
            {
                try {
                    JSONObject jodata = new JSONObject(data);
                    Log.e("Locations Number", jodata.length()+"" +data);
                    Iterator<String> keys = jodata.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        JSONObject jo = (JSONObject) jodata.get(key);
                        //convert the json obj into actual data.
                        String locationName = jo.getString("location_name");
                        locationName = FirebaseHelper.replaceCharAfterGet(locationName);
                        double locationLat = Double.parseDouble(jo.getString("location_lat"));
                        double locationLng = Double.parseDouble(jo.getString("location_long"));
                        double locationRating = Double.parseDouble(jo.getString("location_rating"));

                        FoodieLocation foodieLocation = new FoodieLocation(locationName, locationLat,locationLng,locationRating,key);
                        if(userLocation.distanceTo(foodieLocation.getLocation())<2000)
                        {
                            MarkerOptions markerOptions= new MarkerOptions();
                            markerOptions.position(new LatLng(locationLat,locationLng));
                            markerOptions.title(locationName);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_AZURE));
                            foodieLocation.setMarker(map.addMarker(markerOptions));
                            foodieLocations.add(foodieLocation);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Enables always on.
        setAmbientEnabled();

        setContentView(R.layout.activity_maps);

        final SwipeDismissFrameLayout swipeDismissRootFrameLayout =
                (SwipeDismissFrameLayout) findViewById(R.id.swipe_dismiss_root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Enables the Swipe-To-Dismiss Gesture via the root layout (SwipeDismissFrameLayout).
        // Swipe-To-Dismiss is a standard pattern in Wear for closing an app and needs to be
        // manually enabled for any Google Maps Activity. For more information, review our docs:
        // https://developer.android.com/training/wearables/ui/exit.html
        swipeDismissRootFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                // Hides view before exit to avoid stutter.
                layout.setVisibility(View.GONE);
                finish();
            }
        });

        // Adjusts margins to account for the system window insets when they become available.
        swipeDismissRootFrameLayout.setOnApplyWindowInsetsListener(
                new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                        insets = swipeDismissRootFrameLayout.onApplyWindowInsets(insets);

                        FrameLayout.LayoutParams params =
                                (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                        // Sets Wearable insets to FrameLayout container holding map as margins
                        params.setMargins(
                                insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                insets.getSystemWindowInsetBottom());
                        mapFrameLayout.setLayoutParams(params);

                        return insets;
                    }
                });

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        foodieLocations = new ArrayList<>();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        map = googleMap;

        // Inform user how to close app (Swipe-To-Close).
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // Adds a marker in Sydney, Australia and moves the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        Location loc = new Location("");
//        loc.setLatitude(39.9812);
//        loc.setLongitude(75.1497);
//
//        updateLocationWithMarker(loc);

        if (!hasGPSPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO}, permissionRequestCode);
        } else {
            loadLocation();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationRef.removeEventListener(valueEventListener);
        listenerAdded = false;
    }

    public void loadLocation() {
        for (int i = 0; i < foodieLocations.size(); i++) {
            Log.d("TEST", "onMapReady: location: " + foodieLocations.get(i).getName());
        }

        Location loc = getLastKnownLocation();

        if (loc == null) {
            Log.d("TESTING", "loadLocation: location is null, setting default location");
            loc = new Location("");
            LatLng defaultLoc = new LatLng(39.9812, -75.1497);
            loc.setLatitude(defaultLoc.latitude);
            loc.setLongitude(defaultLoc.longitude);
        }

        updateLocationWithMarker(loc);
    }

    public void updateLocationWithMarker(Location location) {
        Log.d("TESTING", "updateLocationWithMarker: called update location with marker");
        if (location != null && map != null) {
            Log.d("TESTING", "updateLocationWithMarker: successfully updating location with marker");
            LatLng locationCoords = new LatLng(location.getLatitude(), location.getLongitude());

            if (userMarker != null)
                userMarker.setPosition(locationCoords);
            else
                userMarker = map.addMarker(new MarkerOptions().position(locationCoords).title("You"));
            if(!listenerAdded)
            {
                locationRef.addValueEventListener(valueEventListener);
                listenerAdded = true;
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCoords, 15.0f));
            addNearbyLocationsToMap();
        }
    }

    public void addNearbyLocationsToMap() {
        if (updateCountDown == 0) {
            Location location = new Location("");
            location.setLongitude(userMarker.getPosition().longitude);
            location.setLatitude(userMarker.getPosition().latitude);
            FirebaseHelper.getNearbyLocations(MapsActivity.this, 2000.0,location);
            updateCountDown = 20;
        } else {
            updateCountDown--;
        }
    }

    private boolean hasGPSPermission() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadLocation();
        }
    }

    @SuppressLint("MissingPermission") // Already checked for it upon activity launch, don't need to again
    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}