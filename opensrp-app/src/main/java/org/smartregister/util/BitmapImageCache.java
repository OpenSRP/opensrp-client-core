package org.smartregister.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.collection.LruCache;

import com.android.volley.toolbox.ImageLoader;

import org.smartregister.AllConstants;

/**
 * This class holds our bitmap caches (memory).
 */
public class BitmapImageCache implements ImageLoader.ImageCache {

    private static final String TAG = "BitmapImageCache";

    private LruCache<String, Bitmap> mMemoryCache;

    /*
     * Do not invoke this constructor directly. Instead use AppController.getMemoryCacheInstance()
     */
    public BitmapImageCache(int memCacheSize) {
        init(memCacheSize);
    }

    /**
     * Sets the memory cache size based on a percentage of the max available VM memory. Eg.
     * setting percent to 0.2 would set the memory cache to one fifth of
     * the available memory. Throws {@link IllegalArgumentException} if percent is < 0.05 or >
     * .8. memCacheSize is stored in kilobytes instead of bytes as this
     * will eventually be passed to construct a LruCache which takes an int in its constructor.
     * <p>
     * This value should be chosen carefully based on a number of factors Refer to the
     * corresponding Android Training class for more discussion:
     * http://developer.android.com/training/displaying-bitmaps/
     *
     * @param percent Percent of memory class to use to size memory cache
     * @return Memory cache size in KB
     */
    public static int calculateMemCacheSize(float percent) {
        if (percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                    + "between 0.05 and 0.8 (inclusive)");
        }

        int calculatedCacacity = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);

        calculatedCacacity = calculatedCacacity > AllConstants.ImageCache.MEM_CACHE_MAX_SIZE
                ? AllConstants.ImageCache.MEM_CACHE_MAX_SIZE : calculatedCacacity;
        return calculatedCacacity;
    }

    /**
     * Get the size in bytes of a bitmap.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 12) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Initialize the cache.
     */
    private void init(int memCacheSize) {
        // Set up memory cache
        mMemoryCache = new LruCache<String, Bitmap>(memCacheSize) {
            /**
             * Measure item size in kilobytes rather than units which is more practical for a
             * bitmap cache
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                final int bitmapSize = getBitmapSize(bitmap) / 1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     *
     * @param data   Unique identifier for the bitmap to store
     * @param bitmap The bitmap to store
     */
    public void addBitmapToCache(String data, Bitmap bitmap) {
        if (data == null || bitmap == null) {
            return;
        }

        synchronized (mMemoryCache) {
            // Add to memory cache
            if (mMemoryCache.get(data) == null) {
                mMemoryCache.put(data, bitmap);
            }
        }
    }

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromMemCache(String data) {
        if (data != null) {
            synchronized (mMemoryCache) {
                final Bitmap memBitmap = mMemoryCache.get(data);
                if (memBitmap != null) {
                    return memBitmap;
                }
            }
        }
        return null;
    }

    /**
     * Clears the memory cache.
     */
    public void clearCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    @Override
    public Bitmap getBitmap(String key) {
        return getBitmapFromMemCache(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        addBitmapToCache(key, bitmap);
    }

}
