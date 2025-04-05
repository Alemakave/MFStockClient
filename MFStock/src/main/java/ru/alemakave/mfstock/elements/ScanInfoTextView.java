package ru.alemakave.mfstock.elements;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.LinkedHashMap;
import java.util.Map;

import ru.alemakave.mfstock.elements.contextMenu.ContextMenuCallerInfo;

public class ScanInfoTextView extends AppCompatTextView implements ContextMenusItem {
    public static final Map<String, Integer> contextMenuIds = new LinkedHashMap<>();
    private final Activity thisActivity;

    public ScanInfoTextView(Activity context) {
        super(context);
        thisActivity = context;
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        for (String contextMenuStr : contextMenuIds.keySet()) {
            menu.add(Menu.NONE, contextMenuIds.get(contextMenuStr), Menu.NONE, contextMenuStr);
        }
    }

    @Override
    public void updateContextMenu() {
        int contextMenuLastId = 100;
        contextMenuIds.clear();
        setOnCreateContextMenuListener(null);

        for (String scanInfoDataLine : getText().toString().split("\n")) {
            String[] keyAndValue = scanInfoDataLine.split(": ");
            contextMenuIds.put(keyAndValue[0], contextMenuLastId);
            contextMenuLastId++;
        }

        setOnCreateContextMenuListener(getActivity());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String[] scanInfoDataLines = getText().toString().split("\n");
        String scanInfoDataLineValue = scanInfoDataLines[item.getItemId() - 100].split(": ")[1];

        ((ClipboardManager)getActivity().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", scanInfoDataLineValue));
        Toast.makeText(getActivity(), scanInfoDataLines[item.getItemId() - 100] + " Скопировано!", Toast.LENGTH_LONG).show();

        return true;
    }

    private Activity getActivity() {
        return thisActivity;
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return (ContextMenuCallerInfo) () -> ScanInfoTextView.this;
    }
}
