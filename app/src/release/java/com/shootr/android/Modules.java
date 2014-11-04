package com.shootr.android;

final class Modules {
    static Object[] list(ShootrApplication app) {
        return new Object[] {
                new ShootrModule(app)
        };
    }

    private Modules() {
        // No instances.
    }
}