package graph;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.FileReader;
import java.util.*;

public class Graph {
    private int n;
    private boolean directed;
    private List<List<Edge>> adjList;
    private int source;
    private String weightModel;

    public static class Edge {
        @SerializedName("u")
        public int from;

        @SerializedName("v")
        public int to;

        @SerializedName("w")
        public int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private static class GraphData {
        boolean directed;
        int n;
        List<Edge> edges;
        int source;
        String weight_model;
    }

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    /**
     * Load graph from JSON file
     */
    public static Graph fromJson(String filename) throws Exception {
        Gson gson = new Gson();
        GraphData data = gson.fromJson(new FileReader(filename), GraphData.class);

        Graph g = new Graph(data.n, data.directed);
        g.source = data.source;
        g.weightModel = data.weight_model;

        for (Edge e : data.edges) {
            g.addEdge(e.from, e.to, e.weight);
        }

        return g;
    }

    public void addEdge(int from, int to, int weight) {
        adjList.get(from).add(new Edge(from, to, weight));
    }

    public int getN() { return n; }
    public List<Edge> getEdges(int v) { return adjList.get(v); }
    public int getSource() { return source; }
    public String getWeightModel() { return weightModel; }
    public boolean isDirected() { return directed; }
}