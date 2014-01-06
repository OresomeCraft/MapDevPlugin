package com.oresomecraft.mapdev;

import java.util.ArrayList;
import java.util.List;

public class Util {

    private static MapDevPlugin plugin = MapDevPlugin.getInstance();

    public static void setPrivateWorld(String name, String owner) {
        plugin.getConfig().set("worlds." + name + ".owner", owner);
        plugin.getConfig().set("worlds." + name + ".members", new ArrayList<String>());
        plugin.saveConfig();
    }

    public static void unsetPrivateWorld(String name) {
        plugin.getConfig().set("worlds." + name, null);
        plugin.saveConfig();
    }

    public static void removeMember(String world, String player) {
        List<String> temp = plugin.getConfig().getStringList("worlds." + world + ".members");
        if (temp.contains(player)) temp.remove(player);
        plugin.getConfig().set("worlds." + world + ".members", temp);
        plugin.saveConfig();
    }

    public static void addMember(String world, String player) {
        List<String> temp = plugin.getConfig().getStringList("worlds." + world + ".members");
        if (!temp.contains(player)) temp.add(player);
        plugin.getConfig().set("worlds." + world + ".members", temp);
        plugin.saveConfig();
    }

    public static boolean isMember(String world, String player) {
        List<String> temp = plugin.getConfig().getStringList("worlds." + world + ".members");
        if (temp.contains(player)) return true;
        if (plugin.getConfig().getString("worlds." + world + ".owner").equals(player)) return true;
        return false;
    }

    public static boolean isPrivate(String world) {
        return plugin.getConfig().contains("worlds." + world);
    }
}
