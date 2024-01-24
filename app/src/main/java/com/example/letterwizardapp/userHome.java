package com.example.letterwizardapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class userHome extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home);

        //mainActivity
        Button start = findViewById(R.id.start);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        String userName = getIntent().getStringExtra("USERNAME");
        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String mobile = getIntent().getStringExtra("MOBILE");
        String password = getIntent().getStringExtra("PASSWORD");
        String question = getIntent().getStringExtra("QUESTION");
        String answer = getIntent().getStringExtra("ANSWER");

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+userName);

        start.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,letter_inputs.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
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

        UserName.setText("@"+userName);
        Name.setText(name);
        Email.setText(email);

        goBack.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));

        generate.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,letter_inputs.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        downloads.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,YourLetters.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        manage.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,ManageUserProfile.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,UserFeedback.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

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

                Intent intent = new Intent(userHome.this,MainActivity.class);
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
            finishAffinity();
        });

        home.setImageResource(R.drawable.home);
        home.setBackgroundColor(Color.parseColor("#FFC107"));

        setting1.setImageResource(R.drawable.user_setting);
        setting1.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,ManageUserProfile.class);
            intent.putExtra("USERNAME", userName);
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
            Intent intent = new Intent(userHome.this,YourLetters.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        feedback1.setImageResource(R.drawable.feedback_);
        feedback1.setOnClickListener(v -> {
            Intent intent = new Intent(userHome.this,UserFeedback.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
    }

    //back press
    @Override
    public void onBackPressed() {
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