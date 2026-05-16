package com.bawnorton.neruina.mixin.catchers;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.util.annotation.ModLoaderMixin;
import com.bawnorton.neruina.platform.ModLoader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
@ModLoaderMixin(ModLoader.NEOFORGE)
public abstract class GameRendererMixin {
	private static long lastErrorTime = 0;
	private static int errorCount = 0;
	private static final long ERROR_COOLDOWN_MS = 10000; // 10 seconds
	private static final int ERROR_THRESHOLD = 5;

	@WrapOperation(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/renderer/culling/Frustum;)V"
		)
	)
	private void catchRenderingException(LevelRenderer instance, Object poseStack, float partialTick, long finishTimeNano, boolean renderBlockOutline, Object frustum, Operation<Void> original) {
		try {
			original.call(instance, poseStack, partialTick, finishTimeNano, renderBlockOutline, frustum);
		} catch (Exception e) {
			handleRenderingException(e);
		}
	}

	private static void handleRenderingException(Exception e) {
		long currentTime = System.currentTimeMillis();
		
		// Reset error count if cooldown has passed
		if (currentTime - lastErrorTime > ERROR_COOLDOWN_MS) {
			errorCount = 0;
		}
		
		errorCount++;
		lastErrorTime = currentTime;
		
		if (errorCount > ERROR_THRESHOLD) {
			Neruina.LOGGER.error("Neruina caught multiple rendering exceptions in a short period, see below for cause", e);
		} else {
			Neruina.LOGGER.warn("Neruina caught a rendering exception, see below for cause", e);
		}
	}
}
