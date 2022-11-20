package com.jonanorman.android.renderclient.opengl.gl20;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.GLRenderClient;
import com.jonanorman.android.renderclient.opengl.GLTexture;
import com.jonanorman.android.renderclient.opengl.GLTextureCache;

public final class GL20TextureCache extends GLTextureCache {

    private static final String KEY_TEXTURE_CACHE_GL20 = "key_texture_cache_gl20";

    public GL20TextureCache(GLRenderClient client) {
        super(client);
    }

    @Override
    protected GLTexture onCreateTexture(int width, int height) {
        GLTexture texture = new GL20Texture(getClient(), GLTexture.Type.TEXTURE_2D);
        texture.setSize(width, height);
        return texture;
    }


    public static GLTextureCache getCache(GLRenderClient renderClient) {
        GLTextureCache textureCache = renderClient.getExtraParam(KEY_TEXTURE_CACHE_GL20);
        if (textureCache != null) {
            return textureCache;
        }
        textureCache = new GL20TextureCache(renderClient);
        renderClient.putExtraParam(KEY_TEXTURE_CACHE_GL20, textureCache);
        return textureCache;
    }

    public static GLTextureCache getCurrentCache() {
        return getCache(GLRenderClient.getCurrentClient());
    }

    @NonNull
    @Override
    public String toString() {
        return "GL20TextureCache@" + hashCode();
    }
}
