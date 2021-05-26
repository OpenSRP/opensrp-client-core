package org.smartregister.util.mock;

import android.graphics.Bitmap;

import org.smartregister.util.BitmapImageCache;

/**
 * Created by kaderchowdhury on 28/11/17.
 */

public class MockBitmapImageCache {
    public static BitmapImageCache getBitmapImageCache() {
        return new BitmapImageCache(1) {
            @Override
            public void addBitmapToCache(String data, Bitmap bitmap) {
                super.addBitmapToCache(data, bitmap);
            }

            @Override
            public Bitmap getBitmapFromMemCache(String data) {
                return super.getBitmapFromMemCache(data);
            }

            @Override
            public void clearCache() {
                super.clearCache();
            }

            @Override
            public Bitmap getBitmap(String key) {
                return super.getBitmap(key);
            }

            @Override
            public void putBitmap(String key, Bitmap bitmap) {
                super.putBitmap(key, bitmap);
            }
        };
    }
}
