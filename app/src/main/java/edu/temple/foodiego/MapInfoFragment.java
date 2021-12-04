package edu.temple.foodiego;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapInfoFragment extends Fragment {
    public static final String USERNAME_PARAM_KEY = "mapInfoParam1";

    private TextView welcomeMessage;
    private String userName;

    private TextView distanceWalked;
    private double totalDistanceWalked;
    private Location userLocation;
    private Location userLocation1;
    private BroadcastReceiver userLocationReceiver;

    public MapInfoFragment() { }

    public static MapInfoFragment newInstance(String userName, String param2) {
        Bundle args = new Bundle();
        args.putString(USERNAME_PARAM_KEY, userName);

        MapInfoFragment fragment = new MapInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        welcomeMessage = getView().findViewById(R.id.tvMessage);
        distanceWalked = getView().findViewById(R.id.tvDistanceWalked);

        if (getArguments() == null){
            userName = "Dear User";
        } else {
            String username = (String) getArguments().getSerializable(USERNAME_PARAM_KEY);
        }

        totalDistanceWalked =0;
        userLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(userLocation1 == null)
                {
                    userLocation1 = (Location) intent.getParcelableExtra("userLocation");
                }
                else
                {
                    userLocation = (Location) intent.getParcelableExtra("userLocation");
                    updateDistanceWalked(userLocation.distanceTo(userLocation1));
                    userLocation1 = userLocation;
                    userLocation = null;
                }
            }
        };
        getActivity().registerReceiver(userLocationReceiver,new IntentFilter("edu.temple.foodiego.userlocation"));

        updateWelcomeMessage("Welcome to FoodieGo. "+userName);
        distanceWalked.setText("Distance Walked: "+0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_info, container, false);
    }

    public void updateWelcomeMessage(String message){
        welcomeMessage.setText(message);
    }

    public void updateDistanceWalked(double distance){
        totalDistanceWalked += (float)distance;
        distanceWalked.setText("Distance Walked: "+ totalDistanceWalked);
    }
}