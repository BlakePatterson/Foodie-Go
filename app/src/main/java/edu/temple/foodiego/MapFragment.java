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

    private GoogleMap map;
    private Marker userMarker;

    private Context parentActivity;

    private FoodieUser user;

    //foodieLocations and marker onclick behavior.
    private final String NameString = "name";
    private final String LatString = "latitude";
    private final String LngString = "longitude";
    private final String RatingString = "rating";
    private final double DISTANCE = 320;
    private ArrayList<FoodieLocation> foodieLocations;

    FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    DatabaseReference locationRef = fbdb.getReference("location");
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (FoodieLocation f: foodieLocations) {
                f.getMarker().remove();
            }
            foodieLocations.clear();
            String data = String.valueOf(snapshot.getValue());
            Log.e("data on change", data);
            try {
                JSONObject jodata = new JSONObject(data);
                Iterator<String> keys = jodata.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    JSONObject jo = (JSONObject) jodata.get(key);

                    //convert the json obj into actual data.
                    String locationName = jo.getString(NameString);
                    double locationLat = Double.parseDouble(jo.getString(LatString));
                    double locationLng = Double.parseDouble(jo.getString(LngString));
                    double locationRating = Double.parseDouble(jo.getString(RatingString));

                    FoodieLocation foodieLocation = new FoodieLocation(locationName, locationLat,locationLng,locationRating);
                    foodieLocation.setMarker(map.addMarker(new MarkerOptions().position(new LatLng(locationLat,locationLng)).title(locationName)));
                    foodieLocations.add(foodieLocation);
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
                    onClickLocation(marker.getTitle());
                    return false;
                }
            });
            if(foodieLocations == null)
            {
                foodieLocations = new ArrayList<>();
            }
            locationRef.addValueEventListener(valueEventListener);
        }
    };

    public static MapFragment newInstance(String username, String firstname, String lastname) {
        Bundle args = new Bundle();
        args.putSerializable(USERNAME_PARAM_KEY, username);
        args.putSerializable(FIRSTNAME_PARAM_KEY, firstname);
        args.putSerializable(LASTNAME_PARAM_KEY, lastname);
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null){
            user = new FoodieUser("", "", "");
        } else {
            String username = (String) getArguments().getSerializable(USERNAME_PARAM_KEY);
            String firstname = (String) getArguments().getSerializable(FIRSTNAME_PARAM_KEY);
            String lastname = (String) getArguments().getSerializable(LASTNAME_PARAM_KEY);

            user = new FoodieUser(username, firstname, lastname);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MapFragmentInterface) parentActivity = context;
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

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCoords, 17.0f));
            addNearByLocationsToMap();
        }
    }

    interface MapFragmentInterface {
        void openLocationDetailView(FoodieLocation location);
    }

    // addNearbyLocationsToMap
    // @No param
    // get the location of user then calculate the distance to the foodielocation.
    // If distance is less than the range
    // make it visible
    // else make it invisible
    public void addNearByLocationsToMap() {
        //load user location
        //TODO: replace the fake user data
        Location userLocation = new Location("user");
        userLocation.setLongitude(userMarker.getPosition().longitude);
        userLocation.setLatitude(userMarker.getPosition().latitude);

        for (FoodieLocation f : foodieLocations) {
            if (f.getLocation().distanceTo(userLocation) <= DISTANCE) {
                //Log.e("distance", String.valueOf(f.getLocation().distanceTo(userLocation)));
                f.getMarker().setVisible(true);
            } else {
                f.getMarker().setVisible(false);
            }
        }
    }


    //Marker onclick behavior, tell map activity to handle it.
    //TODO: Replace the s string inorder to show data of a location.
    //The method below trigger a start activity to an activity which shows the location detail.
    //Progress: havent done.
    //Test: when user click on a marker which belongs to a foodieLocation, a new activity starts and shows the detail of that foodieLocation.
    //Result: haven't test yet.
    public void onClickLocation(String title) {
        Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
        intent.putExtra("data",title);
        startActivity(intent);
    }

    public void startRouteToLocation()
    {
    }


}