package com.oresomecraft.mapdev.generators;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/*
 * Code forked from NullTerrain.
 * https://github.com/Elizacat/NullTerrain
 */

public class NullChunkGenerator extends ChunkGenerator {

    public byte[] generate(World world, Random random, int cx, int cz) {
        return new byte[65536];
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 64, 0);
    }

    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new NullChunkGenerator();
    }
}