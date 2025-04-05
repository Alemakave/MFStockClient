package ru.alemakave.mfstock;

public class BuildConfig {
    public static final boolean DEBUG = true;
    public static final int VERSION_MAGOR = 0;
    public static final int VERSION_MINOR = 1;
    public static final int VERSION_BUILD = 3;
    public static final int VERSION_CODE = ((VERSION_MAGOR << 8) + (VERSION_MINOR << 4) + VERSION_BUILD);
    public static final String VERSION_NAME = String.format("%d.%d.%d-ALPHA", VERSION_MAGOR, VERSION_MINOR, VERSION_BUILD);
}
