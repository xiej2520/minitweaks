package shulkerclone.mixins;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import shulkerclone.ShulkerCloneSettings;
import shulkerclone.ShulkerEntityColorHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import shulkerclone.ShulkerLidCollisions;
import shulkerclone.WorldMixinAccess;

@Mixin(ShulkerEntity.class)
public abstract class ShulkerEntity_BulletCloneMixin extends GolemEntity {
    protected ShulkerEntity_BulletCloneMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    // isClosed() in 1.16+
    @Shadow
    protected abstract boolean method_7124();

    // tryTeleport in 1.16+
    // @Shadow
    // protected abstract boolean method_7127();

    // shulker cloning from 20w45a
    @Inject(method = "damage", at = @At("RETURN"), cancellable = true)
    private void cloneShulker(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // check if return value is true and rule is enabled
        if(cir.getReturnValue() && ShulkerCloneSettings.shulkerCloning && source.isProjectile()) {
            Entity sourceEntity = source.getSource();
            if(sourceEntity != null && sourceEntity.getType() == EntityType.SHULKER_BULLET) {
                this.spawnClone();
            }
        }
    }

    private void spawnClone() {
        Vec3d pos = this.getPos();
        Box box = this.getBoundingBox();
        // if shulker is open, try to teleport, and spawn new shulker if successful
        if(!this.method_7124() && this.method_7127()) {
            // get odds of successfully cloning based on how many shulkers are within 8 blocks (17x17x17 area)
            int shulkersCount = this.world.getEntities(EntityType.SHULKER, box.expand(8.0D), Entity::isAlive).size();
            float cloneOdds = (float) (shulkersCount - 1) / 5.0F;
            if(cloneOdds <= this.world.random.nextFloat()) {
                ShulkerEntity shulkerEntity = EntityType.SHULKER.create(this.world);
                // replace with this.getColor() in 1.17
                DyeColor dyeColor = ShulkerEntityColorHelper.getColor((ShulkerEntity) (Object) this);
                if(dyeColor != null) {
                    // replace with shulkerEntity.setColor(dyeColor) in 1.17
                    ShulkerEntityColorHelper.setColor(shulkerEntity, dyeColor);
                }


                shulkerEntity.positAfterTeleport(pos.x, pos.y, pos.z);
                this.world.spawnEntity(shulkerEntity);
            }
        }
    }

