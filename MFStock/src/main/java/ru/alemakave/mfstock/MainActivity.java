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
import okhttp3.OkHttpClient;
import ru.alemakave.android.utils.Logger;
import ru.alemakave.mfstock.commands.Commands;
import ru.alemakave.mfstock.model.json.DateTimeJson;
import ru.alemakave.mfstock.utils.HttpUtils;
import ru.alemakave.mfstock.view.IViewContent;
import ru.alemakave.mfstock.view.MainViewContent;
import ru.alemakave.mfstock.view.SettingsViewContent;
import ru.alemakave.xlsx_parser.SheetData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static ru.alemakave.mfstock.utils.TextUtils.*;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Integer> contentViewTree = new ArrayList<>();
    private Settings settings;
    private String scanDataBuffer = "";

    private final IViewContent mainViewContent = new MainViewContent();
    private final IViewContent settingsViewContent = new SettingsViewContent();

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
        if (contentViewTree.size() > 0) {
            setContentView(contentViewTree.get(contentViewTree.size() - 1));
            contentViewTree.remove(contentViewTree.size() - 1);
        } else {
            onScan(scanDataBuffer);
        }
    }

    public void onScan(String scanStr) {
        try {
            if (!scanStr.isEmpty()) {
                clearInfoTexView();
                scanDataBuffer = scanStr;
            }
            if (scanStr.length() < 2) {
                return;
            }

            TextView appInfoView = findViewById(R.id.applicationInfoTextView);

            if (BuildConfig.VERSION_CODE >= 6) {
                String scanData = scanDataBuffer.substring(1, scanDataBuffer.length() - 1);
                String strUrl = String.format("http://%s:%s/%s=%s",
                        settings.ip,
                        settings.port,
                        Commands.FIND_FROM_SCAN,
                        scanData.replaceAll("#", "%23")
                );

                ObjectMapper mapper = new ObjectMapper();
                SheetData sheetData = mapper.readValue(HttpUtils.callAndWait(this, strUrl).getBytes(StandardCharsets.UTF_8), SheetData.class);

                appendToTextView(appInfoView, String.format("%s: %s", getString(R.string.scanned), scanData), Color.rgb(160, 160, 0));
                StringBuilder infoData = new StringBuilder();
                if (sheetData.rows.size() == 1) {
                    appendToTextView(appInfoView, String.format("%s", getString(R.string.scanned_not_found)), Color.RED);
                } else {
                    LinearLayout foundedInfoView = findViewById(R.id.infoView);

                    for (int i = 1; i < sheetData.rows.size(); i++) {
                        for (int j = 0; j < sheetData.rows.get(0).cells.size(); j++) {
                            infoData.append(sheetData.rows.get(0).getCell(j).toString())
                                    .append(": ")
                                    .append(sheetData.rows.get(i).getCell(j).toString())
                                    .append("\n");
                        }

                        TextView infoTextView = new TextView(this);
                        StringBuilder finalizedInfoData = infoData;
                        infoTextView.setOnLongClickListener(v -> {
                            showPrintStickerDialog(finalizedInfoData.toString());

                            return true;
                        });

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

    public Settings getSettings() {
        return settings;
    }

    public void clearInfoTexView()  {
        TextView appInfoView = findViewById(R.id.applicationInfoTextView);
        LinearLayout foundedInfoView = findViewById(R.id.infoView);

        appInfoView.setText("");
        foundedInfoView.removeAllViews();

        String strUrl = String.format("http://%s:%s/%s", settings.ip, settings.port, Commands.GET_DB_DATE);

        try {
            ObjectMapper mapper = new ObjectMapper();
            DateTimeJson dbDate = mapper.readValue(HttpUtils.callAndWait(this, strUrl).getBytes(StandardCharsets.UTF_8), DateTimeJson.class);

            String text = getString(R.string.application_info, BuildConfig.VERSION_NAME, BuildConfig.DEBUG ? " (Debug)" : "", dbDate.getDateTimeString());
            appendToTextView(appInfoView, text, Color.rgb(128, 192, 0));
        } catch (Exception ignore) {}
    }

    private void showPrintStickerDialog(String dialogContent) {
        /*
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_MFStock_Widget_AlertDialog))
                .setPositiveButton(getString(R.string.nomenclature_sticker), (dialog, which) -> printNomenclatureSticker(url, dialogContent))
                .setNegativeButton(getString(R.string.serial_number_sticker), (dialog, which) -> printSerialNumberSticker(dialogContent))
                .setNeutralButton(getString(R.string.cancel), null)
                .setTitle(getString(R.string.print_sticker))
                .setMessage(getString(R.string.print_sticker_message) + "\n\n" + dialogContent)
                .create()
                .show();*/
    }

    private void printNomenclatureSticker(String url, String data) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .writeTimeout(20000, TimeUnit.MILLISECONDS)
                .build();

        Logger.e(this, "printNomenclatureSticker", "Not implemented yet"); //TODO
    }

    private void printSerialNumberSticker(String data) {
        Logger.e(this, "printSerialNumberSticker", "Not implemented yet"); //TODO
    }
}