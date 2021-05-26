package org.smartregister.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ndegwamartin on 2019-07-23.
 */
public class AppPropertiesTest {

    private static final String PROP_KEY = "PROP_KEY";
    private static final String TEST_VAL = "TEST_VAL";
    private static final String TEST_VAL_TRUE = "true";

    @Test
    public void testAppPropertiesInstantiatesCorrectly() {

        AppProperties properties = new AppProperties();
        Assert.assertNotNull(properties);
    }

    @Test
    public void testGetPropertyBooleanReturnsCorrectValue() {

        AppProperties properties = new AppProperties();
        Boolean value = properties.getPropertyBoolean(PROP_KEY);
        Assert.assertNotNull(value);
        Assert.assertFalse(value);

        properties.setProperty(PROP_KEY, TEST_VAL_TRUE);
        value = properties.getPropertyBoolean(PROP_KEY);
        Assert.assertNotNull(value);
        Assert.assertTrue(value);
    }

    @Test
    public void testHasPropertyReturnsCorrectValue() {

        AppProperties properties = new AppProperties();
        Boolean value = properties.hasProperty(PROP_KEY);
        Assert.assertNotNull(value);
        Assert.assertFalse(value);


        properties.setProperty(PROP_KEY, TEST_VAL);
        value = properties.hasProperty(PROP_KEY);
        Assert.assertNotNull(value);
        Assert.assertTrue(value);
    }

}
