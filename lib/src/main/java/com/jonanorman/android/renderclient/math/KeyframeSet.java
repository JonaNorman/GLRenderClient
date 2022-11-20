package com.jonanorman.android.renderclient.math;

import android.animation.FloatArrayEvaluator;
import android.animation.FloatEvaluator;
import android.animation.IntArrayEvaluator;
import android.animation.IntEvaluator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;

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


    private TypeEvaluator<T> typeEvaluator;
    private final Class<T> valueType;
    private final List<Keyframe<T>> keyframes = new ArrayList<>();
    private TimeStamp startTime;
    private TimeStamp duration;


    public KeyframeSet(Class<T> valueType, TypeEvaluator<T> typeEvaluator) {
        this.valueType = valueType;
        this.typeEvaluator = typeEvaluator;
        this.startTime = TimeStamp.MIN_VALUE;
        this.duration = TimeStamp.MATCH_PARENT_VALUE;
    }

    public Class<T> getValueType() {
        return valueType;
    }


    public void add(Keyframe<T>... keyframes) {
        if (keyframes == null) return;
        this.keyframes.addAll(Arrays.asList(keyframes));
    }

    public void remove(Keyframe<T>... keyframes) {
        if (keyframes == null) return;
        this.keyframes.removeAll(Arrays.asList(keyframes));
    }

    public void setTypeEvaluator(TypeEvaluator<T> typeEvaluator) {
        this.typeEvaluator = typeEvaluator;
    }

    public void clear() {
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
        newSet.add(newKeyframes);
        newSet.setStartTime(startTime);
        newSet.setDuration(duration);
        return newSet;
    }


    public boolean getValue(@NonNull TimeStamp currentTime, @NonNull TimeStamp parentDuration, Keyframe<T> result) {
        long renderTimeNs = currentTime.toNanos() - startTime.toNanos();
        long renderDurationNs;
        if (duration != TimeStamp.MATCH_PARENT_VALUE && parentDuration != TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = Math.min(duration.toNanos(), parentDuration.toNanos() - startTime.toNanos());
        } else if (duration != TimeStamp.MATCH_PARENT_VALUE && parentDuration == TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = duration.toNanos();
        } else if (duration == TimeStamp.MATCH_PARENT_VALUE && parentDuration != TimeStamp.MATCH_PARENT_VALUE) {
            renderDurationNs = parentDuration.toNanos() - startTime.toNanos();
        } else {
            renderDurationNs = 0;
        }
        float fraction = renderDurationNs == 0 ? 1 : MathUtils.clamp(renderTimeNs * 1.0f / renderDurationNs, 0, 1);
        return getValue(fraction, result);
    }


    public boolean getValue(float fraction, Keyframe<T> result) {
        int numKeyframes = keyframes.size();
        if (numKeyframes == 0) {
            throw new IllegalStateException("keyframe set size is empty");
        }
        if (fraction < 0 || fraction > 1 || Float.isNaN(fraction)) {
            return false;
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
                interpolator = preKeyFrame.getInterpolator();
            } else if (fraction >= lastFrame.getFraction()) {
                preKeyFrame = keyframes.get(numKeyframes - 2);
                nextKeyFrame = lastFrame;
                interpolator = lastFrame.getInterpolator();
            } else {
                for (int i = 0; i < numKeyframes; ++i) {
                    Keyframe keyframe = keyframes.get(i);
                    if (fraction < keyframe.getFraction()) {
                        nextKeyFrame = keyframe;
                        break;
                    }
                    preKeyFrame = keyframe;
                }
                interpolator = preKeyFrame.getInterpolator();
            }
            if (interpolator == null) interpolator = DEFAULT_INTERPOLATOR;
            float fractionLength = nextKeyFrame.getFraction() - preKeyFrame.getFraction();
            if (fractionLength != 0) {
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
        result.setFraction(keyFraction);
        result.setValue(value);
        result.valueType = preKeyFrame.getValueType();
        return true;
    }

    public TypeEvaluator<T> getTypeEvaluator() {
        return typeEvaluator;
    }

    public void setDuration(@NonNull TimeStamp duration) {
        this.duration = duration;
    }

    public TimeStamp getDuration() {
        return duration;
    }

    public void setStartTime(@NonNull TimeStamp startTime) {
        this.startTime = startTime;
    }

    public TimeStamp getStartTime() {
        return startTime;
    }

    public static KeyframeSet ofInt(TimeStamp duration, int... values) {
        int numKeyframes = values.length;
        Keyframe keyframes[] = new Keyframe[numKeyframes];
        keyframes[0] = Keyframe.ofInt(0f, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = Keyframe.ofInt((float) i / (numKeyframes - 1), values[i]);
        }
        return ofKeyframe(duration, DEFAULT_INT_EVALUATOR, keyframes);
    }

    public static KeyframeSet ofInt(int... values) {
        return ofInt(TimeStamp.MATCH_PARENT_VALUE, values);
    }


    public static KeyframeSet ofFloat(TimeStamp duration, float... values) {
        int numKeyframes = values.length;
        Keyframe keyframes[] = new Keyframe[numKeyframes];
        keyframes[0] = Keyframe.ofFloat(0f, values[0]);
        for (int i = 1; i < numKeyframes; ++i) {
            keyframes[i] = Keyframe.ofFloat((float) i / (numKeyframes - 1), values[i]);
        }
        return ofKeyframe(duration, DEFAULT_FLOAT_EVALUATOR, keyframes);
    }

    public static KeyframeSet ofFloat(float... values) {
        return ofFloat(TimeStamp.MATCH_PARENT_VALUE, values);
    }

    public static KeyframeSet ofIntArray(TimeStamp duration, int[][] values) {
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
        return ofIntArray(TimeStamp.MATCH_PARENT_VALUE, values);
    }

    public static KeyframeSet ofFloatArray(TimeStamp duration, float[][] values) {
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
        return ofFloatArray(TimeStamp.MATCH_PARENT_VALUE, values);
    }


    public static <T> KeyframeSet<T> ofKeyframe(TimeStamp duration, TypeEvaluator<T> typeEvaluator, Keyframe<T>... frames) {
        KeyframeSet newSet = new KeyframeSet(frames[0].valueType, typeEvaluator);
        newSet.add(frames);
        newSet.setDuration(duration);
        return newSet;
    }
}
