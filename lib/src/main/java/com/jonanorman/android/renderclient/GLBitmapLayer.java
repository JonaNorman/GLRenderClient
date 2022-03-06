package com.jonanorman.android.renderclient;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class GLBitmapLayer extends GLTextureLayer {

    private GLTexture ownTexture;
    private WeakReference<Bitmap> currentBitmap;
    private int currentResId;
    private Uri currentUri;


    protected GLBitmapLayer(GLRenderClient client) {
        super(client);
        ownTexture = client.newTexture(GLTextureType.TEXTURE_2D);
        super.setTexture(ownTexture);
    }

    @Override
    protected void onDispose() {
        super.onDispose();
        ownTexture.dispose();
    }


    public void setBitmap(Bitmap bitmap) {
        if (currentBitmap == null || currentBitmap.get() == bitmap) {
            return;
        }
        currentBitmap = new WeakReference<>(bitmap);
        currentResId = 0;
        currentUri = null;
        GLTexture texture = getTexture();
        texture.updateBitmap(bitmap);
    }

    public void setBitmapResId(Context context, int drawResId) {
        if (currentResId == drawResId) {
            return;
        }
        this.currentResId = drawResId;
        this.currentBitmap = null;
        this.currentUri = null;
        Resources resources = context.getResources();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawResId, options);
        GLTexture texture = getTexture();
        texture.updateBitmap(bitmap);
        bitmap.recycle();
    }

    public void setBitmapUri(Context context, Uri uri) {
        if (currentUri == uri) {
            return;
        }
        this.currentUri = uri;
        this.currentBitmap = null;
        this.currentResId = 0;
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        GLTexture texture = getTexture();
        texture.updateBitmap(bitmap);

    }
}
