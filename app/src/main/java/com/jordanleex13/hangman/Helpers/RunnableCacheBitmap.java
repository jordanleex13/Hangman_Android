package com.jordanleex13.hangman.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * Runnable that caches a single bitmap inside the BitmapHelper LruCache
 */
public class RunnableCacheBitmap implements Runnable {

    private int data = 0;
    private String index;
    private Context mContext;

    public RunnableCacheBitmap(Context c, int d, String i) {
        mContext = c;
        data = d;
        index = i;

    }

    @Override
    public void run() {

        Bitmap src = BitmapFactory.decodeResource(mContext.getResources(), data);

        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);


                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                if (R > 50 || G > 50 || B > 50) {
                    R = 255;
                    G = 255;
                    B = 255;
                }
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }


                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        BitmapHelper.addBitmapToMemoryCache(index, bmOut);
        Log.e("RunnableCacheBitmap", "Caching image in index num : " + index);
    }
}
