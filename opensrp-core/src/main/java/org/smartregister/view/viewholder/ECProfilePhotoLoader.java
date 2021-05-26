package org.smartregister.view.viewholder;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.smartregister.view.contract.SmartRegisterClient;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;

public class ECProfilePhotoLoader implements ProfilePhotoLoader {
    private final Resources resources;
    private final Drawable defaultPlaceHolder;
    private final Map<String, Drawable> drawableMap = new HashMap<String, Drawable>();

    public ECProfilePhotoLoader(Resources res, Drawable placeHolder) {
        this.resources = res;
        defaultPlaceHolder = placeHolder;
    }

    public Drawable get(SmartRegisterClient client) {
        if (drawableMap.containsKey(client.entityId())) {
            return drawableMap.get(client.entityId());
        }

        String photoPath = client.profilePhotoPath();
        if (isBlank(photoPath) || isThisDefaultProfilePhoto(photoPath) || !isFileExists(
                photoPath)) {
            return defaultPlaceHolder;
        }

        Drawable profilePhoto = new BitmapDrawable(resources, photoPath.replace("file:///", "/"));
        drawableMap.put(client.entityId(), profilePhoto);
        return profilePhoto;
    }

    private boolean isFileExists(String path) {
        return new File(path).exists();
    }

    private boolean isThisDefaultProfilePhoto(String photoPath) {
        return photoPath.contains(DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH);
    }
}
