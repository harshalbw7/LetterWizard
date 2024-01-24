package com.example.letterwizardapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

public class ManageUserProfile extends AppCompatActivity {
    @SuppressLint("ResourceAsColor")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_user_profile);

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        TextView updateUserName = findViewById(R.id.updateUserName);
        EditText updateName = findViewById(R.id.updateName);
        EditText updateEmail = findViewById(R.id.updateEmail);
        EditText updateMobile = findViewById(R.id.updateMobile);
        EditText updatePassword = findViewById(R.id.updatePassword);
        Button update = findViewById(R.id.update);
        Button delete = findViewById(R.id.delete);

        user user = new user();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String username = getIntent().getStringExtra("USERNAME");
        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String mobile = getIntent().getStringExtra("MOBILE");
        String password = getIntent().getStringExtra("PASSWORD");
        String question = getIntent().getStringExtra("QUESTION");
        String answer = getIntent().getStringExtra("ANSWER");

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+username);

        updateUserName.setText(username);
        updateName.setText(name);
        updateEmail.setText(email);
        updateMobile.setText(mobile);
        updatePassword.setText(password);

        String userName = updateUserName.getText().toString();

        update.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserProfile.this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Update Profile");
            builder.setMessage("Are you sure to update Profile ?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                DatabaseReference reference = database.getReference("user").child(userName);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            user.setUserName(username);

                            if(updateName.getText().toString().matches("[A-Z]{1}[a-z]+[ ]{1}[A-Z]{1}[a-z]+$")){ user.setName(updateName.getText().toString()); }
                            else{
                                Toast.makeText(ManageUserProfile.this, "Firstname and Lastname have first letter capital and no digits.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(updateEmail.getText().toString().matches("[a-z0-9._%+-]{3,}+@+[a-z0-9.-]+\\.[a-z]{2,3}")){ user.setEmail(updateEmail.getText().toString()); }
                            else{
                                Toast.makeText(ManageUserProfile.this, "Please enter a valid email id.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(updateMobile.getText().toString().matches("[7-9]{1}[0-9]{9}")){ user.setMobile(updateMobile.getText().toString()); }
                            else{
                                Toast.makeText(ManageUserProfile.this, "Number must be 10 digit long and starts with 7,8 or 9.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (updatePassword.getText().toString().matches("[A-Z]{1}(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!<>]).{6,}$")) { user.setPassword(updatePassword.getText().toString()); }
                            else {
                                Toast.makeText(ManageUserProfile.this, "Password > 6 characters, have first letter capital and at least one digit.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            user.setQuestion(question);
                            user.setAnswer(answer);
                            reference.setValue(user);
                            Toast.makeText(ManageUserProfile.this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(ManageUserProfile.this, "No record to update.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alert = builder.create();
            alert.show();
            Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (positiveButton != null && negativeButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(ManageUserProfile.this, R.color.black));
                negativeButton.setTextColor(ContextCompat.getColor(ManageUserProfile.this, R.color.black));
            }
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageUserProfile.this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Delete Profile");
            builder.setMessage("You are no longer able to access LetterWizard !");
            builder.setPositiveButton("Continue", (dialog, which) -> {
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
                dialogbuilder.setTitle("Delete Profile");
                dialogbuilder.setMessage("Are you sure to delete Profile ?");
                dialogbuilder.setPositiveButton("Yes", (dialogInterface, id) -> {
                    DatabaseReference reference = database.getReference("user").child(userName);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                reference.removeValue();
                                Toast.makeText(ManageUserProfile.this, "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ManageUserProfile.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else
                                Toast.makeText(ManageUserProfile.this, "No record to delete.", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                });
                dialogbuilder.setNegativeButton("Cancel", (Dialog, Which) -> {
                    Dialog.dismiss();
                });
                AlertDialog alertdialog = dialogbuilder.create();
                alertdialog.show();
                Button positiveButton = alertdialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = alertdialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (positiveButton != null && negativeButton != null) {
                    positiveButton.setTextColor(ContextCompat.getColor(ManageUserProfile.this, R.color.black));
                    negativeButton.setTextColor(ContextCompat.getColor(ManageUserProfile.this, R.color.black));
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alert = builder.create();
            alert.show();
            Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (positiveButton != null && negativeButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(ManageUserProfile.this, R.color.black));
                negativeButton.setTextColor(ContextCompat.getColor(ManageUserProfile.this, R.color.black));
            }
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
            Intent intent = new Intent(ManageUserProfile.this,letter_inputs.class);
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
            Intent intent = new Intent(ManageUserProfile.this,YourLetters.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        manage.setTextColor(Color.parseColor("#D2D2D2"));

        feedback.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUserProfile.this,UserFeedback.class);
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

                Intent intent = new Intent(ManageUserProfile.this,MainActivity.class);
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
            Intent intent = new Intent(ManageUserProfile.this,userHome.class);
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
        setting1.setBackgroundColor(Color.parseColor("#FFC107"));

        downloads1.setImageResource(R.drawable.downloads);
        downloads1.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUserProfile.this,YourLetters.class);
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
        feedback1.setOnClickListener(v -> {
            Intent intent = new Intent(ManageUserProfile.this,UserFeedback.class);
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
