package edu.temple.foodiego.Data;

import android.location.Location;

import java.util.ArrayList;

public class FirebaseHelper {
    public static FoodieUser login(String userName, String password)
    {
        return null;
    }

    public static void logout(FoodieUser foodieUser)
    {
        return ;
    }

    public static FoodieUser addFriend(FoodieUser foodieUser)
    {
        return null;
    }

    public static ArrayList<FoodieUser> getFriends(FoodieUser foodieUser)
    {
        return null;
    }

    public static FoodieLocation getNearbyLocations(Location location)
    {
        return null;
    }

    public static FoodieLocation getFavoriteLocations(FoodieUser foodieUser)
    {
        return null;
    }

    public static void postReview(FoodieUser foodieUser, FoodieLocation location, String review)
    {

    }

    public static ArrayList<FoodieReview> getReviews(FoodieLocation foodieLocation)
    {
        return   null;
    }

    public static void postActivity(FoodieActivityLog foodieActivityLog)
    {

    }

    public static ArrayList<FoodieActivityLog> getFriendsActivities(ArrayList<FoodieUser> foodieUsers)
    {
        return null;
    }

    public static ArrayList<FoodieActivityLog> getLocationActivities(FoodieLocation foodieLocation)
    {
        return  null;
    }
}
