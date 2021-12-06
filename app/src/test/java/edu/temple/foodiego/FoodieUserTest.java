package edu.temple.foodiego;

import org.junit.Test;

import static org.junit.Assert.*;

public class FoodieUserTest {

    @Test
    public void getUsername() {
        String username = "test123";
        FoodieUser user = new FoodieUser(username, "Tester", "Testson", "12345");
        assertEquals(user.getUsername(), username);
    }

    @Test
    public void getFirstname() {
        String firstname = "test123";
        FoodieUser user = new FoodieUser("username", firstname, "Testson", "12345");
        assertEquals(user.getFirstname(), firstname);
    }

    @Test
    public void getLastname() {
        String lastname = "Testson";
        FoodieUser user = new FoodieUser("username", "Tester", lastname, "12345");
        assertEquals(user.getLastname(), lastname);
    }

    @Test
    public void getKey() {
        String key = "12345";
        FoodieUser user = new FoodieUser("username", "Tester", "lastname", key);
        assertEquals(user.getKey(), key);
    }
}