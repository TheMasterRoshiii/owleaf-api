package me.master.owleaf.mixin;

import me.master.owleaf.api.OwleafGravityAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.world.entity.animal.Wolf$BegGoal")
class WolfBegGoalMixin {

    @Shadow
    private Wolf wolf;

    @Shadow
    private Player player;

    @Overwrite
    public boolean canUse() {
        Player player = this.wolf.level().getNearestPlayer(this.wolf, 10.0D);
        if (player == null) {
            return false;
        }

        this.player = player;
        Direction gravityDirection = OwleafGravityAPI.getGravityDirection(player);

        if (gravityDirection == Direction.DOWN) {
            return player.getEyeY() > this.wolf.getEyeY() && this.wolf.distanceToSqr(player) < 81.0D && !this.wolf.isInSittingPose() && this.wolf.isTame();
        } else {
            return player.getEyePosition().y > this.wolf.getEyeY() &&
                    this.wolf.distanceToSqr(player.getEyePosition().x, player.getEyePosition().y, player.getEyePosition().z) < 81.0D &&
                    !this.wolf.isInSittingPose() && this.wolf.isTame();
        }
    }
}
