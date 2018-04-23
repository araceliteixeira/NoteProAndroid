package com.orion.notepro.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.controller.NoteListAdapter;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ListView noteList;
    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        noteList = findViewById(R.id.note_list);
        registerForContextMenu(noteList);

        Subject subject = (Subject) getIntent().getSerializableExtra("subject");

        DatabaseHelper dao = new DatabaseHelper(this);
        if (subject == null) {
            notes = dao.selectAllNotes();
        } else {
            notes = dao.selectNotesBySubject(subject);
        }

        if(notes.size() == 0) {
            dao.addSomeNotes();
            if (subject == null) {
                notes = dao.selectAllNotes();
            } else {
                notes = dao.selectNotesBySubject(subject);
            }
        }

        final NoteListAdapter adapter = new NoteListAdapter(this, R.layout.activity_note_list, notes);
        noteList.setAdapter(adapter);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = (Note) noteList.getItemAtPosition(position);

                Intent intent = new Intent(NoteListActivity.this, NoteDetailActivity.class);
                intent.putExtra("note", note);
                startActivity(intent);
            }
        });
    }
}
