package shulkerclone.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import shulkerclone.WorldMixinAccess;

import java.util.Collections;
import java.util.Set;

@Mixin(World.class)
public abstract class WorldMixin implements WorldMixinAccess, IWorld, AutoCloseable {

    @Shadow public abstract World getWorld();

    private static boolean isOutOfBuildLimitVertically(BlockPos pos) {
        return isOutOfBuildLimitVertically(pos.getY());
    }
    private static boolean isOutOfBuildLimitVertically(int y) {
        return y < 0 || y >= 256;
    }

    @Override
    public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
        if (isOutOfBuildLimitVertically(pos)) {
            return false;
        } else {
            Chunk chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
            return chunk == null ? false : hasSolidTopSurface(chunk.getBlockState(pos), this.getWorld(), pos, entity, direction);
        }
    }

    @Override
    public final boolean hasSolidTopSurface(BlockState block, BlockView world, BlockPos pos, Entity entity, Direction direction) {
        return Block.isFaceFullSquare(block.getCollisionShape(world, pos, EntityContext.of(entity)), direction);
    }

    @Override
    public boolean isSpaceEmpty(Entity entity, Box box) {
        return this.isSpaceEmpty(entity, box, Collections.emptySet());
    }

    @Override
    public boolean isSpaceEmpty(@Nullable Entity entity, Box box, Set<Entity> excluded) {
        return this.getCollisions(entity, box, excluded).allMatch(VoxelShape::isEmpty);
    }
}
