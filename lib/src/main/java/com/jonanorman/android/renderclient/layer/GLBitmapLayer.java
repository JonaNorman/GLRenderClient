package com.jonanorman.android.renderclient.layer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.gl20.GL20Texture;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class GLBitmapLayer extends GLTextureLayer {

    private GLTexture texture;


    public GLBitmapLayer() {
        super();
    }


    @Override
    protected void onRenderInit(GLRenderClient client) {
        super.onRenderInit(client);
        texture = new GL20Texture(getClient(), GLTexture.Type.TEXTURE_2D);
    }

    @Override
    protected void onRenderClean(GLRenderClient client) {
        super.onRenderClean(client);
        texture.dispose();
    }

    public void setBitmap(Bitmap bitmap) {
        postRender(new Runnable() {
            @Override
            public void run() {
                texture.updateBitmap(bitmap);
                setTexture(texture);
            }
        });
    }

    public void setBitmapResId(Context context, int drawResId) {
        postRender(new Runnable() {
            @Override
            public void run() {
                Resources resources = context.getResources();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeResource(resources, drawResId, options);
                texture.updateBitmap(bitmap);
                bitmap.recycle();
                setTexture(texture);
            }
        });
    }

    public void setBitmapUri(Context context, Uri uri) {
        postRender(new Runnable() {
            @Override
            public void run() {
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
                texture.updateBitmap(bitmap);
                bitmap.recycle();
                setTexture(texture);
            }
        });
    }
}
