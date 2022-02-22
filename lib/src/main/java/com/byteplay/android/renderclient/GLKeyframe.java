package com.byteplay.android.renderclient;

import android.animation.TimeInterpolator;
import android.view.animation.LinearInterpolator;

public class GLKeyframe implements Cloneable {

    TimeInterpolator interpolator = new LinearInterpolator();

    float fraction;

    float[] value;

    public GLKeyframe(float fraction, float... value) {
        this.fraction = fraction;
        this.value = value;
    }

    public float[] getValue() {
        return value;
    }


    public void setValue(float... value) {
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
    public GLKeyframe clone() {
        GLKeyframe clone = new GLKeyframe(fraction, value);
        clone.setInterpolator(getInterpolator());
        return clone;
    }

    public static GLKeyframe ofInt(float fraction, int value) {
        return new GLKeyframe(fraction, value);
    }


    public static GLKeyframe ofFloat(float fraction, float value) {
        if (Float.isNaN(value)) {
            throw new IllegalArgumentException("Bad value (NaN) in KeyFrame");
        }
        return new GLKeyframe(fraction, value);
    }

    public static GLKeyframe ofIntArray(float fraction, int[] value) {
        float[] floats = new float[value.length];
        for (int i = 0; i < value.length; i++) {
            floats[i] = value[i];
        }
        return new GLKeyframe(fraction, floats);
    }

    public static GLKeyframe ofFloatArray(float fraction, float[] value) {
        for (int i = 0; i < value.length; i++) {
            if (Float.isNaN(value[i])) {
                throw new IllegalArgumentException("Bad value (NaN) in KeyFrame");
            }
        }
        return new GLKeyframe(fraction, value);
    }
}
