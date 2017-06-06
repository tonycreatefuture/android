package com.zhongdasoft.svwtrainnet.util;

public class Screen {
    private int pxWidth;
    private int pxHeight;
    private int dipWidth;
    private int dipHeight;
    private float density;
    private int densityDpi;

    public int getPxWidth() {
        return pxWidth;
    }

    public void setPxWidth(int pxWidth) {
        this.pxWidth = pxWidth;
    }

    public int getPxHeight() {
        return pxHeight;
    }

    public void setPxHeight(int pxHeight) {
        this.pxHeight = pxHeight;
    }

    public int getDipWidth() {
        return dipWidth;
    }

    public void setDipWidth(int dipWidth) {
        this.dipWidth = dipWidth;
    }

    public int getDipHeight() {
        return dipHeight;
    }

    public void setDipHeight(int dipHeight) {
        this.dipHeight = dipHeight;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public int getDensityDpi() {
        return densityDpi;
    }

    public void setDensityDpi(int densityDpi) {
        this.densityDpi = densityDpi;
    }

}
