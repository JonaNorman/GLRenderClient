package com.jonanorman.android.renderclient.opengl;

import android.graphics.Color;

import java.util.LinkedList;

public abstract class GLFrameBufferCache extends GLDispose {

    private static final int MAX_FRAME_BUFFER_CACHE_SIZE = 10;

    private final LinkedList<GLFrameBuffer> frameBufferCache;
    private int maxCacheSize = MAX_FRAME_BUFFER_CACHE_SIZE;

    public GLFrameBufferCache(GLRenderClient client) {
        super(client);
        this.frameBufferCache = new LinkedList<>();
    }


    @Override
    protected void onDispose() {
        for (GLFrameBuffer frameBuffer : frameBufferCache) {
            frameBuffer.dispose();
        }
        frameBufferCache.clear();
    }


    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public GLFrameBuffer obtain(int currentWidth, int currentHeight) {
        if (currentWidth <= 0 || currentHeight <= 0) {
            throw new IllegalArgumentException(this + " obtain can not set zero");
        }
        checkDispose();
        GLFrameBuffer currentFrameBuffer = null;
        int cacheSize = frameBufferCache.size();
        for (int i = 0; i < cacheSize; i++) {
            GLFrameBuffer frameBuffer = frameBufferCache.get(i);
            if (frameBuffer != null
                    && frameBuffer.getWidth() == currentWidth
                    && frameBuffer.getHeight() == currentHeight) {
                frameBufferCache.remove(frameBuffer);
                currentFrameBuffer = frameBuffer;
                break;
            }
        }
        if (currentFrameBuffer == null) {
            currentFrameBuffer = frameBufferCache.poll();
        }
        if (currentFrameBuffer == null) {
            currentFrameBuffer = onCreateFrameBuffer(currentWidth, currentHeight);
        } else {
            if (currentFrameBuffer.getWidth() != currentWidth
                    || currentFrameBuffer.getHeight() != currentHeight) {
                currentFrameBuffer.setSize(currentWidth, currentHeight);
            }
            currentFrameBuffer.clearColor(Color.TRANSPARENT);
            currentFrameBuffer.clearDepthBuffer();
        }
        return currentFrameBuffer;
    }

    public void cache(GLFrameBuffer buffer) {
        if (buffer == null) return;
        checkDispose();
        frameBufferCache.remove(buffer);
        frameBufferCache.addFirst(buffer);
        while (frameBufferCache.size() > maxCacheSize) {
            GLFrameBuffer frameBuffer = frameBufferCache.pollLast();
            frameBuffer.dispose();
        }
    }

    protected abstract GLFrameBuffer onCreateFrameBuffer(int width, int height);
}
