package me.master.owleaf.util.packet;

import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.GravityComponent;
import me.master.owleaf.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;

public class InvertGravityPacket extends GravityPacket {
    public final boolean inverted;
    public final RotationParameters rotationParameters;
    public final boolean initialGravity;

    public InvertGravityPacket(boolean inverted, RotationParameters rotationParameters, boolean initialGravity) {
        this.inverted = inverted;
        this.rotationParameters = rotationParameters;
        this.initialGravity = initialGravity;
    }

    public InvertGravityPacket(FriendlyByteBuf buf) {
        super(buf);
        this.inverted = buf.readBoolean();
        this.rotationParameters = NetworkUtil.readRotationParameters(buf);
        this.initialGravity = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeBoolean(inverted);
        NetworkUtil.writeRotationParameters(buf, rotationParameters);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.invertGravity(inverted, rotationParameters, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return rotationParameters;
    }
}
