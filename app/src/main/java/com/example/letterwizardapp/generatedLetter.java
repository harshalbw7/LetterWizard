package com.example.letterwizardapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class generatedLetter extends AppCompatActivity {
    LinearLayout letter;
    LinearLayout innerLetter;
    Button download;
    Button share;
    String url;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generated_letter);

        String username = getIntent().getStringExtra("USERNAME");
        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String mobile = getIntent().getStringExtra("MOBILE");
        String password = getIntent().getStringExtra("PASSWORD");
        String question = getIntent().getStringExtra("QUESTION");
        String answer = getIntent().getStringExtra("ANSWER");

        String letterType = getIntent().getStringExtra("TYPE");
        String letterTo = getIntent().getStringExtra("TO");
        String letterToDetails = getIntent().getStringExtra("DETAILS");
        String letterSubject = getIntent().getStringExtra("SUBJECT");
        String letterDate = getIntent().getStringExtra("DATE");
        String letterFrom = getIntent().getStringExtra("FROM");
        Uri imageUri = getIntent().getParcelableExtra("SIGN");

        EditText to = findViewById(R.id.To);
        EditText toDetails = findViewById(R.id.ToDetails);
        EditText subject = findViewById(R.id.subject);
        EditText respected = findViewById(R.id.respect);
        EditText sincerely = findViewById(R.id.sincerely);
        content = findViewById(R.id.content);
        EditText end = findViewById(R.id.end);
        EditText date = findViewById(R.id.date);
        EditText from = findViewById(R.id.from);
        ImageView sign = findViewById(R.id.signature);
        letter = findViewById(R.id.letter);
        innerLetter = findViewById(R.id.innerLetter);
        download = findViewById(R.id.download);
        share = findViewById(R.id.share);

        to.setText(letterTo);
        toDetails.setText(letterToDetails);
        subject.setText(letterSubject);
        respected.setText("R/ "+letterTo+",");
        date.setText(letterDate);

        if (imageUri != null) {
            sign.setImageURI(imageUri);
        }else{ sign.setVisibility(View.GONE); }

        if(letterType.equals("Informal")){
            end.setText("Take Care !");
            sincerely.setText("Warm regards,");
            sign.setVisibility(View.GONE);
        }
        from.setText(letterFrom);

        // Fetch random content from the Realtime Database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference().child("content");
        Query query = reference.orderByChild("subject").equalTo(letterSubject);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Collect all matching documents
                    List<DataSnapshot> matchingDocuments = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        matchingDocuments.add(snapshot);
                    }

                    // Choose a random document from the list of matching documents
                    if (!matchingDocuments.isEmpty()) {
                        Random random = new Random();
                        int randomIndex = random.nextInt(matchingDocuments.size());
                        DataSnapshot randomDocument = matchingDocuments.get(randomIndex);

                        // Get the letterContent from the randomly selected document
                        String contentValue = randomDocument.child("letterContent").getValue(String.class);

                        // Set the content to the EditText
                        content.setText("          "+contentValue);

                        // Template Fragments
                        Button template1 = findViewById(R.id.template1);
                        Button template2 = findViewById(R.id.template2);
                        Button template3 = findViewById(R.id.template3);
                        Button template4 = findViewById(R.id.template4);

                        // Create a bundle with your data
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("TYPE", letterType);
                        dataBundle.putString("TO", letterTo);
                        dataBundle.putString("DETAILS", letterToDetails);
                        dataBundle.putString("SUBJECT", letterSubject);
                        dataBundle.putString("CONTENT", "          "+contentValue);
                        dataBundle.putString("DATE", letterDate);
                        dataBundle.putString("FROM", letterFrom);
                        dataBundle.putParcelable("SIGN", imageUri);

                        // Create and initialize the fragments
                        Fragment t1 = new Template1();
                        t1.setArguments(dataBundle);

                        Fragment t2 = new Template2();
                        t2.setArguments(dataBundle);

                        Fragment t3 = new Template3();
                        t3.setArguments(dataBundle);

                        Fragment t4 = new Template4();
                        t4.setArguments(dataBundle);

                        template1.performClick();
                        template1.setOnClickListener(v -> {
                            template1.setBackgroundColor(Color.parseColor("#FFC107"));
                            template2.setBackgroundColor(Color.parseColor("#ffffff"));
                            template3.setBackgroundColor(Color.parseColor("#ffffff"));
                            template4.setBackgroundColor(Color.parseColor("#ffffff"));

                            innerLetter.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.letter,t1).commit();

                        });

                        template2.setOnClickListener(v -> {
                            template2.setBackgroundColor(Color.parseColor("#FFC107"));
                            template1.setBackgroundColor(Color.parseColor("#ffffff"));
                            template3.setBackgroundColor(Color.parseColor("#ffffff"));
                            template4.setBackgroundColor(Color.parseColor("#ffffff"));

                            innerLetter.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.letter,t2).commit();
                        });

                        template3.setOnClickListener(v -> {
                            template3.setBackgroundColor(Color.parseColor("#FFC107"));
                            template1.setBackgroundColor(Color.parseColor("#ffffff"));
                            template2.setBackgroundColor(Color.parseColor("#ffffff"));
                            template4.setBackgroundColor(Color.parseColor("#ffffff"));

                            innerLetter.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.letter,t3).commit();
                        });

                        template4.setOnClickListener(v -> {
                            template4.setBackgroundColor(Color.parseColor("#FFC107"));
                            template1.setBackgroundColor(Color.parseColor("#ffffff"));
                            template2.setBackgroundColor(Color.parseColor("#ffffff"));
                            template3.setBackgroundColor(Color.parseColor("#ffffff"));

                            innerLetter.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.letter,t4).commit();
                        });
                    }
                    else {
                        content.setText("No content found");
                    }
                } else {
                    Toast.makeText(generatedLetter.this, "No content found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur during the data retrieval
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        ImageView menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> drawer.openDrawer(findViewById(R.id.navigationView)));

        TextView welcome = findViewById(R.id.welcome);
        welcome.setText("Welcome, "+username);

        share.setOnClickListener(v -> {

            // Create a PdfDocument object
            PdfDocument pdfDocument = new PdfDocument();
            // Create a page for the PdfDocument
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(letter.getWidth(), letter.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            // Draw the layout content to the PDF page
            Canvas canvas = page.getCanvas();
            letter.draw(canvas);
            pdfDocument.finishPage(page);

            // Save the generated PDF to a file
            File file = new File(getExternalFilesDir(null), "letterWizard.pdf");
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();

            if (file.exists()) {
                Uri pdfUri = FileProvider.getUriForFile(this, "provider", file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                // Set the type of the file (PDF in this case)
                intent.setType("application/pdf");
                // Put the Uri of the PDF file as an extra to the Intent
                intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
                // Grant permission for the receiving app to read the file
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // Start the activity with the Intent
                startActivity(Intent.createChooser(intent, "Share PDF using"));
            }
            else {
                // Handle the case where the file does not exist
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            }
        });

        download.setOnClickListener(v -> {
            download.setText("Please wait..");
            download.setBackgroundColor(Color.parseColor("#E8ECED"));

            // Show the progress bar and download pdf
            ProgressBar progress = findViewById(R.id.progressBar);
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
                            progress.setVisibility(View.GONE);
                            timer.cancel();

                            download.setText("Download");
                            download.setBackgroundColor(Color.parseColor("#FFC107"));

                            // Create a PDF file in the Downloads directory
                            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            // Initial filename
                            String baseFilename = "letterWizard";
                            String filename = baseFilename + ".pdf";

                            // Check if the file already exists, and if it does, append a counter
                            int counter = 1;
                            File pdfFile = new File(downloadsDirectory, filename);
                            while (pdfFile.exists()) {
                                filename = baseFilename + "(" + counter + ").pdf";
                                pdfFile = new File(downloadsDirectory, filename);
                                counter++;
                            }

                            try {
                                // Create a PdfDocument object
                                PdfDocument pdfDocument = new PdfDocument();
                                // Create a page for the PdfDocument
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(letter.getWidth(), letter.getHeight(), 1).create();
                                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                                // Draw the layout content to the PDF page
                                Canvas canvas = page.getCanvas();
                                letter.draw(canvas);
                                // Finish the page
                                pdfDocument.finishPage(page);

                                // Save the PDF document to a file
                                pdfFile.getParentFile().mkdirs();
                                // Create an output stream for the PDF file
                                try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                                    pdfDocument.writeTo(outputStream);
                                }

                                // Assuming you have a FirebaseStorage instance
                                Random random = new Random();
                                long randomNumber = 100_000_0000L + random.nextInt(900_000_000);
                                String fname = baseFilename +"_"+randomNumber+".pdf";
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                String pdfFileName = username+"_"+fname;
                                StorageReference storageRef = storage.getReference().child(pdfFileName);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                                try {
                                    // Convert the PdfDocument to a byte array
                                    pdfDocument.writeTo(outputStream);
                                    byte[] pdfBytes = outputStream.toByteArray();

                                    // Upload the byte array to Firebase Storage
                                    UploadTask uploadTask = storageRef.putBytes(pdfBytes);

                                    // Register observers to listen for upload success or failure
                                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                            url = downloadUrl.toString();

                                            //store letter details into firebase
                                            Date currentDate = new Date();
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                                            String formattedDate = dateFormat.format(currentDate);

                                            Letters letters = new Letters();
                                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                                            DatabaseReference reference = db.getReference("letters");
                                            String key = reference.push().getKey();
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    letters.setUserName(username);
                                                    letters.setLetterType(letterType);
                                                    letters.setSubject(subject.getText().toString());
                                                    letters.setDate(formattedDate);
                                                    letters.setUrl(url);
                                                    assert key != null;
                                                    reference.child(key).setValue(letters);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {}
                                            });
                                        }).addOnFailureListener(e -> {
                                            // Handle failure to get download URL
                                            Toast.makeText(generatedLetter.this, "Failed to get URL", Toast.LENGTH_SHORT).show();
                                        });
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(generatedLetter.this, "Failed to store pdf on firebase.", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // Close the PDF document
                                pdfDocument.close();
                                //Toast.makeText(generatedLetter.this, "Your letter is downloaded. \nSee your Downloads folder !", Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(generatedLetter.this,R.style.CustomAlertDialogStyle);
                                builder.setTitle("Your letter is downloaded successfully !");
                                builder.setMessage("Please check 'downloads' folder of your device.\nOr you can check downloads section of LetterWizard.");
                                builder.setPositiveButton("Go to Downloads", (dialog, which) -> {
                                    Intent intent = new Intent(generatedLetter.this,YourLetters.class);
                                    intent.putExtra("USERNAME", username);
                                    intent.putExtra("NAME", name);
                                    intent.putExtra("EMAIL", email);
                                    intent.putExtra("MOBILE", mobile);
                                    intent.putExtra("PASSWORD", password);
                                    intent.putExtra("QUESTION", question);
                                    intent.putExtra("ANSWER", answer);
                                    startActivity(intent);
                                });
                                builder.setNegativeButton("Stay Here", (dialog, which) -> {
                                    dialog.dismiss();
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                if (positiveButton != null && negativeButton != null) {
                                    positiveButton.setTextColor(ContextCompat.getColor(generatedLetter.this,R.color.black));
                                    negativeButton.setTextColor(ContextCompat.getColor(generatedLetter.this,R.color.black));
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(task, 0, 20);
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
            Intent intent = new Intent(generatedLetter.this,letter_inputs.class);
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
            Intent intent = new Intent(generatedLetter.this,YourLetters.class);
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
            Intent intent = new Intent(generatedLetter.this,ManageUserProfile.class);
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
            Intent intent = new Intent(generatedLetter.this,UserFeedback.class);
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

                Intent intent = new Intent(generatedLetter.this,MainActivity.class);
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
            Intent intent = new Intent(generatedLetter.this,userHome.class);
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
            Intent intent = new Intent(generatedLetter.this,ManageUserProfile.class);
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
            Intent intent = new Intent(generatedLetter.this,YourLetters.class);
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
            Intent intent = new Intent(generatedLetter.this,UserFeedback.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("NAME", name);
            intent.putExtra("EMAIL", email);
            intent.putExtra("MOBILE", mobile);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("QUESTION", question);
            intent.putExtra("ANSWER", answer);
            startActivity(intent);
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
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


