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

public class adminLogin extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    String storedUsername;
    String storedEmail;
    String storedPassword;
    private static final String PREF_NAME = "MyPre";
    private static final String KEY_ADMINNAME = "adminname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOGGED_IN = "loggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Check if the user is already logged in
        if (sharedPreferences.getBoolean(KEY_LOGGED_IN, false)) {
            String storedUsername = sharedPreferences.getString(KEY_ADMINNAME, "");
            String storedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String storedPassword = sharedPreferences.getString(KEY_PASSWORD, "");

            if (!storedUsername.isEmpty()) {
                // User is already logged in, navigate to home activity
                navigateToHomeActivity(storedUsername, storedEmail, storedPassword);
                return;
            }
        }

        ProgressBar progress = findViewById(R.id.progressBar);
        EditText adminName = findViewById(R.id.userName);
        EditText adimPassword = findViewById(R.id.password);
        Button login = findViewById(R.id.login);

        admin admin = new admin();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        login.setOnClickListener(v -> {

            String userName = adminName.getText().toString();
            String password = adimPassword.getText().toString();
            if(userName.equals("") && password.equals("")){
                Toast.makeText(this, "Please insert adminID and Password !", Toast.LENGTH_LONG).show();
                return;
            }
            if(userName.equals("")){
                Toast.makeText(this, "Please insert adminID !", Toast.LENGTH_LONG).show();
                return;
            }
            if(password.equals("")){
                Toast.makeText(this, "Please insert your Password !", Toast.LENGTH_LONG).show();
                return;
            }
            DatabaseReference reference = db.getReference("admin").child(userName);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        storedUsername = snapshot.child("userName").getValue(String.class);
                        storedEmail = snapshot.child("email").getValue(String.class);
                        storedPassword = snapshot.child("password").getValue(String.class);

                        if (storedUsername != null && storedPassword != null &&
                                storedUsername.equals(userName) && storedPassword.equals(password)) {
                            // Login successful
                            login.setText("Please wait...");
                            login.setBackgroundColor(Color.parseColor("#E8ECED"));
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

                                            login.setText("Login");
                                            login.setBackgroundColor(Color.parseColor("#FFC107"));

                                            Intent intent = new Intent(adminLogin.this,adminHome.class);
                                            intent.putExtra("USERNAME", storedUsername);
                                            intent.putExtra("EMAIL", storedEmail);
                                            intent.putExtra("PASSWORD", storedPassword);
                                            startActivity(intent);
                                            Toast.makeText(adminLogin.this, "Login Successfully !", Toast.LENGTH_SHORT).show();
                                            adminName.setText("");
                                            adimPassword.setText("");

                                            // Save the login status and username to SharedPreferences
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean(KEY_LOGGED_IN, true);
                                            editor.putString(KEY_ADMINNAME, storedUsername);
                                            editor.putString(KEY_EMAIL, storedEmail);
                                            editor.putString(KEY_PASSWORD, storedPassword);
                                            editor.apply();
                                        }
                                    });
                                }
                            };
                            timer.scheduleAtFixedRate(task, 0, 20);
                        } else {
                            // Incorrect username or password
                            Toast.makeText(adminLogin.this, "Incorrect password ! ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // User does not exist, ask to register
                        Toast.makeText(adminLogin.this, "No admin found !", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }

    //to maintain user sign in activity
    private void navigateToHomeActivity(String adminname, String email, String password) {
        Intent intent = new Intent(adminLogin.this, adminHome.class);
        intent.putExtra("USERNAME", adminname);
        intent.putExtra("EMAIL", email);
        intent.putExtra("PASSWORD", password);
        startActivity(intent);
        finish();
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
