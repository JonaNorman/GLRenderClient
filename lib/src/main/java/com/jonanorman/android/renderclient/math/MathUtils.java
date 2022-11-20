package com.jonanorman.android.renderclient.math;

public class MathUtils {
    static public float LN2 = (float) Math.log(2);

    private MathUtils() {
    }

    static public int ceilPowerOfTwo(double value) {
        return (int) Math.pow(2, Math.ceil(Math.log(value) / LN2));
    }

    static public int floorPowerOfTwo(double value) {
        return (int) Math.pow(2, floorLogOfTwo(value));
    }

    static public int floorLogOfTwo(double value) {
        return (int) Math.floor(Math.log(value) / LN2);
    }

    static public boolean isPowerOfTwo(int value) {
        return (value & (value - 1)) == 0 && value != 0;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }


    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }


    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
