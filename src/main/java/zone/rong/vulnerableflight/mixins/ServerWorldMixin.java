package zone.rong.vulnerableflight.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

    @Shadow @Final List<ServerPlayerEntity> players;

    @Shadow protected abstract Optional<BlockPos> getLightningRodPos(BlockPos pos2);

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
        throw new AssertionError();
    }

    /**
     * @author Rongmario
     * @reason cba injecting, redirecting in weird ugly ways
     */
    @Overwrite
    public BlockPos getSurface(BlockPos pos) {
        BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
        Optional<BlockPos> optional = this.getLightningRodPos(blockPos);
        if (optional.isPresent()) {
            return optional.get();
        }
        optional = this.players.stream()
                .filter(LivingEntity::isFallFlying)
                .map(Entity::getBlockPos)
                .filter(entityPos -> entityPos.isWithinDistance(pos, 128)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        Box box = new Box(blockPos, new BlockPos(blockPos.getX(), this.getTopY(), blockPos.getZ())).expand(3.0);
        List<LivingEntity> list = this.getEntitiesByClass(LivingEntity.class, box, entity -> entity != null && entity.isAlive() && this.isSkyVisible(entity.getBlockPos()));
        if (!list.isEmpty()) {
            return list.get(this.random.nextInt(list.size())).getBlockPos();
        }
        if (blockPos.getY() == this.getBottomY() - 1) {
            blockPos = blockPos.up(2);
        }
        return blockPos;
    }

}
