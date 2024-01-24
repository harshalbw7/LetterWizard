package com.example.letterwizardapp;

public class Letters {
     String userName, letterType, subject, date, url;
     public Letters() {}

    public Letters(String userName, String letterType, String subject, String date, String url){
         this.userName = userName;
         this.letterType = letterType;
         this.subject = subject;
         this.date = date;
         this.url = url;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLetterType() {
        return letterType;
    }
    public void setLetterType(String letterType) {
        this.letterType = letterType;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
