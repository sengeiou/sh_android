package com.shootr.mobile.domain.bus;

public interface BusPublisher {

    void post(Object event);
}
