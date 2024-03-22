package dev.enjarai.mls.config;

public enum Orientation {
    DOWN(false, false),
    RIGHT(true, false),
    UP(false, true),
    LEFT(true, true);

    public final boolean switchAxes;
    public final boolean reverseAxes;

    Orientation(boolean switchAxes, boolean reverseAxes) {
        this.switchAxes = switchAxes;
        this.reverseAxes = reverseAxes;
    }
}
