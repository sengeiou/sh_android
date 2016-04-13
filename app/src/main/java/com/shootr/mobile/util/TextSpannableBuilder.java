package com.shootr.mobile.util;

import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;

public interface TextSpannableBuilder {

    CharSequence formatWithUsernameSpans(CharSequence comment, OnUsernameClickListener clickListener);
}
