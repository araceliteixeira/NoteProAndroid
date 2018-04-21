package com.orion.notepro.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.orion.notepro.util.DateUtil;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Note implements Serializable {
    private long noteId;
    private String title;
    private String description;
    private Subject subject;
    private LocalDateTime dateTime;
    private LatLng latLng;
    private List<Media> medias;

    public Note(long noteId, String title, String description, Subject subject, LocalDateTime dateTime, LatLng latLng) {
        this.noteId = noteId;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dateTime = dateTime;
        this.latLng = latLng;
        this.medias = new ArrayList<Media>();
    }

    public Note(long noteId, String title, String description, Subject subject, String dateTimeAsString, LatLng latLng) {
        this(noteId, title, description, subject, DateUtil.stringToDateTime(dateTimeAsString), latLng);
    }

    public Note(String title, String description, Subject subject, LocalDateTime dateTime, LatLng latLng) {
        this.noteId = -1;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dateTime = dateTime;
        this.latLng = latLng;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDateTimeAsString() {
        return DateUtil.dateTimeToString(dateTime);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
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

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", subject=" + subject +
                ", dateTime=" + dateTime +
                ", latLng=" + latLng +
                ", medias=" + medias +
                '}';
    }
}
