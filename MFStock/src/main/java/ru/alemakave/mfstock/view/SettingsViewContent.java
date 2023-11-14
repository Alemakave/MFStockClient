package ru.alemakave.mfstock.view;

import android.graphics.Color;
import android.view.KeyEvent;
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

        ipHeadedTextBox.getInput().setText(mainActivity.getSettings().getIp());
        portHeadedTextBox.getInput().setText(Integer.toString(mainActivity.getSettings().getPort()));
        fontSizeHeadedTextBox.getInput().setText(Integer.toString(mainActivity.getSettings().getFontSize()));

        ((HeadedTextBox)mainActivity.findViewById(R.id.ip_input)).getInput().setOnKeyListener((view, keyCode, keyEvent) -> {
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
        ((HeadedTextBox)mainActivity.findViewById(R.id.port_input)).getInput().setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                return true;
            }

            EditText portInput = ((EditText) view);
            if (!isCorrectPort(Integer.parseInt(portInput.getText().toString()))) {
                portInput.setTextColor(Color.RED);
            } else {
                portInput.setTextColor(-3618616);
            }
            return false;
        });

        mainActivity.findViewById(R.id.save_settings_button).setOnClickListener(v -> {
            String ip = ipHeadedTextBox.getInput().getText().toString();
            int port = Integer.parseInt(portHeadedTextBox.getInput().getText().toString());
            int fontSize = Integer.parseInt(fontSizeHeadedTextBox.getInput().getText().toString());

            if (isCorrectIp(ip) && isCorrectPort(port)) {
                mainActivity.getSettings().setIp(ip);
                mainActivity.getSettings().setPort(port);
                mainActivity.getSettings().setFontSize(fontSize);
                mainActivity.getSettings().saveSettings();
                mainActivity.setContentView(R.layout.activity_main);
            }
        });
    }
}
