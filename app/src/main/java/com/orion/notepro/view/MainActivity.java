package com.orion.notepro.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.orion.notepro.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getApplicationContext().deleteDatabase("noteproandroid.db");
    }

    public void showSubjectList(View view) {
        Intent intent = new Intent(this, SubjectListActivity.class);
        startActivity(intent);
    }

    public void createNewNote(View view) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        startActivity(intent);
    }

    public void showNoteList(View view) {
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
    }

    public void showNotesOnMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("allNotes", 1);
        startActivity(intent);
    }
}
