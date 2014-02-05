package com.oresomecraft.mapdev;

import com.oresomecraft.mapdev.generators.NullChunkGenerator;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
    public static void loadOrCreateWorld(String name) {
        WorldCreator worldc = new WorldCreator(name);
        worldc.generator(new NullChunkGenerator());
        Bukkit.createWorld(worldc);
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
            copyFolder(new File(map), new File(MAPS_REPO + "/" + map));
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
     * Attempts to WGET, unzip, and load a world.
     *
     * @param URL The link
     */
    public static boolean WGET(String URL, CommandSender sender) {
        try {
            saveUrl(URL, sender);
            return true;
        } catch (MalformedURLException e1) {
            sender.sendMessage(ChatColor.RED + "ERROR: Malformed URL!");
            sender.sendMessage(ChatColor.RED + "DETAILS: " + e1.getMessage());
            return false;
        } catch (IOException e2) {
            sender.sendMessage(ChatColor.RED + "ERROR: IO Exception! (did the file not exist?)");
            sender.sendMessage(ChatColor.RED + "DETAILS: " + e2.getMessage());
            return false;
        }
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
        return delete(new File(map));
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

    /**
     * WGETs, and copies a directory
     *
     * @param urlString The direct link to the file
     */
    private static void saveUrl(String urlString, CommandSender sender)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            URL url = new URL(urlString);
            String ID = 10000 + new Random().nextInt(9000) + "";
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(ID + ".zip");
            sender.sendMessage(ChatColor.GOLD + "Found file " + url.getFile());

            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
                in.
            }
            sender.sendMessage(ChatColor.GREEN + "Successfully downloaded and copied!");
            unZipIt(ID + ".zip", sender);
            discardWorld(ID + ".zip");
        } finally {
            if (in != null)
                in.close();
            if (fout != null)
                fout.close();
        }
    }

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     */
    public static void unZipIt(String zipFile, CommandSender sender) {
        try {
            int BUFFER = 2048;
            File file = new File(zipFile);

            ZipFile zip = new ZipFile(file);
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                sender.sendMessage(ChatColor.AQUA + "Unzip: " + entry.getName());

                File destFile = new File(currentEntry);
                //destFile = new File(newPath, destFile.getName());
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,
                            BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }


            }
        } catch (Exception e) {
            //Did we stuff up?
            sender.sendMessage(ChatColor.RED + "ERROR WHILST TRYING TO UNZIP FILE!!");
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

}
