package ru.alemakave.mfstock.utils;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.alemakave.android.utils.Logger;
import ru.alemakave.mfstock.MainActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    public static Response callAndWait(final MainActivity context, String url) {
        String host = null;

        if (url.startsWith("http://")) {
            host = url.substring("http://".length());
        } else if (url.startsWith("https://")) {
            host = url.substring("https://".length());
        }

        if (host != null) {
            if (host.contains("/")) {
                host = host.substring(0, host.indexOf('/'));
            }

            if (host.contains(":")) {
                host = host.substring(0, host.indexOf(':'));
            }
        }

        final Exception[] exception = {null};

        String finalHost = host;
        ResponseRunnable runnable = new ResponseRunnable() {
            @Override
            public void run() {
                OkHttpClient client = NetworkUtils.getClientBuilder(context.getSettings().getCheckConnectionTimeout(), TimeUnit.MILLISECONDS)
                        .authenticator((route, response) -> {
                            String username = context.getSettings().getUsername();
                            String password = context.getSettings().getPassword();

                            responseCode.set(response.code());

                            if (username.isEmpty() || password.isEmpty()) {
                                return response.request();
                            }

                            String authData = Credentials.basic(username, password);
                            return response.request()
                                    .newBuilder()
                                    .header("Authorization", authData)
                                    .header("Host", finalHost == null ? "" : finalHost)
                                    .build();
                        })
                        .build();

                Request request = new Request.Builder()
                        .header("Host", finalHost == null ? "" : finalHost)
                        .url(url)
                        .build();

                responseRequest.set(request);

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message()); //TODO: Localize
                    }
                    responseCode.set(response.code());
                    responseMediaType.set(response.body().contentType());
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

        if (runnable.responseData.get() == null) {
            runnable.responseData.set("");
        }

        return new Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .message("")
                .request(runnable.responseRequest.get())
                .code(runnable.responseCode.get())
                .body(ResponseBody.create(runnable.responseMediaType.get(), runnable.responseData.get()))
                .build();
    }
}
