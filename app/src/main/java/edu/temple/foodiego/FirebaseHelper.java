package edu.temple.foodiego;
import com.android.volley.*;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private static Context ctxt;

    private FirebaseDatabase database;

    private FirebaseHelper(Context context) {
        ctxt = context;
        this.database = FirebaseDatabase.getInstance();
    }

    public static synchronized FirebaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseHelper(context);
        } else {
            ctxt = context;
        }
        return instance;
    }

    public static void getNearByLocations()
    {
        StringRequest sq = new StringRequest();
    }
}
