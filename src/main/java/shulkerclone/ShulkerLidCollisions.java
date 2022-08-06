package shulkerclone;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerLidCollisions {
    // 1.16.5 ShulkerLidCollisions class, with manyrandomthings' bugfix
    public static Box getLidCollisionBox(BlockPos pos, Direction direction) {
        // fixes MC-183884 by making the box that checks for entities inside very slightly smaller, so it doesn't get neighbor shulkers
        return VoxelShapes.fullCube().getBoundingBox()
                .stretch((double)(0.5F * (float)direction.getOffsetX()), (double)(0.5F * (float)direction.getOffsetY()), (double)(0.5F * (float)direction.getOffsetZ()))
                .shrink((double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ())
                .offset(pos.offset(direction))
                .contract(1.0E-6D);
    }
}
