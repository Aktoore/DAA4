package topo;

import graph.*;
import java.util.*;

public class TopologicalSort {
    private Graph graph;
    private Metrics metrics;

    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<Integer> sort() {
        metrics.startTimer();

        int[] inDegree = new int[graph.getN()];

        for (int u = 0; u < graph.getN(); u++) {
            for (Graph.Edge edge : graph.getEdges(u)) {
                inDegree[edge.to]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < graph.getN(); i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            metrics.incrementVisits();

            for (Graph.Edge edge : graph.getEdges(u)) {
                int v = edge.to;
                metrics.incrementEdgeTraversals();
                inDegree[v]--;

                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        metrics.stopTimer();

        if (topoOrder.size() != graph.getN()) {
            return null;
        }

        return topoOrder;
    }

    public static List<Integer> getDerivedTaskOrder(List<Integer> componentOrder,
                                                    List<List<Integer>> sccs) {
        List<Integer> taskOrder = new ArrayList<>();
        for (int compId : componentOrder) {
            taskOrder.addAll(sccs.get(compId));
        }
        return taskOrder;
    }

    public void printResults(List<Integer> order) {
        System.out.println("\n=== Topological Sort Results ===");
        if (order == null) {
            System.out.println("Graph contains a cycle - no topological order exists");
        } else {
            System.out.println("Topological Order: " + order);
        }
        System.out.println("Metrics: " + metrics);
    }
}