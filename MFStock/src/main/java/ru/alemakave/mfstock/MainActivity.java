package ru.alemakave.mfstock;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.alemakave.android.utils.Logger;
import ru.alemakave.mfstock.commands.HttpCommands;
import ru.alemakave.mfstock.model.json.CachedData;
import ru.alemakave.mfstock.model.json.DateTimeJson;
import ru.alemakave.mfstock.utils.HttpUtils;
import ru.alemakave.mfstock.utils.NetworkUtils;
import ru.alemakave.mfstock.view.IViewContent;
import ru.alemakave.mfstock.view.MainViewContent;
import ru.alemakave.mfstock.view.SettingsViewContent;
import ru.alemakave.xlsx_parser.SheetCell;
import ru.alemakave.xlsx_parser.SheetData;
import ru.alemakave.xlsx_parser.SheetRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import static ru.alemakave.mfstock.utils.TextUtils.*;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Integer> contentViewTree = new ArrayList<>();
    private Settings settings;

    private final IViewContent mainViewContent = new MainViewContent();
    private final IViewContent settingsViewContent = new SettingsViewContent();
    private HashMap<String, CachedData> cachedData = new HashMap<>();
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
            try {
                loadCaches();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
        if (contentViewTree.size() > 0) {
            setContentView(contentViewTree.get(contentViewTree.size() - 1));
            contentViewTree.remove(contentViewTree.size() - 1);
        }
    }

    public HashMap<String, CachedData> getCachedData() {
        return cachedData;
    }

    public String getNomCodeScannedBarcode() {
        return nomCodeScannedBarcode;
    }

    public void onScan(String scanStr) {
        try {
            LinearLayout foundedInfoView = findViewById(R.id.infoView);
            if (scanStr == null || scanStr.isEmpty() || scanStr.length() < 2) {
                return;
            }

            clearInfoTexView();

            TextView appInfoView = findViewById(R.id.applicationInfoTextView);

            if (BuildConfig.VERSION_CODE >= 6) {
                String scanData = scanStr.substring(1, scanStr.length() - 1);
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

                        TextView infoTextView = new TextView(this);
                        foundedInfoView.addView(infoTextView);
                        Space spacer = new Space(this);
                        spacer.setMinimumHeight(30);
                        foundedInfoView.addView(spacer);

                        appendToTextView(infoTextView, infoData.toString());
                        infoData = new StringBuilder();
                    }

                    Logger.i("onScan", "Scan data: \n" + sheetData); //TODO: Localize
                }
            }
        } catch (Exception e) {
            Logger.e(this, String.format("onScan[%s:%s]", e.getClass().getName(), e.getStackTrace()[0]), e.getMessage()); //TODO: Reformat and move to logger
        }
    }

    public SheetData getDataFromScan(String scanData) throws IOException, InterruptedException {
        for (String file : cachedData.keySet()) {
            if (checkConnection()
                    && cachedData.get(file).getDateTime().equals(getDBData().toString())) {
                break;
            }

            SheetData sheetData = cachedData.get(file).getData();

            for (SheetRow row : sheetData.rows) {
                if (row.toString().contains(scanData)) {
                    return sheetData;
                }
            }
        }

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

        return mapper.readValue(HttpUtils.callAndWait(this, strUrl).getBytes(StandardCharsets.UTF_8), SheetData.class);
    }

    public DateTimeJson getDBData() throws IOException {
        String strUrl = String.format("http://%s:%s/%s", settings.getIp(), settings.getPort(), HttpCommands.GET_DB_DATE);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(HttpUtils.callAndWait(this, strUrl).getBytes(StandardCharsets.UTF_8), DateTimeJson.class);
    }

    public Settings getSettings() {
        return settings;
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
                date = getDBData().getDateTimeString();
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

    public boolean checkConnection() {
        return NetworkUtils.checkConnection(this, settings.getIp() + ":" + settings.getPort(), settings.getCheckConnectionTimeout());
    }

    public void loadCaches() throws IOException {
        for (File file : getFilesDir().listFiles()) {
            if (!file.getName().endsWith(".json")) {
                continue;
            }

            String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));

            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter();
            System.out.printf("fileName \"%s\": %s\n", fileName, new BufferedReader(new FileReader(file)).readLine().replaceAll("\\{", "\n{"));
            CachedData cacheData = mapper.readValue(file, CachedData.class);
            cachedData.put(fileName, cacheData);
        }
    }
}