/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utexas.edu.shortestpathalgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a directed graph using adjacency lists.
 */
public class Graph {

    private int V;
    private List<Edge>[] adj;

    /**
     * Initializes a graph with the given number of vertices.
     *
     * @param V The number of vertices in the graph.
     */
    public Graph(int V) {
        this.V = V;
        adj = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    /**
     * Adds a directed edge between two vertices with the given weight.
     *
     * @param u The source vertex.
     * @param v The destination vertex.
     * @param weight The weight of the edge.
     */
    public void addEdge(int u, int v, int weight) {
        adj[u].add(new Edge(v, weight));
    }

    /**
     * Returns the adjacency list representation of the graph.
     *
     * @return The adjacency list.
     */
    public List<Edge>[] getAdjacencyList() {
        return adj;
    }
}

/**
 * Represents a directed edge with a destination vertex and a weight.
 */
class Edge {

    int dest;
    int weight;

    /**
     * Initializes an edge with a destination vertex and a weight.
     *
     * @param dest The destination vertex.
     * @param weight The weight of the edge.
     */
    public Edge(int dest, int weight) {
        this.dest = dest;
        this.weight = weight;
    }
}
