package com.shootr.mobile.ui.debug;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier @Retention(RUNTIME)
public @interface NetworkProxy {
}
