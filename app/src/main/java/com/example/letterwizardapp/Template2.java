package com.example.letterwizardapp;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class Template2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.template2, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String letterType = args.getString("TYPE");
            String letterTo = args.getString("TO");
            String letterToDetails = args.getString("DETAILS");
            String letterSubject = args.getString("SUBJECT");
            String letterContent = args.getString("CONTENT");
            String letterDate = args.getString("DATE");
            String letterFrom = args.getString("FROM");
            Uri imageUri = args.getParcelable("SIGN");

            // Accessing views using the fragment's view
            EditText to = view.findViewById(R.id.To);
            EditText toDetails = view.findViewById(R.id.ToDetails);
            EditText subject = view.findViewById(R.id.subject);
            EditText respected = view.findViewById(R.id.respect);
            EditText sincerely = view.findViewById(R.id.sincerely);
            EditText content = view.findViewById(R.id.content);
            EditText end = view.findViewById(R.id.end);
            EditText date = view.findViewById(R.id.date);
            EditText from = view.findViewById(R.id.from);
            ImageView sign = view.findViewById(R.id.signature);

            to.setText(letterTo);
            toDetails.setText(letterToDetails);
            subject.setText(letterSubject);
            content.setText(letterContent);
            respected.setText("R/ " + letterTo + ",");
            date.setText(letterDate);

            if (imageUri != null) {
                sign.setImageURI(imageUri);
            } else {
                sign.setVisibility(View.GONE);
            }

            if (letterType != null && letterType.equals("Informal")) {
                end.setText("Take Care !");
                sincerely.setText("Warm regards,");
                sign.setVisibility(View.GONE);
            }
            from.setText(letterFrom);
        }
        return view;
    }
}
