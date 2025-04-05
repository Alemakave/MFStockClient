package ru.alemakave.mfstock.view;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ru.alemakave.mfstock.MainActivity;
import ru.alemakave.mfstock.R;

public class MainViewContent implements IViewContent {
    @Override
    public void draw(AppCompatActivity activity) {
        MainActivity mainActivity = (MainActivity) activity;

        TextView appInfoView = mainActivity.findViewById(R.id.applicationInfoTextView);
        EditText inputBox = mainActivity.findViewById(R.id.inputBox);
        inputBox.requestFocus();

        appInfoView.setTextSize(mainActivity.getSettings().getFontSize());
        inputBox.setTextSize(mainActivity.getSettings().getFontSize());

        inputBox.setOnKeyListener((v, keyCode, event) -> {
            String inputData = inputBox.getText().toString().trim().replaceAll("\n", "");

            if(keyCode == KeyEvent.KEYCODE_ENTER) {
                if (inputData.isEmpty()) {
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
            }
            return false;
        });
    }
}
