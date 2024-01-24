package com.example.letterwizardapp;

public class content {
    String letterType, subject, letterContent;
    public content(){}
    public content(String letterType, String subject, String letterContent){
        this.letterType = letterType;
        this.subject = subject;
        this.letterContent = letterContent;
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

    public String getLetterContent() {
        return letterContent;
    }

    public void setLetterContent(String letterContent) {
        this.letterContent = letterContent;
    }
}
