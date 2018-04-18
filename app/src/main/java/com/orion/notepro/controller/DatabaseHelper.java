package com.orion.notepro.controller;

import java.io.File;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.orion.notepro.R;
import com.orion.notepro.model.Media;
import com.orion.notepro.model.MediaType;
import com.orion.notepro.model.Note;
import com.orion.notepro.model.Subject;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noteandroid.db";
    private static final int DATABASE_VERSION = 1;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    private static final String DROP_SUBJECT_TABLE = "DROP TABLE tbl_authors;";
    private static final String DROP_NOTE_TABLE = "DROP TABLE tbl_books;";
    private static final String DROP_MEDIA_TABLE = "DROP TABLE tbl_books;";

    // Our database instance
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SUBJECT_TABLE = "CREATE TABLE IF NOT EXISTS SUBJECT (subject_id INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "DESCRIPTION TEXT NOT NULL, COLOR TEXT NOT NULL, ACTIVE INTEGER DEFAULT 1);";
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
        //values.put("datetime", note.getDateTime());
        values.put("latitude", note.getLocation().getLatitude());
        values.put("longitude", note.getLocation().getLongitude());
        values.put("subject_id", note.getSubject().getSubjectId());

        db.insert("NOTE",null, values);
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



}
