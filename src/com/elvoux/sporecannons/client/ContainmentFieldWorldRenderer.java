/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$Stage
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.client.ContainmentProjectorRenderer;
import com.elvoux.sporecannons.munition.ContainmentProjectorRenderRegistry;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid="sporecannons", value={Dist.CLIENT})
public final class ContainmentFieldWorldRenderer {
    private static final ContainmentProjectorRenderer RENDERER = new ContainmentProjectorRenderer(null);

    private ContainmentFieldWorldRenderer() {
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poses = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        boolean rendered = false;
        for (CryogenicContainmentProjectorBlockEntity projector : ContainmentProjectorRenderRegistry.snapshot()) {
            if (projector.isRemoved() || projector.getLevel() != minecraft.level) continue;
            poses.pushPose();
            poses.translate((double)projector.getBlockPos().getX() - camera.x, (double)projector.getBlockPos().getY() - camera.y, (double)projector.getBlockPos().getZ() - camera.z);
            RENDERER.render(projector, partialTick, poses, (MultiBufferSource)buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);
            poses.popPose();
            rendered = true;
        }
        if (rendered) {
            buffers.endBatch();
        }
    }
}

