package org.smartregister.shadows;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowResourcesImpl;

import static android.os.Build.VERSION_CODES.O_MR1;

@Implements(className = "android.content.res.ResourcesImpl", isInAndroidSdk = false, minSdk = O_MR1)
public class ShadowDrawableResourcesImpl extends ShadowResourcesImpl {
/*
    @Implementation
    @Override
    public Drawable loadDrawable(Resources wrapper, TypedValue value, int id, Resources.Theme theme, boolean useCache) throws Resources.NotFoundException {
        try {
            return super.loadDrawable(wrapper, value, id, theme, useCache);
        } catch (Exception e) {
            return new VectorDrawable();
        }
    }

    @Implementation
    @Override
    public Drawable loadDrawable(Resources wrapper, TypedValue value, int id, int density, Resources.Theme theme) {
        try {
            return super.loadDrawable(wrapper, value, id, density, theme);
        } catch (Exception e) {
            return new VectorDrawable();
        }
    }

 */
}