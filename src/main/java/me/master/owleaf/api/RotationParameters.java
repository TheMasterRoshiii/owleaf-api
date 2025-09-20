package me.master.owleaf.api;

import me.master.owleaf.config.OwleafConfig;

public class RotationParameters {
    private boolean rotateVelocity;
    private boolean rotateView;
    private boolean alternateCenter;
    private int rotationTime;

    public RotationParameters() {
        this(OwleafConfig.worldVelocity, !OwleafConfig.keepWorldLook, false, OwleafConfig.rotationTime);
    }

    public RotationParameters(boolean rotateVelocity, boolean rotateView, boolean alternateCenter, int rotationTime) {
        this.rotateVelocity = rotateVelocity;
        this.rotateView = rotateView;
        this.alternateCenter = alternateCenter;
        this.rotationTime = rotationTime;
    }

    public RotationParameters(int rotationTime) {
        this(OwleafConfig.worldVelocity, !OwleafConfig.keepWorldLook, false, rotationTime);
    }

    public boolean rotateVelocity() { return this.rotateVelocity; }
    public boolean rotateView() { return this.rotateView; }
    public boolean alternateCenter() { return this.alternateCenter; }
    public int rotationTime() { return this.rotationTime; }

    public RotationParameters rotateVelocity(boolean rotateVelocity) {
        this.rotateVelocity = rotateVelocity;
        return this;
    }

    public RotationParameters rotateView(boolean rotateView) {
        this.rotateView = rotateView;
        return this;
    }

    public RotationParameters alternateCenter(boolean alternateCenter) {
        this.alternateCenter = alternateCenter;
        return this;
    }

    public RotationParameters rotationTime(int rotationTime) {
        this.rotationTime = rotationTime;
        return this;
    }
}
