package com.byteplay.android.renderclient;

public abstract class GLGravity {


    public abstract float getX(float x,
                               float width,
                               float outWidth);

    public abstract float getY(float y,
                               float height, float outHeight);

    public static final GLGravity LEFT_TOP = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return outHeight - height - y;
        }

        @Override
        public String toString() {
            return "LEFT_TOP";
        }
    };
    public static final GLGravity LEFT_BOTTOM = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return -y;
        }
        @Override
        public String toString() {
            return "LEFT_BOTTOM";
        }

    };
    public static final GLGravity RIGHT_TOP = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return outWidth - width + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return outHeight - height - y;
        }

        @Override
        public String toString() {
            return "RIGHT_TOP";
        }
    };
    public static final GLGravity RIGHT_BOTTOM = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return outWidth - width + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return -y;
        }

        @Override
        public String toString() {
            return "RIGHT_BOTTOM";
        }
    };
    public static final GLGravity LEFT_CENTER_VERTICAL = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return (outHeight - height) / 2 - y;
        }

        @Override
        public String toString() {
            return "LEFT_CENTER_VERTICAL";
        }
    };

    public static final GLGravity RIGHT_CENTER_VERTICAL = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return outWidth - width + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return (outHeight - height) / 2 - y;
        }

        @Override
        public String toString() {
            return "RIGHT_CENTER_VERTICAL";
        }
    };

    public static final GLGravity TOP_CENTER_HORIZONTAL = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return (outWidth - width) / 2 + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return outHeight - height - y;
        }

        @Override
        public String toString() {
            return "TOP_CENTER_HORIZONTAL";
        }
    };

    public static final GLGravity BOTTOM_CENTER_HORIZONTAL = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return (outWidth - width) / 2 + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return -y;
        }

        @Override
        public String toString() {
            return "BOTTOM_CENTER_HORIZONTAL";
        }
    };
    public static final GLGravity CENTER = new GLGravity() {
        @Override
        public float getX(float x, float width, float outWidth) {
            return (outWidth - width) / 2 + x;
        }

        @Override
        public float getY(float y, float height, float outHeight) {
            return (outHeight - height) / 2 - y;
        }

        @Override
        public String toString() {
            return "CENTER";
        }
    };

}
