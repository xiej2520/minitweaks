package shulkerclone;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface WorldMixinAccess {
    // 1.16+ World methods implemented in WorldMixin and exposed to ShulkerEntity_BulletCloneMixin

    boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction);
    boolean hasSolidTopSurface(BlockState block, BlockView world, BlockPos pos, Entity entity, Direction direction);
    boolean isSpaceEmpty(Entity entity, Box box);
    boolean isSpaceEmpty(@Nullable Entity entity, Box box, Set<Entity> excluded);
}
