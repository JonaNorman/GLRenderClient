package com.jonanorman.android.renderclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EGLConfigSimpleChooser implements EGLConfigChooser {

    private final static Comparator<EGLConfig> CONFIG_COMPARATOR = (o1, o2) -> {
        if (o1.getRedSize() > o2.getRedSize()) {
            return 1;
        }
        if (o1.getGreenSize() > o2.getGreenSize()) {
            return 1;
        }
        if (o1.getBlueSize() > o2.getGreenSize()) {
            return 1;
        }
        if (o1.getAlphaSize() > o2.getAlphaSize()) {
            return 1;
        }
        if (o1.getDepthSize() > o2.getDepthSize()) {
            return 1;
        }
        if (o1.getStencilSize() > o2.getStencilSize()) {
            return 1;
        }
        if (o1.getSampleBuffers() > o2.getSampleBuffers()) {
            return 1;
        }
        if (o1.getSamples() > o2.getSamples()) {
            return 1;
        }

        if ((o1.isWindowSurface() == false ||
                o1.isPBufferSurface() == false) && (o2.isWindowSurface() == true &&
                o2.isPBufferSurface() == true)) {
            return 1;
        }

        if ((o1.isRenderGL20() == false ||
                o1.isRenderGL30() == false) && (o2.isRenderGL20() == true &&
                o2.isConformantGL30() == true)) {
            return 1;
        }
        if (o1.getAlphaMaskSize() > o2.getAlphaMaskSize()) {
            return 1;
        }

        if (o1.isSlow() == true && o2.isSlow() == false) {
            return 1;
        }
        if (o1.isRecordable() == false && o2.isRecordable() == true) {
            return 1;
        }
        if (o1.isConformantGL20() != o2.isRenderGL20() || o1.isConformantGL30() != o2.isConformantGL30()
                || o1.isConformantGL10() != o2.isConformantGL10()) {
            return 1;
        }
        if (o1.isTransparent() == true && o2.isTransparent() == false) {
            return 1;
        }
        if (o1.isLuminanceColor() == true && o2.isLuminanceColor() == false) {
            return 1;
        }
        return 0;
    };
    private int alphaSize;
    private int blueSize;
    private int greenSize;
    private int redSize;
    private int depthSize;
    private int stencilSize;
    private int samples;
    private int sampleBuffers;
    private boolean windowSurface;
    private boolean pbufferSurface;
    private Boolean renderGL10;
    private Boolean renderGL20;
    private Boolean renderGL30;
    private Boolean recordable;


    private EGLConfigSimpleChooser() {
        this.windowSurface = true;
        this.pbufferSurface = true;
    }

    public void setAlphaSize(int alphaSize) {
        this.alphaSize = alphaSize;
    }

    public void setBlueSize(int blueSize) {
        this.blueSize = blueSize;
    }

    public void setGreenSize(int greenSize) {
        this.greenSize = greenSize;
    }

    public void setRedSize(int redSize) {
        this.redSize = redSize;
    }

    public void setDepthSize(int depthSize) {
        this.depthSize = depthSize;
    }

    public void setStencilSize(int stencilSize) {
        this.stencilSize = stencilSize;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public void setSampleBuffers(int sampleBuffers) {
        this.sampleBuffers = sampleBuffers;
    }

    public void setRenderGL10(Boolean renderGL10) {
        this.renderGL10 = renderGL10;
    }

    public void setRenderGL20(Boolean renderGL20) {
        this.renderGL20 = renderGL20;
    }

    public void setRenderGL30(Boolean renderGL30) {
        this.renderGL30 = renderGL30;
    }

    public void setRecordable(Boolean recordable) {
        this.recordable = recordable;
    }

    @Override
    public final EGLConfig chooseConfig(EGLConfig[] configs) {
        List<EGLConfig> findConfigs = new ArrayList<>();
        for (EGLConfig config : configs) {
            if (config.getRedSize() >= redSize
                    && config.getGreenSize() >= greenSize
                    && config.getBlueSize() >= blueSize
                    && config.getAlphaSize() >= alphaSize
                    && config.getDepthSize() >= depthSize
                    && config.getStencilSize() >= stencilSize
                    && config.getSampleBuffers() >= sampleBuffers
                    && config.getSamples() >= samples
                    && (config.isWindowSurface() == windowSurface)
                    && (config.isPBufferSurface() == pbufferSurface)
                    && (renderGL10 == null || config.isRenderGL10() == renderGL10)
                    && (renderGL20 == null || config.isRenderGL20() == renderGL20)
                    && (renderGL30 == null || config.isRenderGL30() == renderGL30)
                    && (recordable == null || config.isRecordable() == recordable)) {
                findConfigs.add(config);
            }
        }
        Collections.sort(findConfigs, CONFIG_COMPARATOR);
        return onChooseConfig(findConfigs);
    }


    protected EGLConfig onChooseConfig(List<EGLConfig> configs) {
        return configs.size() > 0 ? configs.get(0) : null;
    }


    public static final class Builder {
        private int alphaSize = 8;
        private int blueSize = 8;
        private int greenSize = 8;
        private int redSize = 8;
        private int depthSize;
        private int stencilSize;
        private int samples;
        private int sampleBuffers;
        private Boolean renderGL10;
        private Boolean renderGL20 = true;
        private Boolean renderGL30;
        private Boolean recordable;

        public Builder() {
        }


        public Builder setAlphaSize(int alphaSize) {
            this.alphaSize = alphaSize;
            return this;
        }

        public Builder setBlueSize(int blueSize) {
            this.blueSize = blueSize;
            return this;
        }

        public Builder setGreenSize(int greenSize) {
            this.greenSize = greenSize;
            return this;
        }

        public Builder setRedSize(int redSize) {
            this.redSize = redSize;
            return this;
        }

        public Builder setDepthSize(int depthSize) {
            this.depthSize = depthSize;
            return this;
        }

        public Builder setStencilSize(int stencilSize) {
            this.stencilSize = stencilSize;
            return this;
        }

        public Builder setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        public Builder setSampleBuffers(int sampleBuffers) {
            this.sampleBuffers = sampleBuffers;
            return this;
        }

        public Builder setRenderGL10(Boolean renderGL10) {
            this.renderGL10 = renderGL10;
            return this;
        }

        public Builder setRenderGL20(Boolean renderGL20) {
            this.renderGL20 = renderGL20;
            return this;
        }

        public Builder setRenderGL30(Boolean renderGL30) {
            this.renderGL30 = renderGL30;
            return this;
        }

        public Builder setRecordable(Boolean recordable) {
            this.recordable = recordable;
            return this;
        }

        public EGLConfigSimpleChooser build() {
            EGLConfigSimpleChooser chooser = new EGLConfigSimpleChooser();
            chooser.setAlphaSize(alphaSize);
            chooser.setBlueSize(blueSize);
            chooser.setGreenSize(greenSize);
            chooser.setRedSize(redSize);
            chooser.setDepthSize(depthSize);
            chooser.setStencilSize(stencilSize);
            chooser.setSamples(samples);
            chooser.setSampleBuffers(sampleBuffers);
            chooser.setRenderGL10(renderGL10);
            chooser.setRenderGL20(renderGL20);
            chooser.setRenderGL30(renderGL30);
            chooser.setRecordable(recordable);
            return chooser;
        }
    }
}