    // bad hack but it works, I guess
    @Override
    public void copyFrom(Entity original) {
        super.copyFrom(original);

        if(ShulkerCloneSettings.shulkerPortalFix) {
            // reset attached face and attached block to default values
            // fixes MC-139265, MC-168900 (probably)
            this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedFaceTrackerKey(), Direction.DOWN);
            this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedBlockTrackerKey(), Optional.empty());
        }
    }


    // 1.16 shulker MC-159773 fixes
    @Nullable
    protected Direction findAttachSide(BlockPos pos) {
        Direction[] var2 = Direction.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction direction = var2[var4];
            if (this.canStay(pos, direction)) {
                return direction;
            }
        }

        return null;
    }
    private boolean canStay(BlockPos pos, Direction attachSide) {
        WorldMixinAccess wm = (WorldMixinAccess) world;
        return wm.isDirectionSolid(pos.offset(attachSide), this, attachSide.getOpposite()) && wm.isSpaceEmpty(this, ShulkerLidCollisions.getLidCollisionBox(pos, attachSide.getOpposite()));
    }

    /**
     * @author xiej2520
     * @reason idk how to inject the method properly, someone help
     * bugfix for MC-159773
     */
    @Overwrite
    public boolean method_7127() {
        if (!this.isAiDisabled() && this.isAlive()) {
            BlockPos blockPos = this.getBlockPos();
            WorldMixinAccess wm = (WorldMixinAccess) this.world;

            for(int i = 0; i < 5; ++i) {
                BlockPos blockPos2 = blockPos.add(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
                if (blockPos2.getY() > 0 && this.world.isAir(blockPos2) && this.world.getWorldBorder().contains(blockPos2) && wm.isSpaceEmpty(this, new Box(blockPos2))) {
                    Direction direction = this.findAttachSide(blockPos2);
                    if (direction != null) {
                        this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedFaceTrackerKey(), direction);
                        this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                        this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedBlockTrackerKey(), Optional.of(blockPos2));
                        this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getPeekAmountTrackerKey(), (byte)0);
                        this.setTarget((LivingEntity)null);
                        return true;
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public Direction getAttachedFace() {
        return (Direction) this.dataTracker.get(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedFaceTrackerKey());
    }
    public int getPeekAmount() {
        return (Byte) this.dataTracker.get(ShulkerEntity_TrackerKeysAccessorMixin.getPeekAmountTrackerKey());
    }
    @Shadow
    private float field_7339; // prevOpenProgress
    @Shadow
    private float field_7337; // openProgress
    @Shadow
    private BlockPos field_7345 = null; // prevAttachedBlock
    @Shadow
    private int field_7340; // teleportLerpTimer

    /**
     * @author xiej2520
     * @reason idk how to inject the method properly, someone help
     * bugfix for MC-159773
     */
    @Overwrite
    public void tick() {
        super.tick();
        BlockPos blockPos = (BlockPos)((Optional)this.dataTracker.get(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedBlockTrackerKey())).orElse((Object)null);
        if (blockPos == null && !this.world.isClient) {
            blockPos = this.getBlockPos();
            this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedBlockTrackerKey(), Optional.of(blockPos));
        }

        float g;
        if (this.hasVehicle()) {
            blockPos = null;
            g = this.getVehicle().yaw;
            this.yaw = g;
            this.bodyYaw = g;
            this.prevBodyYaw = g;
            this.field_7340 = 0;
        } else if (!this.world.isClient) {
            BlockState blockState = this.world.getBlockState(blockPos);
            Direction direction2;
            if (!blockState.isAir()) {
                if (blockState.getBlock() == Blocks.MOVING_PISTON) { // blockState.isOf(Blocks.MOVING_PISTON)
                    direction2 = (Direction)blockState.get(PistonBlock.FACING);
                    if (this.world.isAir(blockPos.offset(direction2))) {
                        blockPos = blockPos.offset(direction2);
                        this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedBlockTrackerKey(), Optional.of(blockPos));
                    } else {
                        this.method_7127();
                    }
                } else if (blockState.getBlock() == Blocks.PISTON_HEAD) { // blockState.isOf(Blocks.PISTON_HEAD)
                    direction2 = (Direction)blockState.get(PistonHeadBlock.FACING);
                    if (this.world.isAir(blockPos.offset(direction2))) {
                        blockPos = blockPos.offset(direction2);
                        this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedBlockTrackerKey(), Optional.of(blockPos));
                    } else {
                        this.method_7127();
                    }
                } else {
                    this.method_7127();
                }
            }

            direction2 = this.getAttachedFace();
            if (!this.canStay(blockPos, direction2)) {
                Direction direction4 = this.findAttachSide(blockPos);
                if (direction4 != null) {
                    this.dataTracker.set(ShulkerEntity_TrackerKeysAccessorMixin.getAttachedFaceTrackerKey(), direction4);
                } else {
                    this.method_7127();
                }
            }
        }

        g = (float)this.getPeekAmount() * 0.01F;
        this.field_7339 = this.field_7337;
        if (this.field_7337 > g) {
            this.field_7337 = MathHelper.clamp(this.field_7337 - 0.05F, g, 1.0F);
        } else if (this.field_7337 < g) {
            this.field_7337 = MathHelper.clamp(this.field_7337 + 0.05F, 0.0F, g);
        }

        if (blockPos != null) {
            if (this.world.isClient) {
                if (this.field_7340 > 0 && this.field_7345 != null) {
                    --this.field_7340;
                } else {
                    this.field_7345 = blockPos;
                }
            }

            this.resetPosition((double)blockPos.getX() + 0.5D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5D);
            double d = 0.5D - (double)MathHelper.sin((0.5F + this.field_7337) * 3.1415927F) * 0.5D;
            double e = 0.5D - (double)MathHelper.sin((0.5F + this.field_7339) * 3.1415927F) * 0.5D;
            Direction direction5 = this.getAttachedFace().getOpposite();
            this.setBoundingBox((new Box(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D, this.getX() + 0.5D, this.getY() + 1.0D, this.getZ() + 0.5D)).stretch((double)direction5.getOffsetX() * d, (double)direction5.getOffsetY() * d, (double)direction5.getOffsetZ() * d));
            double h = d - e;
            if (h > 0.0D) {
                List<Entity> list = this.world.getEntities(this, this.getBoundingBox()); // getOtherEntities in 1.16+
                if (!list.isEmpty()) {
                    Iterator var11 = list.iterator();

                    while(var11.hasNext()) {
                        Entity entity = (Entity)var11.next();
                        if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
                            entity.move(MovementType.SHULKER, new Vec3d(h * (double)direction5.getOffsetX(), h * (double)direction5.getOffsetY(), h * (double)direction5.getOffsetZ()));
                        }
                    }
                }
            }
        }

    }
}
