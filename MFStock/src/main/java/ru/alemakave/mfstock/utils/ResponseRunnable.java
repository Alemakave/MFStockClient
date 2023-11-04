package ru.alemakave.mfstock.utils;

import java.util.concurrent.atomic.AtomicReference;

public abstract class ResponseRunnable implements Runnable {
    public AtomicReference<String> responseData = new AtomicReference<>();
}
