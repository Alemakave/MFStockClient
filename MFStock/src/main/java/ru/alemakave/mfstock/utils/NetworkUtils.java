package ru.alemakave.mfstock.utils;

import android.app.Activity;
import android.content.Context;
import android.net.*;

import java.net.URL;
import java.net.URLConnection;

import static ru.alemakave.mfstock.utils.ConnectionStatus.*;

public final class NetworkUtils {
    public static boolean isCorrectIp(String ip) {
        try {
            boolean isCorrected = true;
            String[] ipParts = ip.split("\\.");

            isCorrected &= ipParts.length == 4;
            for (String ipPart : ipParts) {
                isCorrected &= Integer.parseInt(ipPart) >= 0;
                isCorrected &= Integer.parseInt(ipPart) <= 255;
            }

            return isCorrected;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean isCorrectPort(int port) {
        try {
            boolean isCorrect = true;

            isCorrect &= port >= 0;
            isCorrect &= port <= 65535;

            return isCorrect;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    @Deprecated
    public static boolean checkConnection(Activity activity, String address, int timeout) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
            try {
                final Exception[] exception = {null};

                ResponseRunnable runnable = new ResponseRunnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(String.format("http://%s/", address));
                            URLConnection connection = url.openConnection();
                            connection.setConnectTimeout(timeout);
                            connection.setReadTimeout(timeout);
                            connection.connect();
                        } catch (Exception e) {
                            exception[0] = e;
                        }
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();

                while (thread.isAlive())
                    ;

                if (exception[0] != null) {
                    throw exception[0];
                }
            } catch (Exception e) {
                isConnected = false;
            }
        }

        return isConnected;
    }

    public static ConnectionStatus tryConnect(Activity activity, String address, int timeout) {
        return tryConnect(activity, address, timeout, null, null);
    }

    public static ConnectionStatus tryConnect(Activity activity, String address, int timeout, String username, String password) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return NOT_CONNECTED_TO_NETWORK;
        }

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork == null) {
            return NOT_CONNECTED_TO_NETWORK;
        }

        try {
            final Exception[] exception = {null};

            ResponseRunnable runnable = new ResponseRunnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(String.format("http://%s/", address));
                        URLConnection connection = url.openConnection();
                        connection.setConnectTimeout(timeout);
                        connection.setReadTimeout(timeout);
                        connection.connect();
                    } catch (Exception e) {
                        exception[0] = e;
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

            while (thread.isAlive())
                ;

            if (exception[0] != null) {
                throw exception[0];
            }
        } catch (Exception e) {
            return CONNECTION_ERROR;
        }

        return CONNECTED;
    }
}
