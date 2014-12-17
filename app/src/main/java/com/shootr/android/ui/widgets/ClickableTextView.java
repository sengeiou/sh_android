package com.shootr.android.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;
import com.shootr.android.util.Patterns;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickableTextView extends TextView {

    private static final String[] allowedSchemas = { "http://", "https://", "rtsp://"};
    private static final String defaultSchema = allowedSchemas[0];

    private Pattern urlPattern;

    public ClickableTextView(Context context) {
        super(context);
    }

    public ClickableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClickableTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override public void setText(CharSequence text, BufferType type) {
        // Force spannable text
        super.setText(text, BufferType.SPANNABLE);
    }

    /**
     * Call this method after setting the text with links.
     * Matches any link in the text and sets UrlSpans on them.
     */
    public void addLinks() {
        if (urlPattern == null) {
            urlPattern = Patterns.WEB_URL;
        }
        CharSequence text = getText();
        Matcher matcher = urlPattern.matcher(text);
        SpannableStringBuilder ss = new SpannableStringBuilder(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String url = makeUrl(matcher.group());
            ss.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(ss, BufferType.SPANNABLE);
    }

    /**
     * From {@link Linkify}'s makeUrl()
     */
    private String makeUrl(String url) {
        boolean hasPrefix = false;

        for (int i = 0; i < allowedSchemas.length; i++) {
            if (url.regionMatches(true, 0, allowedSchemas[i], 0, allowedSchemas[i].length())) {
                hasPrefix = true;

                // Fix capitalization if necessary
                if (!url.regionMatches(false, 0, allowedSchemas[i], 0, allowedSchemas[i].length())) {
                    url = allowedSchemas[i] + url.substring(allowedSchemas[i].length());
                }

                break;
            }
        }

        if (!hasPrefix) {
            url = defaultSchema + url;
        }

        return url;
    }


    public void setUrlPattern(Pattern pattern) {
        this.urlPattern = pattern;
    }

    /**
     * Triggers ClickableSpans' onClick event when tapped over the span, and does nothing otherwise.
     * Mirror implementation from {@link LinkMovementMethod}
     */
    @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
        TextView widget = this;
        Object text = widget.getText();
        if (text instanceof Spannable) {
            Spannable buffer = (Spannable) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                    } else if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }
        }
        return false;
    }
}
