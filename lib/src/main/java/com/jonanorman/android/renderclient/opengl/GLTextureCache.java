package com.jonanorman.android.renderclient.opengl;

import android.graphics.Color;

import java.util.LinkedList;

public abstract class GLTextureCache extends GLDispose {

    private static final int MAX_TEXTURE_CACHE_SIZE = 10;

    private final LinkedList<GLTexture> textureCache;
    private int maxCacheSize = MAX_TEXTURE_CACHE_SIZE;

    public GLTextureCache(GLRenderClient client) {
        super(client);
        this.textureCache = new LinkedList<>();
    }


    @Override
    protected void onDispose() {
        for (GLTexture texture : textureCache) {
            texture.dispose();
        }
        textureCache.clear();
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public GLTexture obtain(int currentWidth, int currentHeight) {
        if (currentWidth <= 0 || currentHeight <= 0) {
            throw new IllegalArgumentException(this + " obtain can not set zero");
        }
        checkDispose();
        GLTexture currentTexture = null;
        int cacheSize = textureCache.size();
        for (int i = 0; i < cacheSize; i++) {
            GLTexture texture = textureCache.get(i);
            if (texture != null
                    && texture.getWidth() == currentWidth
                    && texture.getHeight() == currentHeight) {
                textureCache.remove(texture);
                currentTexture = texture;
                break;
            }
        }
        if (currentTexture == null) {
            currentTexture = textureCache.poll();
        }
        if (currentTexture == null) {
            currentTexture = onCreateTexture(currentWidth, currentHeight);
        } else {
            if (currentTexture.getWidth() != currentWidth ||
                    currentTexture.getHeight() != currentHeight) {
                currentTexture.setSize(currentWidth, currentHeight);
            }
            currentTexture.drawColor(Color.TRANSPARENT);
        }
        return currentTexture;
    }

    public void cache(GLTexture texture) {
        if (texture == null) return;
        checkDispose();
        textureCache.remove(texture);
        textureCache.addFirst(texture);
        while (textureCache.size() > maxCacheSize) {
            GLTexture glTexture = textureCache.pollLast();
            glTexture.dispose();
        }
    }

    protected abstract GLTexture onCreateTexture(int width, int height);
}
