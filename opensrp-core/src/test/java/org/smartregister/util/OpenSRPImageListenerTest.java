package org.smartregister.util;

import android.widget.ImageView;
import android.widget.RemoteViews;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by Vincent Karuri on 15/09/2020
 */
public class OpenSRPImageListenerTest {

    @Mock
    private ImageView imageView;
    @Mock
    private RemoteViews remoteViews;

    private String entityId = "entity_id";
    private int defaultImageResId = 0;
    private int errorImageResId = 1;

    private OpenSRPImageListener openSRPImageListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        openSRPImageListener = new OpenSRPImageListener(imageView, entityId, defaultImageResId, errorImageResId);
    }

    @Test
    public void testInitializationShouldCorrectlyPopulateAllFields() {
        String absoluteFilePath = "file_path";
        openSRPImageListener.setAbsoluteFileName(absoluteFilePath);
        openSRPImageListener.setHasImageTag(true);
        Assert.assertEquals(defaultImageResId, openSRPImageListener.getDefaultImageResId());
        Assert.assertEquals(errorImageResId, openSRPImageListener.getErrorImageResId());
        Assert.assertEquals(entityId, openSRPImageListener.getEntityId());
        Assert.assertEquals(absoluteFilePath, openSRPImageListener.getAbsoluteFileName());
        Assert.assertEquals(true, openSRPImageListener.getHasImageViewTag());

        openSRPImageListener = new OpenSRPImageListener(remoteViews, defaultImageResId, defaultImageResId, errorImageResId);
        Assert.assertEquals(defaultImageResId, openSRPImageListener.getImageViewId());
        Assert.assertEquals(remoteViews, openSRPImageListener.getRemoteView());
        Assert.assertEquals(defaultImageResId, openSRPImageListener.getDefaultImageResId());
        Assert.assertEquals(errorImageResId, openSRPImageListener.getErrorImageResId());
    }
}