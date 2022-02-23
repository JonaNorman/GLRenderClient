package com.byteplay.android.renderclient;

import java.util.Objects;

public abstract class GLXfermode {


    public abstract void apply(GLBlend blend);


    public static final GLXfermode CLEAR = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ZERO, GLBLendFactor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "CLEAR";
        }
    };

    public static final GLXfermode SRC = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE, GLBLendFactor.ZERO);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "SRC";
        }
    };


    public static final GLXfermode DST = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ZERO, GLBLendFactor.ONE);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }
        @Override
        public String toString() {
            return "DST";
        }
    };

    public static final GLXfermode SRC_OVER = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE, GLBLendFactor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_OVER";
        }

    };

    public static final GLXfermode DST_OVER = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE_MINUS_DST_ALPHA, GLBLendFactor.ONE);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "DST_OVER";
        }

    };

    public static final GLXfermode SRC_IN = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.DST_ALPHA, GLBLendFactor.ZERO);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_IN";
        }

    };

    public static final GLXfermode DST_IN = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ZERO, GLBLendFactor.SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }


        @Override
        public String toString() {
            return "DST_IN";
        }
    };


    public static final GLXfermode SRC_OUT = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE_MINUS_DST_ALPHA, GLBLendFactor.ZERO);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_OUT";
        }
    };

    public static final GLXfermode DST_OUT = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ZERO, GLBLendFactor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "DST_OUT";
        }
    };

    public static final GLXfermode SRC_ATOP = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.DST_ALPHA, GLBLendFactor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_ATOP";
        }
    };

    public static final GLXfermode DST_ATOP = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE_MINUS_DST_ALPHA, GLBLendFactor.SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "DST_ATOP";
        }
    };

    public static final GLXfermode XOR = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE_MINUS_DST_ALPHA, GLBLendFactor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }
        @Override
        public String toString() {
            return "XOR";
        }
    };


    public static final GLXfermode PLUS = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE, GLBLendFactor.ONE);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "PLUS";
        }
    };

    public static final GLXfermode MULTIPLY = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ZERO, GLBLendFactor.SRC_COLOR);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "MULTIPLY";
        }
    };

    public static final GLXfermode SCREEN = new GLXfermode() {
        @Override
        public void apply(GLBlend blend) {
            blend.setBlendFactor(GLBLendFactor.ONE, GLBLendFactor.ONE_MINUS_SRC_COLOR);
            blend.setBlendEquation(GLBlendEquation.ADD);
        }

        @Override
        public String toString() {
            return "SCREEN";
        }
    };

    public static final class CUSTOM extends GLXfermode {
        private int blendColor;
        private GLBLendFactor rgbSrcFactor = GLBLendFactor.ONE;
        private GLBLendFactor alphaSrcFactor = GLBLendFactor.ZERO;
        private GLBLendFactor rgbDstFactor = GLBLendFactor.ONE;
        private GLBLendFactor alphaDstFactor = GLBLendFactor.ZERO;
        private GLBlendEquation rgbBlendEquation = GLBlendEquation.ADD;
        private GLBlendEquation alphaBlendEquation = GLBlendEquation.ADD;

        @Override
        public void apply(GLBlend blend) {
            blend.setBlendColor(blendColor);
            blend.setRgbSrcFactor(rgbSrcFactor);
            blend.setAlphaSrcFactor(alphaSrcFactor);
            blend.setRgbDstFactor(rgbDstFactor);
            blend.setAlphaDstFactor(alphaDstFactor);
            blend.setRgbBlendEquation(rgbBlendEquation);
            blend.setAlphaBlendEquation(alphaBlendEquation);
        }

        public void setBlendColor(int blendColor) {
            this.blendColor = blendColor;
        }

        public void setRgbSrcFactor(GLBLendFactor rgbSrcFactor) {
            this.rgbSrcFactor = rgbSrcFactor;
        }

        public void setAlphaSrcFactor(GLBLendFactor alphaSrcFactor) {
            this.alphaSrcFactor = alphaSrcFactor;
        }

        public void setRgbDstFactor(GLBLendFactor rgbDstFactor) {
            this.rgbDstFactor = rgbDstFactor;
        }

        public void setAlphaDstFactor(GLBLendFactor alphaDstFactor) {
            this.alphaDstFactor = alphaDstFactor;
        }

        public void setRgbBlendEquation(GLBlendEquation rgbBlendEquation) {
            this.rgbBlendEquation = rgbBlendEquation;
        }

        public void setAlphaBlendEquation(GLBlendEquation alphaBlendEquation) {
            this.alphaBlendEquation = alphaBlendEquation;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CUSTOM)) return false;
            CUSTOM custom = (CUSTOM) o;
            return blendColor == custom.blendColor && rgbSrcFactor == custom.rgbSrcFactor && alphaSrcFactor == custom.alphaSrcFactor && rgbDstFactor == custom.rgbDstFactor && alphaDstFactor == custom.alphaDstFactor && rgbBlendEquation == custom.rgbBlendEquation && alphaBlendEquation == custom.alphaBlendEquation;
        }

        @Override
        public int hashCode() {
            return Objects.hash(blendColor, rgbSrcFactor, alphaSrcFactor, rgbDstFactor, alphaDstFactor, rgbBlendEquation, alphaBlendEquation);
        }

        @Override
        public String toString() {
            return "CUSTOM";
        }
    }

    ;
}
