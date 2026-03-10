package net.paulem.btm.utils;

import net.paulem.btm.BetterMending;
import net.paulem.btm.listeners.extendables.ManagersListener;

public class PluginUtils {
    public static void reloadConfig() {
        BetterMending.getInstance().reloadConfig();

        if(BetterMending.getPlayerConfig() != null) BetterMending.getPlayerConfig().reload();

        ManagersListener.reloadConfig(BetterMending.getConf());
    }
}
