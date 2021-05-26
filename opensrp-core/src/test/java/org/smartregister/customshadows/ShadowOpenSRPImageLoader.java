package org.smartregister.customshadows;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.util.OpenSRPImageLoader;

import java.io.File;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 03-03-2020.
 */
@Implements(OpenSRPImageLoader.class)
public class ShadowOpenSRPImageLoader {

    @Implementation
    public static boolean copyFile(File src, File dst) {
        return true;
    }
}
