package com.jonanorman.android.renderclient.opengl;

import java.util.Objects;

public abstract class GLXfermode {


    public final void apply(GLBlend blend) {
        onApply(blend);
        blend.apply();
    }

    protected abstract void onApply(GLBlend blend);


    public static final GLXfermode CLEAR = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ZERO, GLBlend.Factor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "CLEAR";
        }
    };

    public static final GLXfermode SRC = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE, GLBlend.Factor.ZERO);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "SRC";
        }
    };


    public static final GLXfermode DST = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ZERO, GLBlend.Factor.ONE);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "DST";
        }
    };

    public static final GLXfermode SRC_OVER = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE, GLBlend.Factor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_OVER";
        }

    };

    public static final GLXfermode DST_OVER = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE_MINUS_DST_ALPHA, GLBlend.Factor.ONE);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "DST_OVER";
        }

    };

    public static final GLXfermode SRC_IN = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.DST_ALPHA, GLBlend.Factor.ZERO);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_IN";
        }

    };

    public static final GLXfermode DST_IN = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ZERO, GLBlend.Factor.SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }


        @Override
        public String toString() {
            return "DST_IN";
        }
    };


    public static final GLXfermode SRC_OUT = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE_MINUS_DST_ALPHA, GLBlend.Factor.ZERO);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_OUT";
        }
    };

    public static final GLXfermode DST_OUT = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ZERO, GLBlend.Factor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "DST_OUT";
        }
    };

    public static final GLXfermode SRC_ATOP = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.DST_ALPHA, GLBlend.Factor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "SRC_ATOP";
        }
    };

    public static final GLXfermode DST_ATOP = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE_MINUS_DST_ALPHA, GLBlend.Factor.SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "DST_ATOP";
        }
    };

    public static final GLXfermode XOR = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE_MINUS_DST_ALPHA, GLBlend.Factor.ONE_MINUS_SRC_ALPHA);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "XOR";
        }
    };


    public static final GLXfermode PLUS = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE, GLBlend.Factor.ONE);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "PLUS";
        }
    };

    public static final GLXfermode MULTIPLY = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ZERO, GLBlend.Factor.SRC_COLOR);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "MULTIPLY";
        }
    };

    public static final GLXfermode SCREEN = new GLXfermode() {
        @Override
        protected void onApply(GLBlend blend) {
            blend.setBlendFactor(GLBlend.Factor.ONE, GLBlend.Factor.ONE_MINUS_SRC_COLOR);
            blend.setBlendEquation(GLBlend.Equation.ADD);
        }

        @Override
        public String toString() {
            return "SCREEN";
        }
    };

    public static final class CUSTOM extends GLXfermode {
        private int blendColor;
        private GLBlend.Factor rgbSrcFactor = GLBlend.Factor.ONE;
        private GLBlend.Factor alphaSrcFactor = GLBlend.Factor.ZERO;
        private GLBlend.Factor rgbDstFactor = GLBlend.Factor.ONE;
        private GLBlend.Factor alphaDstFactor = GLBlend.Factor.ZERO;
        private GLBlend.Equation rgbBlendEquation = GLBlend.Equation.ADD;
        private GLBlend.Equation alphaBlendEquation = GLBlend.Equation.ADD;

        @Override
        protected void onApply(GLBlend blend) {
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

        public void setRgbSrcFactor(GLBlend.Factor rgbSrcFactor) {
            this.rgbSrcFactor = rgbSrcFactor;
        }

        public void setAlphaSrcFactor(GLBlend.Factor alphaSrcFactor) {
            this.alphaSrcFactor = alphaSrcFactor;
        }

        public void setRgbDstFactor(GLBlend.Factor rgbDstFactor) {
            this.rgbDstFactor = rgbDstFactor;
        }

        public void setAlphaDstFactor(GLBlend.Factor alphaDstFactor) {
            this.alphaDstFactor = alphaDstFactor;
        }

        public void setRgbBlendEquation(GLBlend.Equation rgbBlendEquation) {
            this.rgbBlendEquation = rgbBlendEquation;
        }

        public void setAlphaBlendEquation(GLBlend.Equation alphaBlendEquation) {
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
            return "CUSTOM[" +
                    "blendColor=" + blendColor +
                    ", rgbSrcFactor=" + rgbSrcFactor +
                    ", alphaSrcFactor=" + alphaSrcFactor +
                    ", rgbDstFactor=" + rgbDstFactor +
                    ", alphaDstFactor=" + alphaDstFactor +
                    ", rgbBlendEquation=" + rgbBlendEquation +
                    ", alphaBlendEquation=" + alphaBlendEquation +
                    ']';
        }
    }

    ;
}
