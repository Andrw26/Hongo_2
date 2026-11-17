/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.EntityTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile
 *  rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile$ImpactResult
 *  rbasamoyai.createbigcannons.munitions.ProjectileContext
 *  rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.ProjectileContext;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

public final class CryogenicSensorFuzeItem
extends FuzeItem {
    private static final String MODE = "SporeCannonsSensorMode";
    private static final String RANGE = "SporeCannonsSensorRange";
    private static final int[] RANGES = new int[]{2, 4, 8};
    private static final int ARMING_TICKS = 2;

    public CryogenicSensorFuzeItem(Item.Properties properties) {
        super(properties);
    }

    public boolean onProjectileTick(ItemStack stack, AbstractCannonProjectile projectile) {
        Level level;
        if (!(projectile instanceof FreezeShellProjectile) || projectile.tickCount < 2 || !((level = projectile.level()) instanceof ServerLevel)) {
            return false;
        }
        ServerLevel level2 = (ServerLevel)level;
        int radius = CryogenicSensorFuzeItem.sensorRange(stack);
        int mode = CryogenicSensorFuzeItem.mode(stack);
        Vec3 end = projectile.position();
        int lookbackTicks = projectile.tickCount == 2 ? 2 : 1;
        Vec3 start = end.subtract(projectile.getDeltaMovement().scale((double)lookbackTicks));
        boolean parasite = mode != 1 && CryogenicSensorFuzeItem.detectsParasite(level2, projectile, start, end, radius);
        boolean infection = mode != 0 && CryogenicSensorFuzeItem.detectsInfection(level2, start, end, radius);
        return parasite || infection;
    }

    public boolean onProjectileImpact(ItemStack stack, AbstractCannonProjectile projectile, HitResult hit, AbstractCannonProjectile.ImpactResult impact, boolean baseFuze) {
        return projectile instanceof FreezeShellProjectile && projectile.tickCount >= 2;
    }

    public boolean onProjectileClip(ItemStack stack, AbstractCannonProjectile projectile, Vec3 start, Vec3 end, ProjectileContext context, boolean baseFuze) {
        return false;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isSecondaryUseActive()) {
                int next = (CryogenicSensorFuzeItem.rangeIndex(stack) + 1) % RANGES.length;
                CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> tag.putInt(RANGE, next));
                player.displayClientMessage((Component)Component.translatable((String)"message.sporecannons.sensor_range", (Object[])new Object[]{RANGES[next]}), true);
            } else {
                int next = (CryogenicSensorFuzeItem.mode(stack) + 1) % 3;
                CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> tag.putInt(MODE, next));
                player.displayClientMessage((Component)Component.translatable((String)"message.sporecannons.sensor_mode", (Object[])new Object[]{CryogenicSensorFuzeItem.modeName(next)}), true);
            }
        }
        return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_sensor_fuze").withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_sensor_fuze.settings", (Object[])new Object[]{CryogenicSensorFuzeItem.modeName(CryogenicSensorFuzeItem.mode(stack)), CryogenicSensorFuzeItem.sensorRange(stack)}).withStyle(ChatFormatting.GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_sensor_fuze.controls").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static boolean detectsParasite(ServerLevel level, AbstractCannonProjectile projectile, Vec3 start, Vec3 end, int radius) {
        AABB area = new AABB(start, end).inflate((double)radius);
        return !level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && CryogenicSensorFuzeItem.isParasite(entity) && CryogenicSensorFuzeItem.distanceToSegmentSqr(entity.getBoundingBox().getCenter(), start, end) <= ((double)radius + (double)entity.getBbWidth() * 0.5) * ((double)radius + (double)entity.getBbWidth() * 0.5)).isEmpty();
    }

    private static boolean detectsInfection(ServerLevel level, Vec3 start, Vec3 end, int radius) {
        int minX = Mth.floor((double)(Math.min(start.x, end.x) - (double)radius));
        int minY = Mth.floor((double)(Math.min(start.y, end.y) - (double)radius));
        int minZ = Mth.floor((double)(Math.min(start.z, end.z) - (double)radius));
        int maxX = Mth.floor((double)(Math.max(start.x, end.x) + (double)radius));
        int maxY = Mth.floor((double)(Math.max(start.y, end.y) + (double)radius));
        int maxZ = Mth.floor((double)(Math.max(start.z, end.z) + (double)radius));
        double radiusSquared = ((double)radius + 0.87) * ((double)radius + 0.87);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    pos.set(x, y, z);
                    if (!level.hasChunkAt((BlockPos)pos)) continue;
                    BlockState state = level.getBlockState((BlockPos)pos);
                    BlockState cleaned = FreezeShellProjectile.cleanedState(state);
                    if (!(CryogenicSensorFuzeItem.distanceToSegmentSqr(Vec3.atCenterOf((Vec3i)pos), start, end) <= radiusSquared) || cleaned == null || cleaned.equals(state)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isParasite(LivingEntity entity) {
        if (entity.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
            return true;
        }
        ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
        return "spore".equals(id.getNamespace());
    }

    private static double distanceToSegmentSqr(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 segment = end.subtract(start);
        double lengthSquared = segment.lengthSqr();
        if (lengthSquared < 1.0E-7) {
            return point.distanceToSqr(end);
        }
        double progress = Mth.clamp((double)(point.subtract(start).dot(segment) / lengthSquared), (double)0.0, (double)1.0);
        return point.distanceToSqr(start.add(segment.scale(progress)));
    }

    private static int mode(ItemStack stack) {
        return Mth.clamp((int)CryogenicSensorFuzeItem.data(stack).copyTag().getInt(MODE), (int)0, (int)2);
    }

    private static int rangeIndex(ItemStack stack) {
        return Mth.clamp((int)CryogenicSensorFuzeItem.data(stack).copyTag().getInt(RANGE), (int)0, (int)(RANGES.length - 1));
    }

    private static int sensorRange(ItemStack stack) {
        return RANGES[CryogenicSensorFuzeItem.rangeIndex(stack)];
    }

    private static CustomData data(ItemStack stack) {
        return (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
    }

    private static Component modeName(int mode) {
        return Component.translatable((String)(switch (mode) {
            case 1 -> "mode.sporecannons.sensor.infection";
            case 2 -> "mode.sporecannons.sensor.hybrid";
            default -> "mode.sporecannons.sensor.parasites";
        }));
    }
}

