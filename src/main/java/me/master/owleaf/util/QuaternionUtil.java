package me.master.owleaf.util;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class QuaternionUtil {

    public static Quaternionf getViewRotation(float pitch, float yaw) {
        Quaternionf r1 = new Quaternionf().fromAxisAngleDeg(new Vector3f(1.0F, 0.0F, 0.0F), pitch);
        Quaternionf r2 = new Quaternionf().fromAxisAngleDeg(new Vector3f(0.0F, 1.0F, 0.0F), yaw + 180.0F);
        r1.mul(r2);
        return r1;
    }

    public static Quaternionf getRotationBetween(Vec3 from, Vec3 to) {
        from = from.normalize();
        to = to.normalize();
        Vec3 axis = from.cross(to).normalize();
        double cos = from.dot(to);
        double angle = Math.acos(cos);
        return new Quaternionf().fromAxisAngleRad(new Vector3f((float)axis.x, (float)axis.y, (float)axis.z), (float)angle);
    }
}
