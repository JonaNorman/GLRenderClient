package com.jonanorman.android.renderclient.layer;

import androidx.annotation.NonNull;

import com.jonanorman.android.renderclient.math.TimeStamp;
import com.jonanorman.android.renderclient.opengl.GLFrameBuffer;
import com.jonanorman.android.renderclient.opengl.GLRenderClient;

import java.util.LinkedList;
import java.util.Queue;

public abstract class GLEffect {

    private TimeStamp startTime;
    private TimeStamp duration;
    private TimeStamp renderTime;
    private TimeStamp renderDuration;
    private boolean enable;
    private GLRenderClient lastRenderClient;
    private Queue<Runnable> postQueue;


    public GLEffect() {
        this.startTime = TimeStamp.MIN_VALUE;
        this.duration = TimeStamp.MATCH_PARENT_VALUE;
        this.renderTime = TimeStamp.ofNanos(0);
        this.renderDuration = TimeStamp.ofNanos(0);
        this.enable = true;
        this.postQueue = new LinkedList<>();
    }


    public final GLFrameBuffer render(GLFrameBuffer inputBuffer, @NonNull TimeStamp currentTime) {
        return render(inputBuffer, currentTime, TimeStamp.MATCH_PARENT_VALUE);
    }

    final GLFrameBuffer render(GLFrameBuffer inputBuffer,
                               @NonNull TimeStamp currentTime,
                               @NonNull TimeStamp parentDuration) {
        if (!enable) {
            return inputBuffer;
        }
        long renderTimeNs = currentTime.toNanos() - startTime.toNanos();
        long renderDurationNs;
        if (duration != TimeStamp.MATCH_PARENT_VALUE && parentDuration != TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = Math.min(duration.toNanos(), parentDuration.toNanos() - startTime.toNanos());
        } else if (duration != TimeStamp.MATCH_PARENT_VALUE && parentDuration == TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = duration.toNanos();
        } else if (duration == TimeStamp.MATCH_PARENT_VALUE && parentDuration != TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = parentDuration.toNanos() - startTime.toNanos();
        } else {
            throw new IllegalStateException("must set duration");
        }
        if (renderTimeNs > renderDurationNs || renderTimeNs < 0) {
            return inputBuffer;
        }
        renderTime.setDuration(renderTimeNs);
        renderDuration.setDuration(renderDurationNs);

        GLRenderClient currentClient = GLRenderClient.getCurrentClient();
        if (currentClient == null) {
            throw new IllegalStateException(this + " must render in attached thread");
        }
        if (lastRenderClient != currentClient) {
            if (lastRenderClient != null) {
                postQueue.clear();
            }
            lastRenderClient = currentClient;
            onRenderInit(currentClient);
        }
        while (!postQueue.isEmpty()) {
            Runnable runnable = postQueue.poll();
            runnable.run();
        }
        return onRenderEffect(currentClient, inputBuffer);
    }

    public void clean() {
        clean(false);
    }

    public void clean(boolean safe) {
        if (lastRenderClient == null) {
            return;
        }
        if (lastRenderClient.isRelease()) {
            postQueue.clear();
            lastRenderClient = null;
            return;
        }
        GLRenderClient currentClient = GLRenderClient.getCurrentClient();
        if (lastRenderClient != currentClient) {
            throw new IllegalStateException(this + " clean must in " + lastRenderClient.getAttachThread());
        }
        if (safe) {
            while (!postQueue.isEmpty()) {
                Runnable runnable = postQueue.poll();
                runnable.run();
            }
        } else {
            postQueue.clear();
        }
        onRenderClean(lastRenderClient);
        lastRenderClient = null;
    }

    public void postRender(Runnable runnable) {
        postQueue.offer(runnable);
    }


    public GLRenderClient getClient() {
        return lastRenderClient;
    }

    public TimeStamp getRenderDuration() {
        return renderDuration;
    }


    public TimeStamp getRenderTime() {
        return renderTime;
    }

    protected abstract void onRenderInit(GLRenderClient client);

    protected abstract void onRenderClean(GLRenderClient client);

    protected abstract GLFrameBuffer onRenderEffect(GLRenderClient client, GLFrameBuffer inputBuffer);

    public TimeStamp getStartTime() {
        return startTime;
    }


    public void setStartTime(@NonNull TimeStamp startTime) {
        this.startTime = startTime;
    }

    public TimeStamp getDuration() {
        return duration;
    }

    public void setDuration(@NonNull TimeStamp duration) {
        this.duration = duration;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }
}
