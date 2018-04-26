package com.orion.notepro.model;

import java.io.Serializable;

public enum MediaType implements Serializable {
    PHOTO("Photo", 1),
    AUDIO("Audio", 2);

    private String stringValue;
    private int intValue;

    private MediaType(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    public int intValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}