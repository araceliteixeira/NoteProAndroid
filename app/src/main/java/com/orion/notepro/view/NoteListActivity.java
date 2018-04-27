package com.orion.notepro.view;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.controller.NoteListAdapter;
import com.orion.notepro.controller.SubjectListAdapter;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private SwipeMenuListView noteList;
    private List<Note> notes;
    private List<Note> newNotes;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private NoteListAdapter adapter;
    private DatabaseHelper dao = new DatabaseHelper(NoteListActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        noteList = findViewById(R.id.note_list);
        registerForContextMenu(noteList);

        loadNoteList(null);

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
    protected void onResume() {
        loadNoteList(null);
        super.onResume();
    }

    private void loadNoteList(String aux) {
        Subject subject = (Subject) getIntent().getSerializableExtra("subject");

        DatabaseHelper dao = new DatabaseHelper(this);

        if (aux == null) {
            if (subject == null) {
                notes = dao.selectAllNotes();
                System.out.println("111");

            } else {
                notes = dao.selectNotesBySubject(subject);
            }
            if (notes.isEmpty()) {
                dao.addSomeNotes();
                if (subject == null) {
                    notes = dao.selectAllNotes();
                    System.out.println("2222");
                } else {
                    notes = dao.selectNotesBySubject(subject);
                }
            }
        } else if (aux == "t") {
            notes = dao.sortNotesByTitle();
            System.out.println("ttttt");
        } else if (aux == "d") {
            notes = dao.sortNotesByDate();
            System.out.println("dddddd");
        } else {
            notes = dao.searchNotesByTitleOrDesc(aux);
            System.out.println("search");
        }

        adapter = new NoteListAdapter(this, R.layout.activity_note_list, notes);
        noteList.setAdapter(adapter);

        configureSwipe();
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
                loadNoteList(s);
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
                loadNoteList("t");
                return true;
            case R.id.sort_date:
                loadNoteList("d");
                return true;
            case R.id.action_left:
                noteList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
                return true;
            case R.id.action_right:
                noteList.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
                return true;
            default:
                Log.w("NotePro", "Menu item not implemented");
                return super.onOptionsItemSelected(item);
        }
    }

    private void configureSwipe() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.GRAY));
                // set item width
                editItem.setWidth(dp2px(90));
                // set item title
                editItem.setTitle("Edit");
                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        noteList.setMenuCreator(creator);

        noteList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Note note = (Note) noteList.getItemAtPosition(position);
                switch (index) {
                    case 0:
                        Intent intent = new Intent(NoteListActivity.this, NoteDetailActivity.class);
                        intent.putExtra("note", note);
                        startActivity(intent);
                        break;
                    case 1:
                        deleteNote(note);
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void deleteNote(Note note) {
        final Note note_ = note;
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Do you really want to delete this note?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseHelper dao = new DatabaseHelper(NoteListActivity.this);
                        dao.deleteNote(note_);
                        dao.close();
                        loadNoteList(null);
                        Toast.makeText(NoteListActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
