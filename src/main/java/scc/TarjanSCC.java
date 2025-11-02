package scc;

import graph.*;
import java.util.*;

public class TarjanSCC {
    private Graph graph;
    private Metrics metrics;
    private int time;
    private int[] disc;
    private int[] low;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.time = 0;
        this.disc = new int[graph.getN()];
        this.low = new int[graph.getN()];
        this.onStack = new boolean[graph.getN()];
        this.stack = new Stack<>();
        this.sccs = new ArrayList<>();
        Arrays.fill(disc, -1);
    }

    public List<List<Integer>> findSCCs() {
        metrics.startTimer();

        for (int v = 0; v < graph.getN(); v++) {
            if (disc[v] == -1) {
                dfs(v);
            }
        }

        metrics.stopTimer();
        return sccs;
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;
        metrics.incrementVisits();

        for (Graph.Edge edge : graph.getEdges(u)) {
            int v = edge.to;
            metrics.incrementEdgeTraversals();

            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                scc.add(v);
            } while (v != u);
            sccs.add(scc);
        }
    }

    public void printResults() {
        System.out.println("\n=== Tarjan SCC Results ===");
        System.out.println("Number of SCCs: " + sccs.size());
        for (int i = 0; i < sccs.size(); i++) {
            System.out.println("SCC " + i + " (size " + sccs.get(i).size() + "): " + sccs.get(i));
        }
        System.out.println("Metrics: " + metrics);
    }
}