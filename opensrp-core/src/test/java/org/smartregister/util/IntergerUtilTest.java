package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;

public class IntergerUtilTest {

    @Test
    public void shouldReturnCorrespondingIntegerValueWhenStringIsValidInteger() throws Exception {
        Assert.assertEquals((Object) 1, IntegerUtil.tryParse("1", 0));
    }

    @Test
    public void shouldReturnDefaultValueWhenStringIsInValidInteger() throws Exception {
        Assert.assertEquals((Object) 1, IntegerUtil.tryParse("invalid", 1));
        Assert.assertEquals((Object) 1, IntegerUtil.tryParse("", 1));
    }
}