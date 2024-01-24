package com.example.letterwizardapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LauncherLayout extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_layout);
        EditText userSubject = findViewById(R.id.subject);
        Button get = findViewById(R.id.getSubject);
        TextView output = findViewById(R.id.output);

        get.setOnClickListener(v -> {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference reference = db.getReference().child("content");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Sentence> sentencesFromFirebase = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        content letter = dataSnapshot.getValue(content.class);
                        String subject = letter.getSubject();
                        String subjectName = subject.toLowerCase();
                        sentencesFromFirebase.add(new Sentence(subjectName));
                    }
                    // Get user input (replace this with your actual input mechanism)
                    String userInput = userSubject.getText().toString();
                    userInput = userInput.toLowerCase();

                    String[] userInputTokens = userInput.split("\\s+");
                    int maxMatchCount = 0;
                    String maxMatchedSentence = "";

                    for (Sentence sentence : sentencesFromFirebase) {
                        int matchCount = 0;
                        for (String word : sentence.getTokens()) {
                            if (Arrays.asList(userInputTokens).contains(word)) {
                                matchCount++;
                            }
                        }

                        if (matchCount > maxMatchCount) {
                            maxMatchCount = matchCount;
                            maxMatchedSentence = sentence.getSentence();
                        }
                    }

                    // Now you can use maxMatchedSentence as needed, for example, display it
                    // or perform any other action based on the extracted subject
                    output.setText(maxMatchedSentence);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }
}
