package edu.temple.foodiego;

public class FoodieUser {

    private String username;
    private String firstname;
    private String lastname;
    private String key;

    public FoodieUser(String username, String firstname, String lastname, String key) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getKey() {
        return key;
    }
}
