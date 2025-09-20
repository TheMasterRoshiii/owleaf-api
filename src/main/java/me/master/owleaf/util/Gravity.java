package me.master.owleaf.util;

import me.master.owleaf.api.RotationParameters;
import net.minecraft.core.Direction;

public class Gravity {
    private final Direction direction;
    private final int priority;
    private int duration;
    private final String source;
    private final RotationParameters rotationParameters;

    public Gravity(Direction direction, int priority, int duration, String source, RotationParameters rotationParameters) {
        this.direction = direction;
        this.priority = priority;
        this.duration = duration;
        this.source = source;
        this.rotationParameters = rotationParameters;
    }

    public Gravity(Direction direction, int priority, int duration, String source) {
        this(direction, priority, duration, source, new RotationParameters());
    }

    public Direction direction() { return this.direction; }
    public int duration() { return this.duration; }
    public int priority() { return this.priority; }
    public String source() { return this.source; }
    public RotationParameters rotationParameters() { return this.rotationParameters; }

    public void decrementDuration() {
        if (this.duration > 0) {
            this.duration--;
        }
    }
}
