package com.shootr.android.service;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DebugServiceAdapter {

    private static final int DEFAULT_DELAY_MS = 250;
    private static final int DEFAULT_VARIANCE_PCT = 40; // Network delay varies by ±40%.
    private static final int DEFAULT_ERROR_PCT = 0;
    private static final int ERROR_DELAY_FACTOR = 3; // Network errors will be scaled by this value.
    public static final boolean DEFAULT_CONNECTED = true;

    private ShootrService service;

    private int delay = DEFAULT_DELAY_MS;
    private int variancePercentage = DEFAULT_VARIANCE_PCT;
    private int errorPercentage = DEFAULT_ERROR_PCT;
    private boolean connected = DEFAULT_CONNECTED;

    private Random random = new Random();

    public ShootrService create(ShootrService service) {
        this.service = service;
        return (ShootrService) Proxy.newProxyInstance(service.getClass().getClassLoader(),
          new Class<?>[] { ShootrService.class }, new MockInvocationHandler());
    }

    //region Getters and Setters
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getVariancePercentage() {
        return variancePercentage;
    }

    public void setVariancePercentage(int variancePercentage) {
        this.variancePercentage = variancePercentage;
    }

    public int getErrorPercentage() {
        return errorPercentage;
    }

    public void setErrorPercentage(int errorPercentage) {
        this.errorPercentage = errorPercentage;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    //endregion

    private class MockInvocationHandler implements InvocationHandler {

        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            if (!isConnected()) {
                throw new IOException("Debug: not connected");
            }

            if (calculateIsFailure()) {
                sleep(calculateDelayForError());
                throw new IOException("Debug network error");
            }

            int callDelay = calculateDelayForCall();
            long beforeNanos = System.nanoTime();
            try {
                Object returnValue = method.invoke(service, args);
                long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeNanos);
                sleep(callDelay - tookMs);
                return returnValue;
            } catch (InvocationTargetException e) {
                long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beforeNanos);
                sleep(callDelay - tookMs);
                throw e.getCause();
            }
        }
    }

    public int calculateDelayForCall() {
        float errorPercent = variancePercentage / 100f; // e.g., 20 / 100f == 0.2f
        float lowerBound = 1f - errorPercent; // 0.2f --> 0.8f
        float upperBound = 1f + errorPercent; // 0.2f --> 1.2f
        float bound = upperBound - lowerBound; // 1.2f - 0.8f == 0.4f
        float delayPercent = (random.nextFloat() * bound) + lowerBound; // 0.8 + (rnd * 0.4)
        return (int) (delay * delayPercent);
    }

    private static void sleep(long ms) {
        // This implementation is modified from Android's SystemClock#sleep.
        long start = uptimeMillis();
        long duration = ms;
        boolean interrupted = false;
        while (duration > 0) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            duration = start + ms - uptimeMillis();
        }

        if (interrupted) {
            // Important: we don't want to quietly eat an interrupt() event,
            // so we make sure to re-interrupt the thread so that the next
            // call to Thread.sleep() or Object.wait() will be interrupted.
            Thread.currentThread().interrupt();
        }
    }

    private static long uptimeMillis() {
        return System.nanoTime() / 1000000L;
    }

    private long calculateDelayForError() {
        return random.nextInt(delay * ERROR_DELAY_FACTOR);
    }

    private boolean calculateIsFailure() {
        int randomValue = random.nextInt(100) + 1;
        return randomValue <= errorPercentage;
    }
}
