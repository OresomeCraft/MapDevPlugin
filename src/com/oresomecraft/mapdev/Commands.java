package com.oresomecraft.mapdev;

import com.oresomecraft.mapdev.generators.NullChunkGenerator;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Commands {
    MapDevPlugin plugin;

    public Commands(MapDevPlugin pl) {
        plugin = pl;
    }

    @Command(aliases = {"privacy", "worldprivacy", "mdprivate", "private"},
            usage = "<set/unset>",
            desc = "Manage region privacy",
            min = 1)
    @CommandPermissions({"mapdev.staff"})
    public void privacy(CommandContext args, CommandSender sender) {
        if (args.getString(0).equalsIgnoreCase("set")) {
            if (args.argsLength() == 3) {
                if (Bukkit.getWorld(args.getString(1)) != null) {
                    if (!plugin.getConfig().contains("worlds." + args.getString(1))) {
                        Util.setPrivateWorld(args.getString(1), args.getString(2));
                        sender.sendMessage(ChatColor.GREEN + "World " + ChatColor.RED + args.getString(1) + ChatColor.GREEN + " is now a private world!");
                        sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.RED + args.getString(2) + ChatColor.GREEN + " owns the world!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "That world has already been claimed by " + plugin.getConfig().getString("worlds." + args.getString(1) + ".owner"));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The world " + args.getString(1) + " could not be privated, perhaps it's not loaded?");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /privacy set <world> <owner>");
            }
        } else if (args.getString(0).equalsIgnoreCase("unset")) {
            if (args.argsLength() == 2) {
                if (!args.getString(1).equalsIgnoreCase("plugins") && !args.getString(1).equalsIgnoreCase("world")) {
                    if (plugin.getConfig().contains("worlds." + args.getString(1))) {
                        Util.unsetPrivateWorld(args.getString(1));
                        sender.sendMessage(ChatColor.GREEN + "World " + ChatColor.RED + args.getString(1) + ChatColor.GREEN + " is no longer a private world!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "The world " + args.getString(1) + " doesn't exist or isn't privated!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You can't private that!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /privacy unset <world>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /privacy <set/unset>");
        }
    }

    @Command(aliases = {"members", "prvmembers", "membermanagement"},
            usage = "<add/remove/list>",
            desc = "Private world member management",
            min = 1)
    public void members(CommandContext args, CommandSender sender) {
        if (args.getString(0).equalsIgnoreCase("add")) {
            if (args.argsLength() == 3) {
                if (plugin.getConfig().contains("worlds." + args.getString(1))) {
                    if (!plugin.getConfig().getString("worlds." + args.getString(1) + ".owner").equals(sender.getName())) {
                        sender.sendMessage(ChatColor.RED + "You are not the owner of that world.");
                        if (!plugin.hasPermission(sender, "mapdev.staff")) return;
                        sender.sendMessage(ChatColor.RED + "But you had overriding permissions!");
                        if (!Util.isMember(args.getString(1), args.getString(2))) {
                            Util.addMember(args.getString(1), args.getString(2));
                            sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.RED + args.getString(2) + ChatColor.GREEN + " can now access " + ChatColor.RED + args.getString(1));
                            return;
                        } else {
                            sender.sendMessage(ChatColor.RED + args.getString(2) + " is already a member of " + args.getString(1) + "!");
                            return;
                        }
                    } else {
                        if (!Util.isMember(args.getString(1), args.getString(2))) {
                            Util.addMember(args.getString(1), args.getString(2));
                            sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.RED + args.getString(2) + ChatColor.GREEN + " can now access " + ChatColor.RED + args.getString(1));
                        } else {
                            sender.sendMessage(ChatColor.RED + args.getString(2) + " is already a member of " + args.getString(1) + "!");
                            return;
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The world " + args.getString(1) + " hasn't been privated, it can't inherit members!");
                    return;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /members add <world> <player>");
                return;
            }
        }
        if (args.getString(0).equalsIgnoreCase("remove")) {
            if (args.argsLength() == 3) {
                if (plugin.getConfig().contains("worlds." + args.getString(1))) {
                    if (!plugin.getConfig().getString("worlds." + args.getString(1) + ".owner").equals(sender.getName())) {
                        sender.sendMessage(ChatColor.RED + "You are not the owner of that world.");
                        if (!plugin.hasPermission(sender, "mapdev.staff")) return;
                        sender.sendMessage(ChatColor.RED + "But you had overriding permissions!");
                        if (Util.isMember(args.getString(1), args.getString(2))) {
                            Util.removeMember(args.getString(1), args.getString(2));
                            sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.RED + args.getString(2) + ChatColor.GREEN + " can no longer access " + ChatColor.RED + args.getString(1));
                        } else {
                            sender.sendMessage(ChatColor.RED + args.getString(2) + " isn't a member of " + args.getString(1) + "!");
                        }
                    } else {
                        if (Util.isMember(args.getString(1), args.getString(2))) {
                            Util.removeMember(args.getString(1), args.getString(2));
                            sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.RED + args.getString(2) + ChatColor.GREEN + " can no longer access " + ChatColor.RED + args.getString(1));
                        } else {
                            sender.sendMessage(ChatColor.RED + args.getString(2) + " isn't a member of " + args.getString(1) + "!");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The world " + args.getString(1) + " hasn't been privated, it can't de-inherit members!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /members remove <world> <player>");
            }
        }
        if (args.getString(0).equalsIgnoreCase("list")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!plugin.hasPermission(sender, "mapdev.staff")) return;
                if (plugin.getConfig().contains("worlds." + player.getWorld().getName())) {
                    player.sendMessage(ChatColor.GREEN + "The members allowed in this world are:");
                    player.sendMessage(Util.getMembers(player.getWorld().getName()).toString());
                }
            } else {
                if (args.argsLength() > 1) {
                    if (plugin.getConfig().contains("worlds." + args.getString(1))) {
                        sender.sendMessage(ChatColor.GREEN + "The members allowed in this world are:");
                        sender.sendMessage(Util.getMembers(args.getString(1)).toString());
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /members list <world>");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /members <add/remove/list>");
        }
    }

    @Command(aliases = {"loadworld", "createworld"},
            usage = "<WorldName>",
            desc = "Loads or creates a world.",
            min = 1,
            max = 1,
            flags = "tn")
    @CommandPermissions({"mapdev.loadworld"})
    public void loadWorld(CommandContext args, CommandSender sender) throws CommandException {
        WorldUtil.loadOrCreateWorld(args.getString(0).toLowerCase(), !args.hasFlag('n'));

        if (args.hasFlag('t')) {
            if (sender instanceof Player) {
                ((Player) sender).teleport(Bukkit.getWorld(args.getString(0)).getSpawnLocation());
            }
        }
        sender.sendMessage(ChatColor.DARK_AQUA + "Created/loaded world " + ChatColor.AQUA + args.getString(0).toLowerCase());
    }

    @Command(aliases = {"unloadworld"},
            usage = "<WorldName>",
            desc = "Unloads a world.",
            min = 1,
            max = 1)
    @CommandPermissions({"mapdev.unloadworld"})
    public void unloadWorld(CommandContext args, CommandSender sender) throws CommandException {
        if (WorldUtil.unloadWorld(args.getString(0).toLowerCase()))
            sender.sendMessage(ChatColor.DARK_AQUA + "Unloaded world " + ChatColor.AQUA + args.getString(0).toLowerCase());
        else
            sender.sendMessage(ChatColor.RED + "You cannot unload this world. Is it unloaded already or can't you unload this?");
    }

    @Command(aliases = {"loadworldfromrepo", "loadmapfromrepo"},
            usage = "<WorldName>",
            desc = "Loads a world from the maps repo",
            min = 1,
            max = 1)
    @CommandPermissions({"mapdev.loadworldfromrepo"})
    public void loadWorldFromRepo(CommandContext args, CommandSender sender) throws CommandException {
        if (WorldUtil.loadWorldFromRepo(args.getString(0).toLowerCase()))
            sender.sendMessage(ChatColor.DARK_AQUA + "Copied and loaded world " + ChatColor.AQUA + args.getString(0).toLowerCase() + ChatColor.DARK_AQUA + " from maps repository!");
        else {
            sender.sendMessage(ChatColor.RED + "Unable to load world from maps repo!");
            sender.sendMessage(ChatColor.RED + "Please double check the name or your spelling...");
        }
    }

    @Command(aliases = {"worldchat", "wc"},
            usage = "<message>",
            desc = "Sends a message only to those in a world",
            min = 1)
    @CommandPermissions({"mapdev.loadworldfromrepo"})
    public void wc(CommandContext args, CommandSender sender) throws CommandException {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "Console can't do this.");
            return;
        }
        Player p = (Player) sender;
        for (Player pl : p.getWorld().getPlayers()) {
            pl.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "WorldChat" + ChatColor.DARK_AQUA + "] " + p.getName() + ": " + ChatColor.AQUA + args.getJoinedStrings(0));
        }
    }

    @Command(aliases = {"putworldinrepo", "putmapinrepo"},
            usage = "<WorldName>",
            desc = "Puts a world into the maps repository",
            min = 1,
            max = 1)
    @CommandPermissions({"mapdev.putworldinrepo"})
    public void putWorldInRepo(CommandContext args, CommandSender sender) throws CommandException {
        if (WorldUtil.putMapInRepo(args.getString(0).toLowerCase()))
            sender.sendMessage(ChatColor.DARK_AQUA + "Copied and put world " + ChatColor.AQUA + args.getString(0).toLowerCase() + ChatColor.DARK_AQUA + " into the maps repository!");
        else sender.sendMessage(ChatColor.RED + "Couldn't copy world into maps repo! Did you misspell the world?");
    }

    @Command(aliases = {"discardworld"},
            usage = "<WorldName>",
            desc = "Unloads and deletes a world",
            min = 1,
            max = 1)
    @CommandPermissions({"mapdev.discardworld"})
    public void discardWorld(CommandContext args, CommandSender sender) throws CommandException {
        if (WorldUtil.discardWorld(args.getString(0).toLowerCase())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Deleted and unloaded " + ChatColor.AQUA + args.getString(0).toLowerCase());
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't discard world, did you get the name right?");
        }
    }

    @Command(aliases = {"renameworld"},
            usage = "<OriginalWorldName> <NewName>",
            desc = "Copies, renames & loads a world",
            flags = "d",
            min = 2, max = 2)
    @CommandPermissions({"mapdev.renameworld"})
    public void renameWorld(CommandContext args, CommandSender sender) throws CommandException {
        try {
            Bukkit.getWorld(args.getString(0)).save();
            WorldUtil.copyFolder(new File(args.getString(0)), new File(args.getString(1)));
            File tar = new File(args.getString(1));
            for (File f : tar.listFiles()) {
                if (f.getName().equals("uid.dat")) {
                    f.delete();
                }
            }
            WorldCreator worldc = new WorldCreator(args.getString(1));
            worldc.generator(new NullChunkGenerator());
            Bukkit.createWorld(worldc);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                sender.sendMessage(ChatColor.RED + "Something went wrong. Perhaps that world doesn't exist?");
                return;
            }
            e.printStackTrace();
            //Love, why didn't the world copy?
        }
        if (args.hasFlag('d')) {
            Bukkit.dispatchCommand(sender, "worldtp " + args.getString(1));
            WorldUtil.discardWorld(args.getString(0));
            sender.sendMessage(ChatColor.RED + "WARNING: You used the -d flag and deleted the original map!");
        }
        sender.sendMessage(ChatColor.DARK_AQUA + "Copied world '" + ChatColor.AQUA +
                args.getString(0) + ChatColor.DARK_AQUA + "' and renamed it to '" + ChatColor.AQUA + args.getString(1) + ChatColor.DARK_AQUA + "'!");
    }

    @Command(aliases = {"listmapsrepo"},
            desc = "Lists all maps in the defined repo")
    @CommandPermissions({"mapdev.listmaps"})
    public void listMaps(CommandContext args, CommandSender sender) throws CommandException {
        int page = 1;
        if (args.argsLength() == 1) {
            try {
                page = Integer.parseInt(args.getString(0));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That is not a number!");
                return;
            }
        }
        ArrayList<String> worlds = new ArrayList<String>();
        for (File f : new File(WorldUtil.MAPS_REPO).listFiles()) {
            if (f.isDirectory() && !(Arrays.asList(WorldUtil.disallowedFiles).contains(f.getName()))) {
                worlds.add(f.getName());
            }
        }
        int maxPage = page * 10;
        int i = maxPage - 10;
        sender.sendMessage(ChatColor.GOLD + "Maps Repo List (Page " + page + ")");
        //10 per page, so if it's page 2 it will check the array-list from 10-20.
        boolean stopCheck = false;
        while (i < maxPage && !stopCheck) {
            try {
                sender.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + worlds.get(i));
                i++;
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(ChatColor.RED + "No further maps found.");
                i++;
                stopCheck = true;
            }
        }
        sender.sendMessage(ChatColor.GOLD + "To see next page, type '/listmaps " + (page + 1) + "'");
    }

    @Command(aliases = {"listmaps"},
            desc = "Lists all maps ever created on the dev")
    @CommandPermissions({"mapdev.listmaps"})
    public void listMaps2(CommandContext args, CommandSender sender) throws CommandException {
        int page = 1;
        if (args.argsLength() == 1) {
            try {
                page = Integer.parseInt(args.getString(0));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That is not a number!");
                return;
            }
        }
        ArrayList<String> worlds = new ArrayList<String>();
        for (File f : Bukkit.getWorldContainer().listFiles()) {
            if (f.isDirectory() && !(Arrays.asList(WorldUtil.disallowedFiles).contains(f.getName()))) {
                worlds.add(f.getName());
            }
        }
        int maxPage = page * 10;
        int i = maxPage - 10;
        sender.sendMessage(ChatColor.GOLD + "Maps List (Page " + page + ")");
        //10 per page, so if it's page 2 it will check the array-list from 10-20.
        boolean stopCheck = false;
        while (i < maxPage && !stopCheck) {
            try {
                sender.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.AQUA + worlds.get(i));
                i++;
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(ChatColor.RED + "No further maps found.");
                i++;
                stopCheck = true;
            }
        }
        sender.sendMessage(ChatColor.GOLD + "To see next page, type '/listmaps " + (page + 1) + "'");
    }

    @Command(aliases = {"worldtp"},
            usage = "<WorldName>",
            desc = "Teleports you to a world.")
    @CommandPermissions({"mapdev.worldtp"})
    public void worldtp(CommandContext args, CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.argsLength() < 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldtp <WorldName>");
            } else {
                if (Bukkit.getWorld(args.getString(0)) != null) {
                    p.teleport(Bukkit.getWorld(args.getString(0)).getSpawnLocation());
                } else {
                    p.sendMessage(ChatColor.RED + "The map you specified doesn't exist or isn't loaded!");
                }
            }
        } else {
            sender.sendMessage("You must be a player to use this command!");
        }
    }

    @Command(aliases = {"worldsetspawn"},
            desc = "Sets spawn for a world.")
    @CommandPermissions({"mapdev.worldsetspawn"})
    public void worldSetSpawn(CommandContext args, CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            World world = p.getWorld();
            world.setSpawnLocation((int) p.getLocation().getX(), (int) p.getLocation().getY(), (int) p.getLocation().getZ());
            sender.sendMessage(ChatColor.AQUA + "Set spawn point for world '" + p.getWorld().getName() + "'");
        } else {
            sender.sendMessage("You must be a player to use this command!");
        }
    }

    @Command(aliases = {"terraform", "tf"},
            usage = "/terraform",
            desc = "Adds Terraforming tools to your inventory.",
            flags = "l")
    @CommandPermissions({"mapdev.terraform"})
    public void terraform(CommandContext args, CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.getInventory().clear();
            p.getInventory().setItem(0, new ItemStack(Material.COMPASS));
            p.getInventory().setItem(1, new ItemStack(Material.WOOD_AXE));
            p.getInventory().setItem(2, new ItemStack(Material.ARROW));
            p.getInventory().setItem(3, new ItemStack(Material.DIRT));
            p.getInventory().setItem(4, new ItemStack(Material.STONE));
            p.getInventory().setItem(5, new ItemStack(Material.DIAMOND_PICKAXE));

            if (args.hasFlag('l')) {
                p.getInventory().setItem(6, new ItemStack(Material.GRASS));
                p.getInventory().setItem(7, new ItemStack(Material.SAND));
                p.getInventory().setItem(8, new ItemStack(Material.LEATHER_HELMET));
                p.sendMessage(ChatColor.DARK_AQUA + "Inventory replaced with Leet TerraForming tools!");
            } else {
                p.sendMessage(ChatColor.DARK_AQUA + "Inventory replaced with TerraForming tools!");
            }
        } else {
            sender.sendMessage("You must be a player to use this command!");
        }
    }
}
