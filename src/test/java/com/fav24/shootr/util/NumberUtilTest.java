package com.fav24.shootr.util;

import org.junit.Test;

import com.fav24.shootr.util.NumberUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NumberUtilTest {


    @Test
    public void isValidIntegerTestCatchOkNulls() {
        assertFalse(NumberUtil.isValidInteger(null));
    }

    @Test
    public void isValidIntegerTestCatchOKEmpty() {
        assertFalse(NumberUtil.isValidInteger(""));
    }


    @Test
    public void isValidIntegerGenericTest() {


        assertTrue(NumberUtil.isValidInteger("1"));
        assertFalse(NumberUtil.isValidInteger("1234.234"));
        assertFalse(NumberUtil.isValidInteger("1234,234"));
        assertFalse(NumberUtil.isValidInteger("a"));
        assertTrue(NumberUtil.isValidInteger("" + Integer.MAX_VALUE));
        assertFalse(NumberUtil.isValidInteger("" + Integer.MAX_VALUE + 1));
        assertFalse(NumberUtil.isValidInteger("2147483649"));
        assertTrue(NumberUtil.isValidInteger("" + Integer.MIN_VALUE));

    }

    @Test
    public void isValidLongTestCatchOkNulls() {
        assertFalse(NumberUtil.isValidLong(null));
    }

    @Test
    public void isValidLongTestCatchOKEmpty() {
        assertFalse(NumberUtil.isValidLong(""));
    }

    @Test
    public void isValidLongGenericTest() {

        assertTrue(NumberUtil.isValidLong("1"));
        assertFalse(NumberUtil.isValidLong("1234.234"));
        assertFalse(NumberUtil.isValidLong("1234,234"));
        assertFalse(NumberUtil.isValidLong("a"));
        assertTrue(NumberUtil.isValidLong("" + Long.MAX_VALUE));
        assertFalse(NumberUtil.isValidLong("" + Long.MAX_VALUE + 1));
        assertTrue(NumberUtil.isValidLong("9223372036854775807"));
        assertFalse(NumberUtil.isValidLong("9223372036854775808"));
        assertTrue(NumberUtil.isValidLong("2147483649"));
        assertTrue(NumberUtil.isValidLong("" + Long.MIN_VALUE));

    }
}