package com.shootr.mobile.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class FollowNumbersFormatUtilsTest {

    private static final String DOT = ".";
    private FollowNumbersFormatUtils followNumbersFormatUtils;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        followNumbersFormatUtils = new FollowNumbersFormatUtils();
    }

    @Test public void shouldReturnStringWithoutFormatWhenNumberIsLowerThanOneThousand() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(999L);

        assertThat(result).isEqualTo("999");
    }

    @Test public void shouldReturnStringWithKSuffixWhenNumberIsGreaterOrEqualsThanOneThousandAndLowerThanOneMillion() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(1000L);

        assertThat(result).isEqualTo("1.0K");
    }

    @Test public void shouldReturnStringWithoutDotWhenNumberIsGreaterThanOneHundredThousand() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(100001L);

        assertThat(result).doesNotContain(DOT);
    }

    @Test public void shouldReturnStringWithDotWhenNumberIsLowerOrEqualThanOneHundredThousand() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(100000L);

        assertThat(result).contains(DOT);
    }

    @Test public void shouldReturnStringWithMSuffixWhenNumberIsGreaterOrEqualsThanOneMillion() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(1000000L);

        assertThat(result).isEqualTo("1M");
    }

    @Test public void shouldReturnStringWithoutDotWhenNumberIsGreaterOrEqualsThanOneMillionAndNumberUnitsAreLowerThanHundredThousand() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(1099999L);

        assertThat(result).doesNotContain(DOT);
    }

    @Test public void shouldReturnStringWithDotWhenNumberIsGreaterOrEqualsThanOneMillionAndLowerThanTenMillionAndNumberUnitsAreGreaterThanOneHundredThousand() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(1500000L);

        assertThat(result).contains(DOT);
    }

    @Test public void shouldReturnStringWithoutDotWhenNumberIsGreaterOrEqualsThanThanTenMillionAndNumberUnitsAreGreaterThanOneHundredThousand() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(15500000L);

        assertThat(result).doesNotContain(DOT);
    }

    @Test public void shouldReturnStringZeroWhenNumberIsZero() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(0L);

        assertThat(result).isEqualTo("0");
    }

    @Test public void shouldReturnNegativeNumberFormatWhenNumberIsNegative() throws Exception {
        String result = followNumbersFormatUtils.formatNumbers(-200000L);

        assertThat(result).isEqualTo("-200K");
    }
}
