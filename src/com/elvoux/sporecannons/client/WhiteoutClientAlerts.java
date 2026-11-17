/*
 * Decompiled with CFR 0.152.
 */
package com.elvoux.sporecannons.client;

public final class WhiteoutClientAlerts {
    private static boolean unread;

    private WhiteoutClientAlerts() {
    }

    public static boolean hasUnread() {
        return unread;
    }

    public static void setUnread(boolean value) {
        unread = value;
    }
}

