package com.shootr.mobile.util;

import java.text.DecimalFormat;
import javax.inject.Inject;

public class StreamPercentageUtils implements PercentageUtils {
    private static final String FORMAT_PERCENTAGE = "#,##0.0";

    @Inject public StreamPercentageUtils() {
    }


    @Override public Double getPercentage(Long number, Long number2){
        return (double)((float)number/number2 * 100);
    }

    @Override public String formatPercentage(Double number){
        DecimalFormat decimalFormat = new DecimalFormat(FORMAT_PERCENTAGE);
        return decimalFormat.format(number);
    }

}
