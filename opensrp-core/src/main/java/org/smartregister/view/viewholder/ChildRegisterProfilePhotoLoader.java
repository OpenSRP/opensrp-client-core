package org.smartregister.view.viewholder;

import android.graphics.drawable.Drawable;

import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClient;

import static org.smartregister.AllConstants.FEMALE_GENDER;

public class ChildRegisterProfilePhotoLoader implements ProfilePhotoLoader {
    private final Drawable maleInfantDrawable;
    private final Drawable femaleInfantDrawable;

    public ChildRegisterProfilePhotoLoader(Drawable maleInfantDrawable, Drawable
            femaleInfantDrawable) {
        this.maleInfantDrawable = maleInfantDrawable;
        this.femaleInfantDrawable = femaleInfantDrawable;
    }

    public Drawable get(SmartRegisterClient client) {
        return FEMALE_GENDER.equalsIgnoreCase(((ChildSmartRegisterClient) client).gender())
                ? femaleInfantDrawable : maleInfantDrawable;
    }
}
