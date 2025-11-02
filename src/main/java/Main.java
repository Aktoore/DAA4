import dagsp.*;
import graph.*;
import scc.*;
import topo.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Временное решение для теста
        if (args.length == 0) {
            args = new String[]{"--all"};
        }

        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  java Main <json_file>           - Process single file");
            System.out.println("  java Main --all                 - Process all datasets");
            return;
        }

        try {
            if (args[0].equals("--all")) {
                processAllDatasets();
                return;
            }

            // Process single file
            processDataset(args[0], null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processAllDatasets() {
        ResultsLogger logger = new ResultsLogger();
        logger.initializeCSVs();

        String[] datasets = {
                "data/small_1.json", "data/small_2.json", "data/small_3.json",
                "data/medium_1.json", "data/medium_2.json", "data/medium_3.json",
                "data/large_1.json", "data/large_2.json", "data/large_3.json"
        };

        int processedCount = 0;
        for (String dataset : datasets) {
            File file = new File(dataset);
            if (file.exists()) {
                processedCount++;
                System.out.println("\n" + "=".repeat(60));
                System.out.println("Processing: " + dataset);
                System.out.println("=".repeat(60));
                processDataset(dataset, logger);
            } else {
                System.out.println("WARNING: File not found - " + dataset);
            }
        }

        if (processedCount > 0) {
            logger.printSummary();
        } else {
            System.out.println("\nNo datasets were processed. Please check file paths.");
        }
    }

    private static void processDataset(String filename, ResultsLogger logger) {
        try {
            // Load graph from JSON
            Graph graph = Graph.fromJson(filename);
            String datasetName = new File(filename).getName().replace(".json", "");
            int edges = countEdges(graph);

            System.out.println("\nDataset: " + datasetName);
            System.out.println("Nodes: " + graph.getN() + ", Edges: " + edges);
            System.out.println("Weight model: " + graph.getWeightModel());

            // 1. Find SCCs using Tarjan's algorithm
            MetricsImpl sccMetrics = new MetricsImpl();
            TarjanSCC tarjan = new TarjanSCC(graph, sccMetrics);
            var sccs = tarjan.findSCCs();
            tarjan.printResults();

            if (logger != null) {
                logger.logSCCResults(datasetName, graph.getN(), edges,
                        "Tarjan", sccs.size(),
                        sccMetrics.getVisits(), sccMetrics.getEdgeTraversals(),
                        sccMetrics.getExecutionTimeNanos() / 1_000_000.0);
            }

            // 2. Build condensation graph
            CondensationGraph condensation = new CondensationGraph(graph, sccs);
            Graph dag = condensation.getCondensation();
            System.out.println("\nCondensation graph: " + dag.getN() + " components");

            // 3. Topological sort on condensation
            MetricsImpl topoMetrics = new MetricsImpl();
            TopologicalSort topo = new TopologicalSort(dag, topoMetrics);
            var order = topo.sort();
            topo.printResults(order);

            // Output derived order of original tasks after SCC compression
            if (order != null) {
                var derivedOrder = TopologicalSort.getDerivedTaskOrder(order, sccs);
                System.out.println("Derived order of original tasks: " + derivedOrder);
            }

            if (logger != null) {
                logger.logTopoResults(datasetName, dag.getN(), countEdges(dag),
                        "Kahn", order != null,
                        topoMetrics.getVisits(), topoMetrics.getEdgeTraversals(),
                        topoMetrics.getExecutionTimeNanos() / 1_000_000.0);
            }

            // 4. Shortest paths in DAG (on original graph if it's a DAG)
            if (order != null && sccs.size() == graph.getN()) {
                int source = graph.getSource();

                MetricsImpl spMetrics = new MetricsImpl();
                DAGShortestPath sp = new DAGShortestPath(graph, spMetrics);
                sp.computeShortestPaths(source);
                sp.printResults(source);

                if (logger != null) {
                    logger.logDAGSPResults(datasetName, graph.getN(), edges,
                            "Shortest", source,
                            spMetrics.getVisits(), spMetrics.getEdgeTraversals(),
                            spMetrics.getRelaxations(),
                            spMetrics.getExecutionTimeNanos() / 1_000_000.0);
                }

                // 5. Longest path (critical path)
                MetricsImpl lpMetrics = new MetricsImpl();
                DAGLongestPath lp = new DAGLongestPath(graph, lpMetrics);
                lp.computeLongestPaths(source);
                lp.printResults(source);

                if (logger != null) {
                    logger.logDAGSPResults(datasetName, graph.getN(), edges,
                            "Longest", source,
                            lpMetrics.getVisits(), lpMetrics.getEdgeTraversals(),
                            lpMetrics.getRelaxations(),
                            lpMetrics.getExecutionTimeNanos() / 1_000_000.0);
                }
            } else {
                System.out.println("\nSkipping shortest/longest path analysis (graph has cycles)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int countEdges(Graph graph) {
        int count = 0;
        for (int i = 0; i < graph.getN(); i++) {
            count += graph.getEdges(i).size();
        }
        return count;
    }
}