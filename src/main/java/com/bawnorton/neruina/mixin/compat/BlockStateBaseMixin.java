//? if <=1.21.1 {
package com.bawnorton.neruina.mixin.compat;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.util.annotation.ConditionalMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
@ConditionalMixin(modids = "create_submarine", applyIfPresent = true)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    private void nullSafeGetDestroySpeed(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (level == null) {
            cir.setReturnValue(1.0f);
        }
    }
}
//?}
