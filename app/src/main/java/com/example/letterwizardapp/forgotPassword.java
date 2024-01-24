package com.example.letterwizardapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class forgotPassword extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        ProgressBar progress = findViewById(R.id.progressBar);
        EditText email = findViewById(R.id.email);
        Spinner list = findViewById(R.id.question);
        EditText answer = findViewById(R.id.answer);
        Button getPassword = findViewById(R.id.getCreds);
        TextView userName = findViewById(R.id.userName);
        TextView password = findViewById(R.id.password);
        Button gotoSignIn = findViewById(R.id.gotoSignIn);

        String[] questions = {"Click to select a security question",
                "What is the name of your first pet?",
                "What is the name of your favorite teacher?",
                "What is the name of your favorite place?",
                "What is the title of your favorite book?"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, Arrays.asList(questions));
        list.setAdapter(adapter);

        getPassword.setOnClickListener(v -> {

            if(email.getText().toString().equals("")){
                Toast.makeText(forgotPassword.this, "Please insert email !", Toast.LENGTH_LONG).show();
                return;
            }
            if(list.getSelectedItem().toString().equals("Click to select a security question")){
                Toast.makeText(forgotPassword.this, "Please select a question.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(answer.getText().toString().equals("")){
                Toast.makeText(forgotPassword.this, "Please provide answer !", Toast.LENGTH_LONG).show();
            }

            //get credentials from firebase
            user user = new user();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference reference = db.getReference().child("user");
            String userEmail = email.getText().toString();
            Query query = reference.orderByChild("email").equalTo(userEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            // Retrieve user data from the snapshot
                            user user = dataSnapshot.getValue(user.class);
                            if (user != null) {
                                // Assuming User class has getUsername() and getPassword() methods
                                String retrievedQuestion = user.getQuestion();
                                String retrievedAnswer = user.getAnswer();
                                String retrievedUsername = user.getUserName();
                                String retrievedPassword = user.getPassword();

                                // Display the retrieved username and password
                                if(!list.getSelectedItem().equals(retrievedQuestion)){
                                    Toast.makeText(forgotPassword.this, "Please select a question, which you have selected at the time of registration!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(!answer.getText().toString().equals(retrievedAnswer)){
                                    Toast.makeText(forgotPassword.this, "Please provide an answer, which you have provided at the time of registration!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                getPassword.setText("Please wait...");
                                getPassword.setBackgroundColor(Color.parseColor("#E8ECED"));

                                //start progress bar
                                final Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    private int counter = 0;
                                    @Override
                                    public void run() {
                                        counter++;
                                        counter = Math.min(counter, 100);
                                        runOnUiThread(() -> {
                                            progress.setProgress(counter);
                                            if (counter == 1) {
                                                progress.setVisibility(View.VISIBLE);
                                            }
                                            if (counter == 100) {
                                                progress.setVisibility(View.INVISIBLE);
                                                timer.cancel();

                                                getPassword.setText(" Get Credentials ");
                                                getPassword.setBackgroundColor(Color.parseColor("#FFC107"));

                                                userName.setText("Username: " + retrievedUsername);
                                                userName.setVisibility(View.VISIBLE);
                                                password.setText("Password: " + retrievedPassword);
                                                password.setVisibility(View.VISIBLE);
                                                gotoSignIn.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                };
                                timer.scheduleAtFixedRate(task, 0, 20);
                            }
                        }
                    } else {
                        Toast.makeText(forgotPassword.this, "Please insert an email, which you have inserted at the time of registration!" + userEmail, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(forgotPassword.this, "Cancelled"+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        gotoSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(forgotPassword.this,MainActivity.class);
            startActivity(intent);
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onResume() {
        super.onResume();
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }
}