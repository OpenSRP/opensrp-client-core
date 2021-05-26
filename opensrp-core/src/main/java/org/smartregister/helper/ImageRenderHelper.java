package org.smartregister.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.widget.ImageView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Photo;
import org.smartregister.util.ImageUtils;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class ImageRenderHelper {

    private Context context;

    public ImageRenderHelper(Context context) {
        this.context = context;
    }

    public void refreshProfileImage(String clientBaseEntityId, ImageView profileImageView, int defaultProfileImage) {

        Photo photo = ImageUtils.profilePhotoByClientID(clientBaseEntityId, defaultProfileImage);

        if (StringUtils.isNotBlank(photo.getFilePath())) {
            try {
                Bitmap myBitmap = BitmapFactory.decodeFile(photo.getFilePath());
                profileImageView.setImageBitmap(myBitmap);
            } catch (Exception e) {
                Timber.e(e);

                profileImageView.setImageDrawable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? context.getDrawable(defaultProfileImage) : ContextCompat.getDrawable(context, defaultProfileImage));

            }
        } else {
            int backgroundResource = photo.getResourceId();
            profileImageView.setImageDrawable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? context.getDrawable(backgroundResource) : ContextCompat.getDrawable(context, backgroundResource));


        }
        profileImageView.setTag(org.smartregister.R.id.entity_id, clientBaseEntityId);
        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(clientBaseEntityId, OpenSRPImageLoader.getStaticImageListener(profileImageView, 0, 0));

    }
}
