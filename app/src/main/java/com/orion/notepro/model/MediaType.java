package com.orion.notepro.model;

public enum MediaType {
    PHOTO("Photo", 1),
    AUDIO("Audio", 2);

    private String stringValue;
    private int intValue;
    private MediaType(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}