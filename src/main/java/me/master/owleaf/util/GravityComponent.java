package me.master.owleaf.util;

import me.master.owleaf.RotationAnimation;
import me.master.owleaf.api.RotationParameters;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public interface GravityComponent {

    void onGravityChanged(Direction oldGravity, Direction newGravity, RotationParameters rotationParameters, boolean initialGravity);

    void updateGravity(RotationParameters rotationParameters, boolean initialGravity);

    Direction getGravityDirection();

    Direction getPrevGravityDirection();

    Direction getDefaultGravityDirection();

    Direction getActualGravityDirection();

    ArrayList<Gravity> getGravity();

    boolean getInvertGravity();

    RotationAnimation getRotationAnimation();

    void setGravity(ArrayList<Gravity> gravityList, boolean initialGravity);

    void invertGravity(boolean isInverted, RotationParameters rotationParameters, boolean initialGravity);

    void setDefaultGravityDirection(Direction gravityDirection, RotationParameters rotationParameters, boolean initialGravity);

    void addGravity(Gravity gravity, boolean initialGravity);

    void removeGravity(Gravity gravity);

    boolean hasGravity(Gravity gravity);

    void clearGravity(RotationParameters rotationParameters, boolean initialGravity);

    void tick();

    void readFromNbt(CompoundTag nbt);

    void writeToNbt(CompoundTag nbt);
}
