package me.master.owleaf.util.packet;

import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.GravityComponent;
import net.minecraft.network.FriendlyByteBuf;

public abstract class GravityPacket {
    public int entityId;

    public GravityPacket() {}

    public GravityPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    public abstract void run(GravityComponent gc);
    public abstract RotationParameters getRotationParameters();
}
