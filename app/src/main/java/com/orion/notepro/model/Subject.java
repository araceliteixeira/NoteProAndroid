package com.orion.notepro.model;

import android.graphics.Color;

import java.io.Serializable;

public class Subject implements Serializable {
    private long subjectId;
    private String subject;
    private int color;

    public Subject(String subject, int color) {
        this.subject = subject;
        this.color = color;
    }

    public Subject(long id, String subject, int color) {
        subjectId = id;
        this.subject = subject;
        this.color = color;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
