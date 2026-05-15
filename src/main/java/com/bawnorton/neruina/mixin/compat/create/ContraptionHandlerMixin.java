package com.bawnorton.neruina.mixin.compat.create;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.util.annotation.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.simibubi.create.content.contraptions.ContraptionCollider", remap = false)
@ConditionalMixin(modids = {"create"}, applyIfPresent = true)
public abstract class ContraptionHandlerMixin {

	@WrapOperation(
		method = "collideEntities",
		at = @At(
			value = "INVOKE",
			target = "Lcom/simibubi/create/foundation/collision/ContinuousOBBCollider;collideMany(Lcom/simibubi/create/foundation/collision/CollisionList;Lcom/simibubi/create/foundation/collision/CollisionList;Lcom/simibubi/create/foundation/collision/OrientedBB;Lnet/minecraft/world/phys/Vec3;FZ)Lcom/simibubi/create/foundation/collision/ContinuousOBBCollider$CollisionResponse;",
			remap = false
		),
		remap = false
	)
	private static Object catchContraptionCollide(
		Object shapes,
		Object entities,
		Object entityBB,
		Object deltaMovement,
		float maxUpStep,
		boolean hasVerticalRotation,
		Operation<Object> original
	) {
		try {
			return original.call(shapes, entities, entityBB, deltaMovement, maxUpStep, hasVerticalRotation);
		} catch (Throwable e) {
			Neruina.LOGGER.warn("Neruina caught an exception in Create contraption collision, skipping this tick", e);
			return null;
		}
	}
}
