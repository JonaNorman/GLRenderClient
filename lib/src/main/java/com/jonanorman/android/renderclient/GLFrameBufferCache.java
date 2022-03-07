package com.jonanorman.android.renderclient;

import android.graphics.Color;

import java.util.LinkedList;
import java.util.Queue;

public class GLFrameBufferCache extends GLObject {

    private static final int MAX_FRAME_BUFFER_CACHE_SIZE = 5;

    private final Queue<GLFrameBuffer> frameBufferCache = new LinkedList<>();

    private int maxFrameBufferCacheSize = MAX_FRAME_BUFFER_CACHE_SIZE;

    public GLFrameBufferCache(GLRenderClient client) {
        super(client);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onDispose() {
        for (GLFrameBuffer frameBuffer : frameBufferCache) {
            frameBuffer.dispose();
        }
        frameBufferCache.clear();
    }

    public void setMaxCacheSize(int maxFrameBufferCacheSize) {
        this.maxFrameBufferCacheSize = maxFrameBufferCacheSize;
    }

    public GLFrameBuffer obtain(int currentWidth, int currentHeight) {
        if (currentWidth <= 0 || currentHeight <= 0) {
            throw new IllegalArgumentException("frameBuffer size can not set zero");
        }
        if (isDisposed()) {
            throw new IllegalStateException("it is disposed");
        }
        create();
        GLFrameBuffer currentFrameBuffer = null;
        int cacheSize = frameBufferCache.size();
        for (int i = 0; i < cacheSize; i++) {
            GLFrameBuffer frameBuffer = frameBufferCache.element();
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
            currentFrameBuffer = client.newFrameBuffer(currentWidth, currentHeight);
        } else {
            if (currentFrameBuffer.getWidth() != currentWidth || currentFrameBuffer.getHeight() != currentHeight) {
                currentFrameBuffer.setSize(currentWidth, currentHeight);
            }
            currentFrameBuffer.clearColor(Color.TRANSPARENT);
        }
        return currentFrameBuffer;
    }

    public void cache(GLFrameBuffer buffer) {
        if (isDisposed()) {
            throw new IllegalStateException("it is disposed");
        }
        create();
        if (buffer == null) return;
        frameBufferCache.offer(buffer);
        while (frameBufferCache.size() > maxFrameBufferCacheSize) {
            GLFrameBuffer frameBuffer = frameBufferCache.poll();
            frameBuffer.dispose();
        }
    }
}
