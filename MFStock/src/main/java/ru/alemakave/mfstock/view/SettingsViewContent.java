package ru.alemakave.mfstock.view;

import androidx.appcompat.app.AppCompatActivity;
import ru.alemakave.mfstock.MainActivity;
import ru.alemakave.mfstock.R;
import ru.alemakave.mfstock.elements.HeadedTextBox;

public class SettingsViewContent implements IViewContent {
    @Override
    public void draw(AppCompatActivity activity) {
        MainActivity mainActivity = (MainActivity) activity;

        ((HeadedTextBox)mainActivity.findViewById(R.id.ip_input)).getInput().setText(mainActivity.getSettings().ip);
        ((HeadedTextBox)mainActivity.findViewById(R.id.port_input)).getInput().setText(mainActivity.getSettings().port);
        ((HeadedTextBox)mainActivity.findViewById(R.id.font_size_input)).getInput().setText(Integer.toString(mainActivity.getSettings().fontSize));

        mainActivity.findViewById(R.id.save_settings_button).setOnClickListener(v -> {
            mainActivity.getSettings().ip = ((HeadedTextBox)mainActivity.findViewById(R.id.ip_input)).getInput().getText().toString();
            mainActivity.getSettings().port = ((HeadedTextBox)mainActivity.findViewById(R.id.port_input)).getInput().getText().toString();
            mainActivity.getSettings().fontSize = Integer.parseInt(((HeadedTextBox)mainActivity.findViewById(R.id.font_size_input)).getInput().getText().toString());
            mainActivity.getSettings().saveSettings();
            mainActivity.setContentView(R.layout.activity_main);
        });
    }
}
