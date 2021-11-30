package edu.temple.foodiego;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public static void getNearbyLocations(Context context, Double radius, Location location) {
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
                            JSONArray responseJsonArray = new JSONArray(((new JSONObject(response)).getString("results")));
                            //Convert database into json obj
                            //data not exist in database
                            if(String.valueOf(task.getResult().getValue()).equals("null"))
                            {
                                //Log.e("GetLcoations", "is null"+ response);
                                for (int i=0; i< responseJsonArray.length(); i++) {
                                    JSONObject location_obj = new JSONObject(new JSONObject(((JSONObject) responseJsonArray.get(i)).getString("geometry")).getString("location"));
                                    DatabaseReference newRef = locationRef.push();
                                    HashMap<String, String> locationDataMap = new HashMap<>();
                                    String responobjName = replaceCharBeforeSet(((JSONObject) responseJsonArray.get(i)).getString("name"));
                                    locationDataMap.put("location_name", responobjName);
                                    locationDataMap.put("location_lat", location_obj.getString("lat"));
                                    locationDataMap.put("location_long", location_obj.getString("lng"));
                                    locationDataMap.put("location_rating", ((JSONObject) responseJsonArray.get(i)).getString("rating"));
                                    newRef.setValue(locationDataMap);
                                }
                            }
                            else
                            {
                                JSONObject dbObjects = new JSONObject(String.valueOf(task.getResult().getValue()));
                                Boolean dataExistInDB = false;
                                for (int i=0; i< responseJsonArray.length(); i++) {
                                    String responobjName=((JSONObject) responseJsonArray.get(i)).getString("name");
                                    responobjName= replaceCharBeforeSet(responobjName);
                                    dataExistInDB = false;
                                    Iterator<String> keys = dbObjects.keys();
                                    while (keys.hasNext())
                                    {
                                        String existobjName =((JSONObject)dbObjects.get(keys.next())).getString("location_name");
                                        if(existobjName.trim().equals(responobjName.trim()))
                                        {
                                            dataExistInDB = true; break;
                                        }
                                    }
                                    if (!dataExistInDB) {
                                        JSONObject location_obj = new JSONObject(new JSONObject(((JSONObject) responseJsonArray.get(i)).getString("geometry")).getString("location"));
                                        DatabaseReference newRef = locationRef.push();
                                        HashMap<String, String> locationDataMap = new HashMap<>();
                                        locationDataMap.put("location_name", responobjName);
                                        locationDataMap.put("location_lat", location_obj.getString("lat"));
                                        locationDataMap.put("location_long", location_obj.getString("lng"));
                                        locationDataMap.put("location_rating", ((JSONObject) responseJsonArray.get(i)).getString("rating"));
                                        newRef.setValue(locationDataMap);
                                    }
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

    public void addFriend(FoodieUser user){
        SharedPreferences prefs = ctxt.getSharedPreferences(ctxt.getString(R.string.credentials_preferences), Context.MODE_PRIVATE);
        String selfKey = prefs.getString(ctxt.getString(R.string.stored_key_key), null);
        if(selfKey == null){
            Toast.makeText(ctxt, "Error, please try again", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "addFriend: couldn't retrieve logged in user's key");
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
                                if(dbUsername.equals(user.getUsername())){
                                    friendKey[0] = key;
                                    //get reference to the user's friends list
                                    DatabaseReference friendsRef = userRef
                                            .child(selfKey)
                                            .child("friends");
                                    friendsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                friendsRef.push().setValue(friendKey[0]);
                                                Toast.makeText(ctxt, "Friend successfully added!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d(TAG, "addFriend: error adding friend");
                                                Toast.makeText(ctxt, "Error contacting server. Please try again.", Toast.LENGTH_LONG).show();
                                              
                                            }
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                        if(friendKey[0] == null){
                            Toast.makeText(ctxt, "Unable to find requested user", Toast.LENGTH_LONG).show();
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "addFriend: error getting user data");
                    Toast.makeText(ctxt, "Error contacting server. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void postReview(FoodieUser user, FoodieLocation location, double rating, String review) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reviewTable = database.getReference("location_review");
        DatabaseReference newReview = reviewTable.push();
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", user.getKey());
        map.put("location_id", location.getName());
        map.put("review_val", "" + rating);
        map.put("review_message", review);
        newReview.setValue(map);
    }

    //@ param foodieuser, foodielocation
    //if token table does not exist, create one and add token into it
    //else just add token.
    public static void addToken(FoodieUser user,FoodieLocation foodieLocation, String occasion, int point, IAddTokenResponse response) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userTokenTableRef = database.getReference("user").child(user.getKey()).child("tokens");
        Task<DataSnapshot> t = userTokenTableRef.get();
        t.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (t.isSuccessful())
                {
                    String data = String.valueOf(t.getResult().getValue());
                    //tokens table is not created.
                    if(data.equals("null"))
                    {
                        Log.e("","data is null");
                        DatabaseReference userRef = database.getReference("user").child(user.getKey());
                        DatabaseReference newTokenRef = userTokenTableRef.push();
                        HashMap<String, String> DataMap = new HashMap<>();
                        DataMap.put("restaurantname", replaceCharBeforeSet(foodieLocation.getName()));
                        DataMap.put("points", String.valueOf(point));
                        DataMap.put("occasion",occasion);
                        newTokenRef.setValue(DataMap);
                        Log.e("tokens", "new token added.");

                    }
                    else
                    {
                        //token table is created.
                        Log.e("tokens", data);
                        try{
                            JSONObject tokens = new JSONObject(data);

                            String foundTokenKey=null;

                            Iterator<String> keys = tokens.keys();
                            while(keys.hasNext()) {
                                String key = keys.next();
                                JSONObject token = new JSONObject(tokens.getString(key));

                                String currentRestaurant = token.getString("restaurantname"); //special char converted
                                String currentOccation= token.getString("occasion");
                                if(currentRestaurant.equals(replaceCharBeforeSet(foodieLocation.getName())))
                                {
                                    if(currentOccation.equals(occasion))
                                    {
                                        //restaurant and occasion exist.
                                        response.result(false);
                                        return;
                                        //exit method
                                    }

                                }
                            }
                            //restaurant and occasion not exist. Add token
                            if (foundTokenKey == null)
                            {
                                DatabaseReference newRef = userTokenTableRef.push();
                                HashMap<String, String> DataMap = new HashMap<>();
                                DataMap.put("restaurantname", replaceCharBeforeSet(foodieLocation.getName()));
                                DataMap.put("occasion",occasion);
                                DataMap.put("points", String.valueOf(1));
                                newRef.setValue(DataMap);
                                Log.e("tokens", "new token added.");
                                response.result(true);
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
    public static void getTokens(FoodieUser user, IGetTokenResponse iGetTokenResponse) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokenTableRef = database.getReference("user").child(user.getKey()).child("tokens");
        Task<DataSnapshot> t = tokenTableRef.get();
        t.addOnCompleteListener(task -> {
            String data = String.valueOf(t.getResult().getValue());
            try{
                JSONObject tokens = new JSONObject(data);
                Iterator<String> keys = tokens.keys();
                int tokenread = 0;
                while(keys.hasNext()) {
                    String key = keys.next();
                    JSONObject token = new JSONObject(tokens.getString(key));
                    tokenread += Integer.parseInt(token.getString("points"));
                }
                iGetTokenResponse.result(tokenread);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    interface IGetTokenResponse
    {
        void result(int points);
    }
    interface IAddTokenResponse
    {
        void result(Boolean b);
    }

    public static void postActivity(FoodieActivityLog log) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference activityTbale = database.getReference("activity");
        DatabaseReference newActivity = activityTbale.push();
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("location_id", replaceCharBeforeSet(log.getLocation().getName()));
        dataMap.put("user_id", log.getUser().getUsername());
        dataMap.put("activity_type", log.getAction());
        dataMap.put("activity_time",replaceCharBeforeSet(log.getTime().toString()));
        dataMap.put("activity_message",replaceCharBeforeSet(log.getActivityLogMessage()));
        newActivity.setValue(dataMap);
    }

    public static void getLocationActivities(FoodieLocation foodieLocation, IGetActivities iGetActivities) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference activityTableRef = database.getReference("activity");
        Task<DataSnapshot> t = activityTableRef.get();
        t.addOnCompleteListener(task -> {
            String data = String.valueOf(t.getResult().getValue());
            try{
                JSONObject tokens = new JSONObject(data);
                ArrayList<FoodieActivityLog> resultList = new ArrayList();
                Iterator<String> keys = tokens.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    JSONObject activityJsonObj = new JSONObject(tokens.getString(key));
                    if(foodieLocation.getName().equals(replaceCharAfterGet((String) activityJsonObj.get("location_id"))))
                    {
                        FoodieUser foodieUser = new FoodieUser(activityJsonObj.getString("user_id"),"","","");
                        FoodieLocation foodieLocation1 = new FoodieLocation(
                                replaceCharAfterGet(activityJsonObj.getString("location_id")),0,0,0 );
                        String action = activityJsonObj.getString("activity_type");
                        String timeString = replaceCharAfterGet(activityJsonObj.getString("activity_time"));
                        LocalDate date = LocalDate.parse(timeString);
                        FoodieActivityLog foodieActivityLog = new FoodieActivityLog(foodieUser, foodieLocation1, action, date);
                        resultList.add(foodieActivityLog);
                    }
                }
                if(resultList.size()>0)
                iGetActivities.result(resultList);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    interface IGetActivities{
        void result(ArrayList<FoodieActivityLog> logs);
    }

    public static String replaceCharBeforeSet(String original)
    {
        String result = original
                .replace(' ', '_')
                .replace('\'' ,'^')
                .replace(',','*')
                .replace(':','!');
        return result;
    }
    public static String replaceCharAfterGet(String original)
    {
        String result = original
                .replace("_", " ")
                .replace('^' ,'\'')
                .replace("*",",")
                .replace("!",":");
        return result;
    }
}
