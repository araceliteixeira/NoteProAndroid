package com.orion.notepro.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.controller.SubjectListAdapter;
import com.orion.notepro.model.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectListActivity extends AppCompatActivity {

    private ListView subjectList;
    private List<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        subjectList = findViewById(R.id.subject_list);
        registerForContextMenu(subjectList);

        DatabaseHelper dao = new DatabaseHelper(this);
        subjects = dao.selectAllSubjects();
        dao.close();

        final SubjectListAdapter adapter = new SubjectListAdapter(this, R.layout.activity_subject_list, subjects);
        subjectList.setAdapter(adapter);

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
}
