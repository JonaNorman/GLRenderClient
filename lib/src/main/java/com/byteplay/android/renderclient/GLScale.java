package com.byteplay.android.renderclient;

public abstract class GLScale {


    public abstract float getWidth(float sourceWidth, float sourceHeight,
                                   float targetWidth, float targetHeight);

    public abstract float getHeight(float sourceWidth, float sourceHeight,
                                    float targetWidth, float targetHeight);


    public static final GLScale FIT = new GLScale() {


        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float targetRatio = targetHeight / targetWidth;
            float sourceRatio = sourceHeight / sourceWidth;
            float scale = targetRatio > sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
            return sourceWidth * scale;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float targetRatio = targetHeight / targetWidth;
            float sourceRatio = sourceHeight / sourceWidth;
            float scale = targetRatio > sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
            return sourceHeight * scale;
        }
    };

    public static final GLScale CONTAIN = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float targetRatio = targetHeight / targetWidth;
            float sourceRatio = sourceHeight / sourceWidth;
            float scale = targetRatio > sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
            if (scale > 1) scale = 1;
            return sourceWidth * scale;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float targetRatio = targetHeight / targetWidth;
            float sourceRatio = sourceHeight / sourceWidth;
            float scale = targetRatio > sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
            if (scale > 1) scale = 1;
            return sourceHeight * scale;
        }
    };

    public static final GLScale FILL = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float targetRatio = targetHeight / targetWidth;
            float sourceRatio = sourceHeight / sourceWidth;
            float scale = targetRatio < sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
            return sourceWidth * scale;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float targetRatio = targetHeight / targetWidth;
            float sourceRatio = sourceHeight / sourceWidth;
            float scale = targetRatio < sourceRatio ? targetWidth / sourceWidth : targetHeight / sourceHeight;
            return sourceHeight * scale;
        }
    };


    public static final GLScale FILL_X = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float scale = targetWidth / sourceWidth;
            return sourceWidth * scale;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float scale = targetWidth / sourceWidth;
            return sourceHeight * scale;
        }
    };

    public static final GLScale FILL_Y = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float scale = targetHeight / sourceHeight;
            return sourceWidth * scale;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            float scale = targetHeight / sourceHeight;
            return sourceHeight * scale;
        }
    };

    public static final GLScale STRETCH = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return targetWidth;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return targetHeight;
        }
    };


    public static final GLScale STRETCH_X = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return targetWidth;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return sourceHeight;
        }
    };

    public static final GLScale STRETCH_Y = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return sourceWidth;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return targetHeight;
        }
    };


    public static final GLScale NONE = new GLScale() {
        @Override
        public float getWidth(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return sourceWidth;
        }

        @Override
        public float getHeight(float sourceWidth, float sourceHeight, float targetWidth, float targetHeight) {
            return sourceHeight;
        }
    };
}
