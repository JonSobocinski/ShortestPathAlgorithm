/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utexas.edu.shortestpathalgorithm;

import java.util.*;
import java.util.concurrent.*;

/**
 * Implements the Radius Stepping algorithm for finding shortest paths in a
 * graph.
 */
class RadiusStepping {

    private final Graph graph;
    private final int source;
    private final int[] dist;

    private LinkedList<Integer> queue;

    /**
     * Initializes the RadiusStepping algorithm with the given graph and source
     * node.
     *
     * @param graph The graph to find shortest paths in.
     * @param source The source node.
     */
    public RadiusStepping(Graph graph, int source) {
        this.graph = graph;
        this.source = source;
        this.dist = new int[graph.getAdjacencyList().length];
        queue = new LinkedList<>();
        Arrays.fill(dist, Integer.MAX_VALUE); // Initialize distances to infinity
    }

    /**
     * Runs the Radius Stepping algorithm with the specified radius value.
     *
     * @param radius The radius value for the algorithm.
     */
    public void radiusStep(int radius, boolean runInParallel) {
        int numThreads = runInParallel ? Runtime.getRuntime().availableProcessors() : 1;
        ForkJoinPool pool = new ForkJoinPool(numThreads);

        queue.add(source);
        dist[source] = 0; // Set the source node's distance to 0

        while (!queue.isEmpty()) {
            LinkedList<Integer> nodes = queue;
            queue = new LinkedList<>();
            for (Integer n: nodes) {
                pool.invoke(new RadiusStepTask(n));
            }
            while(true) {if (pool.awaitQuiescence(100, TimeUnit.SECONDS)) break;};
        }
        pool.shutdown();
    }

    private class RadiusStepTask extends RecursiveAction {

        private final int node;

        RadiusStepTask(int node) {
            this.node = node;
        }

        @Override
        protected void compute() {
            for (Edge edge : graph.getAdjacencyList()[node]) {
                int neighbour = edge.dest;
                int weight = edge.weight;

                if (dist[node] + weight < dist[neighbour]) {
                    dist[neighbour] = dist[node] + weight;
                    queue.add(neighbour);
                }
            }
        }
    }

    /**
     * Gets the computed shortest distances from the source node to all other
     * nodes.
     *
     * @return An array of shortest distances.
     */
    public int[] getShortestDistances() {
        return dist;
    }
}

/**
 * The main class to demonstrate the Radius Stepping algorithm with test cases.
 */
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

    public static long runRadiusStepping(int[][] edges, int source, int radius, boolean outputShortestPath, boolean runInParallel) {
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
            System.out.println("Shortest distances from node " + source + ":");
            for (int i = 0; i < numVertices; i++) {
                System.out.println("Node " + i + ": " + shortestDistances[i]);
            }
        }

        long executionTime = endTime - startTime;
        long totaDistance = 0;
        for (int i = 0; i < numVertices; i++) {
            totaDistance += shortestDistances[i];
        }
        System.out.println("Execution time: " + executionTime + " milliseconds | Total Distance: " + totaDistance + "\n");
        return executionTime;
    }

    public static long runRadiusStepping(int numVertices, int maxWeight, int source, int radius, boolean outputShortestPath, boolean runInParallel) {
        List<int[]> edges = generateRandomConnectedGraph(numVertices, maxWeight);
        return runRadiusStepping(edges.toArray(new int[0][]), source, radius, outputShortestPath, runInParallel);
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
