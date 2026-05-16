package com.bawnorton.neruina.mixin.catchers;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.util.annotation.ModLoaderMixin;
import com.bawnorton.neruina.platform.ModLoader;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.joml.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LevelRenderer.class, priority = 900)
@ModLoaderMixin({ModLoader.NEOFORGE, ModLoader.FABRIC, ModLoader.FORGE})
public abstract class LevelRendererMixin {
	private static int neruina$renderErrorCount = 0;
	private static long neruina$lastRenderErrorTime = 0L;
	private static final int MAX_ERRORS_BEFORE_ESCALATE = 5;
	private static final long ERROR_WINDOW_MS = 10_000L;

	@WrapMethod(method = "renderLevel", remap = false)
	private void catchRenderingException(
		DeltaTracker deltaTracker,
		boolean renderBlockOutline,
		Camera camera,
		GameRenderer gameRenderer,
		LightTexture lightTexture,
		Matrix4f frustumMatrix,
		Matrix4f projectionMatrix,
		Operation<Void> original
	) {
		try {
			original.call(deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture, frustumMatrix, projectionMatrix);
		} catch (Exception e) {
			long now = System.currentTimeMillis();
			if (now - neruina$lastRenderErrorTime > ERROR_WINDOW_MS) {
				neruina$renderErrorCount = 0;
			}
			neruina$lastRenderErrorTime = now;
			neruina$renderErrorCount++;
			if (neruina$renderErrorCount == 1) {
				Neruina.LOGGER.warn("Neruina caught a rendering exception in LevelRenderer.renderLevel, skipping frame", e);
			} else if (neruina$renderErrorCount % MAX_ERRORS_BEFORE_ESCALATE == 0) {
				Neruina.LOGGER.error("Neruina: rendering exception is recurring ({} times in {}s) — something is persistently broken. Last error:", neruina$renderErrorCount, ERROR_WINDOW_MS / 1000, e);
			}
		}
	}
}
