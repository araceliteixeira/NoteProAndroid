package com.orion.notepro.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.constants.ProviderType;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.RECORD_AUDIO;

public class NoteDetailActivity extends AppCompatActivity {

    static final String TAG = "NotePro";
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int LOCATION_PERMISSION_ID = 1001;

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

    @BindView(R.id.edtNoteDescription)
    TextInputEditText edtNoteDescription;

    @BindView(R.id.edtNoteSubject)
    Spinner edtNoteSubject;

    private String mCurrentPhotoPath;
    private List<Media> medias = new ArrayList<>();
    private List<Subject> subjects = new ArrayList<>();
    private Note noteToEdit;
    private boolean isUserSeeking = false;
    private final Player recorder = new Player();
    private Location currentLocation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {/**/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        ButterKnife.bind(this);
        initScreen();
    }

    private void initScreen() {
        ImagePicker.setMinQuality(600, 600);
//        setUpToolbar(); Comentado para ser usado quando for implementar o note view

        prepareToShowSpinner(0, null);
        prepareAudioRecordButton();
        prepareAudioSeekbar();
        getCurrentLocation();

        if (isToEditNote()) {
            prepareToShowSpinner(1, noteToEdit.getSubject().getSubject());
            prepareToEditNote();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
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
                Log.d("RecordView", "onFinish. File name: " + recorder.getFileName() + ". RecordTime: " + new Long(recordTime).intValue());
            }

            @Override
            public void onLessThanSecond() {
                Log.d("RecordView", "onLessThanSecond");
                recorder.stopRecording();
                if(isToEditNote()){
                    recorder.setInitialAudioFile(noteToEdit.getAudio().getMediaFile());
                }
            }
        });
    }

    private void addAudioMedia(String fileName) {
        Log.i(TAG, "BEFORE addAudioMedia: " + Arrays.toString(medias.toArray()));
        medias.removeIf(new Predicate<Media>() {
            @Override
            public boolean test(Media media) {
                return MediaType.AUDIO.equals(media.getType());
            }
        });
        Log.i(TAG, "AFTER addAudioMedia: " + Arrays.toString(medias.toArray()));
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

    private void prepareToShowSpinner(int auxEdit, String subjectEdit) {
        DatabaseHelper dao = new DatabaseHelper(this);
        subjects = dao.selectAllSubjects();
        List<String> spinnerArray = new ArrayList<String>();

        if (subjects.isEmpty()) {
            dao.addSomeSubjects();
            subjects = dao.selectAllSubjects();
        }

        for (Subject subject : subjects) {
            spinnerArray.add(subject.getSubject());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtNoteSubject.setAdapter(adapter);
        if (auxEdit == 1) {
            int spinnerPosition = adapter.getPosition(subjectEdit);
            edtNoteSubject.setSelection(spinnerPosition);
        }

    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(NoteDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NoteDetailActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            return;
        }
        LocationConfiguration awesomeConfiguration = new LocationConfiguration.Builder()
                .useDefaultProviders(
                        new DefaultProviderConfiguration.Builder()
                                .build())
                .build();
        LocationManager.enableLog(true);
        locationManager = new LocationManager.Builder(getApplicationContext())
                .configuration(awesomeConfiguration)
                .activity(this)
                .notify(new LocationListener() {
                    @Override
                    public void onProcessTypeChanged(int processType) {
                        Log.i(TAG, "onProcessTypeChanged");
                    }

                    @Override
                    public void onLocationChanged(Location location) {
                        currentLocation = location;
                        Log.i(TAG, "Lat: " + currentLocation.getLatitude() + " Long: " + currentLocation.getLongitude());
                    }

                    @Override
                    public void onLocationFailed(int type) {
                        Log.i(TAG, "onLocationFailed");
                    }

                    @Override
                    public void onPermissionGranted(boolean alreadyHadPermission) {
                        Log.i(TAG, "onPermissionGranted");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.i(TAG, "onStatusChanged");
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.i(TAG, "onProviderEnabled");
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.i(TAG, "onProviderDisabled");
                    }
                })
                .build();
        locationManager.get();
    }

    private boolean isToEditNote() {
        noteToEdit = (Note) getIntent().getSerializableExtra("note");
        return noteToEdit != null;
    }

    private void prepareToEditNote() {
        Log.i(TAG, "Edit note: " + noteToEdit.toString());
        edtNoteTitle.setText(noteToEdit.getTitle());
        edtNoteDescription.setText(noteToEdit.getDescription());

        List<Media> photos = noteToEdit.getPhotos();
        for (int i = 1; i <= photos.size(); i++) {
            addViewToSlider(i + " of " + photos.size() + " photo(s)", photos.get(i-1).getMediaFile().getAbsolutePath());
        }
        medias = noteToEdit.getMedias();
        recorder.setInitialAudioFile(noteToEdit.getAudio().getMediaFile());
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
            long photosAmount = medias.stream().filter(new Predicate<Media>() {
                @Override
                public boolean test(Media media) {
                    return MediaType.PHOTO.equals(media.getType());
                }
            }).count();
            sliderShow.removeAllSliders();
            for (int i = 0; i < photosAmount; i++) {
                if(MediaType.PHOTO.equals(medias.get(i).getType())) {
                    addViewToSlider((i + 1) + " of " + photosAmount + " photo(s)", medias.get(i).getMediaFile().getAbsolutePath());
                }
            }
        }
    }

    private void saveNote() {

        Log.i(TAG, edtNoteTitle.getText().toString());
        Log.i(TAG, edtNoteSubject.getSelectedItem().toString());

        if(currentLocation != null) {
            Log.i(TAG, "Lat: " + currentLocation.getLatitude() + " Long: " + currentLocation.getLongitude());
        } else {
            Log.i(TAG, "NO LOCATION!!!!!!!");
        }


        Note note;
        if (isToEditNote()) {
            noteToEdit.setTitle(edtNoteTitle.getText().toString());
            noteToEdit.setDescription(edtNoteDescription.getText().toString());
            noteToEdit.setSubject(getSubjectByString(edtNoteSubject.getSelectedItem().toString()));
            noteToEdit.setDateTime(LocalDateTime.now());
            noteToEdit.setLatLng(new LatLng(43.653226, -79.383184));
            note = noteToEdit;
        } else {
            note = new Note(
                    edtNoteTitle.getText().toString(),
                    edtNoteDescription.getText().toString(),
                    getSubjectByString(edtNoteSubject.getSelectedItem().toString()),
                    LocalDateTime.now(),
                    new LatLng(43.653226, -79.383184));
        }

        DatabaseHelper dao = new DatabaseHelper(this);
        Log.i(TAG, "saveNote: mediasBeforeSave: " + Arrays.toString(medias.toArray()));
        note.setMedias(medias);
        dao.save(note);

        //Only to see if the note was saved
        List<Note> notes = dao.selectAllNotes();
        for (Note n : notes) {
            Log.i(TAG, n.toString());
        }

        finish();
    }

    private Subject getSubjectByString(String string) {
        Subject sub = new Subject(1,"Personal", Color.BLUE);
        DatabaseHelper dao = new DatabaseHelper(this);
        subjects = dao.selectAllSubjects();
        for (Subject subject : subjects) {
            System.out.println("from obj  "+subject.getSubject());
            System.out.println("from parametro  "+string);
            if (subject.getSubject().equals(string)) {
                sub = subject;
                System.out.println("entra igual");

            }
        }
        return sub;
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
        if (noteToEdit != null) {
            Bundle args = new Bundle();
            args.putParcelable("noteEditLatLong", noteToEdit.getLatLng());
            intent.putExtra("bundle", args);
        }
        intent.putExtra("eachNote", 1);
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
