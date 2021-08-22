package minitweaks.dispenser.behaviors;

import java.util.List;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class WaterBucketDispenserBehavior extends FallibleItemDispenserBehavior {
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(true);

        ServerWorld serverWorld = pointer.getWorld();

        // get block in front of dispenser
        BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        // get all bucketable mobs in front of dispenser
        List<LivingEntity> list = serverWorld.getEntitiesByClass(LivingEntity.class, new Box(blockPos), EntityPredicates.VALID_LIVING_ENTITY.and((livingEntity) -> {
            return livingEntity instanceof Bucketable;
        }));

        if(!list.isEmpty()) {
            // get random bucketable mob in list
            LivingEntity livingEntity = list.get(serverWorld.random.nextInt(list.size()));
            Bucketable bucketable = (Bucketable) livingEntity;

            // play bucket sound, get bucket item
            livingEntity.playSound(bucketable.getBucketedSound(), 1.0F, 1.0F);
            ItemStack mobBucketItem = bucketable.getBucketItem();
            bucketable.copyDataToStack(mobBucketItem);

            // remove bucketed mob
            livingEntity.discard();

            // return bucket item
            return mobBucketItem;
        }

        // fail to dispense
        this.setSuccess(false);
        return stack;
    }
}