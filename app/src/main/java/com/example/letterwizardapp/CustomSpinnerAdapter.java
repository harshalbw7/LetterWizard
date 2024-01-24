package com.example.letterwizardapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    public CustomSpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        // Apply your style for the selected item (spinner closed)
        view.setBackgroundColor(Color.WHITE);
        ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.BLACK);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        // Apply your style for the dropdown items (spinner opened)
        view.setBackgroundColor(Color.BLACK);
        ((TextView) view.findViewById(android.R.id.text1)).setTextColor(Color.WHITE);
        return view;
    }
}

