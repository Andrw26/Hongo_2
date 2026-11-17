/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import com.elvoux.sporecannons.field.WhiteoutEventLog;
import com.elvoux.sporecannons.field.WhiteoutEventType;
import com.elvoux.sporecannons.field.WhiteoutTargeting;
import com.elvoux.sporecannons.field.WhiteoutTerminalService;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class CDUFieldScannerItem
extends Item {
    private static final String RANGE_INDEX = "SporeCannonsScanRange";
    private static final int[] RANGES = new int[]{32, 64, 128};

    public CDUFieldScannerItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!((Boolean)FreezeConfig.FIELD_SCANNER.get()).booleanValue()) {
            return InteractionResultHolder.fail((Object)stack);
        }
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide) {
                int nextRange = CDUFieldScannerItem.cycleRange(stack);
                player.displayClientMessage((Component)Component.translatable((String)"message.sporecannons.scanner_range", (Object[])new Object[]{nextRange}), true);
            }
            return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide);
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            WhiteoutTerminalService.open(serverPlayer);
        }
        return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cdu_field_scanner").withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cdu_field_scanner.range", (Object[])new Object[]{RANGES[CDUFieldScannerItem.rangeIndex(stack)]}).withStyle(ChatFormatting.GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cdu_field_scanner.controls").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cdu_field_scanner.telemetry").withStyle(ChatFormatting.BLUE));
        if (Screen.hasShiftDown()) {
            tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.protocol.cdu_expanded").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cdu_field_scanner.record").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.protocol.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static int rangeIndex(ItemStack stack) {
        CustomData data = (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
        return Mth.clamp((int)data.copyTag().getInt(RANGE_INDEX), (int)0, (int)(RANGES.length - 1));
    }

    public static int range(ItemStack stack) {
        return RANGES[CDUFieldScannerItem.rangeIndex(stack)];
    }

    public static int cycleRange(ItemStack stack) {
        int next = (CDUFieldScannerItem.rangeIndex(stack) + 1) % RANGES.length;
        CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> tag.putInt(RANGE_INDEX, next));
        return RANGES[next];
    }

    public static void performScan(ServerLevel level, ServerPlayer player, int radius) {
        Vec3 center = player.position();
        AABB area = new AABB(center, center).inflate((double)radius);
        List parasites = level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && WhiteoutTargeting.isAffected((Entity)entity) && entity.distanceToSqr(center) <= (double)radius * (double)radius);
        long signalBlocks = 0L;
        double focusX = 0.0;
        double focusZ = 0.0;
        int focusSamples = 0;
        int step = radius <= 32 ? 2 : (radius <= 64 ? 3 : 4);
        int vertical = Math.min(24, radius / 2);
        BlockPos origin = player.blockPosition();
        for (int dx = -radius; dx <= radius; dx += step) {
            for (int dz = -radius; dz <= radius; dz += step) {
                if ((long)dx * (long)dx + (long)dz * (long)dz > (long)radius * (long)radius) continue;
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                BlockPos chunkCheck = new BlockPos(x, origin.getY(), z);
                if (!level.hasChunkAt(chunkCheck)) continue;
                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                signalBlocks += (long)CDUFieldScannerItem.sample(level, new BlockPos(x, surfaceY, z));
                for (int y = origin.getY() - vertical; y <= origin.getY() + vertical; y += 4) {
                    BlockPos pos = new BlockPos(x, Mth.clamp((int)y, (int)level.getMinBuildHeight(), (int)(level.getMaxBuildHeight() - 1)), z);
                    if (CDUFieldScannerItem.sample(level, pos) <= 0) continue;
                    ++signalBlocks;
                    if (focusSamples >= 128) continue;
                    focusX += (double)x;
                    focusZ += (double)z;
                    ++focusSamples;
                    level.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, (double)x + 0.5, (double)pos.getY() + 1.0, (double)z + 0.5, 2, 0.25, 0.35, 0.25, 0.02);
                }
            }
        }
        for (LivingEntity parasite : parasites) {
            focusX += parasite.getX();
            focusZ += parasite.getZ();
            ++focusSamples;
        }
        int recommendation = CDUFieldScannerItem.recommendTier(signalBlocks, parasites.size());
        int directionOrdinal = -1;
        if (focusSamples != 0) {
            double dx = focusX / (double)focusSamples - center.x;
            double dz = focusZ / (double)focusSamples - center.z;
            Direction facing = Direction.getNearest((double)dx, (double)0.0, (double)dz);
            directionOrdinal = facing.ordinal();
            level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, focusX / (double)focusSamples, player.getY() + 1.0, focusZ / (double)focusSamples, 18, 1.0, 1.8, 1.0, 0.025);
        }
        int packed = recommendation | directionOrdinal + 1 << 8;
        WhiteoutEventLog.record(player, WhiteoutEventType.SCAN_COMPLETE, level, origin, radius, (int)Math.min(Integer.MAX_VALUE, signalBlocks), parasites.size(), packed);
        ((PlayerTrigger)SporeCannonsCriteria.SCAN_COMPLETED.get()).trigger(player);
    }

    public static void performDeepScan(ServerLevel level, ServerPlayer player, int radius) {
        Vec3 center = player.position();
        AABB area = new AABB(center, center).inflate((double)radius);
        List parasites = level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && WhiteoutTargeting.isAffected((Entity)entity) && entity.distanceToSqr(center) <= (double)radius * (double)radius);
        long signalBlocks = 0L;
        double focusX = 0.0;
        double focusY = 0.0;
        double focusZ = 0.0;
        long focusWeight = 0L;
        BlockPos origin = player.blockPosition();
        int horizontalStep = radius <= 32 ? 2 : (radius <= 64 ? 3 : 4);
        int verticalStep = radius <= 32 ? 4 : 6;
        for (int dx = -radius; dx <= radius; dx += horizontalStep) {
            for (int dz = -radius; dz <= radius; dz += horizontalStep) {
                int minimumY;
                if ((long)dx * (long)dx + (long)dz * (long)dz > (long)radius * (long)radius) continue;
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                if (!level.hasChunkAt(new BlockPos(x, origin.getY(), z))) continue;
                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                for (int y = minimumY = Math.max(level.getMinBuildHeight(), surfaceY - radius); y <= surfaceY; y += verticalStep) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (CDUFieldScannerItem.sample(level, pos) == 0) continue;
                    ++signalBlocks;
                    focusX += (double)x + 0.5;
                    focusY += (double)y + 0.5;
                    focusZ += (double)z + 0.5;
                    ++focusWeight;
                }
            }
        }
        for (LivingEntity parasite : parasites) {
            focusX += parasite.getX() * 8.0;
            focusY += parasite.getY() * 8.0;
            focusZ += parasite.getZ() * 8.0;
            focusWeight += 8L;
            parasite.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false, true));
        }
        BlockPos epicentre = origin;
        if (focusWeight > 0L) {
            epicentre = BlockPos.containing((double)(focusX / (double)focusWeight), (double)(focusY / (double)focusWeight), (double)(focusZ / (double)focusWeight));
            level.sendParticles((ParticleOptions)ParticleTypes.END_ROD, (double)epicentre.getX() + 0.5, (double)epicentre.getY() + 1.0, (double)epicentre.getZ() + 0.5, 28, 1.4, 2.2, 1.4, 0.02);
        }
        int recommendation = CDUFieldScannerItem.recommendTier(signalBlocks, parasites.size());
        int depth = Mth.clamp((int)(origin.getY() - epicentre.getY()), (int)Short.MIN_VALUE, (int)Short.MAX_VALUE);
        int packed = recommendation | (depth & 0xFFFF) << 8;
        WhiteoutEventLog.record(player, WhiteoutEventType.DEEP_SCAN_COMPLETE, level, epicentre, radius, (int)Math.min(Integer.MAX_VALUE, signalBlocks), parasites.size(), packed);
        ((PlayerTrigger)SporeCannonsCriteria.SCAN_COMPLETED.get()).trigger(player);
    }

    private static int sample(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return FreezeShellProjectile.cleanedState(state) == null ? 0 : 1;
    }

    private static int recommendTier(long signals, int parasites) {
        long threat = signals + (long)parasites * 12L;
        if (threat < 20L) {
            return 1;
        }
        if (threat < 60L) {
            return 2;
        }
        if (threat < 160L) {
            return 3;
        }
        if (threat < 400L) {
            return 4;
        }
        if (threat < 900L) {
            return 5;
        }
        return 6;
    }
}

