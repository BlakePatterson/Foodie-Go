package edu.temple.foodiego;
import static android.content.ContentValues.TAG;

import com.android.volley.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.ContentValues.TAG;

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

    public static void getNearByLocations(Context context, Double radius, Location location)
    {
        StringRequest sq = new StringRequest(
                Request.Method.GET,
                //"https://maps.googleapis.com/maps/api/place/nearbysearch/json",
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        +location.getLatitude()+"%2C"+location.getLongitude()+"&radius="+radius+"&type=restaurant&key=AIzaSyC8JH8DtkIKCiZFw_kf2xKTR9qtlpym-CE",
                response -> {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference locationRef = database.getReference("location");

                    locationRef.get().addOnCompleteListener(task -> {
                        try {
                            //Convert response into json array
                            //Log.e("response",response);
                            JSONArray responseJsonArray = new JSONArray(((new JSONObject(response)).getString("results")));
                            //Convert database into json obj
                            JSONObject existJsonObjects = new JSONObject(String.valueOf(task.getResult().getValue()));
                            //Log.e("Length",responseJsonArray.length()+"");


                            Boolean dataExistInDB = false;
                            for (int i=0; i< responseJsonArray.length(); i++) {
                                //get the object's name at i in response,then replace special character.
                                String responobjName=((JSONObject) responseJsonArray.get(i)).getString("name");
                                responobjName= responobjName.replace(" ", "_")
                                           .replace('\'' ,'^')
                                           .replace(",","*");

                                //Check if the obj is in database
                                dataExistInDB = false;

                                Iterator<String> keys = existJsonObjects.keys();
                                //search database if there is name already exist
                                while (keys.hasNext())
                                {
                                    String existobjName =((JSONObject)existJsonObjects.get(keys.next())).getString("name");
                                    if(existobjName.trim().equals(responobjName.trim()))
                                    {
                                        dataExistInDB = true;
                                        //Log.e("exist data",existobjName+" not added");break;
                                    }
                                }
                               //if not add it

                                if (!dataExistInDB) {
                                    //Log.e("adding data",responobjName);
                                    JSONObject location_obj = new JSONObject(new JSONObject(((JSONObject) responseJsonArray.get(i)).getString("geometry")).getString("location"));
                                    //Add a new entry to the location list
                                    DatabaseReference newRef = locationRef.push();
                                    HashMap<String, String> locationDataMap = new HashMap<>();
                                    locationDataMap.put("name", responobjName);
                                    locationDataMap.put("latitude", location_obj.getString("lat"));
                                    locationDataMap.put("longitude", location_obj.getString("lng"));
                                    locationDataMap.put("rating", ((JSONObject) responseJsonArray.get(i)).getString("rating"));
                                    //Save the user data on the database
                                    newRef.setValue(locationDataMap);
                                }
                            }
                            } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                },
                error -> error.printStackTrace()
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { return super.getParams();}

            @Override
            public String getBodyContentType() { return super.getBodyContentType(); }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sq);
    }
    public static void postReview(FoodieUser user, FoodieLocation location, double rating, String review) {
        //TODO: test that this works properly
        //get reference to reviews
        DatabaseReference reviewsRef = instance.database.getReference("location_review");
        DatabaseReference newReviewRef = reviewsRef.push();
        //make new key for review
        String key = newReviewRef.getKey();
        reviewsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    //make map with review data
                    HashMap<String, String> map = new HashMap<>();
                    map.put("user", user.getKey());
                    map.put("location", location.getName());
                    map.put("rating", "" + rating);
                    map.put("review", review);
                    //put the data into the database
                    reviewsRef.child(key).setValue(map);
                } else {
                    Log.d(TAG, "onComplete: error contacting server");
                    Toast.makeText(ctxt, "Error contacting database. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public static void openAddFriendDialog(Context c, FoodieUser user){
        new AlertDialog.Builder(c).setView(R.layout.dialog_add_friend)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog d = (Dialog) dialogInterface;
                        EditText inputUsernameField = d.findViewById(R.id.addFriendUsernameField);
                        String inputUsername = inputUsernameField.getText().toString();
                        if(inputUsername.equals("")){
                            Log.d(TAG, "openAddFriendDialog: no username entered");
                            Toast.makeText(c, "Please enter the username of your friend", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //find the key that matches the requested username
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference userRef = db.getReference("user");
                        final String[] friendKey = new String[1];
                        friendKey[0] = null;
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    try{
                                        JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));
                                        Iterator<String> keys = userData.keys();
                                        while(keys.hasNext()){
                                            String key = keys.next();
                                            if(userData.get(key) instanceof JSONObject){
                                                String dbUsername = (String) ((JSONObject) userData.get(key)).get("username");
                                                if(dbUsername.equals(inputUsername)){
                                                    friendKey[0] = key;
                                                    //get reference to the user's friends list
                                                    DatabaseReference friendsRef = userRef
                                                            .child(user.getKey())
                                                            .child("friends");
                                                    friendsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                friendsRef.push().setValue(friendKey[0]);
                                                                Toast.makeText(c, "Friend successfully added!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Log.d(TAG, "openAddFriendDialog: error adding friend");
                                                                Toast.makeText(c, "Error contacting server. Please try again.", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }else{
                                    Log.d(TAG, "openAddFriendDialog: error getting user data");
                                    Toast.makeText(c, "Error contacting server. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    //@ param foodieuser, foodielocation
    //if token table does not exist, create one and add token into it
    //else just add token.
    public static void addToken(FoodieUser user,FoodieLocation foodieLocation)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userTokenTableRef = database.getReference("user").child(user.getKey()).child("tokens");
        Task<DataSnapshot> t = userTokenTableRef.get();
        t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (t.isSuccessful())
                {
                    String data = String.valueOf(t.getResult().getValue());
                    if(data.equals("null"))
                    {
                        Log.e("","data is null");
                        DatabaseReference userRef = database.getReference("user").child(user.getKey());
                        DatabaseReference newTokenRef = userTokenTableRef.push();
                        HashMap<String, String> DataMap = new HashMap<>();
                        DataMap.put("restaurantname", foodieLocation.getName().replace(" ", "_")
                                .replace('\'' ,'^')
                                .replace(",","*"));
                        DataMap.put("points", String.valueOf(1));
                        newTokenRef.setValue(DataMap);
                        Log.e("tokens", "new token added.");

                    }
                    else
                    {
                        Log.e("tokens", data);
                        try{
                            JSONObject tokens = new JSONObject(data);

                            String foundTokenKey=null;
                            String foundRestaurantName=null;
                            int foundTokenPoint = -1;

                            Iterator<String> keys = tokens.keys();
                            while(keys.hasNext()) {
                                String key = keys.next();
                                JSONObject token = new JSONObject(tokens.getString(key));
                                String currentDBRestaurantName = token.getString("restaurantname"); //special char converted

                                if(currentDBRestaurantName.equals(
                                        foodieLocation.getName().replace(" ", "_")
                                                .replace('\'' ,'^')
                                                .replace(",","*")))
                                {
                                    foundRestaurantName = currentDBRestaurantName;
                                    foundTokenPoint =Integer.parseInt(token.getString("points"));
                                    foundTokenPoint +=1;
                                    foundTokenKey = key;
                                    break;
                                }
                            }

                            if (foundTokenKey == null)
                            {
                                DatabaseReference newRef = userTokenTableRef.push();
                                HashMap<String, String> DataMap = new HashMap<>();
                                DataMap.put("restaurantname", foodieLocation.getName().replace(" ", "_")
                                        .replace('\'' ,'^')
                                        .replace(",","*"));
                                DataMap.put("points", String.valueOf(1));
                                newRef.setValue(DataMap);
                                Log.e("tokens", "new token added.");
                            }
                            else
                            {
                                DatabaseReference ref = userTokenTableRef.child(foundTokenKey);
                                HashMap<String, String> DataMap = new HashMap<>();
                                DataMap.put("restaurantname", foundRestaurantName);
                                DataMap.put("points", String.valueOf(foundTokenPoint));
                                ref.setValue(DataMap);
                                Log.e("tokens", "token Point added.");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    //return total tokens user earns
    public static int getTokens(FoodieUser user)
    {
        int tokenread = 0;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokenTableRef = database.getReference("user").child(user.getKey()).child("tokens");
        Task<DataSnapshot> t = tokenTableRef.get();
        while(!t.isComplete())
        {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String data = String.valueOf(t.getResult().getValue());
        try{
            JSONObject tokens = new JSONObject(data);
            Iterator<String> keys = tokens.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                JSONObject token = new JSONObject(tokens.getString(key));
                tokenread += Integer.parseInt(token.getString("points"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tokenread;
    }
}
