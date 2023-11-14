package ru.alemakave.mfstock;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import ru.alemakave.mfstock.exception.IncorrectArgumentException;

import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectIp;
import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectPort;

public class Settings {
    private static Settings instance = null;
    private final SharedPreferences preferences;
    private String ip = "127.0.0.1";
    private int port = 9090;
    private int fontSize = 12;

    private Settings(AppCompatActivity activity) {
        preferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setIp(String ip) {
        if (!isCorrectIp(ip)) {
            throw new IncorrectArgumentException("Port is incorrect!");
        }

        this.ip = ip;
    }

    public void setPort(int port) {
        if (!isCorrectPort(port)) {
            throw new IncorrectArgumentException("Port is incorrect!");
        }

        this.port = port;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void loadSettings() {
        boolean isUpdateConfigFile = false;

        ip = preferences.getString("IP", ip);

        if (preferences.contains("Port") && preferences.getAll().get("Port") instanceof String) {
            port = Integer.parseInt(preferences.getString("Port", Integer.toString(port)));
            isUpdateConfigFile = true;
        } else {
            port = preferences.getInt("Port", port);
        }

        fontSize = preferences.getInt("FontSize", fontSize);

        if (isUpdateConfigFile) {
            saveSettings();
        }
    }

    public void saveSettings() {
        preferences.edit().putString("IP", ip).putInt("Port", port).putInt("FontSize", fontSize).apply();
    }

    public static Settings getSettings(AppCompatActivity activity) {
        if (instance == null)
            instance = new Settings(activity);

        return instance;
    }
}
