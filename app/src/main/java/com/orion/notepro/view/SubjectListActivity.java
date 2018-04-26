package com.orion.notepro.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.*;
import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.controller.SubjectListAdapter;
import com.orion.notepro.model.Subject;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.List;

public class SubjectListActivity extends AppCompatActivity {

    private SwipeMenuListView subjectList;
    private List<Subject> subjects;
    private SubjectListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        subjectList = findViewById(R.id.subject_list);
        registerForContextMenu(subjectList);

        loadSubjectList();

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = (Subject) subjectList.getItemAtPosition(position);

                Intent intent = new Intent(SubjectListActivity.this, NoteListActivity.class);
                intent.putExtra("subject", subject);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        loadSubjectList();
        super.onResume();
    }

    private void loadSubjectList() {
        DatabaseHelper dao = new DatabaseHelper(this);
        subjects = dao.selectAllSubjects();

        if(subjects.size() == 0) {
            dao.addSomeSubjects();
            subjects = dao.selectAllSubjects();
        }

        adapter = new SubjectListAdapter(this, R.layout.activity_subject_list, subjects);
        subjectList.setAdapter(adapter);

        configureSwipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_button_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                Intent intent = new Intent(this, SubjectDetailActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_left:
                subjectList.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
                return true;
            case R.id.action_right:
                subjectList.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        subjectList.setMenuCreator(creator);

        subjectList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Subject subject = (Subject) subjectList.getItemAtPosition(position);
                switch (index) {
                    case 0:
                        Intent intent = new Intent(SubjectListActivity.this, SubjectDetailActivity.class);
                        intent.putExtra("subject", subject);
                        startActivity(intent);
                        break;
                    case 1:
                        deleteSubject(subject);
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

    private void deleteSubject(Subject subject) {
        final Subject subj = subject;
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Do you really want to delete this subject?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseHelper dao = new DatabaseHelper(SubjectListActivity.this);
                        dao.deleteSubject(subj);
                        dao.close();
                        loadSubjectList();
                        Toast.makeText(SubjectListActivity.this, "Subject deleted", Toast.LENGTH_SHORT).show();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
