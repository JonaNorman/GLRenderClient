package com.byteplay.android.renderclient.math;

public class Vector3 {

    public float x;

    public float y;

    public float z;

    public Vector3() {
    }

    public Vector3(float x, float y, float z) {
        this.set(x, y, z);
    }

    public Vector3(final float[] values) {
        this.set(values[0], values[1], values[2]);
    }

    public Vector3(final Vector3 vector) {
        this.set(vector);
    }

    public Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 set(final Vector3 vector) {
        return this.set(vector.x, vector.y, vector.z);
    }

    public Vector3 set(final float[] values) {
        return this.set(values[0], values[1], values[2]);
    }

    public Vector3 setZero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    public Vector3 normalize() {
        final float len2 = this.len2();
        if (len2 == 0f || len2 == 1f) return this;
        return this.scale(1f / (float) Math.sqrt(len2));
    }

    public float len2() {
        return x * x + y * y + z * z;
    }

    public Vector3 scale(float scalar) {
        return set(x * scalar, y * scalar, z * scalar);
    }

    public Vector3 add (final Vector3 vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public Vector3 add (float x, float y, float z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(z);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector3 other = (Vector3) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z)) return false;
        return true;
    }

}
