package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLEffect extends GLObject {
    public static final long DURATION_MATCH_PARENT = -1;
    private long renderDuration;
    private long startTime;
    private long duration = GLEffect.DURATION_MATCH_PARENT;
    private long renderTime;
    private boolean renderEnable;

    public GLEffect(GLRenderClient client) {
        super(client);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onDispose() {

    }

    protected void calculateEffect(long timeMs, long parentDurationMs) {
        setRenderEnable(false);
        long renderDurationMs = getDuration() == DURATION_MATCH_PARENT ? parentDurationMs : Math.max(getDuration(), 0);
        long startTime = getStartTime();
        long renderTime = timeMs - startTime;
        setRenderDuration(renderDurationMs);
        if (renderTime > getRenderDuration() || renderTime < 0) {
            return;
        }
        setRenderEnable(true);
    }

    protected abstract GLFrameBuffer renderEffect(GLFrameBuffer input);

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


    protected void setRenderTime(long renderTime) {
        this.renderTime = renderTime;
    }

    public long getRenderTime() {
        return renderTime;
    }

    public boolean isRenderEnable() {
        return renderEnable;
    }

    protected void setRenderEnable(boolean renderEnable) {
        this.renderEnable = renderEnable;
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
