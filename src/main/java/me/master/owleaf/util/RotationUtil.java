package me.master.owleaf.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class RotationUtil {
    private static final Direction[][] DIR_WORLD_TO_PLAYER = new Direction[6][];
    private static final Direction[][] DIR_PLAYER_TO_WORLD = new Direction[6][];
    private static final Quaternionf[] WORLD_ROTATION_QUATERNIONS = new Quaternionf[6];
    private static final Quaternionf[] ENTITY_ROTATION_QUATERNIONS = new Quaternionf[6];


    public static void applyGravityRotation(PoseStack poseStack, Direction gravityDirection) {

        poseStack.mulPose(new Quaternionf().rotationTo(Direction.UP.step(), gravityDirection.step()));
    }


    public static Direction dirWorldToPlayer(Direction direction, Direction gravityDirection) {
        return DIR_WORLD_TO_PLAYER[gravityDirection.get3DDataValue()][direction.get3DDataValue()];
    }

    public static Direction dirPlayerToWorld(Direction direction, Direction gravityDirection) {
        return DIR_PLAYER_TO_WORLD[gravityDirection.get3DDataValue()][direction.get3DDataValue()];
    }

    public static Vec3 vecWorldToPlayer(double x, double y, double z, Direction gravityDirection) {
        return switch (gravityDirection) {
            case DOWN -> new Vec3(x, y, z);
            case UP -> new Vec3(-x, -y, z);
            case NORTH -> new Vec3(x, z, -y);
            case SOUTH -> new Vec3(-x, -z, -y);
            case WEST -> new Vec3(-z, x, -y);
            case EAST -> new Vec3(z, -x, -y);
        };
    }

    public static Vec3 vecWorldToPlayer(Vec3 vec3d, Direction gravityDirection) {
        return vecWorldToPlayer(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Vec3 vecPlayerToWorld(double x, double y, double z, Direction gravityDirection) {
        return switch (gravityDirection) {
            case DOWN -> new Vec3(x, y, z);
            case UP -> new Vec3(-x, -y, z);
            case NORTH -> new Vec3(x, -z, y);
            case SOUTH -> new Vec3(-x, -z, -y);
            case WEST -> new Vec3(y, -z, -x);
            case EAST -> new Vec3(-y, -z, x);
        };
    }

    public static Vec3 vecPlayerToWorld(Vec3 vec3d, Direction gravityDirection) {
        return vecPlayerToWorld(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Vector3f vecWorldToPlayer(float x, float y, float z, Direction gravityDirection) {
        return switch (gravityDirection) {
            case DOWN -> new Vector3f(x, y, z);
            case UP -> new Vector3f(-x, -y, z);
            case NORTH -> new Vector3f(x, z, -y);
            case SOUTH -> new Vector3f(-x, -z, -y);
            case WEST -> new Vector3f(-z, x, -y);
            case EAST -> new Vector3f(z, -x, -y);
        };
    }

    public static Vector3f vecWorldToPlayer(Vector3f vector3f, Direction gravityDirection) {
        return vecWorldToPlayer(vector3f.x, vector3f.y, vector3f.z, gravityDirection);
    }

    public static Vector3f vecPlayerToWorld(float x, float y, float z, Direction gravityDirection) {
        return switch (gravityDirection) {
            case DOWN -> new Vector3f(x, y, z);
            case UP -> new Vector3f(-x, -y, z);
            case NORTH -> new Vector3f(x, -z, y);
            case SOUTH -> new Vector3f(-x, -z, -y);
            case WEST -> new Vector3f(y, -z, -x);
            case EAST -> new Vector3f(-y, -z, x);
        };
    }

    public static Vector3f vecPlayerToWorld(Vector3f vector3f, Direction gravityDirection) {
        return vecPlayerToWorld(vector3f.x, vector3f.y, vector3f.z, gravityDirection);
    }

    public static Vec3 maskWorldToPlayer(double x, double y, double z, Direction gravityDirection) {
        return switch (gravityDirection) {
            case DOWN, UP -> new Vec3(x, y, z);
            case NORTH, SOUTH -> new Vec3(x, z, y);
            case WEST, EAST -> new Vec3(z, x, y);
        };
    }

    public static Vec3 maskWorldToPlayer(Vec3 vec3d, Direction gravityDirection) {
        return maskWorldToPlayer(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static Vec3 maskPlayerToWorld(double x, double y, double z, Direction gravityDirection) {
        return switch (gravityDirection) {
            case DOWN, UP -> new Vec3(x, y, z);
            case NORTH, SOUTH -> new Vec3(x, z, y);
            case WEST, EAST -> new Vec3(y, z, x);
        };
    }

    public static Vec3 maskPlayerToWorld(Vec3 vec3d, Direction gravityDirection) {
        return maskPlayerToWorld(vec3d.x, vec3d.y, vec3d.z, gravityDirection);
    }

    public static AABB boxWorldToPlayer(AABB box, Direction gravityDirection) {
        return new AABB(
                vecWorldToPlayer(box.minX, box.minY, box.minZ, gravityDirection),
                vecWorldToPlayer(box.maxX, box.maxY, box.maxZ, gravityDirection)
        );
    }

    public static AABB boxPlayerToWorld(AABB box, Direction gravityDirection) {
        return new AABB(
                vecPlayerToWorld(box.minX, box.minY, box.minZ, gravityDirection),
                vecPlayerToWorld(box.maxX, box.maxY, box.maxZ, gravityDirection)
        );
    }

    public static Vec2 rotWorldToPlayer(float yaw, float pitch, Direction gravityDirection) {
        Vec3 vec3d = vecWorldToPlayer(rotToVec(yaw, pitch), gravityDirection);
        return vecToRot(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec2 rotWorldToPlayer(Vec2 vec2f, Direction gravityDirection) {
        return rotWorldToPlayer(vec2f.x, vec2f.y, gravityDirection);
    }

    public static Vec2 rotPlayerToWorld(float yaw, float pitch, Direction gravityDirection) {
        Vec3 vec3d = vecPlayerToWorld(rotToVec(yaw, pitch), gravityDirection);
        return vecToRot(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec2 rotPlayerToWorld(Vec2 vec2f, Direction gravityDirection) {
        return rotPlayerToWorld(vec2f.x, vec2f.y, gravityDirection);
    }

    public static Vec3 rotToVec(float yaw, float pitch) {
        double radPitch = pitch * 0.017453292;
        double radNegYaw = -yaw * 0.017453292;
        double cosNegYaw = Math.cos(radNegYaw);
        double sinNegYaw = Math.sin(radNegYaw);
        double cosPitch = Math.cos(radPitch);
        double sinPitch = Math.sin(radPitch);
        return new Vec3(sinNegYaw * cosPitch, -sinPitch, cosNegYaw * cosPitch);
    }

    public static Vec2 vecToRot(double x, double y, double z) {
        double sinPitch = -y;
        double radPitch = Math.asin(sinPitch);
        double cosPitch = Math.cos(radPitch);
        double sinNegYaw = x / cosPitch;
        double cosNegYaw = Mth.clamp(z / cosPitch, -1.0, 1.0);
        double radNegYaw = Math.acos(cosNegYaw);

        if (sinNegYaw < 0.0) {
            radNegYaw = Math.PI * 2.0 - radNegYaw;
        }

        return new Vec2(Mth.wrapDegrees((float)(-radNegYaw * (180.0 / Math.PI))),
                (float)(radPitch * (180.0 / Math.PI)));
    }

    public static Vec2 vecToRot(Vec3 vec3d) {
        return vecToRot(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Quaternionf getWorldRotationQuaternion(Direction gravityDirection) {
        return new Quaternionf(WORLD_ROTATION_QUATERNIONS[gravityDirection.get3DDataValue()]);
    }

    public static Quaternionf getCameraRotationQuaternion(Direction gravityDirection) {
        return new Quaternionf(ENTITY_ROTATION_QUATERNIONS[gravityDirection.get3DDataValue()]);
    }

    public static Quaternionf getRotationBetween(Direction d1, Direction d2) {
        Vec3 start = new Vec3(d1.getNormal().getX(), d1.getNormal().getY(), d1.getNormal().getZ());
        Vec3 end = new Vec3(d2.getNormal().getX(), d2.getNormal().getY(), d2.getNormal().getZ());

        if (d1.getOpposite() == d2) {
            return new Quaternionf().fromAxisAngleDeg(new Vector3f(0.0F, 0.0F, -1.0F), 180.0F);
        } else {
            return QuaternionUtil.getRotationBetween(start, end);
        }
    }

    public static Quaternionf interpolate(Quaternionf startGravityRotation, Quaternionf endGravityRotation, float progress) {
        return new Quaternionf(startGravityRotation).slerp(endGravityRotation, progress);
    }

    static {
        for (Direction gravityDirection : Direction.values()) {
            DIR_WORLD_TO_PLAYER[gravityDirection.get3DDataValue()] = new Direction[6];
            for (Direction direction : Direction.values()) {
                Vec3 directionVector = new Vec3(direction.getNormal().getX(), direction.getNormal().getY(), direction.getNormal().getZ());
                directionVector = vecWorldToPlayer(directionVector, gravityDirection);
                DIR_WORLD_TO_PLAYER[gravityDirection.get3DDataValue()][direction.get3DDataValue()] =
                        Direction.getNearest(directionVector.x, directionVector.y, directionVector.z);
            }
        }

        for (Direction gravityDirection : Direction.values()) {
            DIR_PLAYER_TO_WORLD[gravityDirection.get3DDataValue()] = new Direction[6];
            for (Direction direction : Direction.values()) {
                Vec3 directionVector = new Vec3(direction.getNormal().getX(), direction.getNormal().getY(), direction.getNormal().getZ());
                directionVector = vecPlayerToWorld(directionVector, gravityDirection);
                DIR_PLAYER_TO_WORLD[gravityDirection.get3DDataValue()][direction.get3DDataValue()] =
                        Direction.getNearest(directionVector.x, directionVector.y, directionVector.z);
            }
        }

        WORLD_ROTATION_QUATERNIONS[0] = new Quaternionf();
        WORLD_ROTATION_QUATERNIONS[1] = new Quaternionf().rotationX((float)Math.PI);
        WORLD_ROTATION_QUATERNIONS[2] = new Quaternionf().rotationX((float)(-Math.PI / 2.0));
        WORLD_ROTATION_QUATERNIONS[3] = new Quaternionf().rotationX((float)(-Math.PI / 2.0));
        WORLD_ROTATION_QUATERNIONS[3].mul(new Quaternionf().rotationY((float)Math.PI));
        WORLD_ROTATION_QUATERNIONS[4] = new Quaternionf().rotationX((float)(-Math.PI / 2.0));
        WORLD_ROTATION_QUATERNIONS[4].mul(new Quaternionf().rotationY((float)(-Math.PI / 2.0)));
        WORLD_ROTATION_QUATERNIONS[5] = new Quaternionf().rotationX((float)(-Math.PI / 2.0));
        WORLD_ROTATION_QUATERNIONS[5].mul(new Quaternionf().rotationY((float)(Math.PI * 3.0 / 2.0)));

        for (int i = 0; i < 6; ++i) {
            ENTITY_ROTATION_QUATERNIONS[i] = new Quaternionf(WORLD_ROTATION_QUATERNIONS[i]).conjugate();
        }
    }
}