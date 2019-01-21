package com.example.alarmmanager;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import timber.log.Timber;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileLoggingTree extends Timber.DebugTree {

    public static final String PATH = "Log";
    private static final String LOG_TAG = FileLoggingTree.class.getSimpleName();
    private Context context;

    public FileLoggingTree(Context context) {
        this.context = context;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            String fileNameTimeStamp = new SimpleDateFormat(TimeUtils.FILE_NAME_DATE_FORMAT,
                    Locale.getDefault()).format(new Date());
            String logTimeStamp = new SimpleDateFormat(TimeUtils.LOG_DATE_FORMAT,
                    Locale.getDefault()).format(new Date());
            String fileName = "alarm-" +fileNameTimeStamp + ".html";

            // Create file
            File file  = generateFile(PATH, fileName);

            // If file created or exists save logs
            if (file != null) {
                FileWriter writer = new FileWriter(file, true);
                writer.append("<p style=\"background:lightgray;\"><strong "
                        + "style=\"background:lightblue;\">&nbsp&nbsp")
                        .append(logTimeStamp)
                        .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                        .append(tag)
                        .append("</strong> - ")
                        .append(message)
                        .append("</p>");
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG,"Error while logging into file : " + e);
        }
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.getLineNumber();
    }

    /*  Helper method to create file*/
    @Nullable
    private File generateFile(@NonNull String path, @NonNull String fileName) {
        File file = null;
        if (isExternalStorageAvailable()) {
            File root = new File(context.getFilesDir().getAbsolutePath());

            boolean dirExists = true;

            if (!root.exists()) {
                dirExists = root.mkdirs();
            }

            if (dirExists) {
                file = new File(root, fileName);
            }
        }
        return file;
    }

    /* Helper method to determine if external storage is available*/
    private static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}