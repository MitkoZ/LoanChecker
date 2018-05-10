package com.zahariev.dimitar.utils;


public class Ref<T> {

    private T value;

    public Ref(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}

