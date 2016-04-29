package com.shootr.mobile.data;

import com.shootr.mobile.service.ApiModule;
import com.shootr.mobile.service.DebugApiModule;

public enum ApiEndpoints {
    TEST("Tst", DebugApiModule.TEST_ENDPOINT_URL),
    TEST_SSL("Tst SSL", DebugApiModule.TEST_SSL_ENDPOINT_URL),
    PRODUCTION("Production", ApiModule.PRODUCTION_ENDPOINT_URL),
    PRE("Pre-production", ApiModule.PRE_PRODUCTION_ENDPOINT_URL),
    CUSTOM("Custom", null);

    public final String name;
    public final String url;

    ApiEndpoints(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override public String toString() {
        return name;
    }

    public static ApiEndpoints from(String endpoint) {
        for (ApiEndpoints value : values()) {
            if (value.url != null && value.url.equals(endpoint)) {
                return value;
            }
        }
        return CUSTOM;
    }

}
