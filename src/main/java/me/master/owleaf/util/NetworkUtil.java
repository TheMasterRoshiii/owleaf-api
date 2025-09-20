package me.master.owleaf.util;

import me.master.owleaf.api.RotationParameters;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

public class NetworkUtil {

    public static void writeDirection(FriendlyByteBuf buf, Direction direction) {
        buf.writeByte(direction == null ? -1 : direction.get3DDataValue());
    }

    public static Direction readDirection(FriendlyByteBuf buf) {
        int rawDirection = buf.readByte();
        return (0 <= rawDirection && rawDirection < Direction.values().length) ?
                Direction.from3DDataValue(rawDirection) : null;
    }

    public static void writeRotationParameters(FriendlyByteBuf buf, RotationParameters rotationParameters) {
        buf.writeBoolean(rotationParameters.rotateVelocity());
        buf.writeBoolean(rotationParameters.rotateView());
        buf.writeBoolean(rotationParameters.alternateCenter());
        buf.writeInt(rotationParameters.rotationTime());
    }

    public static RotationParameters readRotationParameters(FriendlyByteBuf buf) {
        return new RotationParameters(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
    }

    public static void writeGravity(FriendlyByteBuf buf, Gravity gravity) {
        writeDirection(buf, gravity.direction());
        buf.writeInt(gravity.priority());
        buf.writeInt(gravity.duration());
        buf.writeUtf(gravity.source());
        writeRotationParameters(buf, gravity.rotationParameters());
    }

    public static Gravity readGravity(FriendlyByteBuf buf) {
        return new Gravity(
                readDirection(buf),
                buf.readInt(),
                buf.readInt(),
                buf.readUtf(),
                readRotationParameters(buf)
        );
    }
}
