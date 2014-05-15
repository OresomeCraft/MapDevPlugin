package com.oresomecraft.mapdev;

import com.oresomecraft.mapdev.generators.NullChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Arrays;

public abstract class WorldUtil {

    public static final String MAPS_REPO = MapDevPlugin.getInstance().getConfig().getString("maps_repo");

    public static String[] disallowedFiles = {"plugins", "world", "server.properties", "ops.txt",
            "white-list.txt", "bukkit.yml", "spigot.yml", "banned-players.txt", "banned-ips.txt",
            "spigot.jar", "start.sh"};

    /**
     * Copies and loads a world from the main maps repository
     *
     * @param map Name of the map
     * @return Whether or not map was successfully copied!
     */
    public static boolean loadWorldFromRepo(String map) {

        File worldToCopy = new File(MAPS_REPO + "/" + map.toLowerCase());
        try {
            copyFolder(worldToCopy, new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + map));
        } catch (IOException e) {
            return false;
        }

        Bukkit.createWorld(new WorldCreator(map).generator(new NullChunkGenerator()));
        return true;
    }

    /**
     * Creates or loads a world
     *
     * @param name Name of the world
     */
    public static void loadOrCreateWorld(String name, boolean nullified, Long seed) {
        WorldCreator worldc = new WorldCreator(name);
        if (nullified) worldc.generator(new NullChunkGenerator());
        if (seed != null) worldc.seed(seed);
        Bukkit.createWorld(worldc);
    }

    public static void loadOrCreateWorld(String name, boolean nullified) {
        loadOrCreateWorld(name, nullified, null);
    }

    /**
     * Puts a map into the main maps repository
     *
     * @param map Name of the map
     * @return Whether or not the map was successfully copied
     */
    public static boolean putMapInRepo(String map) {

        if (Bukkit.getWorld(map) == null) return false;

        World world = Bukkit.getWorld(map);
        world.save();

        try {
            File toGoTo = new File(MAPS_REPO + "/" + map);
            if (toGoTo.exists()) delete(toGoTo);
            copyFolder(new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + map), new File(MAPS_REPO + "/" + map));
        } catch (IOException ex) {
            return false;
        }

        return true;
    }

    /**
     * Attempts to unload a world
     *
     * @param map Name of the World to unload
     * @return Whether or not the World was succesfully unloaded
     */
    public static boolean unloadWorld(String map) {
        if (Bukkit.getWorld(map) == null) return false;
        World world = Bukkit.getWorld(map);
        for (Player p : world.getPlayers()) {
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        return Bukkit.unloadWorld(world, true);
    }

    /**
     * Discards a world
     *
     * @param map Name of the world
     * @return Whether or not the map was successfully discarded
     */
    public static boolean discardWorld(String map) {
        if (Arrays.asList(disallowedFiles).contains(map)) return false;
        if (Bukkit.getWorld(map) != null) unloadWorld(map);
        return delete(new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + map));
    }

    /**
     * Copy Map folder into main server directory for loading
     *
     * @param src  Source directory
     * @param dest Destination directory
     * @throws IOException Thrown if an error occurs while trying to copy
     */
    public static void copyFolder(File src, File dest) throws IOException {

        if (src.isDirectory()) {

            if (!dest.exists()) {
                dest.mkdir();
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }

        } else {

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();

        }
    }

    /**
     * Deletes a directory
     *
     * @param folder Directory to delete
     * @return true if directory was successfully deleted
     */
    private static boolean delete(File folder) {
        if (!folder.exists())
            return true;
        boolean retVal = true;
        if (folder.isDirectory())
            for (File f : folder.listFiles())
                if (!delete(f)) {
                    retVal = false;
                    System.out.println("Failed to delete file: " + f.getName());
                }
        return folder.delete() && retVal;
    }

}
