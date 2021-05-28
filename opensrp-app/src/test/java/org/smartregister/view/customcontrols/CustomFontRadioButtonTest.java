package org.smartregister.view.customcontrols;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

/**
 * Created by Vincent Karuri on 12/01/2021
 */
public class CustomFontRadioButtonTest extends BaseUnitTest {

    @Test
    public void testCustomFontRadioButtonCreationShouldReturnAValidCustomFontRadioButton() {
        CustomFontRadioButton customFontRadioButton = new CustomFontRadioButton(RuntimeEnvironment.application);
        Assert.assertNotNull(customFontRadioButton);
        Assert.assertTrue(customFontRadioButton instanceof  CustomFontRadioButton);
    }
}