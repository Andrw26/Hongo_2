/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.advancements.critereon.PlayerTrigger
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.component.CustomData
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 *  rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile
 *  rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile$ImpactResult
 *  rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.field.WhiteoutTargeting;
import com.elvoux.sporecannons.munition.CDUActivationManager;
import com.elvoux.sporecannons.munition.CDUFieldTerminal;
import com.elvoux.sporecannons.munition.FreezeShellBlock;
import com.elvoux.sporecannons.munition.FreezeShellProjectile;
import com.elvoux.sporecannons.munition.FreezeShellTier;
import com.elvoux.sporecannons.registry.SporeCannonsCriteria;
import com.elvoux.sporecannons.registry.SporeCannonsSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

public final class CryogenicMineFuzeItem
extends FuzeItem {
    private static final String MODE = "SporeCannonsMineMode";
    private static final String RANGE = "SporeCannonsMineRange";
    private static final String ARMING = "SporeCannonsMineArming";
    private static final String COUNTDOWN = "SporeCannonsMineCountdown";
    private static final String POSITION = "SporeCannonsMinePosition";
    private static final String DIMENSION = "SporeCannonsMineDimension";
    private static final int[] RANGES = new int[]{4, 8, 12};
    private static final int ARMING_TICKS = 60;
    private static final int DETONATION_DELAY = 40;
    private static final int SIGNAL_INTERVAL_TICKS = 55;
    private static final DustParticleOptions RED_INDICATOR = new DustParticleOptions(new Vector3f(1.0f, 0.035f, 0.02f), 0.85f);

    public CryogenicMineFuzeItem(Item.Properties properties) {
        super(properties);
    }

    public boolean onBlockTick(ItemStack stack, Level level, BlockPos pos, BlockState state) {
        boolean infection;
        boolean justArmed;
        int countdown;
        ServerLevel serverLevel;
        block14: {
            block13: {
                if (!(level instanceof ServerLevel)) break block13;
                serverLevel = (ServerLevel)level;
                if (state.getBlock() instanceof FreezeShellBlock) break block14;
            }
            return false;
        }
        String dimension = serverLevel.dimension().location().toString();
        CompoundTag tag = CryogenicMineFuzeItem.data(stack).copyTag();
        if (tag.getLong(POSITION) != pos.asLong() || !dimension.equals(tag.getString(DIMENSION))) {
            CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, mine -> {
                mine.putLong(POSITION, pos.asLong());
                mine.putString(DIMENSION, dimension);
                mine.putInt(ARMING, 0);
                mine.putInt(COUNTDOWN, 0);
            });
            tag = CryogenicMineFuzeItem.data(stack).copyTag();
        }
        if ((countdown = tag.getInt(COUNTDOWN)) > 0) {
            int next = countdown + 1;
            CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, mine -> mine.putInt(COUNTDOWN, next));
            if (next >= 40) {
                CryogenicMineFuzeItem.warningPulse(serverLevel, pos, true);
            }
            return next >= 40;
        }
        int previousArming = tag.getInt(ARMING);
        int arming = Math.min(60, previousArming + 1);
        CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, mine -> mine.putInt(ARMING, arming));
        boolean bl = justArmed = previousArming < 60 && arming == 60;
        if (previousArming == 0) {
            serverLevel.playSound(null, pos, (SoundEvent)SporeCannonsSounds.MINE_FUZE_PLACE.get(), SoundSource.BLOCKS, 0.65f, 1.0f);
        } else if (justArmed) {
            serverLevel.playSound(null, pos, (SoundEvent)SporeCannonsSounds.MINE_FUZE_READY.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
            serverLevel.sendParticles((ParticleOptions)ParticleTypes.SNOWFLAKE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, 5, 0.18, 0.18, 0.18, 0.015);
        }
        if (arming < 60) {
            return false;
        }
        if (!justArmed && CryogenicMineFuzeItem.isSignalTick(serverLevel, pos)) {
            CryogenicMineFuzeItem.operationalSignal(serverLevel, pos, state);
        }
        if (serverLevel.getGameTime() % 10L != 0L) {
            return false;
        }
        int selectedMode = CryogenicMineFuzeItem.mode(stack);
        int radius = CryogenicMineFuzeItem.sensorRange(stack);
        boolean parasite = selectedMode != 1 && CryogenicMineFuzeItem.detectsParasite(serverLevel, pos, radius);
        boolean bl2 = infection = selectedMode != 0 && CryogenicMineFuzeItem.detectsInfection(serverLevel, pos, radius);
        if (!parasite && !infection) {
            return false;
        }
        CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, mine -> mine.putInt(COUNTDOWN, 1));
        FreezeShellTier tier = FreezeShellTier.fromBlock(state.getBlock());
        CDUFieldTerminal.mineTriggered(serverLevel, Vec3.atCenterOf((Vec3i)pos), tier);
        CDUActivationManager.signalMine(serverLevel, Vec3.atCenterOf((Vec3i)pos), tier);
        serverLevel.playSound(null, pos, (SoundEvent)SporeCannonsSounds.MINE_FUZE_TRIGGER.get(), SoundSource.BLOCKS, 0.85f, 1.0f);
        CryogenicMineFuzeItem.triggerAdvancement(serverLevel, pos);
        CryogenicMineFuzeItem.warningPulse(serverLevel, pos, false);
        return false;
    }

    public boolean onProjectileImpact(ItemStack stack, AbstractCannonProjectile projectile, HitResult hit, AbstractCannonProjectile.ImpactResult impact, boolean baseFuze) {
        return projectile instanceof FreezeShellProjectile && projectile.tickCount >= 2;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            CryogenicMineFuzeItem.resetArming(stack);
            if (player.isSecondaryUseActive()) {
                int next = (CryogenicMineFuzeItem.rangeIndex(stack) + 1) % RANGES.length;
                CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> tag.putInt(RANGE, next));
                player.displayClientMessage((Component)Component.translatable((String)"message.sporecannons.mine_range", (Object[])new Object[]{RANGES[next]}), true);
            } else {
                int next = (CryogenicMineFuzeItem.mode(stack) + 1) % 3;
                CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> tag.putInt(MODE, next));
                player.displayClientMessage((Component)Component.translatable((String)"message.sporecannons.mine_mode", (Object[])new Object[]{CryogenicMineFuzeItem.modeName(next)}), true);
            }
        }
        return InteractionResultHolder.sidedSuccess((Object)stack, (boolean)level.isClientSide);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_mine_fuze").withStyle(ChatFormatting.AQUA));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_mine_fuze.settings", (Object[])new Object[]{CryogenicMineFuzeItem.modeName(CryogenicMineFuzeItem.mode(stack)), CryogenicMineFuzeItem.sensorRange(stack)}).withStyle(ChatFormatting.GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_mine_fuze.controls").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_mine_fuze.warning").withStyle(ChatFormatting.RED));
    }

    public void addExtraInfo(List<Component> tooltip, boolean isSneaking, ItemStack stack) {
        tooltip.add((Component)Component.translatable((String)"tooltip.sporecannons.cryogenic_mine_fuze.settings", (Object[])new Object[]{CryogenicMineFuzeItem.modeName(CryogenicMineFuzeItem.mode(stack)), CryogenicMineFuzeItem.sensorRange(stack)}).withStyle(ChatFormatting.GRAY));
    }

    private static boolean detectsParasite(ServerLevel level, BlockPos pos, int radius) {
        Vec3 center = Vec3.atCenterOf((Vec3i)pos);
        double radiusSquared = (double)radius * (double)radius;
        return !level.getEntitiesOfClass(LivingEntity.class, new AABB(center, center).inflate((double)radius), entity -> entity.isAlive() && CryogenicMineFuzeItem.isParasite(entity) && entity.distanceToSqr(center) <= radiusSquared).isEmpty();
    }

    private static boolean detectsInfection(ServerLevel level, BlockPos origin, int radius) {
        long radiusSquared = (long)radius * (long)radius;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockState state;
                    BlockState cleaned;
                    if ((long)x * (long)x + (long)y * (long)y + (long)z * (long)z > radiusSquared) continue;
                    cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!level.hasChunkAt((BlockPos)cursor) || (cleaned = FreezeShellProjectile.cleanedState(state = level.getBlockState((BlockPos)cursor))) == null || cleaned.equals(state)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isParasite(LivingEntity entity) {
        return WhiteoutTargeting.isAffected((Entity)entity);
    }

    private static boolean isSignalTick(ServerLevel level, BlockPos pos) {
        int phase = Math.floorMod(Long.hashCode(pos.asLong()), 55);
        return (long)Math.floorMod((long)(level.getGameTime() + (long)phase), (int)55) == 0L;
    }

    private static void operationalSignal(ServerLevel level, BlockPos pos, BlockState state) {
        level.playSound(null, pos, (SoundEvent)SporeCannonsSounds.MINE_FUZE_SIGNAL.get(), SoundSource.BLOCKS, 0.28f, 1.0f);
        Direction facing = state.hasProperty((Property)DirectionalBlock.FACING) ? (Direction)state.getValue((Property)DirectionalBlock.FACING) : Direction.UP;
        double x = (double)pos.getX() + 0.5 + (double)facing.getStepX() * 0.62;
        double y = (double)pos.getY() + 0.5 + (double)facing.getStepY() * 0.62;
        double z = (double)pos.getZ() + 0.5 + (double)facing.getStepZ() * 0.62;
        level.sendParticles((ParticleOptions)RED_INDICATOR, x, y, z, 1, 0.01, 0.01, 0.01, 0.0);
    }

    private static void warningPulse(ServerLevel level, BlockPos pos, boolean finalPulse) {
        level.sendParticles((ParticleOptions)(finalPulse ? ParticleTypes.FLASH : ParticleTypes.END_ROD), (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, finalPulse ? 1 : 2, 0.12, 0.12, 0.12, 0.006);
    }

    private static void triggerAdvancement(ServerLevel level, BlockPos pos) {
        Player nearest = level.getNearestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 64.0, player -> {
            ServerPlayer candidate;
            return player instanceof ServerPlayer && !(candidate = (ServerPlayer)player).isSpectator();
        });
        if (nearest instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)nearest;
            ((PlayerTrigger)SporeCannonsCriteria.MINE_TRIGGERED.get()).trigger(player2);
        }
    }

    private static void resetArming(ItemStack stack) {
        CustomData.update((DataComponentType)DataComponents.CUSTOM_DATA, (ItemStack)stack, tag -> {
            tag.remove(POSITION);
            tag.remove(DIMENSION);
            tag.remove(ARMING);
            tag.remove(COUNTDOWN);
        });
    }

    private static int mode(ItemStack stack) {
        return Mth.clamp((int)CryogenicMineFuzeItem.data(stack).copyTag().getInt(MODE), (int)0, (int)2);
    }

    private static int rangeIndex(ItemStack stack) {
        return Mth.clamp((int)CryogenicMineFuzeItem.data(stack).copyTag().getInt(RANGE), (int)0, (int)(RANGES.length - 1));
    }

    private static int sensorRange(ItemStack stack) {
        return RANGES[CryogenicMineFuzeItem.rangeIndex(stack)];
    }

    private static CustomData data(ItemStack stack) {
        return (CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, (Object)CustomData.EMPTY);
    }

    static boolean completedGroundCountdown(ItemStack stack) {
        return CryogenicMineFuzeItem.data(stack).copyTag().getInt(COUNTDOWN) >= 40;
    }

    private static Component modeName(int mode) {
        return Component.translatable((String)(switch (mode) {
            case 1 -> "mode.sporecannons.mine.infection";
            case 2 -> "mode.sporecannons.mine.containment";
            default -> "mode.sporecannons.mine.parasites";
        }));
    }
}

