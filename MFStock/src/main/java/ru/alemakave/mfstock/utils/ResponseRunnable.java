package ru.alemakave.mfstock.utils;

import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.Request;

public abstract class ResponseRunnable implements Runnable {
    public AtomicReference<Integer> responseCode = new AtomicReference<>();
    public AtomicReference<Request> responseRequest = new AtomicReference<>();
    public AtomicReference<MediaType> responseMediaType = new AtomicReference<>();
    public AtomicReference<String> responseData = new AtomicReference<>();
}
