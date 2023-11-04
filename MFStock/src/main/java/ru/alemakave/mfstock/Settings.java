package ru.alemakave.mfstock;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class Settings {
    private static Settings instance = null;
    public SharedPreferences preferences;
    public String ip = "127.0.0.1";
    public String port = "9090";
    public int fontSize = 12;

    private Settings(AppCompatActivity activity) {
        preferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void loadSettings() {
        ip = preferences.getString("IP", ip);
        port = preferences.getString("Port", port);
        fontSize = preferences.getInt("FontSize", fontSize);
    }

    public void saveSettings() {
        preferences.edit().putString("IP", ip).putString("Port", port).putInt("FontSize", fontSize).apply();
    }

    public static Settings getSettings(AppCompatActivity activity) {
        if (instance == null)
            instance = new Settings(activity);

        return instance;
    }
}
