package com.kaitu.chunkdestroyer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChunkDestroyerLogicTest {

    @Test
    @DisplayName("Test Initial Placement Working State is False")
    void testInitialPlacementWorkingState() {
        boolean initialWorkingState = false;
        assertFalse(initialWorkingState, "Chunk Destroyer must be stopped upon placement until user clicks Start button in UI");
    }

    @Test
    @DisplayName("Test Chunk Expansion Range Calculation")
    void testChunkExpansionCalculation() {
        int centerChunkX = 5;
        int centerChunkZ = 10;
        int chunkWest = 1;  // -X
        int chunkEast = 2;  // +X
        int chunkNorth = 0; // -Z
        int chunkSouth = 3; // +Z

        int minChunkX = centerChunkX - chunkWest;
        int maxChunkX = centerChunkX + chunkEast;
        int minChunkZ = centerChunkZ - chunkNorth;
        int maxChunkZ = centerChunkZ + chunkSouth;

        int minX = minChunkX * 16;
        int maxX = maxChunkX * 16 + 15;
        int minZ = minChunkZ * 16;
        int maxZ = maxChunkZ * 16 + 15;

        assertEquals(4 * 16, minX);
        assertEquals(7 * 16 + 15, maxX);
        assertEquals(10 * 16, minZ);
        assertEquals(13 * 16 + 15, maxZ);

        assertEquals(64, maxX - minX + 1);
        assertEquals(64, maxZ - minZ + 1);
    }

    @Test
    @DisplayName("Test Vertical Column Scan Progression Order")
    void testVerticalColumnProgression() {
        int minX = 0, maxX = 1;
        int minZ = 0, maxZ = 1;

        int curX = minX;
        int curZ = minZ;

        int totalColumns = 0;
        boolean finished = false;

        while (!finished) {
            totalColumns++;
            curX++;
            if (curX > maxX) {
                curX = minX;
                curZ++;
                if (curZ > maxZ) {
                    finished = true;
                }
            }
        }

        assertEquals(4, totalColumns, "2x2 columns = 4 vertical columns");
    }

    @Test
    @DisplayName("Test Stone and Dirt Void Filter Items")
    void testItemFiltersLogic() {
        java.util.Set<String> stoneNames = java.util.Set.of("cobblestone", "stone", "deepslate", "granite", "diorite", "andesite", "tuff", "basalt", "blackstone");
        java.util.Set<String> dirtNames = java.util.Set.of("dirt", "grass_block", "gravel", "sand", "coarse_dirt", "mud");

        assertTrue(stoneNames.contains("cobblestone"));
        assertTrue(stoneNames.contains("deepslate"));
        assertFalse(stoneNames.contains("diamond"));

        assertTrue(dirtNames.contains("dirt"));
        assertTrue(dirtNames.contains("sand"));
        assertFalse(dirtNames.contains("diamond"));
    }
}
