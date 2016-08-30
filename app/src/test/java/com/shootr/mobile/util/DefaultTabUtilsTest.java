package com.shootr.mobile.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultTabUtilsTest {

  private static final String PAIR_TEXT = "554c7366e4b0cba72c91a7ef";
  private static final String UNPAIR_TEXT = "554c7366e4b0cba72c91a7bd";
  private static final String EMPTY_TEXT = "";
  private static final int DEFAULT_POSITION = 0;
  private static final int POSITION = 1;

  private DefaultTabUtils defaultTabUtils;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    defaultTabUtils = new DefaultTabUtils();
  }

  @Test public void shouldReturnDefaultPositionWhenTextHashCodeIsPair() throws Exception {
    Integer result = defaultTabUtils.getDefaultTabPosition(PAIR_TEXT);

    assertThat(result).isEqualTo(DEFAULT_POSITION);
  }

  @Test public void shouldReturnPositionWhenTextHashCodeIsUnpair() throws Exception {
    Integer result = defaultTabUtils.getDefaultTabPosition(UNPAIR_TEXT);

    assertThat(result).isEqualTo(POSITION);
  }

  @Test public void shouldReturnPositionZeroWhenTextHashCodeIsPair() throws Exception {
    Integer result = defaultTabUtils.getDefaultTabPosition(PAIR_TEXT);

    assertThat(result).isEqualTo(DEFAULT_POSITION);
  }

  @Test public void shouldReturnPositionWhenTextIsNull() throws Exception {
    Integer result = defaultTabUtils.getDefaultTabPosition(null);

    assertThat(result).isEqualTo(POSITION);
  }

  @Test public void shouldReturnPositionWhenTextIsEmpty() throws Exception {
    Integer result = defaultTabUtils.getDefaultTabPosition(EMPTY_TEXT);

    assertThat(result).isEqualTo(POSITION);
  }
}
