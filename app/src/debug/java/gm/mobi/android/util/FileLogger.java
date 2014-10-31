package gm.mobi.android.util;

import android.os.Environment;
import android.util.Log;
import gm.mobi.android.BuildConfig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timber.log.Timber;

public class FileLogger {
    public static final String SHOOTR_LOG_TXT = "/shootrLog.txt";
    private static final String LOG_FORMAT = "%s  %s:  %s\n\n";

    private static FileLogger ourInstance;
    private final SimpleDateFormat sdf;
    private BufferedWriter out;

    public static FileLogger getInstance() {
        if(!BuildConfig.USE_FILE_LOGGER) {
            throw new IllegalStateException("No debe usarse el FileLogger si el flag USE_FILE_LOGGER no está activado para éste BuildConfig");
        }
        if (ourInstance == null) {
            ourInstance = new FileLogger();
        }
        return ourInstance;
    }

    public static void log(String tag, String message) {
        if (!BuildConfig.USE_FILE_LOGGER) {
            Log.w("FileLogger", "Llamada FileLogger.log(), pero no está habilitado. Ignorando llamada.");
            return;
        }
        getInstance().logInternal(tag, message);
    }

    public static void flush() {
        if(!BuildConfig.USE_FILE_LOGGER) {
            Log.w("FileLogger", "Llamada FileLogger.log(), pero no está habilitado. Ignorando llamada.");
            return;
        }
        getInstance().flushInternal();
    }

    private FileLogger() {
        sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        initWriter();
    }

    private void initWriter() {
        File externalPath = Environment.getExternalStorageDirectory();

        if (externalPath.canWrite()) {
            FileWriter logWriter;
            try {
                File logFile = new File(externalPath, SHOOTR_LOG_TXT);
                logWriter = new FileWriter(logFile, true);
                out = new BufferedWriter(logWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void logInternal(String tag, String message) {
        try {
            String date = sdf.format(new Date());
            out.write(String.format(LOG_FORMAT, date, tag, message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flushInternal() {
        if(out==null) return;
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLog() {
        try {
            flush();
            File externalPath = Environment.getExternalStorageDirectory();
            File logFile = new File(externalPath, SHOOTR_LOG_TXT);
            return getStringFromFile(logFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return "Error de lectura del log";
        }
    }

    public boolean deleteLogFile() {
        File externalPath = Environment.getExternalStorageDirectory();
        File logFile = new File(externalPath, SHOOTR_LOG_TXT);
        boolean res = logFile.delete();
        initWriter();
        return res;
    }

    //Source: http://www.java2s.com/Code/Java/File-Input-Output/ConvertInputStreamtoString.htm

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile (String filePath) throws IOException {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static class FileLogTree implements Timber.TaggedTree {
        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
        private static final ThreadLocal<String> NEXT_TAG = new ThreadLocal<String>();

        private static String createTag() {
            String tag = NEXT_TAG.get();
            if (tag != null) {
                NEXT_TAG.remove();
                return tag;
            }

            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length < 6) {
                throw new IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using proguard?");
            }
            tag = stackTrace[5].getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }
            return tag.substring(tag.lastIndexOf('.') + 1);
        }

        static String formatString(String message, Object... args) {
            // If no varargs are supplied, treat it as a request to log the string without formatting.
            return args.length == 0 ? message : String.format(message, args);
        }

        @Override
        public void v(String message, Object... args) {
            throwShade(Log.VERBOSE, formatString(message, args), null);
        }

        @Override
        public void v(Throwable t, String message, Object... args) {
            throwShade(Log.VERBOSE, formatString(message, args), t);
        }

        @Override
        public void d(String message, Object... args) {
            throwShade(Log.DEBUG, formatString(message, args), null);
        }

        @Override
        public void d(Throwable t, String message, Object... args) {
            throwShade(Log.DEBUG, formatString(message, args), t);
        }

        @Override
        public void i(String message, Object... args) {
            throwShade(Log.INFO, formatString(message, args), null);
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            throwShade(Log.INFO, formatString(message, args), t);
        }

        @Override
        public void w(String message, Object... args) {
            throwShade(Log.WARN, formatString(message, args), null);
        }

        @Override
        public void w(Throwable t, String message, Object... args) {
            throwShade(Log.WARN, formatString(message, args), t);
        }

        @Override
        public void e(String message, Object... args) {
            throwShade(Log.ERROR, formatString(message, args), null);
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            throwShade(Log.ERROR, formatString(message, args), t);
        }

        private void throwShade(int priority, String message, Throwable t) {
            if (message == null || message.length() == 0) {
                if (t != null) {
                    message = Log.getStackTraceString(t);
                } else {
                    // Swallow message if it's null and there's no throwable.
                    return;
                }
            } else if (t != null) {
                message += "\n" + Log.getStackTraceString(t);
            }

            String tag = createTag();
            if (message.length() < 4000) {
                log(tag, message);
            } else {
                // It's rare that the message will be this large, so we're ok with the perf hit of splitting
                // and calling Log.println N times.  It's possible but unlikely that a single line will be
                // longer than 4000 characters: we're explicitly ignoring this case here.
                String[] lines = message.split("\n");
                for (String line : lines) {
                    log(tag, line);
                }
            }
        }

        @Override
        public void tag(String tag) {
            NEXT_TAG.set(tag);
        }
    }
}
