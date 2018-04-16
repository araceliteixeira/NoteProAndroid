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
}
