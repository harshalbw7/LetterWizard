package com.example.letterwizardapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.List;

public class YourLetters extends AppCompatActivity {
    public int no;
    TableLayout table;
    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_letters);

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

        table = findViewById(R.id.tableLayout);
        TextView notFound = findViewById(R.id.notFound);
        Button more = findViewById(R.id.more);

        //add urls to table
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child("letters");
        Query query = reference.orderByChild("userName").equalTo(username);
        no = 1;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    notFound.setVisibility(View.GONE);
                    more.setText("Generate More Letters");

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Letters letter = dataSnapshot.getValue(Letters.class);
                        String documentKey = dataSnapshot.getKey();

                        TableRow row = new TableRow(YourLetters.this);
                        row.setPadding(20,20,20,20);
                        row.setBackgroundResource(R.drawable.back);
                        row.setElevation(20f);
                        row.setGravity(Gravity.CENTER_VERTICAL);

                        TextView column0 = new TextView(YourLetters.this);
                        column0.setText("  " + String.valueOf(no) + ". ");
                        column0.setTextColor(getResources().getColor(android.R.color.black));
                        row.addView(column0);

                        ImageView image = new ImageView(YourLetters.this);
                        image.setImageResource(R.drawable.pdf);
                        image.setPadding(0,0,20,0);
                        row.addView(image);

                        TextView column1 = new TextView(YourLetters.this);
                        column1.setText("LetterWizard_"+letter.getSubject()+".pdf");
                        column1.setTextColor(getResources().getColor(android.R.color.black));
                        column1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        row.addView(column1);

                        ImageView column2 = new ImageView(YourLetters.this);
                        column2.setImageResource(R.drawable.delete);
                        column2.setOnClickListener(v1 -> {

                            AlertDialog.Builder builder = new AlertDialog.Builder(YourLetters.this,R.style.CustomAlertDialogStyle);
                            builder.setTitle("Delete Letter");
                            builder.setMessage("Are you sure to delete letter?");

                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                assert documentKey != null;
                                DatabaseReference reference = db.getReference("letters").child(documentKey);
                                reference.removeValue();
                                table.removeView(row);
                                Toast.makeText(YourLetters.this, "Letter deleted successfully !", Toast.LENGTH_SHORT).show();
                            });
                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                            Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            if (positiveButton != null && negativeButton != null) {
                                positiveButton.setTextColor(ContextCompat.getColor(YourLetters.this,R.color.black));
                                negativeButton.setTextColor(ContextCompat.getColor(YourLetters.this,R.color.black));
                            }
                        });
                        row.addView(column2);

                        row.setOnClickListener(view -> {
                            // Handle row click here
                            String url = letter.getUrl();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(url), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        });

                        table.addView(row);
                        no++;
                        TableRow dummy = new TableRow(YourLetters.this);
                        dummy.setPadding(0,10,0,20);
                        table.addView(dummy);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        more.setOnClickListener(v -> {
            Intent intent = new Intent(YourLetters.this,letter_inputs.class);
            intent.putExtra("USERNAME", username);
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

        UserName.setText("@"+username);
        profile.setImageResource(R.drawable.user);
        Name.setText(name);
        Email.setText(email);

        goBack.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));

        generate.setOnClickListener(v -> {
            Intent intent = new Intent(YourLetters.this,letter_inputs.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        downloads.setTextColor(Color.parseColor("#D2D2D2"));

        manage.setOnClickListener(v -> {
            Intent intent = new Intent(YourLetters.this,ManageUserProfile.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(YourLetters.this,UserFeedback.class);
            intent.putExtra("USERNAME", username);
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

                Intent intent = new Intent(YourLetters.this,MainActivity.class);
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
            Intent intent = new Intent(YourLetters.this,userHome.class);
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
            Intent intent = new Intent(YourLetters.this,ManageUserProfile.class);
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
        downloads1.setBackgroundColor(Color.parseColor("#FFC107"));

        feedback1.setImageResource(R.drawable.feedback_);
        feedback1.setOnClickListener(v -> {
            Intent intent = new Intent(YourLetters.this,UserFeedback.class);
            intent.putExtra("USERNAME", username);
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
