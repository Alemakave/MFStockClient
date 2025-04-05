package ru.alemakave.mfstock;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import okhttp3.Response;
import ru.alemakave.android.utils.Logger;
import ru.alemakave.mfstock.commands.HttpCommands;
import ru.alemakave.mfstock.elements.ContextMenusItem;
import ru.alemakave.mfstock.elements.ScanInfoTextView;
import ru.alemakave.mfstock.elements.contextMenu.ContextMenuCallerInfo;
import ru.alemakave.mfstock.model.json.DateTimeJson;
import ru.alemakave.mfstock.utils.ConnectionStatus;
import ru.alemakave.mfstock.utils.HttpUtils;
import ru.alemakave.mfstock.utils.NetworkUtils;
import ru.alemakave.mfstock.view.IViewContent;
import ru.alemakave.mfstock.view.MainViewContent;
import ru.alemakave.mfstock.view.SettingsViewContent;
import ru.alemakave.xlsx_parser.SheetCell;
import ru.alemakave.xlsx_parser.SheetData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.alemakave.mfstock.utils.TextUtils.*;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Integer> contentViewTree = new ArrayList<>();
    @Getter
    private Settings settings;

    private final IViewContent mainViewContent = new MainViewContent();
    private final IViewContent settingsViewContent = new SettingsViewContent();
    @Getter
    private String nomCodeScannedBarcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if (layoutResID == R.layout.activity_main) {
            settings = Settings.getSettings(this);
            settings.loadSettings();
            clearInfoTexView();

            mainViewContent.draw(this);
        }
        else if (layoutResID == R.layout.settings_layout) {
            contentViewTree.add(R.layout.activity_main);

            settingsViewContent.draw(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && BuildConfig.VERSION_CODE < 4) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);

                SpannableStringBuilder text = new SpannableStringBuilder();
                text.append(menuItem.getTitle().toString());
                text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                menuItem.setTitle(text);
            }
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            setContentView(R.layout.settings_layout);
            return true;
        } else if (id == R.id.action_clear) {
            clearInfoTexView();
            return true;
        } else {
            Logger.i("MFStock", String.format("onOptionsItemSelected: ID \"%d\" not found!", id)); //TODO: Localize
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!contentViewTree.isEmpty()) {
            setContentView(contentViewTree.get(contentViewTree.size() - 1));
            contentViewTree.remove(contentViewTree.size() - 1);
        }
    }

    public void onScan(String scanData) {
        try {
            LinearLayout foundedInfoView = findViewById(R.id.infoView);
            if (scanData == null || scanData.isEmpty()) {
                return;
            }

            clearInfoTexView();

            TextView appInfoView = findViewById(R.id.applicationInfoTextView);

            if (BuildConfig.VERSION_CODE >= 6) {
                scanData = scanData.trim();

                SheetData sheetData = getDataFromScan(scanData);

                if (sheetData == null) {
                    appendToTextView(appInfoView, "Not connected", Color.RED);
                    return;
                }

                int nomCodeIndex = sheetData.rows.get(0).cells.indexOf(new SheetCell("Номенклатурный код"));
                if (nomCodeIndex > 0 && sheetData.rows.size() > 1) {
                    nomCodeScannedBarcode = sheetData.rows.get(1).cells.get(nomCodeIndex).toString();
                }

                appendToTextView(appInfoView, String.format("%s: %s", getString(R.string.scanned), scanData), Color.rgb(160, 160, 0));
                StringBuilder infoData = new StringBuilder();
                if (sheetData.rows.size() == 1) {
                    appendToTextView(appInfoView, String.format("%s", getString(R.string.scanned_not_found)), Color.RED);
                } else {
                    for (int i = 1; i < sheetData.rows.size(); i++) {
                        for (int j = 0; j < sheetData.rows.get(0).cells.size(); j++) {
                            infoData.append(sheetData.rows.get(0).getCell(j).toString())
                                    .append(": ")
                                    .append(sheetData.rows.get(i).getCell(j).toString())
                                    .append("\n");
                        }

                        ScanInfoTextView infoTextView = new ScanInfoTextView(this);
                        foundedInfoView.addView(infoTextView);
                        Space spacer = new Space(this);
                        spacer.setMinimumHeight(30);
                        foundedInfoView.addView(spacer);

                        appendToTextView(infoTextView, infoData.toString());
                        infoTextView.updateContextMenu();
                        infoData = new StringBuilder();
                    }

                    Logger.i("onScan", "Scan data: \n" + sheetData); //TODO: Localize
                }
            }
        } catch (Exception e) {
            Logger.e(this, String.format("onScan[%s:%s]", e.getClass().getName(), e.getStackTrace()[0]), e.getMessage()); //TODO: Reformat and move to logger
        }
    }

    public SheetData getDataFromScan(String scanData) throws IOException {
        if (!checkConnection()) {
            return null;
        }

        String strUrl = String.format("http://%s:%s/%s?searchString=%s",
                settings.getIp(),
                settings.getPort(),
                HttpCommands.FIND_FROM_SCAN,
                scanData.replaceAll("#", "%23")
        );

        ObjectMapper mapper = new ObjectMapper();

        Response response = HttpUtils.callAndWait(this, strUrl);

        if (!response.isSuccessful()) {
            return null;
        }

        return mapper.readValue(response.body().string().getBytes(StandardCharsets.UTF_8), SheetData.class);
    }

    public DateTimeJson getDBData() throws IOException {
        String strUrl = String.format("http://%s:%s/%s", settings.getIp(), settings.getPort(), HttpCommands.GET_DB_DATE);
        ObjectMapper mapper = new ObjectMapper();

        Response response = HttpUtils.callAndWait(this, strUrl);

        if (!response.isSuccessful()) {
            return null;
        }

        return mapper.readValue(response.body().string().getBytes(StandardCharsets.UTF_8), DateTimeJson.class);
    }

    public void clearInfoTexView()  {
        TextView appInfoView = findViewById(R.id.applicationInfoTextView);
        LinearLayout foundedInfoView = findViewById(R.id.infoView);

        appInfoView.setText("");
        foundedInfoView.removeAllViews();
        nomCodeScannedBarcode = "";

        try {
            String date;
            if (checkConnection()) {
                DateTimeJson dateTimeJson = getDBData();
                if (dateTimeJson == null) {
                    date = "Connection error";
                } else {
                    date = getDBData().getDateTimeString();
                }
            } else {
                date = "Not connected";
            }
            String text = getString(R.string.application_info, BuildConfig.VERSION_NAME, BuildConfig.DEBUG ? " (Debug)" : "", date);
            appendToTextView(appInfoView, text, Color.rgb(128, 192, 0));
            mainViewContent.draw(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public boolean checkConnection() {
        return NetworkUtils.tryConnect(this, settings.getIp() + ":" + settings.getPort(), settings.getCheckConnectionTimeout()) == ConnectionStatus.CONNECTED;
    }

    public void onClickSettingsButton(View view) {
        setContentView(R.layout.settings_layout);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getMenuInfo() == null) {
            return super.onContextItemSelected(item);
        }

        if (!(item.getMenuInfo() instanceof ContextMenuCallerInfo)) {
            return super.onContextItemSelected(item);
        }

        if (!(((ContextMenuCallerInfo) item.getMenuInfo()).getCaller() instanceof ContextMenusItem)) {
            return super.onContextItemSelected(item);
        }

        return ((ContextMenusItem) ((ContextMenuCallerInfo) item.getMenuInfo()).getCaller()).onContextItemSelected(item);
    }
}