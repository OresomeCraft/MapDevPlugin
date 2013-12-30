package com.oresomecraft.mapdev;

import com.oresomecraft.mapdev.generators.NullChunkGenerator;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Commands {
    MapDevPlugin plugin;

    public Commands(MapDevPlugin pl) {
        plugin = pl;
    }

    @Command(aliases = {"privacy", "worldprivacy", "mdprivate"},
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
                        return;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The world " + args.getString(1) + " could not be privated, perhaps it's not loaded?");
                    return;
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
            usage = "<add/remove>",
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
                return;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /members <add/remove>");
        }
    }

    @Command(aliases = {"loadworld", "createworld"},
            usage = "<WorldName>",
            desc = "Loads or creates a world.",
            min = 1,
            max = 1)
    @CommandPermissions({"mapdev.loadworld"})
    public void loadWorld(CommandContext args, CommandSender sender) throws CommandException {
        WorldUtil.loadOrCreateWorld(args.getString(0).toLowerCase());
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
        else sender.sendMessage(ChatColor.RED + "Unable to unload world!");
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
            sender.sendMessage(ChatColor.RED + "Unable to load map from maps repo!");
            sender.sendMessage(ChatColor.RED + "Are you sure the map exists/is spelt correctly?");
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
        else sender.sendMessage(ChatColor.RED + "Unable put world into maps repository!");
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
            sender.sendMessage(ChatColor.RED + "Unable to delete worlds");
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
        sender.sendMessage(ChatColor.AQUA + "Copied world '" + args.getString(0) + "' and renamed it to '" + args.getString(1) + "'!");
    }

    @Command(aliases = {"listmaps"},
            desc = "Lists all maps in the defined repo")
    @CommandPermissions({"mapdev.listmaps"})
    public void listMaps(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(ChatColor.DARK_AQUA + "Maps in the defined repo:");
        for (File f : new File(WorldUtil.MAPS_REPO).listFiles()) {
            sender.sendMessage(ChatColor.AQUA + f.getName());
        }
    }

    @Command(aliases = {"worldtp"},
            usage = "<WorldName>",
            desc = "Teleports you to a world.")
    @CommandPermissions({"mapdev.worldtp"})
    public void worldtp(CommandContext args, CommandSender sender) throws CommandException {
        Player p = (Player) sender;
        if (args.argsLength() < 1) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /worldtp <WorldName>");
        } else {
            if (Bukkit.getWorld(args.getString(0)) != null) {
                p.teleport(Bukkit.getWorld(args.getString(0)).getSpawnLocation());
            }
        }
    }

    @Command(aliases = {"worldsetspawn"},
            desc = "Sets spawn for a world.")
    @CommandPermissions({"mapdev.worldsetspawn"})
    public void worldsetspawn(CommandContext args, CommandSender sender) throws CommandException {
        Player p = (Player) sender;
        World world = p.getWorld();
        world.setSpawnLocation((int) p.getLocation().getX(), (int) p.getLocation().getY(), (int) p.getLocation().getZ());
        sender.sendMessage(ChatColor.AQUA + "Set spawn point for world '" + p.getWorld().getName() + "'");
    }

    @Command(aliases = {"terraform", "tf"},
            usage = "/terraform",
            desc = "Adds Terraforming tools to your inventory.")
    @CommandPermissions({"mapdev.terraform"})
    public void terraform(CommandContext args, CommandSender sender) throws CommandException {
        Player p = (Player) sender;
        p.getInventory().clear();
        p.getInventory().setItem(0, new ItemStack(Material.COMPASS));
        p.getInventory().setItem(1, new ItemStack(Material.WOOD_AXE));
        p.getInventory().setItem(2, new ItemStack(Material.ARROW));
        p.getInventory().setItem(3, new ItemStack(Material.DIRT));
        p.getInventory().setItem(4, new ItemStack(Material.STONE));
        p.getInventory().setItem(5, new ItemStack(Material.DIAMOND_PICKAXE));

        p.sendMessage(ChatColor.DARK_AQUA + "Inventory replaced with TerraForming tools!");
    }

}
