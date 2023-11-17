package ru.alemakave.mfstock.commands;

import android.content.Context;

public interface Action {
    int getId();
    void call(Context context);
    String getDescription(Context context);
    boolean isPossibleCall(Context context);
}
