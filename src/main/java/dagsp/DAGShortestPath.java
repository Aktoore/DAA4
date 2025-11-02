package dagsp;

import graph.*;
import topo.TopologicalSort;
import java.util.*;

public class DAGShortestPath {
    private Graph graph;
    private Metrics metrics;
    private int[] dist;
    private int[] parent;

    public DAGShortestPath(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.dist = new int[graph.getN()];
        this.parent = new int[graph.getN()];
    }

    public void computeShortestPaths(int source) {
        metrics.startTimer();

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        MetricsImpl topoMetrics = new MetricsImpl();
        TopologicalSort topo = new TopologicalSort(graph, topoMetrics);
        List<Integer> order = topo.sort();

        if (order == null) {
            throw new IllegalArgumentException("Graph contains a cycle");
        }

        for (int u : order) {
            if (dist[u] != Integer.MAX_VALUE) {
                metrics.incrementVisits();

                for (Graph.Edge edge : graph.getEdges(u)) {
                    int v = edge.to;
                    int weight = edge.weight;
                    metrics.incrementEdgeTraversals();

                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                        metrics.incrementRelaxations();
                    }
                }
            }
        }

        metrics.stopTimer();
    }

    public int[] getDistances() { return dist; }
    public int[] getParents() { return parent; }

    public List<Integer> reconstructPath(int target) {
        if (dist[target] == Integer.MAX_VALUE) {
            return null;
        }

        List<Integer> path = new ArrayList<>();
        for (int v = target; v != -1; v = parent[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }

    public void printResults(int source) {
        System.out.println("\n=== Shortest Paths from " + source + " ===");
        for (int i = 0; i < graph.getN(); i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                System.out.println("To " + i + ": unreachable");
            } else {
                List<Integer> path = reconstructPath(i);
                System.out.println("To " + i + ": distance = " + dist[i] + ", path = " + path);
            }
        }
        System.out.println("Metrics: " + metrics);
    }
}