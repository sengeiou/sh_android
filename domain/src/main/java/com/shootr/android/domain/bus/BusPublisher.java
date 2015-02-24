package com.shootr.android.domain.bus;

public interface BusPublisher {

    void post(Object event);
}
