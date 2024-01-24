package com.example.letterwizardapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    String storedUsername;
    String storedName;
    String storedEmail;
    String storedMobile;
    String storedPassword;
    String storedQuestion;
    String storedAnswer;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWER = "answer";
    private static final String KEY_LOGGED_IN = "loggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Check if the user is already logged in
        if (sharedPreferences.getBoolean(KEY_LOGGED_IN, false)) {
            String storedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String storedName = sharedPreferences.getString(KEY_NAME, "");
            String storedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String storedMobile = sharedPreferences.getString(KEY_MOBILE, "");
            String storedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            String storedQuestion = sharedPreferences.getString(KEY_QUESTION, "");
            String storedAnswer = sharedPreferences.getString(KEY_ANSWER, "");

            if (!storedUsername.isEmpty()) {
                // User is already logged in, navigate to home activity
                navigateToHomeActivity(storedUsername, storedName, storedEmail, storedMobile, storedPassword, storedQuestion, storedAnswer);
                return;
            }
        }

        ProgressBar progress = findViewById(R.id.progressBar);
        EditText username = findViewById(R.id.username);
        EditText userPassword = findViewById(R.id.userPassword);
        Button signup = findViewById(R.id.signUp);
        Button forgot = findViewById(R.id.forgot);
        ImageView admin = findViewById(R.id.admin);
        Button signIn = findViewById(R.id.signIn);

        user user = new user();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,registration.class);

            startActivity(intent);
        });

        signIn.setOnClickListener(v -> {

            //Intent intent = new Intent(MainActivity.this,userHome.class);
            //startActivity(intent);

            String userName = username.getText().toString();
            String password = userPassword.getText().toString();
            if(userName.equals("") && password.equals("")){
                Toast.makeText(this, "Please insert userName and Password !", Toast.LENGTH_LONG).show();
                return;
            }
            if(userName.equals("")){
                Toast.makeText(this, "Please insert userName !", Toast.LENGTH_LONG).show();
                return;
            }
            if(password.equals("")){
                Toast.makeText(this, "Please insert your Password !", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseReference reference = db.getReference("user").child(userName);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        storedUsername = snapshot.child("userName").getValue(String.class);
                        storedName = snapshot.child("name").getValue(String.class);
                        storedEmail = snapshot.child("email").getValue(String.class);
                        storedMobile = snapshot.child("mobile").getValue(String.class);
                        storedPassword = snapshot.child("password").getValue(String.class);
                        storedQuestion = snapshot.child("question").getValue(String.class);
                        storedAnswer = snapshot.child("answer").getValue(String.class);

                        if (storedUsername != null && storedPassword != null && storedUsername.equals(userName) && storedPassword.equals(password)) {
                            // Login successful
                            signIn.setText("Please wait...");
                            signIn.setBackgroundColor(Color.parseColor("#E8ECED"));

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

                                            signIn.setText("Sign in");
                                            signIn.setBackgroundColor(Color.parseColor("#FFC107"));

                                            Intent intent = new Intent(MainActivity.this,userHome.class);
                                            intent.putExtra("USERNAME", storedUsername);
                                            intent.putExtra("NAME", storedName);
                                            intent.putExtra("EMAIL", storedEmail);
                                            intent.putExtra("MOBILE", storedMobile);
                                            intent.putExtra("PASSWORD", storedPassword);
                                            intent.putExtra("QUESTION", storedQuestion);
                                            intent.putExtra("ANSWER", storedAnswer);
                                            startActivity(intent);
                                            Toast.makeText(MainActivity.this, "Sign in Successfully !", Toast.LENGTH_SHORT).show();
                                            username.setText("");
                                            userPassword.setText("");

                                            // Save the login status and username to SharedPreferences
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean(KEY_LOGGED_IN, true);
                                            editor.putString(KEY_USERNAME, storedUsername);
                                            editor.putString(KEY_NAME, storedName);
                                            editor.putString(KEY_EMAIL, storedEmail);
                                            editor.putString(KEY_MOBILE, storedMobile);
                                            editor.putString(KEY_PASSWORD, storedPassword);
                                            editor.putString(KEY_QUESTION, storedQuestion);
                                            editor.putString(KEY_ANSWER, storedAnswer);
                                            editor.apply();
                                        }
                                    });
                                }
                            };
                            timer.scheduleAtFixedRate(task, 0, 20);
                        } else {
                            // Incorrect username or password
                            Toast.makeText(MainActivity.this, "Incorrect password ! click on Forgot password.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // User does not exist, ask to register
                        Toast.makeText(MainActivity.this, "No user found ! please register.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        forgot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,forgotPassword.class);
            startActivity(intent);
        });

        admin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,adminLogin.class);
            startActivity(intent);
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }

    //to maintain user sign in activity
    private void navigateToHomeActivity(String username,String name,String email,String mobile,String password,String question,String answer) {
        Intent intent = new Intent(MainActivity.this, userHome.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("NAME", name);
        intent.putExtra("EMAIL", email);
        intent.putExtra("MOBILE", mobile);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("QUESTION", question);
        intent.putExtra("ANSWER", answer);
        startActivity(intent);
        finish();
    }

    //back press
    @Override
    public void onBackPressed() {
        // Redirect to the login screen or perform other actions as needed
        super.onBackPressed();
        finishAffinity();
    }

    //network connection
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
