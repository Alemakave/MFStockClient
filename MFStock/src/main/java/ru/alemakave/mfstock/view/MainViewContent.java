package ru.alemakave.mfstock.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ru.alemakave.mfstock.MainActivity;
import ru.alemakave.mfstock.R;

public class MainViewContent implements IViewContent {
    @Override
    public void draw(AppCompatActivity activity) {
        MainActivity mainActivity = (MainActivity) activity;

        TextView appInfoView = mainActivity.findViewById(R.id.applicationInfoTextView);
        LinearLayout infoView = mainActivity.findViewById(R.id.infoView);
        EditText inputBox = mainActivity.findViewById(R.id.inputBox);
        inputBox.requestFocus();

        appInfoView.setTextSize(mainActivity.getSettings().getFontSize());
        inputBox.setTextSize(mainActivity.getSettings().getFontSize());

        inputBox.setOnKeyListener((v, keyCode, event) -> {
            String inputData = inputBox.getText().toString().trim().replaceAll("\n", "");

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                String[] appInfoViewTextLines = appInfoView.getText().toString().split("\n");

                if (inputData.isEmpty()) {
                    if (infoView.getChildCount() == 0 && appInfoViewTextLines.length >= 3) {
                        mainActivity.onScan(appInfoViewTextLines[2].split(": ")[1]);
                    }

                    return true;
                }

                if (inputData.startsWith("!") && inputData.endsWith("?")) {
                    mainActivity.onScan(inputData.substring(1, inputData.length() - 1));
                } else {
                    mainActivity.onScan(inputData);
                }

                inputBox.setText("");
                inputBox.requestFocus();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mainActivity.getCurrentFocus().getWindowToken(), 0);
            }

            return false;
        });
    }
}
