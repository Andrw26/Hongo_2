/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.phys.AABB
 */
package com.elvoux.sporecannons.munition;

import com.elvoux.sporecannons.munition.ContainmentFieldMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public record ContainmentFieldGeometry(ContainmentFieldMode mode, Direction facing, double extent, int wallDepth, int wallHeight) {
    public ContainmentFieldGeometry {
        if (!facing.getAxis().isHorizontal()) {
            facing = Direction.NORTH;
        }
        extent = Math.max(0.0, extent);
        wallDepth = Math.max(1, wallDepth);
        wallHeight = Math.max(1, wallHeight);
    }

    public double width() {
        return this.mode == ContainmentFieldMode.RADIAL ? this.extent * 2.0 : this.extent * 2.0 + 1.0;
    }

    public AABB bounds(BlockPos origin) {
        double x = (double)origin.getX() + 0.5;
        double y = (double)origin.getY() + 0.7;
        double z = (double)origin.getZ() + 0.5;
        if (this.mode == ContainmentFieldMode.RADIAL) {
            return new AABB(x - this.extent, y - this.extent, z - this.extent, x + this.extent, y + this.extent, z + this.extent);
        }
        double halfDepth = (double)this.wallDepth / 2.0;
        double halfHeight = (double)this.wallHeight / 2.0;
        double halfX = this.facing.getAxis() == Direction.Axis.Z ? this.extent + 0.5 : halfDepth;
        double halfZ = this.facing.getAxis() == Direction.Axis.X ? this.extent + 0.5 : halfDepth;
        return new AABB(x - halfX, y - halfHeight, z - halfZ, x + halfX, y + halfHeight, z + halfZ);
    }

    public boolean contains(BlockPos origin, double x, double y, double z) {
        double dx = x - ((double)origin.getX() + 0.5);
        double dy = y - ((double)origin.getY() + 0.7);
        double dz = z - ((double)origin.getZ() + 0.5);
        if (this.mode == ContainmentFieldMode.RADIAL) {
            return dx * dx + dy * dy + dz * dz <= this.extent * this.extent;
        }
        double lateral = this.facing.getAxis() == Direction.Axis.Z ? Math.abs(dx) : Math.abs(dz);
        double depth = this.facing.getAxis() == Direction.Axis.Z ? Math.abs(dz) : Math.abs(dx);
        return lateral <= this.extent + 0.5 && depth <= (double)this.wallDepth / 2.0 && Math.abs(dy) <= (double)this.wallHeight / 2.0;
    }

    public int scanVolume() {
        int radius = (int)Math.ceil(this.extent);
        if (this.mode == ContainmentFieldMode.RADIAL) {
            int diameter = radius * 2 + 1;
            return diameter * diameter * diameter;
        }
        return (radius * 2 + 1) * this.wallDepth * this.wallHeight;
    }

    public boolean containsBlockOffset(BlockPos offset) {
        if (this.mode == ContainmentFieldMode.RADIAL) {
            return (double)(offset.getX() * offset.getX() + offset.getY() * offset.getY() + offset.getZ() * offset.getZ()) <= this.extent * this.extent;
        }
        return this.contains(BlockPos.ZERO, (double)offset.getX() + 0.5, (double)offset.getY() + 0.5, (double)offset.getZ() + 0.5);
    }

    public BlockPos scanOffset(int cursor) {
        int radius = (int)Math.ceil(this.extent);
        if (this.mode == ContainmentFieldMode.RADIAL) {
            int diameter = radius * 2 + 1;
            int index = Math.floorMod(cursor, diameter * diameter * diameter);
            int dx = index % diameter - radius;
            int dz = index / diameter % diameter - radius;
            int dy = index / (diameter * diameter) - radius;
            return new BlockPos(dx, dy, dz);
        }
        int width = radius * 2 + 1;
        int index = Math.floorMod(cursor, width * this.wallDepth * this.wallHeight);
        int lateral = index % width - radius;
        int depth = index / width % this.wallDepth - this.wallDepth / 2;
        int dy = index / (width * this.wallDepth) - this.wallHeight / 2;
        return this.facing.getAxis() == Direction.Axis.Z ? new BlockPos(lateral, dy, depth) : new BlockPos(depth, dy, lateral);
    }
}

