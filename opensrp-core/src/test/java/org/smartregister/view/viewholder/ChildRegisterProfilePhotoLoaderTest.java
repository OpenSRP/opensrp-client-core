package org.smartregister.view.viewholder;

import android.graphics.drawable.Drawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.view.contract.ChildSmartRegisterClient;

/**
 * Created by ndegwamartin on 2020-04-14.
 */
public class ChildRegisterProfilePhotoLoaderTest extends BaseUnitTest {

    @Mock
    private ChildSmartRegisterClient smartRegisterClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetReturnsCorrectDrawableForFemaleGender() {


        Drawable maleInfant = RuntimeEnvironment.application.getDrawable(R.drawable.child_boy_infant);
        Drawable femaleInfant = RuntimeEnvironment.application.getDrawable(R.drawable.child_girl_infant);
        ChildRegisterProfilePhotoLoader childRegisterProfilePhotoLoader = new ChildRegisterProfilePhotoLoader(maleInfant, femaleInfant);

        Assert.assertNotNull(childRegisterProfilePhotoLoader);

        Mockito.doReturn("female").when(smartRegisterClient).gender();
        Assert.assertEquals(femaleInfant, childRegisterProfilePhotoLoader.get(smartRegisterClient));
    }

    @Test
    public void testGetReturnsCorrectDrawableForMaleGender() {


        Drawable maleInfant = RuntimeEnvironment.application.getDrawable(R.drawable.child_boy_infant);
        Drawable femaleInfant = RuntimeEnvironment.application.getDrawable(R.drawable.child_girl_infant);
        ChildRegisterProfilePhotoLoader childRegisterProfilePhotoLoader = new ChildRegisterProfilePhotoLoader(maleInfant, femaleInfant);

        Assert.assertNotNull(childRegisterProfilePhotoLoader);


        Mockito.doReturn("male").when(smartRegisterClient).gender();
        Assert.assertEquals(maleInfant, childRegisterProfilePhotoLoader.get(smartRegisterClient));
    }
}