/**
 * 
 */
package com.morpho.dao;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface AuthBfdCap {
	
	void updateImageView(final ImageView imgPreview, final Bitmap previewBitmap, String message, final boolean flagComplete, int captureError);
    void updateImageView(final byte[] templateData, final ImageView imgPreview, final Bitmap previewBitmap, String message, final boolean flagComplete, int captureError);
}
