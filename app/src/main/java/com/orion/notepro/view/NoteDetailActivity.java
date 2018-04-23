package com.orion.notepro.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.maps.model.LatLng;
import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteDetailActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;

    @BindView(R.id.edtNoteTitle)
    TextInputEditText edtNoteTitle;

    @BindView(R.id.slider)
    SliderLayout sliderShow;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);

        initScreen();
    }

    private void initScreen() {
        addViewToSlider("Toronto", "https://www.toronto.ca/wp-content/uploads/2018/03/8842-toronto-skyline.jpg");
//        setUpToolbar(); Comentado para ser usado quando for implementar o note view
    }

    private void addViewToSlider(String description, String imagePath) {
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .description(description)
                .image(new File(imagePath));

        sliderShow.addSlider(textSliderView);
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
                saveNote();
                return true;
            default:
                Log.w("NotePro", "Menu item not implemented");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.i("NotePro", mCurrentPhotoPath);
            addViewToSlider("1 photo of 10", mCurrentPhotoPath);
        }
    }

    private void saveNote() {
        Log.i("NotePro", edtNoteTitle.getText().toString());

        //public Note(String title, String description, Subject subject, LocalDateTime dateTime, LatLng latLng) {
        final Note note = new Note(
                edtNoteTitle.getText().toString(),
                "Test Description",
                new Subject(1,"Personal", Color.BLUE),
                LocalDateTime.now(),
                new LatLng(43.653226, -79.383184));

        DatabaseHelper dao = new DatabaseHelper(this);
        dao.addNote(note);

        //Only to see if the note was saved
        List<Note> notes = dao.selectAllNotes();
        for (Note n : notes) {
            Log.i("NotePro", n.toString());
        }
    }

    private void setUpToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Test");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    public void showMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fabPhotos)
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("NotePro", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
