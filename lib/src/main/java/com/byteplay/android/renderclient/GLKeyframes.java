package com.byteplay.android.renderclient;

import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GLKeyframes implements Cloneable {

    private static final Comparator<GLKeyframe> GL_KEY_FRAME_COMPARATOR = (o1, o2) -> {
        if (o1.fraction < o2.fraction) {
            return -1;
        } else if (o1.fraction > o2.fraction) {
            return 1;
        }
        return 0;
    };


    final TypeEvaluator<float[]> evaluator = new TypeEvaluator<float[]>() {
        float[] values;

        public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
            if (startValue == null) {
                throw new NullPointerException("start value is null");
            }
            if (endValue == null) {
                throw new NullPointerException("end value is null");
            }
            if (startValue.length != endValue.length) {
                throw new NullPointerException("start and end lengths are not equal");
            }
            if (values == null || values.length != startValue.length) {
                values = new float[startValue.length];
            }
            for (int i = 0; i < values.length; i++) {
                float start = startValue[i];
                float end = endValue[i];
                values[i] = start + (fraction * (end - start));
            }
            return values;
        }
    };

    List<GLKeyframe> keyframes = new ArrayList<>();
    long startTime;
    long duration = Long.MAX_VALUE;

    public GLKeyframes() {
    }

    public List<GLKeyframe> getKeyframes() {
        return keyframes;
    }


    public void addKeyFrame(GLKeyframe... keyframes) {
        if (keyframes == null) return;
        this.keyframes.addAll(Arrays.asList(keyframes));
    }

    public void removeKeyFrame(GLKeyframe... keyframes) {
        if (keyframes == null) return;
        this.keyframes.removeAll(Arrays.asList(keyframes));
    }

    public void clearKeyFrame() {
        this.keyframes.clear();
    }


    @Override
    public GLKeyframes clone() {
        List<GLKeyframe> keyframes = this.keyframes;
        int numKeyframes = this.keyframes.size();
        final GLKeyframe[] newKeyframes = new GLKeyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; ++i) {
            newKeyframes[i] = keyframes.get(i).clone();
        }
        GLKeyframes newSet = new GLKeyframes();
        newSet.addKeyFrame(newKeyframes);
        newSet.setStartTime(startTime);
        newSet.setDuration(duration);
        return newSet;
    }


    public float[] getValue(float fraction) {
        int numKeyframes = keyframes.size();
        if (numKeyframes == 0) {
            return new float[0];
        }
        if (numKeyframes == 1) {
            return keyframes.get(0).getValue();
        }
        Collections.sort(keyframes, GL_KEY_FRAME_COMPARATOR);
        GLKeyframe firstFrame = keyframes.get(0);
        GLKeyframe lastFrame = keyframes.get(numKeyframes - 1);
        GLKeyframe preKeyFrame = null;
        GLKeyframe nextKeyFrame = null;

        if (fraction <= firstFrame.getFraction()) {
            preKeyFrame = firstFrame;
            nextKeyFrame = keyframes.get(1);
        } else if (fraction >= lastFrame.getFraction()) {
            preKeyFrame = keyframes.get(numKeyframes - 2);
            nextKeyFrame = lastFrame;
        } else {
            for (int i = 0; i < numKeyframes; ++i) {
                GLKeyframe keyframe = keyframes.get(i);
                if (fraction < keyframe.getFraction()) {
                    nextKeyFrame = keyframe;
                    break;
                }
                preKeyFrame = keyframe;
            }
        }
        if (nextKeyFrame.getFraction() == preKeyFrame.getFraction()) {
            return preKeyFrame.getValue();
        }
        float intervalFraction = 0.0f;
        TimeInterpolator interpolator = preKeyFrame.getInterpolator();
        if (interpolator != null) {
            intervalFraction = interpolator.getInterpolation(
                    (fraction - preKeyFrame.getFraction()) /
                            (nextKeyFrame.getFraction() - preKeyFrame.getFraction())
            );
        }
        return evaluator.evaluate(intervalFraction,
                preKeyFrame.getValue(),
                nextKeyFrame.getValue());

    }

    public static GLKeyframes ofInt(long duration, int... values) {
        int numKeyframes = values.length;
        GLKeyframe keyframes[] = new GLKeyframe[numKeyframes];
        keyframes[0] = GLKeyframe.ofInt(0f, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = GLKeyframe.ofInt((float) i / (numKeyframes - 1), values[i]);
        }
        GLKeyframes newSet = new GLKeyframes();
        newSet.addKeyFrame(keyframes);
        newSet.setDuration(duration);
        return newSet;
    }

    public static GLKeyframes ofInt(int... values) {
        return ofInt(Long.MAX_VALUE, values);
    }


    public static GLKeyframes ofFloat(long duration, float... values) {
        int numKeyframes = values.length;
        GLKeyframe keyframes[] = new GLKeyframe[numKeyframes];
        keyframes[0] = GLKeyframe.ofFloat(0f, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = GLKeyframe.ofFloat((float) i / (numKeyframes - 1), values[i]);
        }
        GLKeyframes newSet = new GLKeyframes();
        newSet.addKeyFrame(keyframes);
        newSet.setDuration(duration);
        return newSet;
    }

    public static GLKeyframes ofFloat(float... values) {
        return ofFloat(Long.MAX_VALUE, values);
    }

    public static GLKeyframes ofIntArray(long duration, int[][] values) {
        if (values.length < 2) {
            throw new IllegalArgumentException("At least 2 values must be supplied");
        }
        int numParameters = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                throw new IllegalArgumentException("values must not be null");
            }
            int length = values[i].length;
            if (i == 0) {
                numParameters = length;
            } else if (length != numParameters) {
                throw new IllegalArgumentException("Values must all have the same length");
            }
        }
        int numKeyframes = values.length;
        GLKeyframe keyframes[] = new GLKeyframe[numKeyframes];
        keyframes[0] = GLKeyframe.ofIntArray(0, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = GLKeyframe.ofIntArray((float) i / (numKeyframes - 1), values[i]);
        }
        GLKeyframes newSet = new GLKeyframes();
        newSet.addKeyFrame(keyframes);
        newSet.setDuration(duration);
        return newSet;
    }

    public static GLKeyframes ofIntArray(int[][] values) {
        return ofIntArray(Long.MAX_VALUE, values);
    }

    public static GLKeyframes ofFloatArray(long duration, float[][] values) {
        int numKeyframes = values.length;
        if (numKeyframes < 2) {
            throw new IllegalArgumentException("At least 2 values must be supplied");
        }
        int numParameters = 0;
        for (int i = 0; i < numKeyframes; i++) {
            if (values[i] == null) {
                throw new IllegalArgumentException("values must not be null");
            }
            int length = values[i].length;
            if (i == 0) {
                numParameters = length;
            } else if (length != numParameters) {
                throw new IllegalArgumentException("Values must all have the same length");
            }
        }
        GLKeyframe keyframes[] = new GLKeyframe[numKeyframes];
        keyframes[0] = GLKeyframe.ofFloatArray(0, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = GLKeyframe.ofFloatArray((float) i / (numKeyframes - 1), values[i]);
        }
        GLKeyframes newSet = new GLKeyframes();
        newSet.addKeyFrame(keyframes);
        newSet.setDuration(duration);
        return newSet;
    }

    public static GLKeyframes ofFloatArray(float[][] values) {
        return ofFloatArray(Long.MAX_VALUE, values);
    }


    public static GLKeyframes ofKeyframe(GLKeyframe... frames) {
        GLKeyframes newSet = new GLKeyframes();
        newSet.addKeyFrame(frames);
        return newSet;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }
}
