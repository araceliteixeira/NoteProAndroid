package com.orion.notepro.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.orion.notepro.model.Media;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noteproandroid.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    private static final String DROP_SUBJECT_TABLE = "DROP TABLE tbl_authors;";
    private static final String DROP_NOTE_TABLE = "DROP TABLE tbl_books;";
    private static final String DROP_MEDIA_TABLE = "DROP TABLE tbl_books;";

    // Our database instance
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SUBJECT_TABLE = "CREATE TABLE IF NOT EXISTS SUBJECT (SUBJECT_ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "DESCRIPTION TEXT NOT NULL, COLOR INTEGER NOT NULL, ACTIVE INTEGER DEFAULT 1);";
        String CREATE_NOTE_TABLE = "CREATE TABLE IF NOT EXISTS NOTE (NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "TITLE TEXT NOT NULL, DESCRIPTION TEXT NOT NULL, DATETIME TEXT NOT NULL, LATITUDE REAL NOT NULL, LONGITUDE REAL NOT NULL, " +
                "SUBJECT_ID INTEGER NOT NULL, FOREIGN KEY (SUBJECT_ID) REFERENCES SUBJECT(SUBJECT_ID));";
        String CREATE_MEDIA_TABLE = "CREATE TABLE IF NOT EXISTS MEDIA (MEDIA_ID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "NOTE_ID INTEGER NOT NULL, AUDIO BLOB NOT NULL, PICTURE BLOB NOT NULL, MEDIATYPE INTEGER NOT NULL, FOREIGN KEY (NOTE_ID) REFERENCES NOTE(NOTE_ID));";

        db.execSQL(CREATE_SUBJECT_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
        db.execSQL(CREATE_MEDIA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS SUBJECT");
        db.execSQL("DROP TABLE IF EXISTS NOTE");
        db.execSQL("DROP TABLE IF EXISTS MEDIA");

        this.onCreate(db);
    }

    public void addSomeSubjects() {
        addSubject(new Subject("Personal", Color.BLUE));
        addSubject(new Subject("Work", Color.YELLOW));
        addSubject(new Subject("College", Color.GREEN));
    }

    public void addSomeNotes() {
        List<Subject> subjects = selectAllSubjects();

        for (int i = 0; i < subjects.size(); i++) {
            addNote(new Note("Title " + i + "-1", "Description " + i + "-1",
                    subjects.get(i), LocalDateTime.of(2018, 04, i+1, 10+i, 0),
                    new LatLng(43.6532, -79.3832)));

            addNote(new Note("Title " + i + "-2", "Description " + i + "-2",
                    subjects.get(i), LocalDateTime.of(2018, 04, i+2, 11+i, 0),
                    new LatLng(43.6532, -79.3832)));
        }
    }

    public void addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", subject.getSubject());
        values.put("color", subject.getColor());

        db.insert("SUBJECT",null, values);
        db.close();
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", note.getTitle());
        values.put("description", note.getDescription());
        values.put("datetime", note.getDateTimeAsString());
        values.put("latitude", note.getLatLng().latitude);
        values.put("longitude", note.getLatLng().longitude);
        values.put("subject_id", note.getSubject().getSubjectId());

        long response = db.insert("NOTE",null, values);
        Log.i("NotePro", "Insert return: " + response);
        db.close();
    }

    public void addMedia(Media media) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("note_id", media.getPicture());
        //values.put("audio", media.getAudio()));
        //values.put("picture", media.getPicture());
        //values.put("mediatype", media.getMediaId());

        db.insert("MEDIA",null, values);
        db.close();
    }

    public List<Subject> selectAllSubjects() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM SUBJECT;";

        Cursor c = db.rawQuery(sql, null);
        List<Subject> subjectList = new ArrayList<Subject>();

        while (c.moveToNext()) {

            int id = c.getInt(c.getColumnIndex("SUBJECT_ID"));
            String subject = c.getString(c.getColumnIndex("DESCRIPTION"));
            int color = c.getInt(c.getColumnIndex("COLOR"));

            subjectList.add(new Subject(id, subject, color));
        }
        c.close();
        db.close();

        return subjectList;
    }

    public List<Note> selectAllNotes() {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " +
                "NOTE.NOTE_ID AS NOTE_ID, " +
                "NOTE.TITLE AS NOTE_TITLE, " +
                "NOTE.DESCRIPTION AS NOTE_DESCRIPTION, " +
                "NOTE.DATETIME AS NOTE_DATETIME, " +
                "NOTE.LONGITUDE AS NOTE_LONGITUDE, " +
                "NOTE.LATITUDE AS NOTE_LATITUDE, " +
                "SUBJECT.SUBJECT_ID AS SUBJECT_ID, " +
                "SUBJECT.DESCRIPTION AS SUBJECT_DESCRIPTION, " +
                "SUBJECT.COLOR AS SUBJECT_COLOR " +
                "FROM NOTE INNER JOIN SUBJECT ON NOTE.SUBJECT_ID = SUBJECT.SUBJECT_ID;";

        db.compileStatement(sql);

        Cursor c = db.rawQuery(sql, null);
        List<Note> noteList = new ArrayList<Note>();

        while (c.moveToNext()) {

            int id = c.getInt(c.getColumnIndex("NOTE_ID"));
            String title = c.getString(c.getColumnIndex("NOTE_TITLE"));
            String description = c.getString(c.getColumnIndex("NOTE_DESCRIPTION"));
            String dateTime = c.getString(c.getColumnIndex("NOTE_DATETIME"));
            double longitude = c.getDouble(c.getColumnIndex("NOTE_LONGITUDE"));
            double latitude = c.getDouble(c.getColumnIndex("NOTE_LATITUDE"));

            int subjectId = c.getInt(c.getColumnIndex("SUBJECT_ID"));
            String subjectDescription = c.getString(c.getColumnIndex("SUBJECT_DESCRIPTION"));
            int color = c.getInt(c.getColumnIndex("SUBJECT_COLOR"));

            final Subject subject = new Subject(subjectId, subjectDescription, color);

            noteList.add(new Note(id, title, description, subject, dateTime, new LatLng(latitude, longitude)));
        }
        c.close();

        return noteList;
    }

    public List<Note> selectNotesBySubject(Subject subject) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " +
                "NOTE.NOTE_ID AS NOTE_ID, " +
                "NOTE.TITLE AS NOTE_TITLE, " +
                "NOTE.DESCRIPTION AS NOTE_DESCRIPTION, " +
                "NOTE.DATETIME AS NOTE_DATETIME, " +
                "NOTE.LONGITUDE AS NOTE_LONGITUDE, " +
                "NOTE.LATITUDE AS NOTE_LATITUDE " +
                "FROM NOTE WHERE NOTE.SUBJECT_ID = " + subject.getSubjectId() + " ;";

        db.compileStatement(sql);

        Cursor c = db.rawQuery(sql, null);
        List<Note> noteList = new ArrayList<Note>();

        while (c.moveToNext()) {

            int id = c.getInt(c.getColumnIndex("NOTE_ID"));
            String title = c.getString(c.getColumnIndex("NOTE_TITLE"));
            String description = c.getString(c.getColumnIndex("NOTE_DESCRIPTION"));
            String dateTime = c.getString(c.getColumnIndex("NOTE_DATETIME"));
            double longitude = c.getDouble(c.getColumnIndex("NOTE_LONGITUDE"));
            double latitude = c.getDouble(c.getColumnIndex("NOTE_LATITUDE"));

            noteList.add(new Note(id, title, description, subject, dateTime, new LatLng(latitude, longitude)));
        }
        c.close();

        return noteList;
    }
}
