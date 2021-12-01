package edu.temple.foodiego;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Location;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
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

    public void getFriends(FoodieUser user, GetFriendsResponse callingActivity) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("user");

        ArrayList<String> friendIds = new ArrayList<>();
        ArrayList<FoodieUser> resultingFriends = new ArrayList<>();

        userRef.child(user.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: failed to get friend data for user: " + user.getUsername());
                }
                else {
                    try {
                        JSONObject friendData = new JSONObject(String.valueOf(task.getResult().getValue()));
                        friendData = friendData.getJSONObject("friends");
                        Iterator<String> keys = friendData.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            friendIds.add(friendData.getString(key));
                        }

                        for (int i = 0; i < friendIds.size(); i++) {
                            String user_id = friendIds.get(i);
                            int finalI = i;
                            userRef.child(user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: failed to get friend data for user: " + user.getUsername());
                                    } else {
                                        try {
                                            JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));

                                            String username = userData.getString("username");
                                            String firstname = userData.getString("firstname");
                                            String lastname = userData.getString("lastname");

                                            FoodieUser user = new FoodieUser(username, firstname, lastname, user_id);

                                            resultingFriends.add(user);

                                            if (finalI == friendIds.size() - 1) {
                                                //The final user's data has been retrieved, so return the data
                                                callingActivity.result(resultingFriends);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "onComplete: error while parsing through user data while parsing through friends");
                                        }
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onComplete: error while parsing through friend data");
                    }

                }
            }
        });
    }
    interface GetFriendsResponse
    {
        void result(ArrayList<FoodieUser> friends);
    }


    public void postReview(FoodieUser user, FoodieLocation location, double rating, String review) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reviewTable = database.getReference("location_review");
        DatabaseReference newReview = reviewTable.push();
        HashMap<String, String> map = new HashMap<>();

        String formattedLocationName = replaceCharBeforeSet(location.getName());
        String formattedReviewMessage = replaceCharBeforeSet(review);

        map.put("user_id", user.getKey());
        map.put("location_id", formattedLocationName);
        map.put("review_val", "" + rating);
        map.put("review_message", formattedReviewMessage);
        newReview.setValue(map);
    }

    public void getReviews(FoodieLocation location, GetReviewsResponse callingActivity) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reviewsRef = database.getReference("location_review");
        DatabaseReference userRef = database.getReference("user");

        ArrayList<FoodieReview> resultingReviews = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();

        reviewsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: failed to retrieve reviews for location " + location.getName());
                }
                else {

                    try {

                        JSONObject reviewData = new JSONObject(String.valueOf(task.getResult().getValue()));
                        Iterator<String> keys = reviewData.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            if (reviewData.get(key) instanceof JSONObject) {

                                //Read the review location from the database
                                String db_location = (String) ((JSONObject) reviewData.get(key)).get("location_id");
                                db_location = replaceCharAfterGet(db_location);

//                                Log.d(TAG, "onComplete: now comparing location names: local name: " + location.getName() + "; db name: " + db_location);

                                //If the location name matches
                                if (location.getName().equals(db_location)) {
//                                    Log.d(TAG, "onComplete: THE LOCATION NAMES WERE EQUAL");

                                    //The review corresponds to the current location, so add it to the list

                                    String user_id = (String) ((JSONObject) reviewData.get(key)).get("user_id");
                                    double rating = (double) ((JSONObject) reviewData.get(key)).get("review_val");
                                    String review = (String) ((JSONObject) reviewData.get(key)).get("review_message");
                                    review = replaceCharAfterGet(review);

                                    resultingReviews.add(new FoodieReview(null, location, rating, review));
                                    userIds.add(user_id);
                                }
                            }
                        }

                        for (int i = 0; i < userIds.size(); i++) {
                            String user_id = userIds.get(i);
                            int finalI = i;
                            userRef.child(user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: failed to retrieve user data for reviews for location " + location.getName());
                                    }
                                    else {
                                        try {
                                            JSONObject userData = new JSONObject(String.valueOf(task.getResult().getValue()));

                                            String username = userData.getString("username");
                                            String firstname = userData.getString("firstname");
                                            String lastname = userData.getString("lastname");

                                            FoodieUser user = new FoodieUser(username, firstname, lastname, user_id);

//                                            Log.d(TAG, "onComplete: NOW SETTING USER DATA");
                                            resultingReviews.get(finalI).setUser(user);

                                            if (finalI == userIds.size() - 1) {
                                                //The final user's data has been retrieved, so return the data

                                                for (int i = 0; i < resultingReviews.size(); i++) {
                                                    Log.d(TAG, "onComplete: FOUND REVIEW FOR LOCATION: " + resultingReviews.get(i).getUser().getUsername() + "; " + resultingReviews.get(i).getRating());
                                                }

                                                callingActivity.result(resultingReviews);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, "onComplete: error while parsing through user data while parsing through reviews");
                                        }
                                    }
                                }
                            });

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onComplete: error while parsing through reviews");
                    }

                }
            }
        });
    }
    interface GetReviewsResponse
    {
        void result(ArrayList<FoodieReview> reviews);
    }


    //@ param foodieuser, foodielocation
    //if token table does not exist, create one and add token into it
    //else just add token.
    public static void addToken(FoodieUser user,FoodieLocation foodieLocation, String reason, int point, IAddTokenResponse response) {
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
                        DataMap.put("location_id", replaceCharBeforeSet(foodieLocation.getName()));
                        DataMap.put("points", String.valueOf(point));
                        DataMap.put("reason",reason);
                        newTokenRef.setValue(DataMap);
                        Log.e("tokens", "new token added.");
                        response.result(true);

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

                                String currentLocation = token.getString("location_id"); //special char converted
                                String currentReason= token.getString("reason");
                                if(currentLocation.equals(replaceCharBeforeSet(foodieLocation.getName())))
                                {
                                    if(currentReason.equals(reason))
                                    {
                                        //restaurant and reason exist.
                                        response.result(false);
                                        return;
                                        //exit method
                                    }

                                }
                            }
                            //restaurant and reason not exist. Add token
                            if (foundTokenKey == null)
                            {
                                DatabaseReference newRef = userTokenTableRef.push();
                                HashMap<String, String> DataMap = new HashMap<>();
                                DataMap.put("location_id", replaceCharBeforeSet(foodieLocation.getName()));
                                DataMap.put("reason",reason);
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

    public void getLocationActivities(FoodieLocation foodieLocation, IGetLocationActivities iGetLocationActivities) {
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
                    iGetLocationActivities.result(resultList);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    interface IGetLocationActivities {
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
