package com.oresomecraft.mapdev;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldListener implements Listener {

    MapDevPlugin plugin;

    public WorldListener(MapDevPlugin pl) {
        plugin = pl;
        pl.getServer().getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent event) {
        World w = event.getTo().getWorld();
        Player p = event.getPlayer();
        if (Util.isPrivate(w.getName())) {
            if (Util.isMember(w.getName(), p.getName()) || p.hasPermission("mapdev.staff")) {
                if (p.hasPermission("mapdev.staff")) {
                    p.sendMessage(ChatColor.RED + "[WARNING] This world is private, but you bypassed it with your permissions!");
                }
                p.sendMessage(ChatColor.GREEN + "This world is private, but you are allowed in!");
            } else {
                noTeleport(p);
                event.setCancelled(true);
            }
        }
    }

    private void noTeleport(Player p) {
        p.sendMessage(ChatColor.RED + "That world is private, you cannot go into it!");
    }
}
