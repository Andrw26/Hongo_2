/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.munition;

public final class WhiteoutKineticScaling {
    public static final float MINIMUM_SPEED = 32.0f;
    public static final float MAXIMUM_EFFECTIVE_SPEED = 256.0f;
    private static final float[] SPEEDS = new float[]{32.0f, 64.0f, 128.0f, 256.0f};
    private static final double[] STATION_MULTIPLIERS = new double[]{1.0, 1.5, 2.25, 3.0};
    private static final double[] PROJECTOR_RADII = new double[]{12.0, 16.0, 24.0, 64.0};
    private static final double[] PROJECTOR_CONSUMPTION = new double[]{0.75, 1.0, 1.5, 4.0};
    private static final double[] PROJECTOR_WALL_HEIGHTS = new double[]{9.0, 12.0, 18.0, 32.0};

    private WhiteoutKineticScaling() {
    }

    public static double stationMultiplier(float speed) {
        return WhiteoutKineticScaling.interpolate(speed, STATION_MULTIPLIERS);
    }

    public static double projectorRadius(float speed) {
        return WhiteoutKineticScaling.interpolate(speed, PROJECTOR_RADII);
    }

    public static double projectorConsumption(float speed) {
        return WhiteoutKineticScaling.interpolate(speed, PROJECTOR_CONSUMPTION);
    }

    public static double projectorWallHeight(float speed) {
        return WhiteoutKineticScaling.interpolate(speed, PROJECTOR_WALL_HEIGHTS);
    }

    private static double interpolate(float rawSpeed, double[] values) {
        if (!Float.isFinite(rawSpeed)) {
            return 0.0;
        }
        float speed = Math.abs(rawSpeed);
        if (speed < 32.0f) {
            return 0.0;
        }
        if (speed >= 256.0f) {
            return values[values.length - 1];
        }
        for (int i = 1; i < SPEEDS.length; ++i) {
            if (speed > SPEEDS[i]) continue;
            double blend = (speed - SPEEDS[i - 1]) / (SPEEDS[i] - SPEEDS[i - 1]);
            return values[i - 1] + (values[i] - values[i - 1]) * blend;
        }
        return values[values.length - 1];
    }
}

