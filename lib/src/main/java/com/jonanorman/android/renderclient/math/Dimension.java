package com.jonanorman.android.renderclient.math;

public class Dimension {

    private float length;

    public static final Dimension MATCH_PARENT_VALUE = new Dimension() {

        @Override
        public void setLength(float length) {
            throw new RuntimeException("Dimension matchParentValue can not setLength");
        }
    };

    public Dimension() {
    }

    public Dimension(float length) {
        this.length = length;
    }


    public void setLength(float length) {
        this.length = length;
    }

    public float getLength() {
        return length;
    }
}
