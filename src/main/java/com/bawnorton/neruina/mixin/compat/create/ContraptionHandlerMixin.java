package com.bawnorton.neruina.mixin.compat.create;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.util.annotation.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "com.simibubi.create.content.contraptions.ContraptionHandler", remap = false)
@ConditionalMixin(modids = {"create"}, applyIfPresent = true)
public abstract class ContraptionHandlerMixin {

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lcom/simibubi/create/content/contraptions/ContraptionCollider;collideEntities(Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;)V",
			remap = false
		),
		remap = false
	)
	private static void catchContraptionCollide(
		@Coerce Object contraptionEntity,
		Operation<Void> original
	) {
		try {
			original.call(contraptionEntity);
		} catch (Throwable e) {
			Neruina.LOGGER.warn("Neruina caught an exception in Create contraption collision, skipping this tick", e);
		}
	}
}
