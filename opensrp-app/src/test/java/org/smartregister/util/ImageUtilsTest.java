package org.smartregister.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;

/**
 * Created by Vincent Karuri on 01/12/2020
 */
public class ImageUtilsTest extends BaseUnitTest {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mockMethods();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "context", null);
    }

    @Test
    public void testProfilePhotoByClientIDShouldSetCorrectPhotoProperties() {
        Photo photo = ImageUtils.profilePhotoByClientID("entity_id", 1);
        Assert.assertEquals(photo.getFilePath(), "file_path");

        photo = ImageUtils.profilePhotoByClientID(null, 1);
        Assert.assertEquals(photo.getResourceId(), 1);
    }

    private void mockMethods() {
        ProfileImage profileImage = new ProfileImage();
        profileImage.setFilepath("file_path");
        Mockito.doReturn(profileImage).when(imageRepository).findByEntityId(ArgumentMatchers.anyString());
        Mockito.doReturn(imageRepository).when(context).imageRepository();
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "context", context);
    }
}