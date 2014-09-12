package gm.mobi.android;

import gm.mobi.android.GolesApplication;
import gm.mobi.android.GolesModule;

final class Modules {
    static Object[] list(GolesApplication app) {
        return new Object[] {
                new GolesModule(app)
        };
    }

    private Modules() {
        // No instances.
    }
}