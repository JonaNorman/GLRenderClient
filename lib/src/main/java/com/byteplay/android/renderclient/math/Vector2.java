package com.byteplay.android.renderclient.math;

public class Vector2 {

    public float x;
    public float y;

    public Vector2() {
    }


    public Vector2(float x, float y) {
        set(x, y);
    }


    public Vector2(Vector2 v) {
        set(v);
    }


    public Vector2 cpy() {
        return new Vector2(this);
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float len2() {
        return x * x + y * y;
    }


    public Vector2 set(Vector2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }


    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }


    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 normalize() {
        float len = len();
        if (len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }


    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }


    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public float dot(float ox, float oy) {
        return x * ox + y * oy;
    }


    public Vector2 scale(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }


    public Vector2 scale(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2 scale(Vector2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }


    public Vector2 lerp(Vector2 target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (x * invAlpha) + (target.x * alpha);
        this.y = (y * invAlpha) + (target.y * alpha);
        return this;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector2 other = (Vector2) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) return false;
        return true;
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }


    public Vector2 setZero() {
        this.x = 0;
        this.y = 0;
        return this;
    }
}
