package com.byteplay.android.renderclient.math;

import android.animation.FloatArrayEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntArrayEvaluator;
import android.animation.IntEvaluator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KeyframeSet<T> implements Cloneable {

    private static final TypeEvaluator DEFAULT_INT_EVALUATOR = new IntEvaluator();
    private static final TypeEvaluator DEFAULT_FLOAT_EVALUATOR = new FloatEvaluator();
    private static final TimeInterpolator DEFAULT_INTERPOLATOR = new LinearInterpolator();

    private static final Comparator<Keyframe> GL_KEY_FRAME_COMPARATOR = (o1, o2) -> {
        if (o1.fraction < o2.fraction) {
            return -1;
        } else if (o1.fraction > o2.fraction) {
            return 1;
        }
        return 0;
    };


    private final TypeEvaluator<T> typeEvaluator;
    private final Class<T> valueType;
    private final List<Keyframe<T>> keyframes = new ArrayList<>();
    long startTime;
    long duration = Long.MAX_VALUE;


    KeyframeSet(Class<T> valueType, TypeEvaluator<T> typeEvaluator) {
        this.valueType = valueType;
        this.typeEvaluator = typeEvaluator;
    }

    public Class<T> getValueType() {
        return valueType;
    }


    public void addKeyFrame(Keyframe<T>... keyframes) {
        if (keyframes == null) return;
        this.keyframes.addAll(Arrays.asList(keyframes));
    }

    public void removeKeyFrame(Keyframe<T>... keyframes) {
        if (keyframes == null) return;
        this.keyframes.removeAll(Arrays.asList(keyframes));
    }

    public void clearKeyFrame() {
        this.keyframes.clear();
    }


    @Override
    public KeyframeSet<T> clone() {
        List<Keyframe<T>> keyframes = this.keyframes;
        int numKeyframes = this.keyframes.size();
        final Keyframe[] newKeyframes = new Keyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; ++i) {
            newKeyframes[i] = keyframes.get(i).clone();
        }
        KeyframeSet newSet = new KeyframeSet(valueType, typeEvaluator);
        newSet.addKeyFrame(newKeyframes);
        newSet.setStartTime(startTime);
        newSet.setDuration(duration);
        return newSet;
    }


    public T getValueByTime(long currentTime, long currentDuration) {
        long frameDuration = Math.min(getDuration() - getStartTime(), currentDuration - getStartTime());
        float fraction = frameDuration == 0 ? 1 : (currentTime - getStartTime()) * 1.0f / frameDuration;
        return getValue(fraction);
    }


    public T getValue(float fraction) {
        int numKeyframes = keyframes.size();
        if (numKeyframes == 0) {
            throw new IllegalStateException("keyframe set size is empty");
        }
        if (fraction < 0 || fraction > 1 || Float.isNaN(fraction)) {
            return null;
        }
        Keyframe<T> preKeyFrame = null;
        Keyframe<T> nextKeyFrame = null;
        float keyFraction = 0;
        if (numKeyframes == 1) {
            preKeyFrame = keyframes.get(0);
            nextKeyFrame = preKeyFrame;
        } else {
            Collections.sort(keyframes, GL_KEY_FRAME_COMPARATOR);
            Keyframe firstFrame = keyframes.get(0);
            Keyframe lastFrame = keyframes.get(numKeyframes - 1);
            TimeInterpolator interpolator;
            if (fraction <= firstFrame.getFraction()) {
                preKeyFrame = firstFrame;
                nextKeyFrame = keyframes.get(1);
                interpolator = preKeyFrame.defaultInterpolator ? null : preKeyFrame.getInterpolator();
            } else if (fraction >= lastFrame.getFraction()) {
                preKeyFrame = keyframes.get(numKeyframes - 2);
                nextKeyFrame = lastFrame;
                interpolator = preKeyFrame.defaultInterpolator ? null : preKeyFrame.getInterpolator();
            } else {
                for (int i = 0; i < numKeyframes; ++i) {
                    Keyframe keyframe = keyframes.get(i);
                    if (fraction < keyframe.getFraction()) {
                        nextKeyFrame = keyframe;
                        break;
                    }
                    preKeyFrame = keyframe;
                }
                interpolator = preKeyFrame.getInterpolator() == null ? DEFAULT_INTERPOLATOR : preKeyFrame.getInterpolator();
            }
            float fractionLength = nextKeyFrame.getFraction() - preKeyFrame.getFraction();
            if (interpolator != null && fractionLength != 0) {
                keyFraction = interpolator.getInterpolation(
                        (fraction - preKeyFrame.getFraction())
                                / fractionLength);
            }
        }
        T value = preKeyFrame.getValue();
        if (typeEvaluator != null) {
            value = typeEvaluator.evaluate(keyFraction,
                    preKeyFrame.getValue(),
                    nextKeyFrame.getValue());
        }
        return value;
    }

    public TypeEvaluator<T> getTypeEvaluator() {
        return typeEvaluator;
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

    public static KeyframeSet ofInt(long duration, int... values) {
        int numKeyframes = values.length;
        Keyframe keyframes[] = new Keyframe[numKeyframes];
        keyframes[0] = Keyframe.ofInt(0f, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = Keyframe.ofInt((float) i / (numKeyframes - 1), values[i]);
        }
        return ofKeyframe(duration, DEFAULT_INT_EVALUATOR, keyframes);
    }

    public static KeyframeSet ofInt(int... values) {
        return ofInt(Long.MAX_VALUE, values);
    }


    public static KeyframeSet ofFloat(long duration, float... values) {
        int numKeyframes = values.length;
        Keyframe keyframes[] = new Keyframe[numKeyframes];
        keyframes[0] = Keyframe.ofFloat(0f, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = Keyframe.ofFloat((float) i / (numKeyframes - 1), values[i]);
        }
        return ofKeyframe(duration, DEFAULT_FLOAT_EVALUATOR, keyframes);
    }

    public static KeyframeSet ofFloat(float... values) {
        return ofFloat(Long.MAX_VALUE, values);
    }

    public static KeyframeSet ofIntArray(long duration, int[][] values) {
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
        Keyframe keyframes[] = new Keyframe[numKeyframes];
        keyframes[0] = Keyframe.ofIntArray(0, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = Keyframe.ofIntArray((float) i / (numKeyframes - 1), values[i]);
        }
        return ofKeyframe(duration, new IntArrayEvaluator(new int[numParameters]), keyframes);
    }

    public static KeyframeSet ofIntArray(int[][] values) {
        return ofIntArray(Long.MAX_VALUE, values);
    }

    public static KeyframeSet ofFloatArray(long duration, float[][] values) {
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
        Keyframe keyframes[] = new Keyframe[numKeyframes];
        keyframes[0] = Keyframe.ofFloatArray(0, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = Keyframe.ofFloatArray((float) i / (numKeyframes - 1), values[i]);
        }
        return ofKeyframe(duration, new FloatArrayEvaluator(new float[numParameters]), keyframes);
    }

    public static KeyframeSet ofFloatArray(float[][] values) {
        return ofFloatArray(Long.MAX_VALUE, values);
    }


    public static <T> KeyframeSet<T> ofKeyframe(long duration, TypeEvaluator<T> typeEvaluator, Keyframe<T>... frames) {
        KeyframeSet newSet = new KeyframeSet(frames[0].valueType, typeEvaluator);
        newSet.addKeyFrame(frames);
        newSet.setDuration(duration);
        return newSet;
    }
}
