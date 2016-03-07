package com.shootr.mobile.ui.component;

import android.app.Activity;
import java.io.File;

public class OptionPickerController extends PhotoPickerController {

    private OptionPickerController(Activity activity, Handler handler, File temporaryFiles) {
        super(activity, handler, temporaryFiles);
    }
}
