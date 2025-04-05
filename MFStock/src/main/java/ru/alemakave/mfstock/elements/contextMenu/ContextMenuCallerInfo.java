package ru.alemakave.mfstock.elements.contextMenu;

import android.view.ContextMenu;
import android.view.View;

public interface ContextMenuCallerInfo extends ContextMenu.ContextMenuInfo {
    View getCaller();
}
