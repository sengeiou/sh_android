package com.shootr.mobile.util;

import java.text.DecimalFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class FollowFormatUtilTest {

    private static final String DOT = ".";
    private NumberFormatUtil followsFormatUtil;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        followsFormatUtil = new NumberFormatUtil();
    }

    @Test public void shouldReturnStringWithoutFormatWhenNumberIsLowerThanTenThousand() throws Exception {
        String result = followsFormatUtil.formatNumbers(1580L);
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        assertThat(result).isEqualTo(formatter.format(1580L));
    }

    @Test public void shouldReturnStringWithKSuffixWhenNumberIsGreaterOrEqualsThanTenThousand() throws Exception {
        String result = followsFormatUtil.formatNumbers(45000L);

        assertThat(result).isEqualTo("45.0K");
    }

    @Test public void shouldReturnStringWithMSuffixWhenNumberIsGreaterOrEqualsThanOneMillion() throws Exception {
        String result = followsFormatUtil.formatNumbers(1500000L);

        assertThat(result).isEqualTo("1.5M");
    }

    @Test public void shouldReturnStringZeroWhenNumberIsZero() throws Exception {
        String result = followsFormatUtil.formatNumbers(0L);

        assertThat(result).isEqualTo("0");
    }

    @Test public void shouldReturnNegativeNumberFormatWhenNumberIsNegative() throws Exception {
        String result = followsFormatUtil.formatNumbers(-200000L);

        assertThat(result).isEqualTo("-200.0K");
    }
}
