package com.example.letterwizardapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UserFeedback extends AppCompatActivity {
    float rating = 0;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_feedback);

        String username = getIntent().getStringExtra("USERNAME");
        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String mobile = getIntent().getStringExtra("MOBILE");
        String password = getIntent().getStringExtra("PASSWORD");
        String question = getIntent().getStringExtra("QUESTION");
        String answer = getIntent().getStringExtra("ANSWER");

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+username);

        TextView userName = findViewById(R.id.userName);
        userName.setText(username);

        ProgressBar progress = findViewById(R.id.progressBar);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText Suggestion = findViewById(R.id.suggestion);
        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            String suggestion = Suggestion.getText().toString();
            rating = ratingBar.getRating();

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedDate = dateFormat.format(currentDate);

            Feedback feedback = new Feedback();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference reference = db.getReference("feedback");
            String key = reference.push().getKey();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    feedback.setUserName(username);
                    if(rating == 0){
                        Toast.makeText(UserFeedback.this, "Please give rating !", Toast.LENGTH_SHORT).show();
                        return;
                    }else { feedback.setRating(rating); }
                    if(suggestion.equals("")){
                        Toast.makeText(UserFeedback.this, "Please provide suggestion !", Toast.LENGTH_SHORT).show();
                        return;
                    }else { feedback.setSuggestion(suggestion); }
                    feedback.setDate(formattedDate);

                    submit.setText("Please wait...");
                    submit.setBackgroundColor(Color.parseColor("#E8ECED"));

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

                                    submit.setText("Submit");
                                    submit.setBackgroundColor(Color.parseColor("#FFC107"));

                                    assert key != null;
                                    reference.child(key).setValue(feedback);
                                    Toast.makeText(UserFeedback.this, "Feedback submitted !", Toast.LENGTH_SHORT).show();
                                    ratingBar.setRating(0);
                                    Suggestion.setText("");
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

        //drawer
        ImageView goBack = findViewById(R.id.goBack);
        ImageView profile = findViewById(R.id.profilePhoto);
        TextView Name = findViewById(R.id.name);
        TextView Email = findViewById(R.id.email);
        Button generate = findViewById(R.id.generate);
        Button downloads = findViewById(R.id.downloads);
        Button manage = findViewById(R.id.manage);
        Button feedback = findViewById(R.id.feedback);
        Button logout = findViewById(R.id.logOut);
        TextView UserName = findViewById(R.id.username);

        UserName.setText("@"+username);
        profile.setImageResource(R.drawable.user);
        Name.setText(name);
        Email.setText(email);

        goBack.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));

        generate.setOnClickListener(v -> {
            Intent intent = new Intent(UserFeedback.this,letter_inputs.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        downloads.setOnClickListener(v -> {
            Intent intent = new Intent(UserFeedback.this,YourLetters.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        manage.setOnClickListener(v -> {
            Intent intent = new Intent(UserFeedback.this,ManageUserProfile.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        feedback.setTextColor(Color.parseColor("#D2D2D2"));

        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure to logout ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", false);
                editor.remove("username");
                editor.apply();

                Intent intent = new Intent(UserFeedback.this,MainActivity.class);
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
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
                negativeButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //bottomLayout
        ImageView previous = findViewById(R.id.previous);
        ImageView home = findViewById(R.id.home);
        ImageView setting1 = findViewById(R.id.setting1);
        ImageView downloads1 = findViewById(R.id.downloads1);
        ImageView feedback1 = findViewById(R.id.feedback1);

        previous.setImageResource(R.drawable.previous);
        previous.setOnClickListener(v -> {
            finish();
        });

        home.setImageResource(R.drawable.home);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(UserFeedback.this,userHome.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        setting1.setImageResource(R.drawable.user_setting);
        setting1.setOnClickListener(v -> {
            Intent intent = new Intent(UserFeedback.this,ManageUserProfile.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        downloads1.setImageResource(R.drawable.downloads);
        downloads1.setOnClickListener(v -> {
            Intent intent = new Intent(UserFeedback.this,YourLetters.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        feedback1.setImageResource(R.drawable.feedback_);
        feedback1.setBackgroundColor(Color.parseColor("#FFC107"));

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
