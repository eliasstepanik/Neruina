package com.bawnorton.neruina.mixin.catchers;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.util.annotation.ModLoaderMixin;
import com.bawnorton.neruina.platform.ModLoader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GameRenderer.class, priority = 900)
@ModLoaderMixin(ModLoader.NEOFORGE)
public abstract class GameRendererMixin {
	private static int neruina$renderErrorCount = 0;
	private static long neruina$lastRenderErrorTime = 0L;
	private static final int MAX_ERRORS_BEFORE_ESCALATE = 5;
	private static final long ERROR_WINDOW_MS = 10_000L;

	@WrapOperation(
		method = "render",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/renderer/RenderBuffers;Lnet/minecraft/client/Camera;FJZLnet/minecraft/client/renderer/culling/Frustum;)V"
		)
	)
	private void catchRenderingException(GameRenderer instance, RenderBuffers renderBuffers, Camera camera, float partialTick, long finishTimeNano, boolean renderBlockOutline, Frustum frustum, Operation<Void> original) {
		try {
			original.call(instance, renderBuffers, camera, partialTick, finishTimeNano, renderBlockOutline, frustum);
		} catch (Exception e) {
			long now = System.currentTimeMillis();
			if (now - neruina$lastRenderErrorTime > ERROR_WINDOW_MS) {
				neruina$renderErrorCount = 0;
			}
			neruina$lastRenderErrorTime = now;
			neruina$renderErrorCount++;
			if (neruina$renderErrorCount == 1) {
				Neruina.LOGGER.warn("Neruina caught a rendering exception in GameRenderer.renderLevel, skipping frame", e);
			} else if (neruina$renderErrorCount % MAX_ERRORS_BEFORE_ESCALATE == 0) {
				Neruina.LOGGER.error("Neruina: rendering exception is recurring ({} times in {}s) — something is persistently broken. Last error:", neruina$renderErrorCount, ERROR_WINDOW_MS / 1000, e);
			}
		}
	}
}
