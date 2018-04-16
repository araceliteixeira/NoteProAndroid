package com.orion.notepro.model;

import android.graphics.Color;

public class Subject {
    private long subjectId;
    private String subject;
    private Color color;

    public Subject(String subject, Color color) {
        this.subject = subject;
        this.color = color;
    }

    public Subject(long id, String subject, Color color) {
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
