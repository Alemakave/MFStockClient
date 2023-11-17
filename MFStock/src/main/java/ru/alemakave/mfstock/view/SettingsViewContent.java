package ru.alemakave.mfstock.view;

import android.graphics.Color;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import ru.alemakave.mfstock.MainActivity;
import ru.alemakave.mfstock.R;
import ru.alemakave.mfstock.elements.HeadedTextBox;

import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectIp;
import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectPort;

public class SettingsViewContent implements IViewContent {
    @Override
    public void draw(AppCompatActivity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        HeadedTextBox ipHeadedTextBox = mainActivity.findViewById(R.id.ip_input);
        HeadedTextBox portHeadedTextBox = mainActivity.findViewById(R.id.port_input);
        HeadedTextBox fontSizeHeadedTextBox = mainActivity.findViewById(R.id.font_size_input);
        HeadedTextBox checkConnectionTimeoutHeadedTextBox = mainActivity.findViewById(R.id.check_connection_timeout_input);

        Button saveButton = mainActivity.findViewById(R.id.save_settings_button);

        ipHeadedTextBox.getInput().setText(mainActivity.getSettings().getIp());
        portHeadedTextBox.getInput().setText(Integer.toString(mainActivity.getSettings().getPort()));
        fontSizeHeadedTextBox.getInput().setText(Integer.toString(mainActivity.getSettings().getFontSize()));
        checkConnectionTimeoutHeadedTextBox.getInput().setText(Integer.toString(mainActivity.getSettings().getCheckConnectionTimeout()));

        ipHeadedTextBox.getInput().setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return true;
            }

            EditText ipInput = ((EditText) view);
            if (!isCorrectIp(ipInput.getText().toString())) {
                ipInput.setTextColor(Color.RED);
            } else {
                ipInput.setTextColor(-3618616);
            }
            return false;
        });
        portHeadedTextBox.getInput().setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return true;
            }

            EditText portInput = ((EditText) view);
            if (portInput.getText().toString().matches("^[0-9]+$") && !isCorrectPort(Integer.parseInt(portInput.getText().toString()))) {
                portInput.setTextColor(Color.RED);
            } else {
                portInput.setTextColor(-3618616);
            }
            return false;
        });
        checkConnectionTimeoutHeadedTextBox.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return true;
            }

            EditText timeoutInput = ((EditText) view);
            if (timeoutInput.getText().toString().matches("^[0-9]+$")) {
                timeoutInput.setTextColor(-3618616);
            } else {
                timeoutInput.setTextColor(Color.RED);
            }

            return false;
        });

        saveButton.setOnClickListener(v -> {
            String ipInputText = ipHeadedTextBox.getInput().getText().toString();
            String portInputText = portHeadedTextBox.getInput().getText().toString();
            String fontSizeInputText = fontSizeHeadedTextBox.getInput().getText().toString();
            String timeoutInputText = checkConnectionTimeoutHeadedTextBox.getInput().getText().toString();

            if (portInputText.matches("^[0-9]+$")
                    && fontSizeInputText.matches("^[0-9]+$")
                    && timeoutInputText.matches("^[0-9]+$")) {
                int port = Integer.parseInt(portInputText);
                int fontSize = Integer.parseInt(fontSizeInputText);
                int timeout = Integer.parseInt(timeoutInputText);

                if (isCorrectIp(ipInputText) && isCorrectPort(port)) {
                    mainActivity.getSettings().setIp(ipInputText);
                    mainActivity.getSettings().setPort(port);
                    mainActivity.getSettings().setFontSize(fontSize);
                    mainActivity.getSettings().setCheckConnectionTimeout(timeout);
                    mainActivity.getSettings().saveSettings();
                    mainActivity.setContentView(R.layout.activity_main);
                }
            }
        });
    }
}
