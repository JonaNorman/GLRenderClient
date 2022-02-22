package com.byteplay.android.renderclient;

public abstract class GLEffect extends GLObject {
    public static final long DURATION_MATCH_PARENT = -1;
    private long renderDuration = GLEffect.DURATION_MATCH_PARENT;
    private long startTime;
    private long duration = GLEffect.DURATION_MATCH_PARENT;

    public GLEffect(GLRenderClient client) {
        super(client);
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
}
