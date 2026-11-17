/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.Block
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.FreezeConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public enum FreezeShellTier {
    I("freeze_shell", "I", 0, 0, 1.0, 0, 0.0, 6.0f, 160),
    II("freeze_shell_ii", "II", 12, 10, 2.0, 200, 0.5, 10.0f, 220),
    III("freeze_shell_iii", "III", 37, 26, 4.0, 400, 1.0, 16.0f, 300),
    IV("freeze_shell_iv", "IV", 72, 58, 7.0, 800, 1.5, 24.0f, 420),
    V("freeze_shell_v", "V", 142, 122, 10.5, 1200, 2.0, 32.0f, 560),
    VI("freeze_shell_vi", "VI", 242, 244, 16.0, 1800, 3.0, 40.0f, 720);

    private final String id;
    private final String roman;
    private final int entityRadiusBonus;
    private final int blockRadiusBonus;
    private final double damageMultiplier;
    private final int durationBonus;
    private final double addedChargePower;
    private final float collateralDamage;
    private final int collateralFrozenTicks;

    private FreezeShellTier(String id, String roman, int entityRadiusBonus, int blockRadiusBonus, double damageMultiplier, int durationBonus, double addedChargePower, float collateralDamage, int collateralFrozenTicks) {
        this.id = id;
        this.roman = roman;
        this.entityRadiusBonus = entityRadiusBonus;
        this.blockRadiusBonus = blockRadiusBonus;
        this.damageMultiplier = damageMultiplier;
        this.durationBonus = durationBonus;
        this.addedChargePower = addedChargePower;
        this.collateralDamage = collateralDamage;
        this.collateralFrozenTicks = collateralFrozenTicks;
    }

    public String id() {
        return this.id;
    }

    public String roman() {
        return this.roman;
    }

    public int entityRadius() {
        return (Integer)FreezeConfig.ENTITY_RADIUS.get() + this.entityRadiusBonus;
    }

    public int blockRadius() {
        return (Integer)FreezeConfig.BLOCK_RADIUS.get() + this.blockRadiusBonus;
    }

    public float damage() {
        return (float)((Double)FreezeConfig.DAMAGE.get() * this.damageMultiplier);
    }

    public int duration() {
        return (Integer)FreezeConfig.DURATION.get() + this.durationBonus;
    }

    public int frostbiteAmplifier() {
        return this.ordinal();
    }

    public double addedChargePower() {
        return this.addedChargePower;
    }

    public float collateralDamage() {
        return this.collateralDamage;
    }

    public int collateralFrozenTicks() {
        return this.collateralFrozenTicks;
    }

    public int screenEffectTicks() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 30;
            case 1 -> 60;
            case 2 -> 100;
            case 3 -> 180;
            case 4 -> 300;
            case 5 -> 500;
        };
    }

    public int snowfallTicks() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 40;
            case 1 -> 80;
            case 2 -> 140;
            case 3 -> 240;
            case 4 -> 360;
            case 5 -> 600;
        };
    }

    public int snowRadius() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 4;
            case 1 -> 8;
            case 2 -> 16;
            case 3 -> 28;
            case 4 -> 45;
            case 5 -> 70;
        };
    }

    public int maximumSnowLayers() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 6;
            case 4, 5 -> 8;
        };
    }

    public int freshBileShatterRadius() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 1 -> 0;
            case 2 -> Math.max(4, this.blockRadius() / 4);
            case 3 -> this.blockRadius() / 2;
            case 4 -> this.blockRadius() * 2 / 3;
            case 5 -> this.blockRadius() * 9 / 10;
        };
    }

    public int containmentRadius() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 4;
            case 1 -> 8;
            case 2 -> 16;
            case 3 -> 28;
            case 4 -> 45;
            case 5 -> 70;
        };
    }

    public int containmentTicks() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 200;
            case 1 -> 600;
            case 2 -> 2400;
            case 3 -> 6000;
            case 4 -> 12000;
            case 5 -> 24000;
        };
    }

    public static FreezeShellTier fromBlock(Block block) {
        String path = BuiltInRegistries.BLOCK.getKey((Object)block).getPath();
        return FreezeShellTier.fromId(path);
    }

    public static FreezeShellTier fromEntityType(EntityType<?> type) {
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath();
        return FreezeShellTier.fromId(path);
    }

    private static FreezeShellTier fromId(String id) {
        for (FreezeShellTier tier : FreezeShellTier.values()) {
            if (!tier.id.equals(id)) continue;
            return tier;
        }
        return I;
    }
}

