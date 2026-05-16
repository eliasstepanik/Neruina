//? if <=1.21.1 {
package com.bawnorton.neruina.mixin.compat;

import com.bawnorton.neruina.util.annotation.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BlockBehaviour.BlockStateBase.class, priority = 2000)
@ConditionalMixin(modids = "create_submarine", applyIfPresent = true)
public abstract class BlockStateBaseMixin {
    @WrapMethod(method = "getDestroySpeed")
    private float catchNullLevelGetDestroySpeed(BlockGetter level, BlockPos pos, Operation<Float> original) {
        if (level == null || pos == null) {
            return 1.0f;
        }
        try {
            return original.call(level, pos);
        } catch (Exception e) {
            return 1.0f;
        }
    }
}
//?}
