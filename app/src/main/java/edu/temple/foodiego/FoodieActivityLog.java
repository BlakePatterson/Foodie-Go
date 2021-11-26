package edu.temple.foodiego;

import java.util.Date;

public class FoodieActivityLog {
    private FoodieUser user;
    private FoodieLocation location;
    private String action;
    private Date time;

    public FoodieActivityLog(FoodieUser user, FoodieLocation location, String action, Date time){
        this.user = user;
        this.location = location;
        this.action = action;
        this.time = time;
    }
    public FoodieUser getUser() {
        return user;
    }
    public void setUser(FoodieUser user) {
        this.user = user;
    }
    public FoodieLocation getLocation() {
        return location;
    }
    public void setLocation(FoodieLocation location) {
        this.location = location;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public String getActivityLogMessage(){
        String result = "";
        if(time != null){
            result = time.toString() + "- ";
        }
        result += user.getUsername() + " " + action;
        if(location != null){
            result += " at " + location.getName();
        }
        return result;
    }
}