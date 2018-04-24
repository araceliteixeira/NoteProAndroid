package com.orion.notepro.model;

import android.graphics.Bitmap;

import com.orion.notepro.util.DateUtil;

import java.io.File;
import java.io.Serializable;

public class Media implements Serializable {
    private long mediaId;
    private Bitmap picture;
    private File audio;
    private MediaType type;
    private long noteId;

    public Media(long mediaId, Bitmap picture, MediaType type, long noteId) {
        this.mediaId = mediaId;
        this.picture = picture;
        this.audio = null;
        this.type = type;
        this.noteId = noteId;
    }

    public Media(long mediaId, File audio, MediaType type, long noteId) {
        this.mediaId = mediaId;
        this.picture = null;
        this.audio = audio;
        this.type = type;
        this.noteId = noteId;
    }

    public Media(Bitmap picture, MediaType type, long noteId) {
        this.mediaId = -1;
        this.picture = picture;
        this.audio = null;
        this.type = type;
        this.noteId = noteId;
    }

    public Media(File audio, MediaType type, long noteId) {
        this.mediaId = -1;
        this.picture = null;
        this.audio = audio;
        this.type = type;
        this.noteId = noteId;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public byte[] getPictureAsBlob() {
        return DateUtil.bitmapToBlob(picture);
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public File getAudio() {
        return audio;
    }

    public void setAudio(File audio) {
        this.audio = audio;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }
}
