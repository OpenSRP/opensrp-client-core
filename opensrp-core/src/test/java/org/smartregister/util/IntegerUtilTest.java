package org.smartregister.util;


import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

public class IntegerUtilTest extends BaseUnitTest {

    @Test
    public void shouldReturnDefaultValueWhenStringIsNaN() {
        int value = IntegerUtil.tryParse("NaN", 0);
        Assert.assertEquals(value, 0);
    }

    @Test
    public void shouldReturn0WhenStringValueIs0() {
        int value = IntegerUtil.tryParse("0", 0);

        Assert.assertEquals(value, 0);
    }

    @Test
    public void shouldReturn1WhenStringValueIs01() {
        int value = IntegerUtil.tryParse("01", 0);

        Assert.assertEquals(value, 1);
    }

    @Test
    public void shouldReturn10WhenStringValueIs10() {
        int value = IntegerUtil.tryParse("10", 0);

        Assert.assertEquals(value, 10);
    }

    @Test
    public void shouldReturnDefaultValueWhenStringIsEmpty() {
        int value = IntegerUtil.tryParse("", 1);

        Assert.assertEquals(value, 1);
    }

    @Test
    public void shouldReturnStringDefaultValueWhenStringIsNaN() {
        String value = IntegerUtil.tryParse("NaN", "0");

        Assert.assertEquals(value, "0");
    }

    @Test
    public void shouldReturnString0WhenStringValueIs0() {
        String value = IntegerUtil.tryParse("0", "0");

        Assert.assertEquals(value, "0");
    }

    @Test
    public void shouldReturnString1WhenStringValueIs01() {
        String value = IntegerUtil.tryParse("01", "0");

        Assert.assertEquals(value, "1");
    }

    @Test
    public void shouldReturnString10WhenStringValueIs10() {
        String value = IntegerUtil.tryParse("10", "0");

        Assert.assertEquals(value, "10");
    }

    @Test
    public void shouldReturnStringDefaultValueWhenStringIsEmpty() {
        String value = IntegerUtil.tryParse("", "1");

        Assert.assertEquals(value, "1");
    }

    @Test
    public void assertInitializationNotNull() {
        Assert.assertNotNull(new IntegerUtil());
    }

    @Test
    public void assertIntegerCompareReturnsInt() {
        Assert.assertEquals(IntegerUtil.compare(1, 2), -1);
        Assert.assertEquals(IntegerUtil.compare(2, 2), 0);
        Assert.assertEquals(IntegerUtil.compare(3, 2), 1);
    }
}
