package com.orion.notepro.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.maps.model.LatLng;
import com.mvc.imagepicker.ImagePicker;
import com.orion.notepro.R;
import com.orion.notepro.controller.DatabaseHelper;
import com.orion.notepro.model.Media;
import com.orion.notepro.model.MediaType;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;
import com.orion.notepro.util.PlaybackInfoListener;
import com.orion.notepro.util.Player;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.RECORD_AUDIO;

public class NoteDetailActivity extends AppCompatActivity {

    static final String TAG = "NotePro";
    static final int REQUEST_TAKE_PHOTO = 1;

    @BindView(R.id.edtNoteTitle)
    TextInputEditText edtNoteTitle;

    @BindView(R.id.slider)
    SliderLayout sliderShow;

    @BindView(R.id.recordButton)
    RecordButton audioRecordButton;

    @BindView(R.id.recordView)
    RecordView audioRecordView;

    @BindView(R.id.audioSeekBar)
    SeekBar audioSeekBar;

    private String mCurrentPhotoPath;
    private List<Media> medias = new ArrayList<>();
    private Note noteToEdit;
    private boolean isUserSeeking = false;

    private final Player recorder = new Player();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);

        initScreen();
    }

    private void initScreen() {
        ImagePicker.setMinQuality(600, 600);
//        setUpToolbar(); Comentado para ser usado quando for implementar o note view

        prepareAudioRecordButton();
        prepareAudioSeekbar();

        if (isToEditNote()) {
            prepareToEditNote();
        }
    }

    private void prepareAudioRecordButton() {
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, 0);
        recorder.setPlaybackInfoListener(new PlaybackListener());
        audioRecordButton.setRecordView(audioRecordView);
        audioRecordView.setSoundEnabled(true);
        audioRecordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                Log.d("RecordView", "onStart");
                recorder.releasePlaying();
                recorder.startRecording(getApplicationContext());
            }

            @Override
            public void onCancel() {
                Log.d("RecordView", "onCancel");
                recorder.stopRecording();
            }

            @Override
            public void onFinish(long recordTime) {
                recorder.stopRecording();
                addAudioMedia(recorder.getFileName());
                audioSeekBar.setMax(new Long(recordTime).intValue());
                Log.d("RecordView", "onFinish. File name: " + recorder.getFileName() + ". RecordTime: " + new Long(recordTime).intValue());
            }

            @Override
            public void onLessThanSecond() {
                Log.d("RecordView", "onLessThanSecond");
            }
        });
    }

    private void addAudioMedia(String fileName) {
        medias.removeIf(new Predicate<Media>() {
            @Override
            public boolean test(Media media) {
                return MediaType.AUDIO.equals(media.getType());
            }
        });
        medias.add(new Media(new File(recorder.getFileName()), MediaType.AUDIO));
    }

    private void prepareAudioSeekbar() {
        audioSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isUserSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isUserSeeking = false;
                        recorder.seekTo(userSelectedPosition);
                    }
                });
    }

    private boolean isToEditNote() {
        noteToEdit = (Note) getIntent().getSerializableExtra("note");
        return noteToEdit != null;
    }

    private void prepareToEditNote() {
        Log.i(TAG, "Edit note: " + noteToEdit.toString());
        edtNoteTitle.setText(noteToEdit.getTitle());
        noteToEdit.getMedias().forEach(new Consumer<Media>() {
            @Override
            public void accept(Media media) {
                addViewToSlider("Test", media.getMediaFile().getAbsolutePath());
                medias.add(media);
            }
        });
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
        recorder.releasePlayers();
        super.onStop();
    }

    @OnClick(R.id.fabPhotos)
    public void dispatchTakePictureIntent() {
        ImagePicker.pickImage(this, "Select your image:", REQUEST_TAKE_PHOTO, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            mCurrentPhotoPath = ImagePicker.getImagePathFromResult(this, requestCode, resultCode, data);
            Log.i(TAG, mCurrentPhotoPath);
            medias.add(new Media(new File(mCurrentPhotoPath), MediaType.PHOTO));
            addViewToSlider("1 photo of 10", mCurrentPhotoPath);
        }
    }

    private void saveNote() {
        Log.i(TAG, edtNoteTitle.getText().toString());
        Note note;
        if (isToEditNote()) {
            noteToEdit.setTitle(edtNoteTitle.getText().toString());
            noteToEdit.setSubject(new Subject(1,"Personal", Color.BLUE));
            noteToEdit.setDateTime(LocalDateTime.now());
            noteToEdit.setLatLng(new LatLng(43.653226, -79.383184));
            note = noteToEdit;
        } else {
            note = new Note(
                edtNoteTitle.getText().toString(),
                "Test Description",
                new Subject(1,"Personal", Color.BLUE),
                LocalDateTime.now(),
                new LatLng(43.653226, -79.383184));
        }

        DatabaseHelper dao = new DatabaseHelper(this);
        note.setMedias(medias);
        dao.save(note);

        //Only to see if the note was saved
        List<Note> notes = dao.selectAllNotes();
        for (Note n : notes) {
            Log.i(TAG, n.toString());
        }

        finish();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.playButton)
    public void onStartPlay() {
        recorder.startPlaying();
    }

    @OnClick(R.id.pauseButton)
    public void onPausePlay() {
        recorder.pausePlaying();
    }

    class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            audioSeekBar.setMax(duration);
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
            if (!isUserSeeking) {
                audioSeekBar.setProgress(position, true);
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
            }
        }

        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            Log.i(TAG, String.format("onStateChanged(%s)", stateToString));
        }

        @Override
        public void onPlaybackCompleted() {
        }

    }
}
