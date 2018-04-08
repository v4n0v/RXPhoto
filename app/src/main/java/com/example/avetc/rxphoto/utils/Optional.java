package com.example.avetc.rxphoto.utils;


public class Optional<T> {

    private T value;

    private Optional() {
        this.value = null;
    }

    private Optional(T value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value != null;
    }

    public T value() {
        return value;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> empty() {
        return new Optional<>();
    }
}