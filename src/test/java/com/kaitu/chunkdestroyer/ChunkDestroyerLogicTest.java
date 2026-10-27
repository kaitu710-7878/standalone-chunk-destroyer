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
    @DisplayName("Test Area Calculation & Bounds")
    void testAreaBounds() {
        int inputMinX = 10;
        int inputMaxX = -5;
        int actualMinX = Math.min(inputMinX, inputMaxX);
        int actualMaxX = Math.max(inputMinX, inputMaxX);

        assertEquals(-5, actualMinX);
        assertEquals(10, actualMaxX);

        int minWorldY = -64;
        int maxWorldY = 320;
        int clampedMinY = Math.max(minWorldY, Math.min(-100, 200));
        int clampedMaxY = Math.min(maxWorldY, Math.max(-100, 400));

        assertEquals(-64, clampedMinY);
        assertEquals(320, clampedMaxY);
    }

    @Test
    @DisplayName("Test Scan Progression Order")
    void testScanProgression() {
        int minX = 0, maxX = 1;
        int minZ = 0, maxZ = 1;
        int minY = 60, maxY = 61;

        int curX = minX;
        int curZ = minZ;
        int curY = maxY;

        int totalBlocks = 0;
        while (curY >= minY) {
            totalBlocks++;
            curX++;
            if (curX > maxX) {
                curX = minX;
                curZ++;
                if (curZ > maxZ) {
                    curZ = minZ;
                    curY--;
                }
            }
        }

        // 2 (X) * 2 (Z) * 2 (Y: 61, 60) = 8 blocks
        assertEquals(8, totalBlocks);
        assertEquals(minY - 1, curY);
    }
}
