package org.smartregister.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.ImageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Karuri on 27/04/2021
 */
public class ImageUploadSyncServiceTest extends BaseUnitTest {

    @Test
    public void testOnHandleIntentShouldUploadImages() throws Exception {
        // mock dependencies
        Context opensrpContext = CoreLibrary.getInstance().context();

        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);
        Mockito.doReturn(ResponseStatus.success.displayValue()).when(httpAgent).httpImagePost(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(ProfileImage.class));
        ReflectionHelpers.setField(opensrpContext, "httpAgent", httpAgent);

        final String IMAGE_ID_1 = "image_id_1";
        final String IMAGE_ID_2 = "image_id_2";
        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        ReflectionHelpers.setField(opensrpContext, "imageRepository", imageRepository);

        List<ProfileImage> profileImages = new ArrayList<ProfileImage>() {{
            add(new ProfileImage(IMAGE_ID_1));
            add(new ProfileImage(IMAGE_ID_2));
        }};
        Mockito.doReturn(profileImages).when(imageRepository).findAllUnSynced();

        // verify uploads
        ImageUploadSyncService imageUploadSyncService = new ImageUploadSyncService();
        imageUploadSyncService.onHandleIntent(null);

        String imageUploadEndpoint = Whitebox.invokeMethod(imageUploadSyncService, "getImageUploadEndpoint");
        Mockito.verify(httpAgent).httpImagePost(imageUploadEndpoint, profileImages.get(0));
        Mockito.verify(httpAgent).httpImagePost(imageUploadEndpoint, profileImages.get(1));
        Mockito.verify(imageRepository).close(IMAGE_ID_1);
        Mockito.verify(imageRepository).close(IMAGE_ID_2);
    }
}