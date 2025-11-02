package scc;

import graph.*;
import java.util.*;

public class CondensationGraph {
    private Graph originalGraph;
    private List<List<Integer>> sccs;
    private Graph condensation;
    private Map<Integer, Integer> nodeToComponent;

    public CondensationGraph(Graph originalGraph, List<List<Integer>> sccs) {
        this.originalGraph = originalGraph;
        this.sccs = sccs;
        this.nodeToComponent = new HashMap<>();
        buildMapping();
        buildCondensation();
    }

    private void buildMapping() {
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                nodeToComponent.put(node, i);
            }
        }
    }

    private void buildCondensation() {
        condensation = new Graph(sccs.size(), true);
        Set<String> addedEdges = new HashSet<>();

        for (int u = 0; u < originalGraph.getN(); u++) {
            int compU = nodeToComponent.get(u);

            for (Graph.Edge edge : originalGraph.getEdges(u)) {
                int v = edge.to;
                int compV = nodeToComponent.get(v);

                if (compU != compV) {
                    String edgeKey = compU + "-" + compV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(compU, compV, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }
    }

    public Graph getCondensation() { return condensation; }
    public Map<Integer, Integer> getMapping() { return nodeToComponent; }
}