/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utexas.edu.shortestpathalgorithm;

import java.util.*;
import java.util.concurrent.*;

class RadiusStepping {

    private Graph graph;
    private int source;
    private int[] dist;

    public RadiusStepping(Graph graph, int source) {
        this.graph = graph;
        this.source = source;
        this.dist = new int[graph.getAdjacencyList().length];
        Arrays.fill(dist, Integer.MAX_VALUE); // Initialize distances to infinity
    }

    public void radiusStep(int radius, boolean runInParallel) {
        dist[source] = 0; // Set the source node's distance to 0
        int numThreads = runInParallel ? Runtime.getRuntime().availableProcessors() : 1;
        ForkJoinPool pool = new ForkJoinPool(numThreads);

        for (int r = 1; r <= radius; r++) {
            pool.invoke(new RadiusStepTask(0, dist.length));
        }

        pool.shutdown();
    }

    private class RadiusStepTask extends RecursiveAction {

        private int start;
        private int end;

        RadiusStepTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            for (int u = start; u < end; u++) {
                for (Edge edge : graph.getAdjacencyList()[u]) {
                    int v = edge.dest;
                    int weight = edge.weight;

                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                    }
                }
            }

            if (end - start > 1) {
                int middle = (start + end) / 2;
                RadiusStepTask leftTask = new RadiusStepTask(start, middle);
                RadiusStepTask rightTask = new RadiusStepTask(middle, end);
                invokeAll(leftTask, rightTask);
            }
        }
    }

    public int[] getShortestDistances() {
        return dist;
    }
}

public class RadiusSteppingMain {

    public static void main(String[] args) {
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
        int radius1 = 2;

        runRadiusStepping(edges1, source1, radius1, true, true);

        // Test Case 2: Randomly generated test case
        int numVertices2 = 100; // Number of vertices for the random graph
        int maxWeight2 = 10; // Maximum edge weight for the random graph
        int source2 = 0; // Source node for the Radius Stepping algorithm
        int radius2 = 2; // Radius value for the Radius Stepping algorithm

        runRadiusStepping(numVertices2, maxWeight2, source2, radius2, true, true);
    }

    public static void runRadiusStepping(int[][] edges, int source, int radius, boolean outputShortestPath, boolean runInParallel) {
        int numVertices = Arrays.stream(edges)
                .flatMapToInt(Arrays::stream)
                .max()
                .orElse(0) + 1;

        // Add edges to the graph
        Graph graph = new Graph(numVertices);
        for (int[] edge : edges) {
            graph.addEdge(edge[0], edge[1], edge[2]);
        }

        // Run the parallel Radius Stepping algorithm
        RadiusStepping radiusStepping = new RadiusStepping(graph, source);

        long startTime = System.currentTimeMillis(); // Record start time
        radiusStepping.radiusStep(radius, runInParallel);
        long endTime = System.currentTimeMillis(); // Record end time

        int[] shortestDistances = radiusStepping.getShortestDistances();

        if (outputShortestPath) {
            // Print the shortest distances and execution time
            System.out.println("Shortest distances from node " + source + " with radius " + radius + ":");
            for (int i = 0; i < numVertices; i++) {
                System.out.println("Node " + i + ": " + shortestDistances[i]);
            }
        }

        long executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime + " milliseconds\n");
    }

    public static void runRadiusStepping(int numVertices, int maxWeight, int source, int radius, boolean outputShortestPath, boolean runInParallel) {
        List<int[]> edges = generateRandomConnectedGraph(numVertices, maxWeight);
        runRadiusStepping(edges.toArray(new int[0][]), source, radius, outputShortestPath, runInParallel);
    }

    public static List<int[]> generateRandomConnectedGraph(int numVertices, int maxWeight) {
        List<int[]> edges = new ArrayList<>();
        Random random = new Random();

        // Create a fully connected graph
        for (int u = 0; u < numVertices; u++) {
            for (int v = u + 1; v < numVertices; v++) {
                int weight = random.nextInt(maxWeight) + 1; // Generate a random weight
                edges.add(new int[]{u, v, weight});
                edges.add(new int[]{v, u, weight}); // Add reverse edge for undirected graph
            }
        }

        // Shuffle the edges randomly to create a random connected graph
        Collections.shuffle(edges);

        return edges;
    }
}
