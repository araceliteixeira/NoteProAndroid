package com.orion.notepro.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.controller.NoteListAdapter;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ListView noteList;
    private List<Note> notes;
    private SearchView searchView;
    private MenuItem searchMenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_list_menu, menu);

        inflater.inflate(R.menu.note_list_search, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //searchMenuItem = menu.findItem(R.id.search);
        //searchView = (SearchView) searchMenuItem.getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        //searchView.setOnQueryTextListener(this);


        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("onQueryTextSubmit ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                System.out.println("onQueryTextChange ");
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_title:
                sortTitle();
                return true;
            case R.id.sort_date:
                sortDate();
                return true;
            default:
                Log.w("NotePro", "Menu item not implemented");
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortTitle () {
        Collections.sort(notes, new Comparator<Note>(){
            public int compare(Note obj1, Note obj2) {
                return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
                // Descending order
                // return obj2.getTitle().compareToIgnoreCase(obj1.getTitle());
            }
        });
    }
    private void sortDate () {
        Collections.sort(notes, new Comparator<Note>(){
            public int compare(Note obj1, Note obj2) {
                return obj1.getDateTime().compareTo(obj2.getDateTime());
            }
        });
    }


}
