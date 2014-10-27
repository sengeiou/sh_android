package gm.mobi.android;

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