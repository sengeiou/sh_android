package com.shootr.mobile.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.model.ShotModel;

public class Clipboard {

    public static final String SHOT_LABEL = "Shot";

    public static void copyShotComment(Context context, ShotModel shotModel) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(SHOT_LABEL, shotModel.getComment());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
    }

    private Clipboard() {
        throw new AssertionError("No instances.");
    }

}
