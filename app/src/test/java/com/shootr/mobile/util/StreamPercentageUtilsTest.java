package com.shootr.mobile.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamPercentageUtilsTest {

    public static final long ONE = 1L;
    public static final long TEN = 10L;
    public static final long ZERO = 0L;
    public static final long NEGATIVE = -1L;
    public static final double RESULT_ZERO = 0.0;
    StreamPercentageUtils streamPercentageUtils;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        streamPercentageUtils = new StreamPercentageUtils();
    }

    @Test public void shouldReturnMoreThanZeroWhenDividenIsDifferentToZero() throws Exception {
        Double result = streamPercentageUtils.getPercentage(ONE, TEN);

        assertThat(result).isGreaterThan(RESULT_ZERO);
    }

    @Test public void shouldReturnZeroWhenDividenIsZero() throws Exception {
        Double result = streamPercentageUtils.getPercentage(ZERO, TEN);

        assertThat(result).isEqualTo(RESULT_ZERO);
    }

    @Test public void shouldReturnMoreThanZeroWhenDividerIsDifferentToZero() throws Exception {
        Double result = streamPercentageUtils.getPercentage(ONE, TEN);

        assertThat(result).isGreaterThan(RESULT_ZERO);
    }

    @Test public void shouldReturnZeroWhenDividerIsZero() throws Exception {
        Double result = streamPercentageUtils.getPercentage(ONE, ZERO);

        assertThat(result).isEqualTo(RESULT_ZERO);
    }

    @Test public void shouldReturnZeroWhenDividerIsNull() throws Exception {
        Double result = streamPercentageUtils.getPercentage(ONE, null);

        assertThat(result).isEqualTo(RESULT_ZERO);
    }

    @Test public void shouldReturnZeroWhenDividenIsNull() throws Exception {
        Double result = streamPercentageUtils.getPercentage(null, TEN);

        assertThat(result).isEqualTo(RESULT_ZERO);
    }

    @Test public void shouldFormatNumberWhenDoubleIsDifferentToNull() throws Exception {
        String numberFormatted = streamPercentageUtils.formatPercentage(2.0);

        assertThat(numberFormatted).isNotNull();
    }

    @Test public void shouldNotFormatNumberWhenDoubleIsNull() throws Exception {
        String numberFormatted = streamPercentageUtils.formatPercentage(null);

        assertThat(numberFormatted).isEmpty();
    }

    @Test public void shouldReturnZeroWhenDividenIsNegative() throws Exception {
        Double result = streamPercentageUtils.getPercentage(NEGATIVE, TEN);

        assertThat(result).isEqualTo(RESULT_ZERO);
    }

    @Test public void shouldReturnZeroWhenDividerIsNegative() throws Exception {
        Double result = streamPercentageUtils.getPercentage(TEN, NEGATIVE);

        assertThat(result).isEqualTo(RESULT_ZERO);
    }
}
