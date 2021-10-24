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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    public static final String USERNAME_PARAM_KEY = "mapParam1";
    public static final String FIRSTNAME_PARAM_KEY = "mapParam2";
    public static final String LASTNAME_PARAM_KEY = "mapParam3";

    private GoogleMap map;
    private Marker userMarker;

    private Context parentActivity;

    private FoodieUser user;

    //foodieLocations and marker onclick behavior.
    private final String NameString = "username";
    private final String LatString = "latitude";
    private final String LngString = "longitude";
    private final String RatingString = "longitude";
    private ArrayList<FoodieLocation> foodieLocations;

    //Marker onclick behavior, tell map activity to handle it.
    //TODO: Replace the s string inorder to show data of a location.
    //The method below trigger a start activity to an activity which shows the location detail.
    //Progress: havent done.
    //Test: when user click on a marker which belongs to a foodieLocation, a new activity starts and shows the detail of that foodieLocation.
    //Result: haven't test yet.
    private GoogleMap.OnMarkerClickListener markerClickListener= new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(@NonNull Marker marker) {
            String s = "";
            Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
            intent.putExtra("data",s);
            startActivity(intent);
            //((MapActivity)getActivity()).markerClicked(marker, "locationname");
            return false;
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
            map.setOnMarkerClickListener(markerClickListener);
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
        }
    }

    interface MapFragmentInterface {
        void openLocationDetailView(FoodieLocation location);
        //void markerClicked(Marker marker, String s);
    }

    //@param JsonArray
    //This method first copy what data we have into a new Array list-data not update,
    //Then it loop through the data see if the data exists, if exist remove the data from data not update,
    //if not add a new foodielication to foodielocations.
    //and finally remove the marker that is not updated.
    //Test: when new data come in, new marker shows up.
    //      when data that is not update, foodielocation removed and marker disappear.
    //Result: pass. oct 24 2021 12:10 am.
    public void updateFoodieLocation(JSONArray jsonArray)
    {
        if(foodieLocations == null)
        {
            foodieLocations = new ArrayList<>();
        }
        //get the full list of user's markers.
        ArrayList dataNotUpdated = new ArrayList();
        if(foodieLocations.size()>0)
        {
            for (Object s:foodieLocations.toArray()) {
                dataNotUpdated.add(s);
            }
        }

        //loop through the data that received
        for (int i =0; i< jsonArray.length(); i++)
        {
            JSONObject jo = null;
            try {
                jo = (JSONObject) jsonArray.get(i);

            //convert the json obj into actual data.
            String locationName = jo.getString(NameString);
            double locationLat = Double.parseDouble(jo.getString(LatString));
            double locationLng = Double.parseDouble(jo.getString(LngString));
            double locationRating = Double.parseDouble(jo.getString(RatingString));
            //LatLng latLng = new LatLng(lat,lng);
            FoodieLocation foodieLocation = new FoodieLocation(locationName, locationLat,locationLng,locationRating);
            //add a new marker if the location previously not exist.

            if( !foodieLocations.contains(foodieLocation))
            {
                MarkerOptions mo = new MarkerOptions();
                mo.title(foodieLocation.getName());

                // TODO: 10/21/2021
                //mo.snippet("put extra informatio here.");
                LatLng latLng = new LatLng(foodieLocation.getLocation().getLatitude(),foodieLocation.getLocation().getLongitude());
                Marker m = map.addMarker(new MarkerOptions().position(latLng));
                m.setTitle(locationName);
                m.showInfoWindow();
                foodieLocation.setMarker(m);
                foodieLocations.add(foodieLocation);
                //foodieLocations.put(key,m);
            }
            else
            {
                dataNotUpdated.remove(foodieLocation);
            }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Update foodie locations",e.getMessage());
            }

        }
        //remove the marker if the location is not received from server.
        if(dataNotUpdated.size()>0)
        {
            for (Object s: dataNotUpdated) {
                ((FoodieLocation)s).getMarker().remove();
                foodieLocations.remove(((FoodieLocation)s));
            }
            dataNotUpdated = null;
        }
    }

}