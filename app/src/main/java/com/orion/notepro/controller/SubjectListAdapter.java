package com.orion.notepro.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.notepro.R;
import com.orion.notepro.model.Subject;

import java.util.List;

public class SubjectListAdapter extends ArrayAdapter {
    public SubjectListAdapter(@NonNull Context context, int resource, @NonNull List subjects) {
        super(context, resource, subjects);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = ViewGroup.inflate(parent.getContext(), R.layout.subject_list_cell, null);
        }

        Subject subject = (Subject) getItem(position);

        if (subject != null) {
            ImageView color = (ImageView) view.findViewById(R.id.subject_cell_color);
            TextView text = (TextView) view.findViewById(R.id.subject_cell_subject);

            if (color != null) {
                color.setColorFilter(subject.getColor());
            }
            if (text != null) {
                text.setText(subject.getSubject());
            }
        }

        return view;
    }
}
