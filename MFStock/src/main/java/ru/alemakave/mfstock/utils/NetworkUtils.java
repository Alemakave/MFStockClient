package ru.alemakave.mfstock.utils;

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
}
