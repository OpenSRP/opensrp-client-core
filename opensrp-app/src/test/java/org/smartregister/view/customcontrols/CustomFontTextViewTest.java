package org.smartregister.view.customcontrols;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

/**
 * Created by Vincent Karuri on 12/01/2021
 */
public class CustomFontTextViewTest extends BaseUnitTest {

    @Test
    public void testCustomFontTextViewCreationShouldCreateValidCustomFontTextView() {
        CustomFontTextView customFontTextView = new CustomFontTextView(RuntimeEnvironment.application);
        Assert.assertNotNull(customFontTextView);
        Assert.assertTrue(customFontTextView instanceof  CustomFontTextView);
    }
}