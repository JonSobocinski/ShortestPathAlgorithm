/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utexas.edu.shortestpathalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShortestPathCompare {

    //Term Paper Template:
    /*
    https://www.overleaf.com/project/5f24af6fa945b90001210be8
     */
    private static final boolean OUTPUT_SHORTEST_PATH = false;
    private static final boolean RUN_IN_PARALLEL = false;
    private static final int LOOP = 100;

    private static final Map<String, Long> TIMING_MAP = new HashMap<>();

    public static void main(String[] args) {
//        singleTests();
        loopingTest();
    }

    public static void loopingTest() {
        Random random = new Random();
        /**
         * Parallel, V=1000,W=1000,S=0,D=100,R=100 Avg Completion Time For
         * Delta: 7375 ms Avg Completion Time For Radius: 8906 ms
         *
         * Serial, V=1000,W=1000,S=0,D=100,R=100 Avg Completion Time For Delta:
         * 10248 ms Avg Completion Time For Radius: 10358 ms
         *
         * Parallel, V=100,W=1000,S=0,D=1000,R=1000 Avg Completion Time For
         * Delta: 231 ms Avg Completion Time For Radius: 674 ms
         *
         * Serial, V=100,W=1000,S=0,D=1000,R=1000 Avg Completion Time For Delta:
         * 289 ms Avg Completion Time For Radius: 294 ms
         *
         * Parallel, V=10,W=1000,S=0,D=10000,R=10000 Avg Completion Time For
         * Delta: 872 ms Avg Completion Time For Radius: 882 ms
         *
         * Serial, V=10,W=1000,S=0,D=10000,R=10000 Avg Completion Time For
         * Delta: 742 ms Avg Completion Time For Radius: 814 ms
         *
         * Parallel, V=1000,W=1000,S=0,D=5,R=5 Avg Completion Time For Delta:
         * 495 ms Avg Completion Time For Radius: 579 ms
         *
         * Serial, V=1000,W=1000,S=0,D=5,R=5 Avg Completion Time For Delta: 622
         * ms Avg Completion Time For Radius: 602 ms
         *
         * Parallel, V=1000,W=2,S=R,D=2,R=2 Avg Completion Time For Delta: 217
         * ms Avg Completion Time For Radius: 225 ms
         *
         * Serial, V=1000,W=2,S=R,D=2,R=2 Avg Completion Time For Delta: 260 ms
         * Avg Completion Time For Radius: 258 ms
         *
         * Parallel, V=25,W=2,S=R,D=200000,R=200000 Avg Completion Time For
         * Delta: 6828 ms Avg Completion Time For Radius: 6861 ms
         *
         * Serial, V=25,W=2,S=R,D=200000,R=200000 Avg Completion Time For Delta:
         * 4729 ms Avg Completion Time For Radius: 4758 ms
         */
        int numVertices = 25; // Number of vertices for the random graph
        int maxWeight = 2; // Maximum edge weight for the random graph
        int source = 0; // Source node for both algorithms
        int delta = 200000; // Delta value for Delta Stepping
        int radius = 200000; // Radius value for Radius Stepping

        for (int i = 0; i < LOOP; i++) {
            source = random.nextInt(numVertices);
            System.out.println("Itteration " + i + "/" + LOOP);
            int[][] randomGraph = generateRandomConnectedGraph(numVertices, maxWeight);
            compareRandomAlgorithms(randomGraph, source, delta, radius, RUN_IN_PARALLEL);
        }

        long deltaAvg = TIMING_MAP.get("DELTA") / LOOP;
        long radiusAvg = TIMING_MAP.get("RADIUS") / LOOP;

        System.out.println("Avg Completion Time For Delta: " + deltaAvg + " ms");
        System.out.println("Avg Completion Time For Radius: " + radiusAvg + " ms");

    }

    public static void singleTests() {
        // Test Case 1: User-provided test case
        int[][] edges1 = {
            {0, 1, 2},
            {0, 3, 6},
            {1, 2, 3},
            {2, 4, 1},
            {3, 2, 1},
            {3, 4, 4}
        };
        int source1 = 0;
        int delta1 = 2;
        int radius1 = 100;

        compareAlgorithms(edges1, source1, delta1, radius1, RUN_IN_PARALLEL);

        // Test Case 2: Random test case with a small graph
        int numVertices2 = 100; // Number of vertices for the random graph
        int maxWeight2 = 10; // Maximum edge weight for the random graph
        int source2 = 0; // Source node for both algorithms
        int delta2 = 2; // Delta value for Delta Stepping
        int radius2 = 2; // Radius value for Radius Stepping

        int[][] randomGraph = generateRandomConnectedGraph(numVertices2, maxWeight2);
        compareRandomAlgorithms(randomGraph, source2, delta2, radius2, RUN_IN_PARALLEL);

        // Test Case 3: Random test case with a larger graph
        int numVertices3 = 1000; // Number of vertices for the random graph
        int maxWeight3 = 1000; // Maximum edge weight for the random graph
        int source3 = 0; // Source node for both algorithms
        int delta3 = 50; // Delta value for Delta Stepping
        int radius3 = 100; // Radius value for Radius Stepping

        randomGraph = generateRandomConnectedGraph(numVertices3, maxWeight3);
        compareRandomAlgorithms(randomGraph, source3, delta3, radius3, RUN_IN_PARALLEL);
    }

    public static void compareAlgorithms(int[][] edges, int source, int delta, int radius, boolean runInParallel) {
        System.out.println("Running test case with user-provided edges:");
        System.out.println("============================================");

        System.out.println("Delta Stepping Algorithm:");
        DeltaSteppingMain.runDeltaStepping(edges, source, delta, OUTPUT_SHORTEST_PATH, runInParallel);

        System.out.println("Radius Stepping Algorithm:");
        RadiusSteppingMain.runRadiusStepping(edges, source, radius, OUTPUT_SHORTEST_PATH, runInParallel);
    }

    public static void compareRandomAlgorithms(int[][] randomGraph, int source, int delta, int radius, boolean runInParallel) {
        System.out.println("Running random test case with " + randomGraph.length + " edges:");
        System.out.println("============================================");

        long deltaSteppingRunningTotal = TIMING_MAP.getOrDefault("DELTA", 0l);
        long radiusSteppingRunningTotal = TIMING_MAP.getOrDefault("RADIUS", 0l);

        System.out.println("Delta Stepping Algorithm:");
        deltaSteppingRunningTotal += DeltaSteppingMain.runDeltaStepping(randomGraph, source, delta, OUTPUT_SHORTEST_PATH, runInParallel);

        System.out.println("Radius Stepping Algorithm:");
        radiusSteppingRunningTotal += RadiusSteppingMain.runRadiusStepping(randomGraph, source, radius, OUTPUT_SHORTEST_PATH, runInParallel);

        TIMING_MAP.put("DELTA", deltaSteppingRunningTotal);
        TIMING_MAP.put("RADIUS", radiusSteppingRunningTotal);
    }

    public static int[][] generateRandomConnectedGraph(int numVertices, int maxWeight) {
        Random random = new Random();
        List<int[]> edgesList = new ArrayList<>();

        // Create a fully connected graph
        for (int u = 0; u < numVertices; u++) {
            for (int v = u + 1; v < numVertices; v++) {
                int weight = random.nextInt(maxWeight) + 1; // Generate a random weight
                edgesList.add(new int[]{u, v, weight});
                edgesList.add(new int[]{v, u, weight}); // Add reverse edge for an undirected graph
            }
        }

        // Shuffle the edges randomly to create a random connected graph
        Collections.shuffle(edgesList);

        // Convert the list of edges to a 2D array
        int[][] edgesArray = new int[edgesList.size()][3];
        for (int i = 0; i < edgesList.size(); i++) {
            edgesArray[i] = edgesList.get(i);
        }

        return edgesArray;
    }

    public static void shuffleArray(int[][] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

}
