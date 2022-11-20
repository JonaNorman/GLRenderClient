package com.jonanorman.android.renderclient.opengl.gl20;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLFrameBufferCache;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

public final class GL20FrameBufferCache extends GLFrameBufferCache {

    private static final String KEY_FRAME_BUFFER_CACHE_GL20 = "key_frame_buffer_cache_gl20";

    public GL20FrameBufferCache(GLRenderClient client) {
        super(client);
    }

    @Override
    protected GLFrameBuffer onCreateFrameBuffer(int width, int height) {
        return new GL20FrameBuffer(getClient(), width, height);
    }


    public static GLFrameBufferCache getCache(GLRenderClient renderClient) {
        GLFrameBufferCache frameBufferCache = renderClient.getExtraParam(KEY_FRAME_BUFFER_CACHE_GL20);
        if (frameBufferCache != null) {
            return frameBufferCache;
        }
        frameBufferCache = new GL20FrameBufferCache(renderClient);
        renderClient.putExtraParam(KEY_FRAME_BUFFER_CACHE_GL20, frameBufferCache);
        return frameBufferCache;
    }

    public static GLFrameBufferCache getCurrentCache() {
        return getCache(GLRenderClient.getCurrentClient());
    }

    @NonNull
    @Override
    public String toString() {
        return "GL20FrameBufferCache@" + hashCode();
    }
}
