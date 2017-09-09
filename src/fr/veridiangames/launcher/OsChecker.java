package fr.veridiangames.launcher;

import java.util.Locale;

public class OsChecker {

    public static final int NULL = 0;
    public static final int WINDOWS = 1;
    public static final int LINUX = 2;
    public static final int MACOS = 3;

    public static int getOsId()
    {
        int detectedOS = NULL;

        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            detectedOS = MACOS;
        } else if (OS.contains("win")) {
            detectedOS = WINDOWS;
        } else if (OS.contains("nux")) {
            detectedOS = LINUX;
        }

        return  detectedOS;
    }

    public static String getOsName()
    {
        switch (getOsId())
        {
            case WINDOWS:
                return "windows";
            case LINUX:
                return "linux";
            case MACOS:
                return "macos";
            default:
                System.err.println("Os not found !");
                System.exit(1);
        }
        return null;
    }
}
