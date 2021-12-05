package edu.temple.foodiego;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import androidx.fragment.app.Fragment;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapInfoFragment extends Fragment {
    public static final String USERNAME_PARAM_KEY = "mapInfoParam1";

    private TextView welcomeMessage;
    private String userName;
//
//    private TextView distanceWalked;
//    private double totalDistanceWalked;
//    private Location userLocation;
//    private Location userLocation1;
//    private BroadcastReceiver userLocationReceiver;

    public MapInfoFragment() { }

    public static MapInfoFragment newInstance(String userName) {
        Bundle args = new Bundle();
        args.putString(USERNAME_PARAM_KEY, userName);

        MapInfoFragment fragment = new MapInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null){
            userName = "Dear User";
        } else {
            userName = (String) getArguments().getSerializable(USERNAME_PARAM_KEY);
        }

//        userLocationReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//            if(userLocation1 == null)
//            {
//                userLocation1 = (Location) intent.getParcelableExtra("userLocation");
//            }
//            else
//            {
//                userLocation = (Location) intent.getParcelableExtra("userLocation");
//                //updateDistanceWalked(userLocation.distanceTo(userLocation1));
//                userLocation1 = userLocation;
//                userLocation = null;
//            }
//            }
//        };
//        getActivity().registerReceiver(userLocationReceiver,new IntentFilter("edu.temple.foodiego.userlocation"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_info, container, false);
        //totalDistanceWalked =0;
        welcomeMessage = (TextView) view.findViewById(R.id.tvMessage);
//        distanceWalked =  (TextView) view.findViewById(R.id.tvDistanceWalked);
        updateWelcomeMessage("Welcome, " + userName + "!");
//        distanceWalked.setText("Distance Walked: "+ totalDistanceWalked);
        return view;
    }

    public void updateWelcomeMessage(String message){
        if(message != null)
        {
            welcomeMessage.setText(message);
            Log.d("Map Info Fragment","welcome message updated");
        }
    }

    public void updateDistanceWalked(double distance){
        //totalDistanceWalked += (float)distance;
        //distanceWalked.setText("Distance Walked: "+ (int)totalDistanceWalked +"meters");
    }
}