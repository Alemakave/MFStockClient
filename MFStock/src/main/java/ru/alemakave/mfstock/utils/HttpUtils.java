package ru.alemakave.mfstock.utils;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.alemakave.android.utils.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    public static String callAndWait(final Context context, String url) {
        final Exception[] exception = {null};

        ResponseRunnable runnable = new ResponseRunnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(20000, TimeUnit.MILLISECONDS)
                        .readTimeout(20000, TimeUnit.MILLISECONDS)
                        .writeTimeout(20000, TimeUnit.MILLISECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message()); //TODO: Localize
                    responseData.set(response.body().string());
                } catch (IOException e) {
                    exception[0] = e;
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        while (thread.isAlive())
            ;

        if (exception[0] != null) {
            String exceptionMessage = exception[0].getMessage();
            if (exception[0].getClass().getName().equals("java.net.ConnectException") && exceptionMessage != null && exceptionMessage.length() > 21)
                Logger.e(context, "Failed to connect to", exceptionMessage.substring(21)); //TODO: Localize
            else
                Logger.e(context, String.format("callAndWait[%s:%s]", exception[0].getClass().getName(), exception[0].getStackTrace()[0]), exceptionMessage); //TODO: Reformat and move to logger
        }

        return runnable.responseData.get();
    }
}
