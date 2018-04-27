package com.orion.notepro.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.orion.notepro.util.DateUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Note implements Serializable {
    private long noteId;
    private String title;
    private String description;
    private Subject subject;
    private LocalDateTime dateTime;
    private List<Media> medias;
    private transient LatLng latLng;

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
                ", medias=" + Arrays.toString(medias.toArray()) +
                '}';
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(latLng.latitude);
        out.writeDouble(latLng.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        latLng = new LatLng(in.readDouble(), in.readDouble());
    }

    public List<Media> getPhotos() {
        final List<Media> photos = new ArrayList<>();
        medias.forEach(new Consumer<Media>() {
            @Override
            public void accept(Media media) {
                if(MediaType.PHOTO.equals(media.getType())) photos.add(media);
            }
        });

        return photos;
    }

    public Media getAudio() {
        Media audio = medias.stream().filter(new Predicate<Media>() {
            @Override
            public boolean test(Media media) {
                return MediaType.AUDIO.equals(media.getType());
            }
        }).findFirst().orElse(new EmptyMedia());

        return audio;
    }
}
