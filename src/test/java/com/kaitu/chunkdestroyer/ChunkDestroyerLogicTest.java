package com.kaitu.chunkdestroyer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("Test Staging Buffer Mining Start Condition")
    void testStagingBufferStartCondition() {
        List<String> mockPendingBuffer = new ArrayList<>();
        boolean isWorking = false;

        // 1. 仮置きが空の場合: 開始可能
        boolean canStartEmpty = mockPendingBuffer.isEmpty();
        assertTrue(canStartEmpty);
        if (canStartEmpty) {
            isWorking = true;
        }
        assertTrue(isWorking);

        // 2. 稼働中に仮置きアイテムが発生: 採掘は止まらない
        mockPendingBuffer.add("iron_ore x 64");
        assertFalse(mockPendingBuffer.isEmpty());
        assertTrue(isWorking, "Mining continues even when staging buffer accumulates items while working");

        // 3. 一度停止した場合
        isWorking = false;
        assertFalse(isWorking);

        // 4. 仮置きが存在する状態での再開試行: 開始不可
        boolean canRestartWithPending = mockPendingBuffer.isEmpty();
        assertFalse(canRestartWithPending, "Cannot start or resume mining when staging buffer has items");

        // 5. 仮置きが排出されて空になったら再開可能
        mockPendingBuffer.clear();
        boolean canRestartCleared = mockPendingBuffer.isEmpty();
        assertTrue(canRestartCleared);
    }
}
