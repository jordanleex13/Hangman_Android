package com.jordanleex13.hangman.Helpers;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Fully static class to control access to the hangman images
 */
public class BitmapHelper {

    public static final String TAG = BitmapHelper.class.getSimpleName();

    public static final int NUM_OF_HANGMAN_STAGES = 9;

    // The cache of all the images
    private static LruCache<String, Bitmap> mMemoryCache = createCache();

    private static LruCache<String, Bitmap> createCache() {
        // Setup the Bitmap cache stuff
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;

        LruCache<String, Bitmap> memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        return memoryCache;
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }



}
