package com.example.letterwizardapp;

import java.util.Date;

public class Feedback {
    String userName,suggestion;
    String date;
    float rating;
    public Feedback(){}

    public String getUserName() {
        return userName;
    }

    public Feedback(String userName, float rating, String suggestion, String date){
        this.userName = userName;
        this.rating = rating;
        this.suggestion = suggestion;
        this.date = date;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
