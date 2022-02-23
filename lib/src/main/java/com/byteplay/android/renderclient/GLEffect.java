package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLEffect {
    public static final long DURATION_MATCH_PARENT = -1;
    private long renderDuration = GLEffect.DURATION_MATCH_PARENT;
    private long startTime;
    private long duration = GLEffect.DURATION_MATCH_PARENT;
    protected GLRenderClient client;

    public GLEffect(GLRenderClient client) {
        this.client = client;
    }


    protected final GLFrameBuffer apply(GLFrameBuffer input, long timeMs) {
        return client.applyEffect(GLEffect.this, input, timeMs);
    }

    protected abstract GLFrameBuffer actualApplyEffect(GLEffect effect, GLFrameBuffer input, long timeMs);

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    protected void setRenderDuration(long renderDuration) {
        this.renderDuration = renderDuration;
    }

    public long getRenderDuration() {
        return renderDuration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GLEffect)) return false;
        GLEffect glEffect = (GLEffect) o;
        return renderDuration == glEffect.renderDuration && startTime == glEffect.startTime && duration == glEffect.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(renderDuration, startTime, duration);
    }
}
