package org.smartregister.util;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;

/**
 * Created by ndegwamartin on 12/04/2018.
 */

public class ImageUtils {

    public static Photo profilePhotoByClientID(String clientEntityId, int defaultProfileImage) {
        Photo photo = new Photo();
        ProfileImage profileImage = CoreLibrary.getInstance().context().imageRepository().findByEntityId(clientEntityId);
        if (profileImage != null) {
            photo.setFilePath(profileImage.getFilepath());
        } else {
            photo.setResourceId(defaultProfileImage);
        }
        return photo;
    }
}
