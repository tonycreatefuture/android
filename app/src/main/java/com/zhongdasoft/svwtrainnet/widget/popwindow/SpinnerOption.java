package com.zhongdasoft.svwtrainnet.widget.popwindow;

public class SpinnerOption {
    private String value = "";
    private String text = "";

    public SpinnerOption() {
        value = "";
        text = "";
    }

    public SpinnerOption(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
