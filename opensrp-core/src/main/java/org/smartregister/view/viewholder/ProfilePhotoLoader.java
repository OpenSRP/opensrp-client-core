package org.smartregister.view.viewholder;

import android.graphics.drawable.Drawable;

import org.smartregister.view.contract.SmartRegisterClient;

public interface ProfilePhotoLoader {
    Drawable get(SmartRegisterClient client);
}
