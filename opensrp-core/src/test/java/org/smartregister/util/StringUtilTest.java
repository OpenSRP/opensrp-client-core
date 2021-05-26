package org.smartregister.util;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void assertShouldCapitalize() throws Exception {
        org.junit.Assert.assertEquals("Abc", org.smartregister.util.StringUtil.humanize("abc"));
        org.junit.Assert.assertEquals("Abc", org.smartregister.util.StringUtil.humanize("Abc"));
    }

    @Test
    public void shouldReplaceUnderscoreWithSpace() throws Exception {
        org.junit.Assert.assertEquals("Abc def", org.smartregister.util.StringUtil.humanize("abc_def"));
        org.junit.Assert.assertEquals("Abc def", org.smartregister.util.StringUtil.humanize("Abc_def"));
    }

    @Test
    public void assertShouldHandleEmptyAndNull() throws Exception {
        org.junit.Assert.assertEquals("", org.smartregister.util.StringUtil.humanize(""));
    }

    @Test
    public void assertStringUtilNotNull() throws Exception {
        org.junit.Assert.assertNotNull(new StringUtil());
    }

    @Test
    public void assertReplaceAndHumanizeCallsHumanize() throws Exception {
        org.junit.Assert.assertEquals("Abc def", StringUtil.replaceAndHumanize("abc def", " ", "_"));
    }

    @Test
    public void assertReplaceAndHumanizeWithInitCapTextCallsHumanize() throws Exception {
        org.junit.Assert.assertEquals("Abc def", StringUtil.replaceAndHumanizeWithInitCapText("abc def", " ", "_"));
    }

    @Test
    public void assertHumanizeAndUppercase() throws Exception {
        org.junit.Assert.assertEquals("ABC DEF", StringUtil.humanizeAndUppercase("abc def", " "));
    }
}
