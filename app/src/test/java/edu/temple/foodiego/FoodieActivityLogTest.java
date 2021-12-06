package edu.temple.foodiego;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class FoodieActivityLogTest {

    @Test
    public void getUser() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, action, time);
        assertEquals(activityLog.getUser(), user);
    }

    @Test
    public void setUser() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieUser notUser = new FoodieUser("notUsername", "notFirstname", "notLastname", "notKey");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(notUser, loc, action, time);
        activityLog.setUser(user);
        assertEquals(activityLog.getUser(), user);
    }

    @Test
    public void getLocation() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, action, time);
        assertEquals(activityLog.getLocation(), loc);
    }

    @Test
    public void setLocation() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        FoodieLocation notLoc = new FoodieLocation("notLocation", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(user, notLoc, action, time);
        activityLog.setLocation(loc);
        assertEquals(activityLog.getLocation(), loc);
    }

    @Test
    public void getAction() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, action, time);
        assertEquals(activityLog.getAction(), action);
    }

    @Test
    public void setAction() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        String notAction = "notAction";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, notAction, time);
        activityLog.setAction(action);
        assertEquals(activityLog.getAction(), action);
    }

    @Test
    public void getTime() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, action, time);
        assertEquals(activityLog.getTime(), time);
    }

    @Test
    public void setTime() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime notTime = LocalDateTime.parse("2017-08-03T10:15:30");
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, action, notTime);
        activityLog.setTime(time);
        assertEquals(activityLog.getTime(), time);
    }

    @Test
    public void getActivityLogMessage() {
        FoodieUser user = new FoodieUser("username", "firstname", "lastname", "key");
        FoodieLocation loc = new FoodieLocation("location", 0, 0, 0);
        String action = "action";
        LocalDateTime time = LocalDateTime.parse("2021-12-06T00:27:55.628");
        FoodieActivityLog activityLog = new FoodieActivityLog(user, loc, action, time);
        assertEquals(activityLog.getActivityLogMessage(), "2021-12-06T00:27:55.628- username action at location");
    }
}