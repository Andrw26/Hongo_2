/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.elvoux.sporecannons.client;

import com.elvoux.sporecannons.ClientConfig;
import com.elvoux.sporecannons.SporeCannons;
import com.elvoux.sporecannons.munition.ContainmentFieldMode;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlock;
import com.elvoux.sporecannons.munition.CryogenicContainmentProjectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public final class ContainmentProjectorRenderer
implements BlockEntityRenderer<CryogenicContainmentProjectorBlockEntity> {
    private static long budgetFrame = Long.MIN_VALUE;
    private static int detailedFields;
    private static final ResourceLocation FIELD_TEXTURE;

    public ContainmentProjectorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(CryogenicContainmentProjectorBlockEntity projector, float partialTick, PoseStack poses, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        boolean photosensitive;
        boolean detailed;
        double maxDistance;
        if (!((Boolean)projector.getBlockState().getValue((Property)CryogenicContainmentProjectorBlock.ACTIVE)).booleanValue()) {
            return;
        }
        ClientConfig.ContainmentFieldEffects quality = (ClientConfig.ContainmentFieldEffects)((Object)ClientConfig.CONTAINMENT_FIELD_EFFECTS.get());
        if (quality == ClientConfig.ContainmentFieldEffects.OFF || projector.getLevel() == null) {
            return;
        }
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 center = Vec3.atCenterOf((Vec3i)projector.getBlockPos()).add(0.0, 0.2, 0.0);
        double distanceSquared = camera.distanceToSqr(center);
        double d = quality == ClientConfig.ContainmentFieldEffects.LOW ? 72.0 : (maxDistance = quality == ClientConfig.ContainmentFieldEffects.NORMAL ? 112.0 : 144.0);
        if (distanceSquared > maxDistance * maxDistance) {
            return;
        }
        long gameTime = projector.getLevel().getGameTime();
        long frame = gameTime * 1024L + (long)(partialTick * 1024.0f);
        if (frame != budgetFrame) {
            budgetFrame = frame;
            detailedFields = 0;
        }
        int detailBudget = quality == ClientConfig.ContainmentFieldEffects.LOW ? 4 : (quality == ClientConfig.ContainmentFieldEffects.NORMAL ? 8 : 12);
        double distance = Math.sqrt(distanceSquared);
        double fieldExtent = projector.fieldMode() == ContainmentFieldMode.RADIAL ? projector.currentFieldRadius() : Math.max(projector.wallHalfWidth(), (double)projector.wallHeight() / 2.0);
        double detailFadeStart = fieldExtent + 8.0;
        double detailFadeEnd = fieldExtent + 32.0;
        float detailFade = (float)Mth.clamp((double)((detailFadeEnd - distance) / (detailFadeEnd - detailFadeStart)), (double)0.0, (double)1.0);
        boolean bl = detailed = detailFade > 0.001f && detailedFields++ < detailBudget;
        if (!detailed) {
            detailFade = 0.0f;
        }
        int period = (photosensitive = ((Boolean)ClientConfig.PHOTOSENSITIVE_MODE.get()).booleanValue()) ? 180 : (quality == ClientConfig.ContainmentFieldEffects.HIGH ? 90 : 110);
        int duration = photosensitive ? 10 : 20;
        int offset = Math.floorMod(projector.getBlockPos().hashCode(), period);
        float pulseAge = (float)Math.floorMod((int)gameTime + offset, period) + partialTick;
        float pulse = pulseAge < (float)duration ? Mth.sin((float)(pulseAge / (float)duration * (float)Math.PI)) : 0.0f;
        float breathing = 0.5f + 0.5f * Mth.sin((float)(((float)gameTime + partialTick) * 0.035f + (float)offset));
        poses.pushPose();
        poses.translate(0.5, 0.7, 0.5);
        VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        float alpha = pulse * (photosensitive ? 0.1f : (detailed ? 0.3f : 0.19f));
        if (pulse > 0.001f) {
            if (projector.fieldMode() == ContainmentFieldMode.RADIAL) {
                ContainmentProjectorRenderer.renderRadialPulse(poses, lines, projector.currentFieldRadius(), detailed ? ContainmentProjectorRenderer.segmentCount(quality) : 12, alpha);
            } else if (photosensitive) {
                ContainmentProjectorRenderer.renderWallPulse(poses, lines, projector, detailed, alpha);
            }
        }
        if (detailed && !photosensitive) {
            ContainmentProjectorRenderer.renderAmbientSnow(poses, lines, projector, gameTime, partialTick, quality == ClientConfig.ContainmentFieldEffects.HIGH ? 22 : (quality == ClientConfig.ContainmentFieldEffects.NORMAL ? 14 : 8));
        }
        if (!photosensitive) {
            int layers;
            VertexConsumer surface = buffers.getBuffer(RenderType.entityTranslucent((ResourceLocation)FIELD_TEXTURE));
            int n = layers = detailed && quality != ClientConfig.ContainmentFieldEffects.LOW ? 2 : 1;
            int horizontalSegments = quality == ClientConfig.ContainmentFieldEffects.HIGH ? 16 : (quality == ClientConfig.ContainmentFieldEffects.NORMAL ? 14 : 10);
            float fieldStrength = 0.5f + breathing * 0.06f + pulse * 0.45f;
            if (projector.fieldMode() == ContainmentFieldMode.RADIAL) {
                ContainmentProjectorRenderer.renderSphereLayers(poses, surface, projector.currentFieldRadius(), layers, horizontalSegments, Math.max(4, horizontalSegments / 2), fieldStrength, (float)gameTime + partialTick, detailFade);
            } else {
                ContainmentProjectorRenderer.renderWallLayers(poses, surface, projector, layers, Math.max(4, horizontalSegments / 2), Math.max(3, horizontalSegments / 4), fieldStrength, (float)gameTime + partialTick, detailFade);
            }
            ContainmentProjectorRenderer.renderEnergyBeam(poses, surface, projector, (float)gameTime + partialTick, detailed ? 0.48f : 0.34f, pulse);
        }
        poses.popPose();
    }

    private static int segmentCount(ClientConfig.ContainmentFieldEffects quality) {
        return quality == ClientConfig.ContainmentFieldEffects.HIGH ? 32 : (quality == ClientConfig.ContainmentFieldEffects.NORMAL ? 24 : 16);
    }

    private static void renderSphereLayers(PoseStack poses, VertexConsumer consumer, double radius, int layers, int longitudeSegments, int latitudeSegments, float strength, double time, float detailFade) {
        double circumferenceRepeats = Math.max(3.0, radius * 6.2831854820251465 / 20.0);
        double verticalRepeats = Math.max(2.0, radius * Math.PI / 20.0);
        for (int layer = 0; layer < layers; ++layer) {
            double layerOffset = layer == 0 ? 0.0 : 0.075;
            double shellRadius = radius + layerOffset;
            float layerAlpha = strength * (layer == 0 ? 0.24f : 0.1f * detailFade);
            float direction = (layer & 1) == 0 ? 1.0f : -1.0f;
            float uOffset = (float)layer * 0.19f + direction * (float)(time * 0.0024);
            float vOffset = (float)layer * 0.11f + (float)(time * (8.0E-4 + (double)layer * 2.5E-4));
            for (int lat = 0; lat < latitudeSegments; ++lat) {
                double v0 = (double)lat / (double)latitudeSegments;
                double v1 = (double)(lat + 1) / (double)latitudeSegments;
                double phi0 = -1.5707963267948966 + Math.PI * v0;
                double phi1 = -1.5707963267948966 + Math.PI * v1;
                for (int lon = 0; lon < longitudeSegments; ++lon) {
                    double u0 = (double)lon / (double)longitudeSegments;
                    double u1 = (double)(lon + 1) / (double)longitudeSegments;
                    double theta0 = 6.2831854820251465 * u0;
                    double theta1 = 6.2831854820251465 * u1;
                    ContainmentProjectorRenderer.surfaceQuad(poses, consumer, ContainmentProjectorRenderer.spherePoint(shellRadius, phi0, theta0), ContainmentProjectorRenderer.spherePoint(shellRadius, phi0, theta1), ContainmentProjectorRenderer.spherePoint(shellRadius, phi1, theta1), ContainmentProjectorRenderer.spherePoint(shellRadius, phi1, theta0), (float)(u0 * circumferenceRepeats) + uOffset, (float)(v0 * verticalRepeats) + vOffset, (float)(u1 * circumferenceRepeats) + uOffset, (float)(v1 * verticalRepeats) + vOffset, layerAlpha);
                }
            }
        }
    }

    private static Vec3 spherePoint(double radius, double latitude, double longitude) {
        double horizontal = Math.cos(latitude) * radius;
        return new Vec3(Math.cos(longitude) * horizontal, Math.sin(latitude) * radius, Math.sin(longitude) * horizontal);
    }

    private static void renderWallLayers(PoseStack poses, VertexConsumer consumer, CryogenicContainmentProjectorBlockEntity projector, int layers, int widthSegments, int heightSegments, float strength, double time, float detailFade) {
        double halfWidth = projector.wallHalfWidth() + 0.5;
        double halfHeight = (double)projector.wallHeight() / 2.0;
        boolean normalZ = projector.fieldFacing().getAxis() == Direction.Axis.Z;
        for (int layer = 0; layer < layers; ++layer) {
            double depth = layer == 0 ? 0.0 : 0.075;
            float layerAlpha = strength * (layer == 0 ? 0.25f : 0.1f * detailFade);
            float direction = (layer & 1) == 0 ? 1.0f : -1.0f;
            float uOffset = (float)layer * 0.23f + direction * (float)(time * 0.003);
            float vOffset = (float)layer * 0.13f + (float)(time * (0.001 + (double)layer * 3.0E-4));
            for (int x = 0; x < widthSegments; ++x) {
                double lateral0 = Mth.lerp((double)((double)x / (double)widthSegments), (double)(-halfWidth), (double)halfWidth);
                double lateral1 = Mth.lerp((double)((double)(x + 1) / (double)widthSegments), (double)(-halfWidth), (double)halfWidth);
                for (int y = 0; y < heightSegments; ++y) {
                    double vertical0 = Mth.lerp((double)((double)y / (double)heightSegments), (double)(-halfHeight), (double)halfHeight);
                    double vertical1 = Mth.lerp((double)((double)(y + 1) / (double)heightSegments), (double)(-halfHeight), (double)halfHeight);
                    ContainmentProjectorRenderer.surfaceQuad(poses, consumer, ContainmentProjectorRenderer.wallPoint(normalZ, lateral0, vertical0, depth), ContainmentProjectorRenderer.wallPoint(normalZ, lateral1, vertical0, depth), ContainmentProjectorRenderer.wallPoint(normalZ, lateral1, vertical1, depth), ContainmentProjectorRenderer.wallPoint(normalZ, lateral0, vertical1, depth), (float)((lateral0 + halfWidth) / 12.0) + uOffset, (float)((vertical0 + halfHeight) / 12.0) + vOffset, (float)((lateral1 + halfWidth) / 12.0) + uOffset, (float)((vertical1 + halfHeight) / 12.0) + vOffset, layerAlpha);
                }
            }
        }
    }

    private static void surfaceVertex(PoseStack poses, VertexConsumer consumer, Vec3 point, float u, float v, float alpha) {
        Vec3 normal = point.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 1.0, 0.0) : point.normalize();
        consumer.addVertex(poses.last().pose(), (float)point.x, (float)point.y, (float)point.z).setColor(0.78f, 0.96f, 1.0f, alpha).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(poses.last(), (float)normal.x, (float)normal.y, (float)normal.z);
    }

    private static void surfaceQuad(PoseStack poses, VertexConsumer consumer, Vec3 a, Vec3 b, Vec3 c, Vec3 d, float u0, float v0, float u1, float v1, float alpha) {
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, a, u0, v0, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, b, u1, v0, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, c, u1, v1, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, d, u0, v1, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, d, u0, v1, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, c, u1, v1, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, b, u1, v0, alpha);
        ContainmentProjectorRenderer.surfaceVertex(poses, consumer, a, u0, v0, alpha);
    }

    private static void renderEnergyBeam(PoseStack poses, VertexConsumer consumer, CryogenicContainmentProjectorBlockEntity projector, double time, float alpha, float pulse) {
        double top = projector.fieldMode() == ContainmentFieldMode.RADIAL ? Math.max(2.5, projector.currentFieldRadius() * 0.82) : Math.max(2.5, (double)projector.wallHeight() * 0.45);
        double bottom = -0.18;
        double sway = (double)Mth.sin((float)((float)time * 0.075f)) * 0.025;
        double width = 0.115 + (double)pulse * 0.055;
        float beamAlpha = alpha + pulse * 0.18f;
        ContainmentProjectorRenderer.surfaceQuad(poses, consumer, new Vec3(-width + sway, bottom, 0.0), new Vec3(width + sway, bottom, 0.0), new Vec3(width - sway, top, 0.0), new Vec3(-width - sway, top, 0.0), 0.0f, 0.0f, 1.0f, (float)(top * 0.75), beamAlpha);
        ContainmentProjectorRenderer.surfaceQuad(poses, consumer, new Vec3(0.0, bottom, -width - sway), new Vec3(0.0, bottom, width - sway), new Vec3(0.0, top, width + sway), new Vec3(0.0, top, -width + sway), 0.0f, 0.0f, 1.0f, (float)(top * 0.75), beamAlpha * 0.88f);
        double halo = width * 2.6;
        float haloAlpha = beamAlpha * 0.24f;
        ContainmentProjectorRenderer.surfaceQuad(poses, consumer, new Vec3(-halo, bottom, 0.015), new Vec3(halo, bottom, 0.015), new Vec3(halo, top, 0.015), new Vec3(-halo, top, 0.015), 0.0f, 0.0f, 1.0f, (float)(top * 0.5), haloAlpha);
    }

    private static void renderRadialPulse(PoseStack poses, VertexConsumer lines, double radius, int segments, float alpha) {
        for (int i = 0; i < segments; i += 2) {
            double a = (float)Math.PI * 2 * (float)i / (float)segments;
            double b = (float)Math.PI * 2 * (float)(i + 1) / (float)segments;
            ContainmentProjectorRenderer.line(poses, lines, Math.cos(a) * radius, 0.0, Math.sin(a) * radius, Math.cos(b) * radius, 0.0, Math.sin(b) * radius, alpha * 0.78f);
        }
    }

    private static void renderWallPulse(PoseStack poses, VertexConsumer lines, CryogenicContainmentProjectorBlockEntity projector, boolean detailed, float alpha) {
        double[] depths;
        double[] dArray;
        boolean normalZ;
        double halfWidth = projector.wallHalfWidth() + 0.5;
        double halfDepth = (double)projector.wallDepth() / 2.0;
        double halfHeight = (double)projector.wallHeight() / 2.0;
        boolean bl = normalZ = projector.fieldFacing().getAxis() == Direction.Axis.Z;
        if (detailed) {
            double[] dArray2 = new double[2];
            dArray2[0] = -halfDepth;
            dArray = dArray2;
            dArray2[1] = halfDepth;
        } else {
            double[] dArray3 = new double[1];
            dArray = dArray3;
            dArray3[0] = 0.0;
        }
        for (double depth : depths = dArray) {
            Vec3 a = ContainmentProjectorRenderer.wallPoint(normalZ, -halfWidth, -halfHeight, depth);
            Vec3 b = ContainmentProjectorRenderer.wallPoint(normalZ, halfWidth, -halfHeight, depth);
            Vec3 c = ContainmentProjectorRenderer.wallPoint(normalZ, halfWidth, halfHeight, depth);
            Vec3 d = ContainmentProjectorRenderer.wallPoint(normalZ, -halfWidth, halfHeight, depth);
            ContainmentProjectorRenderer.line(poses, lines, a, b, alpha * 0.75f);
            ContainmentProjectorRenderer.line(poses, lines, b, c, alpha);
            ContainmentProjectorRenderer.line(poses, lines, c, d, alpha);
            ContainmentProjectorRenderer.line(poses, lines, d, a, alpha);
        }
        if (detailed) {
            for (int i = -2; i <= 2; ++i) {
                double lateral = halfWidth * (double)i / 3.0;
                Vec3 bottom = ContainmentProjectorRenderer.wallPoint(normalZ, lateral, -halfHeight, 0.0);
                Vec3 top = ContainmentProjectorRenderer.wallPoint(normalZ, lateral, halfHeight, 0.0);
                ContainmentProjectorRenderer.line(poses, lines, bottom, top, alpha * 0.45f);
            }
        }
    }

    private static void renderRadialTiles(PoseStack poses, VertexConsumer quads, double radius, int count, float alpha) {
        double goldenAngle = Math.PI * (3.0 - Math.sqrt(5.0));
        for (int i = 0; i < count; ++i) {
            double heightFraction = ((double)i + 0.65) / ((double)count + 0.8);
            double y = heightFraction * radius;
            double horizontal = Math.sqrt(Math.max(0.0, radius * radius - y * y));
            double angle = (double)i * goldenAngle;
            Vec3 normal = new Vec3(Math.cos(angle) * horizontal, y, Math.sin(angle) * horizontal).normalize();
            Vec3 center = normal.scale(radius);
            Vec3 tangent = new Vec3(-normal.z, 0.0, normal.x).normalize();
            Vec3 bitangent = normal.cross(tangent).normalize();
            double width = 0.45 + (double)(i % 4) * 0.18;
            double height = 0.18 + (double)(i % 3) * 0.12;
            ContainmentProjectorRenderer.tile(poses, quads, center, tangent.scale(width), bitangent.scale(height), alpha * (0.62f + (float)(i % 3) * 0.16f));
        }
    }

    private static void renderWallTiles(PoseStack poses, VertexConsumer quads, CryogenicContainmentProjectorBlockEntity projector, int count, float alpha) {
        Random random = new Random(projector.getBlockPos().asLong() ^ 0x57484954454F5554L);
        double halfWidth = projector.wallHalfWidth() + 0.5;
        double halfHeight = (double)projector.wallHeight() / 2.0;
        double depth = (double)projector.wallDepth() / 2.0;
        boolean normalZ = projector.fieldFacing().getAxis() == Direction.Axis.Z;
        Vec3 lateralAxis = normalZ ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 0.0, 1.0);
        Vec3 verticalAxis = new Vec3(0.0, 1.0, 0.0);
        for (int i = 0; i < count; ++i) {
            double lateral = (random.nextDouble() * 2.0 - 1.0) * halfWidth;
            double vertical = (random.nextDouble() * 2.0 - 1.0) * halfHeight;
            double face = (i & 1) == 0 ? -depth : depth;
            Vec3 center = ContainmentProjectorRenderer.wallPoint(normalZ, lateral, vertical, face);
            double width = 0.35 + random.nextDouble() * 0.65;
            double height = 0.18 + random.nextDouble() * 0.55;
            ContainmentProjectorRenderer.tile(poses, quads, center, lateralAxis.scale(width), verticalAxis.scale(height), alpha * (0.55f + random.nextFloat() * 0.4f));
        }
    }

    private static void renderEmitterColumn(PoseStack poses, VertexConsumer quads, double time, float alpha) {
        for (int i = 0; i < 7; ++i) {
            double y = 1.1 + (double)i * 0.52 + Math.sin(time * 0.08 + (double)i) * 0.12;
            double drift = Math.sin(time * 0.045 + (double)i * 2.1) * 0.16;
            Vec3 center = new Vec3(drift, y, -drift * 0.6);
            double size = 0.055 + (double)(i % 3) * 0.025;
            ContainmentProjectorRenderer.tile(poses, quads, center, new Vec3(size, 0.0, 0.0), new Vec3(0.0, size, 0.0), alpha * (1.0f - (float)i * 0.07f));
            ContainmentProjectorRenderer.tile(poses, quads, center, new Vec3(0.0, 0.0, size), new Vec3(0.0, size, 0.0), alpha * (1.0f - (float)i * 0.07f));
        }
    }

    private static Vec3 wallPoint(boolean normalZ, double lateral, double vertical, double depth) {
        return normalZ ? new Vec3(lateral, vertical, depth) : new Vec3(depth, vertical, lateral);
    }

    private static void renderAmbientSnow(PoseStack poses, VertexConsumer lines, CryogenicContainmentProjectorBlockEntity projector, long gameTime, float partialTick, int count) {
        Random random = new Random(projector.getBlockPos().asLong());
        double extent = projector.fieldMode() == ContainmentFieldMode.WALL ? projector.wallHalfWidth() : projector.currentFieldRadius();
        for (int i = 0; i < count; ++i) {
            double z;
            double x;
            double phase = (double)((float)gameTime + partialTick) * (0.012 + random.nextDouble() * 0.008) + random.nextDouble() * 6.2831854820251465;
            double y = -1.0 + random.nextDouble() * 5.0 + Math.sin(phase) * 0.35;
            if (projector.fieldMode() == ContainmentFieldMode.RADIAL) {
                double r = extent * (0.65 + random.nextDouble() * 0.32);
                x = Math.cos(phase) * r;
                z = Math.sin(phase) * r;
            } else {
                double lateral = (random.nextDouble() * 2.0 - 1.0) * extent;
                double depth = (random.nextDouble() - 0.5) * (double)projector.wallDepth();
                boolean normalZ = projector.fieldFacing().getAxis() == Direction.Axis.Z;
                x = normalZ ? lateral : depth;
                z = normalZ ? depth : lateral;
            }
            double size = 0.09 + random.nextDouble() * 0.08;
            ContainmentProjectorRenderer.snowflake(poses, lines, new Vec3(x, y, z), size, 0.3f);
        }
    }

    private static void snowflake(PoseStack poses, VertexConsumer lines, Vec3 center, double size, float alpha) {
        ContainmentProjectorRenderer.line(poses, lines, center.add(-size, 0.0, 0.0), center.add(size, 0.0, 0.0), alpha);
        ContainmentProjectorRenderer.line(poses, lines, center.add(0.0, -size, 0.0), center.add(0.0, size, 0.0), alpha);
        double diagonal = size * 0.72;
        ContainmentProjectorRenderer.line(poses, lines, center.add(-diagonal, -diagonal, 0.0), center.add(diagonal, diagonal, 0.0), alpha * 0.85f);
        ContainmentProjectorRenderer.line(poses, lines, center.add(-diagonal, diagonal, 0.0), center.add(diagonal, -diagonal, 0.0), alpha * 0.85f);
    }

    private static void tile(PoseStack poses, VertexConsumer consumer, Vec3 center, Vec3 halfWidth, Vec3 halfHeight, float alpha) {
        Vec3 a = center.subtract(halfWidth).subtract(halfHeight);
        Vec3 b = center.add(halfWidth).subtract(halfHeight);
        Vec3 c = center.add(halfWidth).add(halfHeight);
        Vec3 d = center.subtract(halfWidth).add(halfHeight);
        ContainmentProjectorRenderer.quadVertex(poses, consumer, a, alpha);
        ContainmentProjectorRenderer.quadVertex(poses, consumer, b, alpha);
        ContainmentProjectorRenderer.quadVertex(poses, consumer, c, alpha);
        ContainmentProjectorRenderer.quadVertex(poses, consumer, d, alpha);
    }

    private static void quadVertex(PoseStack poses, VertexConsumer consumer, Vec3 point, float alpha) {
        consumer.addVertex(poses.last().pose(), (float)point.x, (float)point.y, (float)point.z).setColor(0.48f, 0.88f, 0.95f, alpha);
    }

    private static void line(PoseStack poses, VertexConsumer consumer, Vec3 from, Vec3 to, float alpha) {
        ContainmentProjectorRenderer.line(poses, consumer, from.x, from.y, from.z, to.x, to.y, to.z, alpha);
    }

    private static void line(PoseStack poses, VertexConsumer consumer, double ax, double ay, double az, double bx, double by, double bz, float alpha) {
        float nx = (float)(bx - ax);
        float ny = (float)(by - ay);
        float nz = (float)(bz - az);
        float length = Mth.sqrt((float)(nx * nx + ny * ny + nz * nz));
        if (length > 0.0f) {
            nx /= length;
            ny /= length;
            nz /= length;
        }
        consumer.addVertex(poses.last().pose(), (float)ax, (float)ay, (float)az).setColor(0.28f, 0.72f, 0.82f, alpha).setNormal(poses.last(), nx, ny, nz);
        consumer.addVertex(poses.last().pose(), (float)bx, (float)by, (float)bz).setColor(0.66f, 0.9f, 0.94f, alpha).setNormal(poses.last(), nx, ny, nz);
    }

    public int getViewDistance() {
        return 160;
    }

    public boolean shouldRenderOffScreen(CryogenicContainmentProjectorBlockEntity projector) {
        return true;
    }

    static {
        FIELD_TEXTURE = SporeCannons.id("textures/misc/containment_field.png");
    }
}

