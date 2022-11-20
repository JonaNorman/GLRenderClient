package com.jonanorman.android.renderclient.math;

import android.animation.TimeInterpolator;
import android.view.animation.LinearInterpolator;

public class Keyframe<T> implements Cloneable {

    private static final TimeInterpolator DEFAULT_INTERPOLATOR = new LinearInterpolator();

    TimeInterpolator interpolator = DEFAULT_INTERPOLATOR;

    float fraction;

    T value;

    Class<T> valueType;

    public Keyframe(float fraction, T value, Class<T> valueType) {
        this.fraction = fraction;
        this.value = value;
        this.valueType = valueType;
    }

    public T getValue() {
        return value;
    }

    public Class<T> getValueType() {
        return valueType;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public float getFraction() {
        return fraction;
    }


    public void setFraction(float fraction) {
        this.fraction = fraction;
    }


    public TimeInterpolator getInterpolator() {
        return interpolator;
    }


    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }


    @Override
    public Keyframe clone() {
        Keyframe clone = new Keyframe(fraction, value, valueType);
        clone.setInterpolator(interpolator);
        return clone;
    }

    public static Keyframe ofInt(float fraction, int value) {
        return new Keyframe(fraction, value, int.class);
    }


    public static Keyframe ofFloat(float fraction, float value) {
        if (Float.isNaN(value)) {
            throw new IllegalArgumentException("bad value (NaN) in KeyFrame");
        }
        return new Keyframe(fraction, value, float.class);
    }

    public static Keyframe ofIntArray(float fraction, int... value) {
        float[] floats = new float[value.length];
        for (int i = 0; i < value.length; i++) {
            floats[i] = value[i];
        }
        return new Keyframe(fraction, floats, int[].class);
    }

    public static Keyframe ofFloatArray(float fraction, float... value) {
        for (int i = 0; i < value.length; i++) {
            if (Float.isNaN(value[i])) {
                throw new IllegalArgumentException("bad value (NaN) in KeyFrame");
            }
        }
        return new Keyframe(fraction, value, float[].class);
    }


    public static <T> Keyframe ofValue(float fraction, T value, Class<T> valueType) {
        return new Keyframe(fraction, value, valueType);
    }
}
