package com.orion.notepro.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.notepro.R;
import com.orion.notepro.model.Note;

import java.util.List;

public class NoteListAdapter extends ArrayAdapter {
    public NoteListAdapter(@NonNull Context context, int resource, @NonNull List notes) {
        super(context, resource, notes);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = ViewGroup.inflate(parent.getContext(), R.layout.note_list_cell, null);
        }

        Note note = (Note) getItem(position);

        if (note != null) {
            ImageView color = view.findViewById(R.id.note_cell_SubjectColor);
            TextView title = view.findViewById(R.id.note_cell_title);
            TextView description = view.findViewById(R.id.note_cell_description);
            TextView dateTime = view.findViewById(R.id.note_cell_dateTime);


            if (color != null) {
                color.setColorFilter(note.getSubject().getColor());
            }
            if (title != null) {
                title.setText(note.getTitle());
            }
            if (description != null) {
                description.setText(note.getDescription());
            }
            if (dateTime != null) {
                dateTime.setText(note.getDateTimeAsString());
            }
        }

        return view;
    }
}
