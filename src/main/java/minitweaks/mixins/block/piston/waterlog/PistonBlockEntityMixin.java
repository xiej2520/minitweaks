package minitweaks.mixins.block.piston.waterlog;

import minitweaks.MiniTweaksSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;contains(Lnet/minecraft/state/property/Property;)Z"))
    private static boolean checkWaterloggedState(BlockState blockState, Property<Boolean> property) {
        return !MiniTweaksSettings.moveableWaterloggedBlocks && blockState.contains(property);
    }
}