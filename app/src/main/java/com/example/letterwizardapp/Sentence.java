package com.example.letterwizardapp;
import java.util.Arrays;
import java.util.List;

public class Sentence {
    private int id;
    private String sentence;

    public Sentence(String sentence) {
        this.id = id;
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    // You need to implement this method based on your specific requirements
    public List<String> getTokens() {
        // Implement logic to tokenize the sentence into words
        // For example, you can use StringTokenizer or split the sentence based on spaces
        // Return a list of tokens
        // Note: This is a placeholder, and you need to adapt it to your needs
        return Arrays.asList(sentence.split("\\s+"));
    }
}
