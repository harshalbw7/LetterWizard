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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ManageContent extends AppCompatActivity {
    public int no;
    TableLayout table;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_content);

        String userName = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String password = getIntent().getStringExtra("PASSWORD");

        Button formalContents = findViewById(R.id.formalContents);
        Button informalContents = findViewById(R.id.informalContents);
        table = findViewById(R.id.tableLayout);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+userName);

        String[] headers = {" Sr.No. ", "  Letter Type ", "  Letter Subject "};
        TableRow headerRow = new TableRow(this);
        for(String column : headers){
            TextView header = new TextView(this);
            header.setText(column);
            header.setTextColor(getResources().getColor(android.R.color.black));
            header.setTypeface(null, Typeface.BOLD);
            headerRow.addView(header);
        }
        table.addView(headerRow);

        formalContents.setOnClickListener(v -> {
            table.removeViews(1, table.getChildCount() - 1);
            String type = "Formal";
            addContentToTable(type);
        });
        informalContents.setOnClickListener(v -> {
            table.removeViews(1, table.getChildCount() - 1);
            String type = "Informal";
            addContentToTable(type);
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

        manageContent.setTextColor(Color.parseColor("#D2D2D2"));

        viewUser.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContent.this, ViewUser.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        letterDetails.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContent.this,letterDetails.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            startActivity(intent);
        });

        viewFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(ManageContent.this,ViewFeedbacks.class);
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

                Intent intent = new Intent(ManageContent.this,MainActivity.class);
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
    //add content to table
    public void addContentToTable(String type){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child("content");
        Query query = reference.orderByChild("letterType").equalTo(type);
        no = 1;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    content letter = dataSnapshot.getValue(content.class);
                    String documentKey = dataSnapshot.getKey();

                    TableRow dummy = new TableRow(ManageContent.this);
                    dummy.setPadding(0,10,0,20);
                    table.addView(dummy);

                    TableRow row = new TableRow(ManageContent.this);
                    row.setPadding(20,40,20,40);
                    row.setBackgroundResource(R.drawable.back);
                    row.setElevation(20f);
                    row.setGravity(Gravity.CENTER_VERTICAL);

                    TextView column0 = new TextView(ManageContent.this);
                    column0.setText("  " + String.valueOf(no) + ". ");
                    column0.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column0);

                    TextView column1 = new TextView(ManageContent.this);
                    column1.setText("" + letter.getLetterType() + " ");
                    column1.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column1);

                    TextView column2 = new TextView(ManageContent.this);
                    column2.setText("" + letter.getSubject() + "   ");
                    column2.setTextColor(getResources().getColor(android.R.color.black));
                    row.addView(column2);

                    ImageView column4 = new ImageView(ManageContent.this);
                    column4.setImageResource(R.drawable.delete);

                    column4.setOnClickListener(v1 -> {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageContent.this,R.style.CustomAlertDialogStyle);
                        builder.setTitle("Delete Subject");
                        builder.setMessage("Are you sure to delete ?");

                        builder.setPositiveButton("Yes", (dialog, which) -> {
                            assert documentKey != null;
                            DatabaseReference reference = db.getReference("content").child(documentKey);
                            reference.removeValue();
                            table.removeView(row);
                            Toast.makeText(ManageContent.this, "Subject deleted.", Toast.LENGTH_SHORT).show();
                        });
                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        if (positiveButton != null && negativeButton != null) {
                            positiveButton.setTextColor(ContextCompat.getColor(ManageContent.this,R.color.black));
                            negativeButton.setTextColor(ContextCompat.getColor(ManageContent.this,R.color.black));
                        }
                    });
                    row.addView(column4);
                    table.addView(row);
                    no++;
                }
                TableRow dummy = new TableRow(ManageContent.this);
                dummy.setPadding(0,10,0,20);
                table.addView(dummy);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
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
