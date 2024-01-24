package com.example.letterwizardapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class registration extends AppCompatActivity {
    int flag ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        ProgressBar progress = findViewById(R.id.progressBar);
        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText mobile = findViewById(R.id.mobile);
        EditText userName = findViewById(R.id.userName);
        EditText password = findViewById(R.id.password);
        EditText confirm = findViewById(R.id.confirm);
        EditText answer = findViewById(R.id.answer);
        Spinner list = findViewById(R.id.question);
        CheckBox agree = findViewById(R.id.agree);
        Button signUp = findViewById(R.id.sign_Up);
        flag=0;
        //firebase
        user user = new user();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        String[] questions = {"Click to select a security question",
                "What is the name of your first pet?",
                "What is the name of your favorite teacher?",
                "What is the name of your favorite place?",
                "What is the title of your favorite book?"};
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_item, Arrays.asList(questions));
        list.setAdapter(adapter);

        signUp.setOnClickListener(v -> {
            if(name.getText().toString().equals("")){
                Toast.makeText(this, "Please insert your name !", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                if(name.getText().toString().matches("[A-Z]{1}[a-z]+[ ]{1}[A-Z]{1}[a-z]+$")){ user.setName(name.getText().toString()); }
                else{
                    Toast.makeText(registration.this, "Firstname and Lastname have first letter capital and no digits.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(email.getText().toString().equals("")){
                Toast.makeText(this, "Please insert your email ID !", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                if(email.getText().toString().matches("[a-z0-9._%+-]{3,}+@+[a-z0-9.-]+\\.[a-z]{2,3}")){ user.setEmail(email.getText().toString()); }
                else{
                    Toast.makeText(registration.this, "Please enter a valid email id.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(mobile.getText().toString().equals("")){
                Toast.makeText(this, "Please insert your mobile number !", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                if(mobile.getText().toString().matches("[7-9]{1}[0-9]{9}")){ user.setMobile(mobile.getText().toString()); }
                else{
                    Toast.makeText(registration.this, "Number must be 10 digit long and starts with 7,8 or 9.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(userName.getText().toString().equals("")){
                Toast.makeText(this, "Please insert your desired userName !", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                if (userName.getText().toString().matches("[a-z]{3,}[0-9]{1,}")) {
                    DatabaseReference reference = db.getReference("user");
                    DatabaseReference ref = db.getReference().child("user");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isUsernameExist = false;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String documentKey = dataSnapshot.getKey();
                                if (documentKey.equals(userName.getText().toString())) {
                                    isUsernameExist = true;
                                    break; // No need to continue checking, username already exists
                                }
                            }

                            if (isUsernameExist) {
                                Toast.makeText(registration.this, "UserName is already exist!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                user.setUserName(userName.getText().toString());

                                // Continue with other validations and registration logic
                                if(password.getText().toString().equals("")){
                                    Toast.makeText(registration.this, "Please insert your desired password !", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{
                                    if (password.getText().toString().matches("[A-Z]{1}(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!<>]).{6,}$")) {
                                        user.setPassword(password.getText().toString());
                                        // Continue with other validations...
                                    } else {
                                        Toast.makeText(registration.this, "First letter should be capital and have at least one digit.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                if(!confirm.getText().toString().equals(password.getText().toString())){
                                    Toast.makeText(registration.this, "Password and Confirm Password are not matching !", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Continue with the rest of your registration logic...
                                if(list.getSelectedItem().toString().equals("Click to select a security question")){
                                    Toast.makeText(registration.this, "Please select a question.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{ user.setQuestion(list.getSelectedItem().toString()); }

                                if(answer.getText().toString().equals("")){
                                    Toast.makeText(registration.this, "Please insert your answer !", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                else{
                                    if(answer.getText().toString().matches("[A-Za-z0-9@<>_-]{1,}")){ user.setAnswer(answer.getText().toString()); }
                                    else{
                                        Toast.makeText(registration.this, "Security Answer should be in one word.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                if (!agree.isChecked()) {
                                    Toast.makeText(registration.this, "Please check the checkBox.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                signUp.setText("Please wait...");
                                signUp.setBackgroundColor(Color.parseColor("#E8ECED"));

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
                                                reference.child(userName.getText().toString()).setValue(user);
                                                Toast.makeText(registration.this, "You have registered successfully.", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(registration.this, MainActivity.class);
                                                startActivity(intent);

                                                // sending mail
                                                String recipientEmail = email.getText().toString();
                                                String userNameValue = userName.getText().toString();
                                                String passwordValue = password.getText().toString();

                                                String subject = "LetterWizard Credentials";
                                                String body = "UserName: " + userNameValue + "\nPassword: " + passwordValue;
                                                String senderEmail = "letterwizard79@gmail.com";
                                                String senderPassword = "Sneharsh@79";

                                            }
                                        });
                                    }
                                };
                                timer.scheduleAtFixedRate(task, 0, 20);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });
                } else {
                    Toast.makeText(registration.this, "UserName > 4, have small letters followed by digit !", Toast.LENGTH_SHORT).show();
                    return;
                }
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
