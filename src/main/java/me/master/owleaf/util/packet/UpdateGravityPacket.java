package me.master.owleaf.util.packet;

import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.Gravity;
import me.master.owleaf.util.GravityComponent;
import me.master.owleaf.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;

public class UpdateGravityPacket extends GravityPacket {
    public final Gravity gravity;
    public final boolean initialGravity;

    public UpdateGravityPacket(Gravity gravity, boolean initialGravity) {
        this.gravity = gravity;
        this.initialGravity = initialGravity;
    }

    public UpdateGravityPacket(FriendlyByteBuf buf) {
        super(buf);
        this.gravity = NetworkUtil.readGravity(buf);
        this.initialGravity = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        NetworkUtil.writeGravity(buf, gravity);
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.addGravity(gravity, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return gravity.rotationParameters();
    }
}
