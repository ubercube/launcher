package fr.veridiangames.launcher;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils
{
    public static boolean unzip(String zipFilePath, String fileName, String destDirectory) {
        try {
            System.out.println("Unziping File (" + fileName + ")");
            File destDir = new File(destDirectory);
            if (!destDir.exists()) {
                destDir.mkdir();
            }
            ZipInputStream zipIn;
            zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            System.out.println("File (" + fileName + ") has been unziped !");
            zipIn.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    public static boolean download(final String adresse, final String path) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;

        String adress[] = adresse.split("/");
        String fileName = adress[adress.length - 1];

        try {
            URL url = new URL(adresse);
            out = new BufferedOutputStream(new FileOutputStream(path));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];

            int numRead;
            long numWritten = 0;
            System.out.println("Downloading File (" + fileName + ")");
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }
            System.out.println("File (" + path + "): has been downloaded !");
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
            }
        }
        return false;
    }

    public static int getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        } finally {
            conn.disconnect();
        }
    }

    public static void deleteFolder(File dir)
    {
        if(dir == null) return;

        final File[] files = dir.listFiles();
        if(files != null)
            for (File f: files) deleteFolder(f);
        dir.delete();
    }

    public static String cropText(String text, int max)
    {
        if(text.length() > max)
        {
            text = text.substring(text.length() - max, text.length());
            text = text.substring(text.indexOf("\n") + 1, text.length());
        }

        return text;
    }
}
