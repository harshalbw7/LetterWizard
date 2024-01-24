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
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ValueEventListener;

public class letterDetails extends AppCompatActivity {
    public int no;
    TableLayout table;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_details);

        String userName = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String password = getIntent().getStringExtra("PASSWORD");

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+userName);

        table = findViewById(R.id.tableLayout);
        String[] headers = {" Sr.No. ", "  UserName ", "  Letter Type ", "  Subject ", "  Generated At "};
        TableRow headerRow = new TableRow(this);
        for(String column : headers){
            TextView header = new TextView(this);
            header.setText(column);
            header.setTextColor(getResources().getColor(android.R.color.black));
            header.setTypeface(null, Typeface.BOLD);
            headerRow.addView(header);
        }
        table.addView(headerRow);

        //add users to table
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child("letters");
        no = 1;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Letters letter = dataSnapshot.getValue(Letters.class);
                    //String documentKey = dataSnapshot.getKey();
                    TableRow dummy = new TableRow(letterDetails.this);
                    dummy.setPadding(0,10,0,20);
                    table.addView(dummy);

                    TableRow row = new TableRow(letterDetails.this);
                    row.setPadding(20,40,20,40);
                    row.setBackgroundResource(R.drawable.back);
                    row.setElevation(20f);
                    row.setGravity(Gravity.CENTER_VERTICAL);

                    TextView column0 = new TextView(letterDetails.this);
                    column0.setText("  " + String.valueOf(no) + ". ");
                    column0.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column0);

                    TextView column1 = new TextView(letterDetails.this);
                    column1.setText("" + letter.getUserName() + " ");
                    column1.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column1);

                    TextView column2 = new TextView(letterDetails.this);
                    column2.setText("" + letter.getLetterType() + "   ");
                    column2.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column2);

                    TextView column3 = new TextView(letterDetails.this);
                    column3.setText("" + letter.getSubject() + "   ");
                    column3.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column3);

                    TextView column4 = new TextView(letterDetails.this);
                    column4.setText("" + letter.getDate() + "   ");
                    column4.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column4);

                    table.addView(row);
                    no++;
                }
                TableRow dummy = new TableRow(letterDetails.this);
                dummy.setPadding(0,10,0,20);
                table.addView(dummy);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        //drawer
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
            Intent intent = new Intent(letterDetails.this,ManageContent.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        viewUser.setOnClickListener(v -> {
            Intent intent = new Intent(letterDetails.this, ViewUser.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        letterDetails.setTextColor(Color.parseColor("#D2D2D2"));

        viewFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(letterDetails.this,ViewFeedbacks.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure to logout ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {

                SharedPreferences sharedPreferences = getSharedPreferences("MyPre", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", false);
                editor.remove("adminname");
                editor.apply();

                Intent intent = new Intent(letterDetails.this,MainActivity.class);
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
