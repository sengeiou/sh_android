package com.shootr.android.data.bus;

import com.squareup.otto.Bus;

public class MultiBusPublisher implements BusPublisher{

    private final Bus[] buses;

    public MultiBusPublisher(Bus... buses) {
        this.buses = buses;
    }

    @Override public void post(Object event) {
        for (Bus bus : buses) {
            bus.post(event);
        }
    }
}
