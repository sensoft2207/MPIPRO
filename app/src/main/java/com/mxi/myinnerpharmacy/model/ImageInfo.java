package com.mxi.myinnerpharmacy.model;

import android.graphics.Bitmap;

/**
 * Created by aksahy on 15/6/17.
 */

public class ImageInfo {
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    Bitmap imageBitmap;
    String imageUrl;
}
