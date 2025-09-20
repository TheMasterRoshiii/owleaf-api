package me.master.owleaf.util.packet;

import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.GravityComponent;
import me.master.owleaf.util.NetworkUtil;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

public class DefaultGravityPacket extends GravityPacket {
    public final Direction direction;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public DefaultGravityPacket(Direction direction, RotationParameters rotationParameters, boolean initialGravity) {
        this.direction = direction;
        this.rotationParameters = rotationParameters;
        this.initialGravity = initialGravity;
    }

    public DefaultGravityPacket(FriendlyByteBuf buf) {
        super(buf);
        this.direction = NetworkUtil.readDirection(buf);
        this.rotationParameters = NetworkUtil.readRotationParameters(buf);
        this.initialGravity = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        NetworkUtil.writeDirection(buf, direction);
        NetworkUtil.writeRotationParameters(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.setDefaultGravityDirection(direction, rotationParameters, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
}
