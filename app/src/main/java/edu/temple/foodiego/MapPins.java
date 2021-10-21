package com.example.myapplication;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapPins {
    //Location id, and their name(title) latlong(position)
    private HashMap<String, Marker> foodieLocations;

    //keep the map reference in MapPins
    private GoogleMap mapRef;

    //List of String that will be used by the json
    private final String NameString = "username";
    private final String LatString = "latitude";
    private final String LngString = "longitude";

    //handle the event outside the class.
    public MapPins(GoogleMap googleMap, GoogleMap.OnMarkerClickListener clickListener) {
        mapRef = googleMap;
        foodieLocations = new HashMap<>();
        mapRef.setOnMarkerClickListener(clickListener);
    }

    public MapPins(GoogleMap googleMap) {
        mapRef = googleMap;
        foodieLocations = new HashMap<>();
        mapRef.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                return false;
            }
        });
    }

    //assuming the raw string data of array is passed in.
    public void OnJsonStringReceived(String data) {
        //get the full list of user's markers.
        ArrayList dataNotUpdated = new ArrayList();
        for (Object s:foodieLocations.keySet().toArray()) {
            dataNotUpdated.add(s);
        }

        //loop over the json data array that is passed in.
        try {
            JSONObject ja1 = new JSONObject(data);
            String array = ja1.getString("data");
            JSONArray ja = new JSONArray(array);
            for (int i =0; i< ja.length(); i++)
            {
                JSONObject jo = (JSONObject) ja.get(i);

                //convert the json obj into actual data.
                String key = jo.getString(NameString);
                double lat = Double.parseDouble(jo.getString(LatString));
                double lng = Double.parseDouble(jo.getString(LngString));
                LatLng latLng = new LatLng(lat,lng);

                //add a new marker if the location previously not exist.

                if( !foodieLocations.containsKey(key))
                {
                    MarkerOptions mo = new MarkerOptions();
                    mo.title(key);
                    mo.snippet("put extra informatio here.");
                    Marker m = mapRef.addMarker(new MarkerOptions().position(latLng).snippet("hiiii."));
                    m.setTitle(key);
                    foodieLocations.put(key,m);
                }
                else
                {
                    dataNotUpdated.remove(key);
                }
            }

            //remove the marker if the location is not received from server.
            if(dataNotUpdated.size()>0)
            {
                for (Object s: dataNotUpdated) {
                    foodieLocations.get((String)s).remove();
                    foodieLocations.remove((String)s);
                }
                dataNotUpdated = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//DEMO:
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng sydney = new LatLng(39.9545, -75.2026);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17));
//
//        mapPins = new MapPins(googleMap, new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(@NonNull Marker marker) {
//                    Intent intent = new Intent(MapsActivity.this, secondactivity.class);
//                    intent.putExtra("foodieLocationName", marker.getTitle());
//                    startActivity(intent);
//                return false;
//            }
//        });
//
//        String s = "{action:'UPDATE', 'data':[" +
//                "{'username':'user1', 'firstname':'firstname1', 'lastname':'lastname1' , 'latitude':39.9545, 'longitude':-75.2026},"
//                +"{'username':'user2', 'firstname':'firstname2', 'lastname':'lastname2' , 'latitude':39.9544, 'longitude':-75.2030},"
//                +"{'username':'user3', 'firstname':'firstname3', 'lastname':'lastname3' , 'latitude':39.9555, 'longitude':-75.2045},"
//                +"{'username':'user4', 'firstname':'firstname4', 'lastname':'lastname4' , 'latitude':39.9566, 'longitude':-75.2016}"
//                +"]}";
//        mapPins.OnJsonStringReceived(s);
//}



}