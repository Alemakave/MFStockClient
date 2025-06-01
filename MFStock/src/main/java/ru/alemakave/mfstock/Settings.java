package ru.alemakave.mfstock;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import lombok.Getter;
import lombok.Setter;
import ru.alemakave.mfstock.exception.IncorrectArgumentException;

import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectHost;
import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectPort;

public class Settings {
    private static Settings instance = null;
    private final SharedPreferences preferences;
    private String host = "127.0.0.1";
    @Getter
    private int port = 9090;
    @Getter
    @Setter
    private int fontSize = 12;
    @Getter
    @Setter
    private int checkConnectionTimeout = 5000;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;

    private Settings(AppCompatActivity activity) {
        preferences = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public String getHost() {
        if (host.startsWith("http://") || host.startsWith("https")) {
            return host;
        } else {
            return host;
        }
    }

    public void setHost(String host) {
        if (!isCorrectHost(host)) {
            throw new IncorrectArgumentException("Host is incorrect!");
        }

        this.host = host;
    }

    public void setPort(int port) {
        if (!isCorrectPort(port)) {
            throw new IncorrectArgumentException("Port is incorrect!");
        }

        this.port = port;
    }

    public void loadSettings() {
        boolean isUpdateConfigFile = false;

        host = preferences.getString("IP", host);

        if (preferences.contains("Port") && preferences.getAll().get("Port") instanceof String) {
            port = Integer.parseInt(preferences.getString("Port", Integer.toString(port)));
            isUpdateConfigFile = true;
        } else {
            port = preferences.getInt("Port", port);
        }

        username = preferences.getString("Username", "");
        password = preferences.getString("Password", "");

        fontSize = preferences.getInt("FontSize", fontSize);

        if (isUpdateConfigFile) {
            saveSettings();
        }
    }

    public void saveSettings() {
        preferences.edit()
                .putString("IP", host)
                .putInt("Port", port)
                .putInt("FontSize", fontSize)
                .putString("Username", username)
                .putString("Password", password)
                .apply();
    }

    public static Settings getSettings(AppCompatActivity activity) {
        if (instance == null) {
            instance = new Settings(activity);
        }

        return instance;
    }
}
