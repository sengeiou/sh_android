package gm.mobi.android.util;

import timber.log.Timber;

public class LogTreeFactoryImpl implements LogTreeFactory {

    @Override public Timber.Tree[] getTrees() {
        return new Timber.Tree[] {
          new Timber.DebugTree(), new FileLogger.FileLogTree()
        };
    }
}
