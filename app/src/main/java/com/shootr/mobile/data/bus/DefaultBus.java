package com.shootr.mobile.data.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class DefaultBus extends Bus{

    public DefaultBus() {
        super(ThreadEnforcer.ANY);
    }
}
