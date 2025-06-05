package ru.alemakave.mfstock.view.elements;

import android.view.MenuItem;

import androidx.annotation.NonNull;

public interface ContextMenusItem {
    void updateContextMenu();
    boolean onContextItemSelected(@NonNull MenuItem item);
}
