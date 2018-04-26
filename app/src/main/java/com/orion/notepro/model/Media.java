package com.orion.notepro.model;

import java.io.File;
import java.io.Serializable;

public class Media implements Serializable {
    private long mediaId;
    private File mediaFile;
    private MediaType type;
    private long noteId;

    public Media(long mediaId, MediaType type, long noteId) {
        this.mediaId = mediaId;
        this.mediaFile = null;
        this.type = type;
        this.noteId = noteId;
    }

    public Media(File file, MediaType type) {
        this.mediaFile = file;
        this.type = type;
        this.mediaId = -1;
        this.noteId = -1;
    }

    public Media(long mediaId, File audio, MediaType type, long noteId) {
        this.mediaId = mediaId;
        this.mediaFile = audio;
        this.type = type;
        this.noteId = noteId;
    }

    public Media(MediaType type, long noteId) {
        this.mediaId = -1;
        this.mediaFile = null;
        this.type = type;
        this.noteId = noteId;
    }

    public Media(File audio, MediaType type, long noteId) {
        this.mediaId = -1;
        this.mediaFile = audio;
        this.type = type;
        this.noteId = noteId;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public File getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(File mediaFile) {
        this.mediaFile = mediaFile;
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

    @Override
    public String toString() {
        return "Media{" +
                "mediaId=" + mediaId +
                ", mediaFile=" + mediaFile +
                ", type=" + type +
                ", noteId=" + noteId +
                '}';
    }
}
