package edu.temple.foodiego;

import org.junit.Test;

import static org.junit.Assert.*;

public class FoodieReviewTest {

    @Test
    public void setUser() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieUser notUser = new FoodieUser("notUsername", "notFirstname", "notLastname", "notKey");
        FoodieReview review = new FoodieReview(notUser, new FoodieLocation("1", 0, 0, 0), 5.0, "review");
        review.setUser(user);
        assertEquals(review.getUser(), user);
    }

    @Test
    public void getUser() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieReview review = new FoodieReview(user, new FoodieLocation("1", 0, 0, 0), 5.0, "review");
        assertEquals(review.getUser(), user);
    }

    @Test
    public void setLocation() {
        FoodieLocation loc = new FoodieLocation("1", 0, 0, 0);
        FoodieLocation notLoc = new FoodieLocation("123", 0.5, 0.5, 1.5);
        FoodieReview review = new FoodieReview(new FoodieUser("username", "firstname", "lastname", "key"), notLoc, 5.0, "review");
        review.setLocation(loc);
        assertEquals(review.getLocation(), loc);
    }

    @Test
    public void getLocation() {
        FoodieLocation loc = new FoodieLocation("1", 0, 0, 0);
        FoodieReview review = new FoodieReview(new FoodieUser("username", "firstname", "lastname", "key"), loc, 5.0, "review");
        assertEquals(review.getLocation(), loc);
    }

    @Test
    public void setRating() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("1", 0, 0, 0);
        double rating = 5.0;
        double notRating = 2.5;
        FoodieReview review = new FoodieReview(user, loc, notRating, "review");
        review.setRating(rating);
        assertEquals(String.valueOf(review.getRating()), String.valueOf(rating));
    }

    @Test
    public void getRating() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("1", 0, 0, 0);
        double rating = 5.0;
        FoodieReview review = new FoodieReview(user, loc, rating, "review");
        assertEquals(String.valueOf(review.getRating()), String.valueOf(rating));
    }

    @Test
    public void setReview() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("1", 0, 0, 0);
        String reviewText = "review";
        String notReviewText = "notReview";
        FoodieReview review = new FoodieReview(user, loc, 5.0, notReviewText);
        review.setReview(reviewText);
        assertEquals(review.getReview(), reviewText);
    }

    @Test
    public void getReview() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("1", 0, 0, 0);
        String reviewText = "review";
        FoodieReview review = new FoodieReview(user, loc, 5.0, reviewText);
        assertEquals(review.getReview(), reviewText);
    }
}