package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;

public class UrlUtilTest {

    @Test
    public void assertValidUrlPasses() {
        Assert.assertTrue(UrlUtil.isValidUrl("https://smartregister.atlassian.net/wiki/spaces/Documentation/overview"));
        Assert.assertTrue(UrlUtil.isValidUrl("http://smartregister.atlassian.net/wiki/spaces/Documentation/overview"));
    }

    @Test
    public void assertInValidUrlFails() {
        Assert.assertFalse(UrlUtil.isValidUrl("invalid.org"));
        Assert.assertFalse(UrlUtil.isValidUrl("error.test"));
    }
}
