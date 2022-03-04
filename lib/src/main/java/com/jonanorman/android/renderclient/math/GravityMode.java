package com.jonanorman.android.renderclient.math;

public abstract class GravityMode {


    public abstract float getX(float x,
                               float width,
                               float outWidth);

    public abstract float getY(float y,
                               float height, float outHeight);

    public static final GravityMode LEFT_TOP = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return y;
        }

        @Override
        public String toString() {
            return "LEFT_TOP";
        }
    };
    public static final GravityMode LEFT_BOTTOM = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return outHeight - height + y;
        }

        @Override
        public String toString() {
            return "LEFT_BOTTOM";
        }

    };
    public static final GravityMode RIGHT_TOP = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return outWidth - width + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return y;
        }

        @Override
        public String toString() {
            return "RIGHT_TOP";
        }
    };
    public static final GravityMode RIGHT_BOTTOM = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return outWidth - width + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return outHeight - height + y;
        }

        @Override
        public String toString() {
            return "RIGHT_BOTTOM";
        }
    };
    public static final GravityMode LEFT_CENTER_VERTICAL = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return (outHeight - height) / 2 + y;
        }

        @Override
        public String toString() {
            return "LEFT_CENTER_VERTICAL";
        }
    };

    public static final GravityMode RIGHT_CENTER_VERTICAL = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return outWidth - width + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return (outHeight - height) / 2 + y;
        }

        @Override
        public String toString() {
            return "RIGHT_CENTER_VERTICAL";
        }
    };

    public static final GravityMode TOP_CENTER_HORIZONTAL = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return (outWidth - width) / 2 + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return y;
        }

        @Override
        public String toString() {
            return "TOP_CENTER_HORIZONTAL";
        }
    };

    public static final GravityMode BOTTOM_CENTER_HORIZONTAL = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return (outWidth - width) / 2 + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return outHeight - height + y;
        }

        @Override
        public String toString() {
            return "BOTTOM_CENTER_HORIZONTAL";
        }
    };
    public static final GravityMode CENTER = new GravityMode() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return (outWidth - width) / 2 + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return (outHeight - height) / 2 + y;
        }

        @Override
        public String toString() {
            return "CENTER";
        }
    };

}
