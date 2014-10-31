package gm.mobi.android;

final class Modules {
    static Object[] list(ShootrApplication app) {
        return new Object[] {
                new ShootrModule(app),
                new DebugShootrModule()
        };
    }

    private Modules() {
        // No instances.
    }
}