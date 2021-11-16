package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MapFragment extends Fragment {

    public static final String USERNAME_PARAM_KEY = "mapParam1";
    public static final String FIRSTNAME_PARAM_KEY = "mapParam2";
    public static final String LASTNAME_PARAM_KEY = "mapParam3";
    public static final String KEY_PARAM_KEY = "mapParam4";

    private GoogleMap map;
    private Marker userMarker;

    private Context parentActivity;

    private FoodieUser user;

    //foodieLocations and marker onclick behavior.
    private ArrayList<FoodieLocation> foodieLocations;
    private int updateCountDown = 0;

    private FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private DatabaseReference locationRef = fbdb.getReference("location");
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Location userLocation = new Location("");
            if(userMarker != null)
            {
                userLocation.setLatitude(userMarker.getPosition().latitude);
                userLocation.setLongitude(userMarker.getPosition().longitude);
            }

            for (FoodieLocation f: foodieLocations) {
                f.getMarker().remove();
            }
            foodieLocations.clear();
            String data = String.valueOf(snapshot.getValue());

            try {
                JSONObject jodata = new JSONObject(data);
                Log.e("Locations Number", jodata.length()+"");
                Iterator<String> keys = jodata.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    JSONObject jo = (JSONObject) jodata.get(key);
                    //convert the json obj into actual data.
                    String locationName = jo.getString("name");
                    locationName = locationName.replace("_"," ");
                    locationName = locationName.replace("^","'");
                    locationName = locationName.replace("*",",");
                    double locationLat = Double.parseDouble(jo.getString("latitude"));
                    double locationLng = Double.parseDouble(jo.getString("longitude"));
                    double locationRating = Double.parseDouble(jo.getString("rating"));

                    FoodieLocation foodieLocation = new FoodieLocation(locationName, locationLat,locationLng,locationRating);
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

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };



    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    if (!marker.getTitle().equals(user.getFirstname() + " " + user.getLastname())) {
                        FoodieLocation clickedLocation = new FoodieLocation("Not Found", 0, 0, 0);
                        for (int i = 0; i < foodieLocations.size(); i++) {
                            if (foodieLocations.get(i).getName().equals(marker.getTitle())) {
                                clickedLocation = foodieLocations.get(i);
                                break;
                            }
                        }
                        onClickLocation(clickedLocation);
                    }
                    return false;
                }
            });
            if(foodieLocations == null)
            {
                foodieLocations = new ArrayList<>();
            }


        }
    };

    public static MapFragment newInstance(String username, String firstname, String lastname, String key) {
        Bundle args = new Bundle();
        args.putSerializable(USERNAME_PARAM_KEY, username);
        args.putSerializable(FIRSTNAME_PARAM_KEY, firstname);
        args.putSerializable(LASTNAME_PARAM_KEY, lastname);
        args.putString(KEY_PARAM_KEY, key);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null){
            user = new FoodieUser("", "", "", "");
        } else {
            String username = (String) getArguments().getSerializable(USERNAME_PARAM_KEY);
            String firstname = (String) getArguments().getSerializable(FIRSTNAME_PARAM_KEY);
            String lastname = (String) getArguments().getSerializable(LASTNAME_PARAM_KEY);
            String key = getArguments().getString(KEY_PARAM_KEY);
            user = new FoodieUser(username, firstname, lastname, key);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //if (context instanceof MapFragmentInterface) parentActivity = context;
        if(context == null)
        {Log.e("fragment context", "is null");}

        if (context instanceof MapActivity) parentActivity = context;
        else throw new RuntimeException("Activity must implement MapFragmentInterface in order to use MapFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void updateLocationWithMarker(Location location) {
        if (location != null && map != null) {
            LatLng locationCoords = new LatLng(location.getLatitude(), location.getLongitude());

            if (userMarker != null)
                userMarker.setPosition(locationCoords);
            else
                userMarker = map.addMarker(new MarkerOptions().position(locationCoords).title(user.getFirstname() + " " + user.getLastname()));
            locationRef.addValueEventListener(valueEventListener);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCoords, 15.0f));
            addNearByLocationsToMap();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationRef.removeEventListener(valueEventListener);
    }

    interface MapFragmentInterface {
        void openLocationDetailView(FoodieLocation location);
    }

    // addNearbyLocationsToMap
    // @No param
    // call api to get location from google api every 20 updates of location.
    public void addNearByLocationsToMap() {
        if (updateCountDown == 0) {
            Location location = new Location("");
            location.setLongitude(userMarker.getPosition().longitude);
            location.setLatitude(userMarker.getPosition().latitude);
            FirebaseHelper.getNearByLocations(getContext(), 2000.0,location);
            updateCountDown = 20;
        } else {
            updateCountDown--;
        }
    }

    //Marker onclick behavior, tell map activity to handle it.
    public void onClickLocation(FoodieLocation location) {
        ((MapFragmentInterface) this.parentActivity).openLocationDetailView(location);
    }

    public void startRouteToLocation()
    {
    }


}