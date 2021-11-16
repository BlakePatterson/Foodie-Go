package edu.temple.foodiego;

public class FoodieReview {
    private FoodieUser user;
    private FoodieLocation location;
    private double rating;
    private String review;

    public FoodieReview(FoodieUser user, FoodieLocation location, double rating, String review){
        this.user = user;
        this.location = location;
        this.rating = rating;
        this.review = review;
    }
    public void setUser(FoodieUser user) {
        this.user = user;
    }
    public FoodieUser getUser() {
        return user;
    }
    public void setLocation(FoodieLocation location) {
        this.location = location;
    }
    public FoodieLocation getLocation() {
        return location;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public double getRating() {
        return rating;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public String getReview() {
        return review;
    }
}
