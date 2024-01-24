package com.example.letterwizardapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class adminHome extends AppCompatActivity {
    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);

        String userName = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String password = getIntent().getStringExtra("PASSWORD");

        ProgressBar progress = findViewById(R.id.progressBar);
        RadioGroup letterType = findViewById(R.id.letterType);
        EditText letterSubject = findViewById(R.id.subject);
        EditText letterContent = findViewById(R.id.content);
        Button add = findViewById(R.id.addContent);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, " + userName);

        letterType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                type = radioButton.getText().toString();

                add.setOnClickListener(v -> {
                    content content = new content();
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReference("content");
                    String key = reference.push().getKey();

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            content.setLetterType(type);

                            if (letterSubject.getText().toString().equals("")) {
                                Toast.makeText(adminHome.this, "Please insert letter subject !", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                content.setSubject(letterSubject.getText().toString());
                            }
                            if (letterContent.getText().toString().equals("")) {
                                Toast.makeText(adminHome.this, "Please insert letter content !", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                content.setLetterContent(letterContent.getText().toString());
                            }

                            add.setText("Please wait...");
                            add.setBackgroundColor(Color.parseColor("#E8ECED"));

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

                                            add.setText("ADD");
                                            add.setBackgroundColor(Color.parseColor("#FFC107"));

                                            assert key != null;
                                            reference.child(key).setValue(content);
                                            Toast.makeText(adminHome.this, "Content added successfully !", Toast.LENGTH_SHORT).show();
                                            letterType.clearCheck();
                                            letterSubject.setText("");
                                            letterContent.setText("");
                                        }
                                    });
                                }
                            };
                            timer.scheduleAtFixedRate(task, 0, 20);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });

            }
            else {
                // No radio button is selected
                add.setOnClickListener(v -> Toast.makeText(adminHome.this, "Please select a letter type first !", Toast.LENGTH_SHORT).show());
            }
        });

        ///drawer
        ImageView goBack = findViewById(R.id.goBack);
        ImageView profile = findViewById(R.id.profilePhoto);
        TextView Email = findViewById(R.id.email);
        Button manageContent = findViewById(R.id.manageContent);
        Button viewUser = findViewById(R.id.viewUsers);
        Button letterDetails = findViewById(R.id.viewLetters);
        Button viewFeedback = findViewById(R.id.viewFeedback);
        Button logout = findViewById(R.id.logOut);

        profile.setImageResource(R.drawable.user);
        Email.setText(email);

        goBack.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));

        manageContent.setOnClickListener(v -> {
            Intent intent = new Intent(adminHome.this, ManageContent.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        viewUser.setOnClickListener(v -> {
            Intent intent = new Intent(adminHome.this, ViewUser.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        letterDetails.setOnClickListener(v -> {
            Intent intent = new Intent(adminHome.this, letterDetails.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        viewFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(adminHome.this, ViewFeedbacks.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure to logout ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {

                SharedPreferences sharedPreferences = getSharedPreferences("MyPre", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", false);
                editor.remove("adminname");
                editor.apply();

                Intent intent = new Intent(adminHome.this, MainActivity.class);
                startActivity(intent);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alert = builder.create();
            alert.show();
            Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (positiveButton != null && negativeButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.black));
                negativeButton.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    //back press
    @Override
    public void onBackPressed() {
        // Redirect to the login screen or perform other actions as needed
        super.onBackPressed();
        finishAffinity();
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
