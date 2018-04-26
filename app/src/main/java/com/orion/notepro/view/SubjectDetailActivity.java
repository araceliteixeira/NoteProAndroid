package com.orion.notepro.view;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.model.Subject;
import com.orion.notepro.util.ColorPickerDialog;

public class SubjectDetailActivity extends AppCompatActivity {
    Subject subject;
    EditText description;
    Button color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        description = findViewById(R.id.subject_detail_description);
        color = findViewById(R.id.pick_color);

        subject = (Subject) getIntent().getSerializableExtra("subject");

        if (subject != null) {
            description.setText(subject.getSubject());
            color.setBackgroundColor(subject.getColor());
        }
    }

    public void showColorPicker(View view) {
        if (color.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) color.getBackground();
            int colorCode = cd.getColor();

            new ColorPickerDialog(SubjectDetailActivity.this, new UpdateColor(), colorCode).show();
        }
    }

    public class UpdateColor implements ColorPickerDialog.OnColorChangedListener {
        public void colorChanged(int color) {
            SubjectDetailActivity.this.color.setBackgroundColor(color);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_detail_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save_note_menu:
                saveSubject();
                return true;
            default:
                Log.w("NotePro", "Menu item not implemented");
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveSubject() {
        DatabaseHelper dao = new DatabaseHelper(this);

        if (subject != null) {
            subject.setSubject(description.getText().toString());
            if (color.getBackground() instanceof ColorDrawable) {
                ColorDrawable cd = (ColorDrawable) color.getBackground();
                int colorCode = cd.getColor();
                subject.setColor(colorCode);
            }
            dao.updateSubject(subject);
        } else {
            String text = description.getText().toString();
            int colorCode = 0;
            if (color.getBackground() instanceof ColorDrawable) {
                ColorDrawable cd = (ColorDrawable) color.getBackground();
                colorCode = cd.getColor();
            }
            subject = new Subject(text, colorCode);
            dao.addSubject(subject);
        }
        dao.close();
        finish();
    }
}
