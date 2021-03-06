package com.shootr.mobile.util;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import javax.inject.Inject;

public class NumberFormatUtil implements FormatNumberUtils {

    private static final NavigableMap<Long, String> SUFFIXES_MAP = new TreeMap<>();
    public static final String FORMAT = "%.1f%c";
    public static final String SUFFIXES = "KMGTPE";
    public static final String COMMA = ",";
    public static final String DOT = ".";

    static {
        SUFFIXES_MAP.put(1000L, "K");
        SUFFIXES_MAP.put(1000000L, "M");
    }

    @Inject public NumberFormatUtil() {
    }

    @Override public String formatNumbers(Long number) {
        if (number >= 100000 || number < 0) {
            return formatNumbersBiggerThanHundredThousand(number);
        }

        if (number < 10000) {
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            return formatter.format(number);
        }

        int exp = (int) (Math.log(number) / Math.log(1000));
        String result = formatString(number, exp);
        String comma = COMMA;
        String dot = DOT;
        if (result.contains(comma)) {
            result = result.replaceAll(comma, dot);
        }
        return result;
    }

    @Override public String formatNumbersBiggerThanHundredThousand(Long number) {
        if (number == Long.MIN_VALUE) {
            return formatNumbersBiggerThanHundredThousand(Long.MIN_VALUE + 1);
        }
        if (number < 0) {
            return "-" + formatNumbersBiggerThanHundredThousand(-number);
        }
        if (number < 10000) {
            return Long.toString(number);
        }

        Map.Entry<Long, String> e = SUFFIXES_MAP.floorEntry(number);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = number / (divideBy / 10);
        return (truncated / 10d) + suffix;
    }

    private String formatString(Long number, Integer exp) {
        return String.format(FORMAT, number / Math.pow(1000, exp), SUFFIXES.charAt(exp - 1));
    }
}
