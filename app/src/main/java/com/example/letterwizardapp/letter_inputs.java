package com.example.letterwizardapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class letter_inputs extends AppCompatActivity {
    String type ="";
    Spinner subject;
    Uri selectedImageUri;
    ImageView sign;
    ImageView upload;
    ProgressBar circular;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_inputs);

        String username = getIntent().getStringExtra("USERNAME");
        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String mobile = getIntent().getStringExtra("MOBILE");
        String password = getIntent().getStringExtra("PASSWORD");
        String question = getIntent().getStringExtra("QUESTION");
        String answer = getIntent().getStringExtra("ANSWER");

        //mainLayout
        ProgressBar progress = findViewById(R.id.progressBar);
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+username);

        RadioGroup letterType = findViewById(R.id.letterType);
        ImageView letterTypeInfo = findViewById(R.id.letterTypeInfo);
        EditText To = findViewById(R.id.To);
        ImageView ToInfo = findViewById(R.id.ToInfo);
        EditText toDetails = findViewById(R.id.ToDetails);
        ImageView toDetailInfo = findViewById(R.id.ToDetailInfo);
        subject = findViewById(R.id.subject);
        subject.setPrompt("Select a subject");
        ImageView subjectInfo = findViewById(R.id.subjectInfo);
        TextView date = findViewById(R.id.date);
        ImageView dateInfo = findViewById(R.id.dateInfo);
        EditText from = findViewById(R.id.from);
        from.setText(name);
        ImageView fromInfo = findViewById(R.id.fromInfo);
        LinearLayout signInfoLayout = findViewById(R.id.signInfoLayout);
        LinearLayout signLayout = findViewById(R.id.signLayout);
        sign = findViewById(R.id.sign);
        upload = findViewById(R.id.upload);
        ImageView signInfo = findViewById(R.id.signInfo);
        Button generateLetter = findViewById(R.id.generateLetter);

        //set drawer
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        //set letterType info
        letterTypeInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Select Letter Type");
            builder.setMessage("Formal : When your are writing letter to school, office, community, etc.\n\nInformal: When your are writing letter to friends, family etc.");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //set recipient info
        ToInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Insert Name of Recipient");
            builder.setMessage("Name of the person to whom you want to write a letter.");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //set recipient details info
        toDetailInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Insert Details of Recipient");
            builder.setMessage("Recipient's occupation, job role, working place, city, etc.");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //set letter subject info
        subjectInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Select Letter Subject");
            builder.setMessage("Select a similar subject that you want for letter, further you can edit it.\n\nNote: to select subject, please select letter type first !");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //set letter Date info
        dateInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Insert Letter Date");
            builder.setMessage("Date on which you want to send or write a letter to Recipient.");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //set corespondent info
        fromInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Insert Name of Correspondent");
            builder.setMessage("Name of the person who writing this letter, you can also add details like occupation, job role, working place, city, etc.");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });

        //set sign info
        signInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialogStyle);
            builder.setTitle("Upload Signature");
            builder.setMessage("Click on the below image box to upload your signature.");
            builder.setPositiveButton("Got it !", (dialog, id) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        });
        circular = findViewById(R.id.progressRound);
        upload.setOnClickListener(v -> {
            // Open the image picker when the button is clicked
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);
        });

        letterType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                type = radioButton.getText().toString();
                if(type.equals("Formal")){
                    signInfoLayout.setVisibility(View.VISIBLE);
                    signLayout.setVisibility(View.VISIBLE);
                }
                else{
                    signInfoLayout.setVisibility(View.GONE);
                    signLayout.setVisibility(View.GONE);
                }
                List<String> subjects = new ArrayList<>();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference reference = db.getReference().child("content");
                Query query = reference.orderByChild("letterType").equalTo(type);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //List<String> subjects = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            content letter = dataSnapshot.getValue(content.class);
                            String subjectName = letter.getSubject();
                            subjects.add(subjectName);
                        }
                        // Create an ArrayAdapter to populate the Spinner
                        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(letter_inputs.this, R.layout.custom_spinner_item, subjects);
                        subject.setAdapter(adapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                date.setOnClickListener(v -> {
                    // Use the current date as the default date in the picker
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            this,
                            (view, year1, monthOfYear, dayOfMonth) -> {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                                date.setText(selectedDate);
                            },
                            year, month, day
                    );
                    datePickerDialog.show();
                });

                generateLetter.setOnClickListener(v -> {

                    if(To.getText().toString().equals("")){
                        Toast.makeText(this, "Please insert name of Recipient !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(toDetails.getText().toString().equals("")){
                        Toast.makeText(this, "Please insert Details of Recipient !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(subject.getSelectedItem().toString().equals("Select Subject")){
                        Toast.makeText(this, "Please select Letter Subject !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(date.getText().toString().equals("")){
                        Toast.makeText(this, "Please select a letter Date !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(from.getText().toString().equals("")){
                        Toast.makeText(this, "Please insert name of Correspondent !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    generateLetter.setText("Please wait...");
                    generateLetter.setBackgroundColor(Color.parseColor("#E8ECED"));

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

                                    generateLetter.setText("Generate Letter");
                                    generateLetter.setBackgroundColor(Color.parseColor("#FFC107"));

                                    Intent intent = new Intent(letter_inputs.this,generatedLetter.class);
                                    intent.putExtra("USERNAME", username);
                                    intent.putExtra("NAME", name);
                                    intent.putExtra("EMAIL", email);
                                    intent.putExtra("MOBILE", mobile);
                                    intent.putExtra("PASSWORD", password);
                                    intent.putExtra("QUESTION", question);
                                    intent.putExtra("ANSWER", answer);

                                    intent.putExtra("TYPE", type);
                                    intent.putExtra("TO", To.getText().toString());
                                    intent.putExtra("DETAILS", toDetails.getText().toString());
                                    intent.putExtra("SUBJECT", subject.getSelectedItem().toString());
                                    intent.putExtra("DATE", date.getText().toString());
                                    intent.putExtra("FROM", from.getText().toString());
                                    intent.putExtra("SIGN",selectedImageUri);
                                    startActivity(intent);
                                }
                            });
                        }
                    };
                    timer.scheduleAtFixedRate(task, 0, 20);
                });

            }
            else {
                // No radio button is selected, disable the Spinner
                generateLetter.setOnClickListener(v -> Toast.makeText(letter_inputs.this, "Please select a letter type first !", Toast.LENGTH_SHORT).show());
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

        generate.setTextColor(Color.parseColor("#D2D2D2"));

        downloads.setOnClickListener(v -> {
            Intent intent = new Intent(letter_inputs.this,YourLetters.class);
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
            Intent intent = new Intent(letter_inputs.this,ManageUserProfile.class);
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
            Intent intent = new Intent(letter_inputs.this,UserFeedback.class);
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

                Intent intent = new Intent(letter_inputs.this,MainActivity.class);
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
            Intent intent = new Intent(letter_inputs.this,userHome.class);
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
            Intent intent = new Intent(letter_inputs.this,ManageUserProfile.class);
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
            Intent intent = new Intent(letter_inputs.this,YourLetters.class);
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
            Intent intent = new Intent(letter_inputs.this,UserFeedback.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                assert selectedImageUri != null;
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                circular.setVisibility(View.VISIBLE);
                upload.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sign.setImageBitmap(bitmap);
                        Toast.makeText(letter_inputs.this, "Sign uploaded successfully !", Toast.LENGTH_SHORT).show();
                        circular.setVisibility(View.GONE);
                        upload.setVisibility(View.VISIBLE);
                    }
                },2000);
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
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
