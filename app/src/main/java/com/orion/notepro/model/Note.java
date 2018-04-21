package com.orion.notepro.model;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Note implements Serializable {
    private long noteId;
    private String title;
    private String description;
    private Subject subject;
    private Date dateTime;
    private Location location;
    private List<Media> medias;

    public Note(long noteId, String title, String description, Subject subject, Date dateTime, Location location) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dateTime = dateTime;
        this.location = location;
        this.medias = new ArrayList<Media>();
    }

    public Note(String title, String description, Subject subject, Date dateTime, Location location) {
        this.noteId = -1;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dateTime = dateTime;
        this.location = location;
        this.medias = new ArrayList<Media>();
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public void addMedia(Media media) {
        this.medias.add(media);
    }
}
