package zone.rong.vulnerableflight.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

// Least prioritized
@Mixin(value = LivingEntity.class, priority = Integer.MAX_VALUE - 1)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin() {
        super(null, null);
        throw new AssertionError();
    }

    @ModifyConstant(method = "tickFallFlying", constant = @Constant(intValue = 1, ordinal = 2))
    private int damageElytra(int originalDamage) {
        return getDamage(this.getBlockPos(), originalDamage);
    }

    private int getDamage(BlockPos pos, int originalDamage) {
        if (!this.world.isSkyVisible(pos)) {
            return originalDamage;
        }
        if (this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return originalDamage;
        }
        if (!this.world.isRaining()) {
            return originalDamage;
        }
        return this.world.hasHighHumidity(pos) ? (originalDamage * 2) + 1 : originalDamage * 2;
    }

}
