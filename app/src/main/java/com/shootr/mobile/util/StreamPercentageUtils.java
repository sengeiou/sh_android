package com.shootr.mobile.util;

import java.text.DecimalFormat;
import javax.inject.Inject;

public class StreamPercentageUtils implements PercentageUtils {
    private static final String FORMAT_PERCENTAGE = "#,##0.0";

    @Inject public StreamPercentageUtils() {
    }


    @Override public Double getPercentage(Long dividend, Long divider){
        Double result = 0.0;
        if(divider!=null && dividend != null && divider !=0 ){
            result = (double)((float) dividend / divider * 100);
        }
        return result;
    }

    @Override public String formatPercentage(Double number){
        String numberFormatted="";
        if(number != null){
            DecimalFormat decimalFormat = new DecimalFormat(FORMAT_PERCENTAGE);
            numberFormatted= decimalFormat.format(number);
        }

        return numberFormatted;
    }

}
