package com.shootr.mobile.stetho;

import java.io.PrintStream;
import timber.log.Timber;

public class StethoTree implements Timber.Tree {

    private final PrintStream output;

    public StethoTree(PrintStream output) {
        this.output = output;
    }

    @Override
    public void v(String message, Object... args) {
        printLog("V", message, args);
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        printLog("V", message, args);
        printThrowable(t);
    }

    @Override
    public void d(String message, Object... args) {
        printLog("D", message, args);
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        printLog("D", message, args);
        printThrowable(t);
    }

    @Override
    public void i(String message, Object... args) {
        printLog("I", message, args);
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        printLog("I", message, args);
        printThrowable(t);
    }

    @Override
    public void w(String message, Object... args) {
        printLog("W", message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        printLog("W", message, args);
        printThrowable(t);
    }

    @Override
    public void e(String message, Object... args) {
        printLog("E", message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        printLog("E", message, args);
        printThrowable(t);
    }

    private void printLog(String level, String message, Object... args) {
        output.println(level + "/ " + String.format(message, args));
    }

    private void printThrowable(Throwable t) {
        output.println(t.getClass().getSimpleName() + " thrown. See logcat for details.");
    }
}
