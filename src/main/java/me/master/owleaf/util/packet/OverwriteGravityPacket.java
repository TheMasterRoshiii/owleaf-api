package me.master.owleaf.util.packet;

import me.master.owleaf.api.RotationParameters;
import me.master.owleaf.util.Gravity;
import me.master.owleaf.util.GravityComponent;
import me.master.owleaf.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;

public class OverwriteGravityPacket extends GravityPacket {
    public final ArrayList<Gravity> gravityList;
    public final boolean initialGravity;

    public OverwriteGravityPacket(ArrayList<Gravity> gravityList, boolean initialGravity) {
        this.gravityList = gravityList;
        this.initialGravity = initialGravity;
    }

    public OverwriteGravityPacket(FriendlyByteBuf buf) {
        super(buf);
        int size = buf.readInt();
        this.gravityList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            gravityList.add(NetworkUtil.readGravity(buf));
        }
        this.initialGravity = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        buf.writeInt(gravityList.size());
        for (Gravity gravity : gravityList) {
            NetworkUtil.writeGravity(buf, gravity);
        }
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(GravityComponent gc) {
        gc.setGravity(gravityList, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        return gravityList.isEmpty() ? new RotationParameters() : gravityList.get(0).rotationParameters();
    }
}
