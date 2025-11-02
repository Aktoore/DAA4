package graph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResultsLogger {
    private static final String RESULTS_DIR = "results/";
    private static final String SCC_RESULTS = RESULTS_DIR + "scc_results.csv";
    private static final String TOPO_RESULTS = RESULTS_DIR + "topo_results.csv";
    private static final String DAGSP_RESULTS = RESULTS_DIR + "dagsp_results.csv";

    public ResultsLogger() {
        try {
            Files.createDirectories(Paths.get(RESULTS_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeCSVs() {
        initializeSCCResults();
        initializeTopoResults();
        initializeDAGSPResults();
    }

    private void initializeSCCResults() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCC_RESULTS))) {
            writer.println("Dataset,Nodes,Edges,Algorithm,NumSCCs,Visits,EdgeTraversals,TimeMS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTopoResults() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TOPO_RESULTS))) {
            writer.println("Dataset,Nodes,Edges,Algorithm,Success,Visits,EdgeTraversals,TimeMS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeDAGSPResults() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DAGSP_RESULTS))) {
            writer.println("Dataset,Nodes,Edges,PathType,Source,Visits,EdgeTraversals,Relaxations,TimeMS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logSCCResults(String dataset, int nodes, int edges,
                              String algorithm, int numSCCs,
                              long visits, long edgeTraversals, double timeMS) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCC_RESULTS, true))) {
            writer.printf("%s,%d,%d,%s,%d,%d,%d,%.3f%n",
                    dataset, nodes, edges, algorithm, numSCCs,
                    visits, edgeTraversals, timeMS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log Topological Sort results
     */
    public void logTopoResults(String dataset, int nodes, int edges,
                               String algorithm, boolean success,
                               long visits, long edgeTraversals, double timeMS) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TOPO_RESULTS, true))) {
            writer.printf("%s,%d,%d,%s,%s,%d,%d,%.3f%n",
                    dataset, nodes, edges, algorithm, success ? "Yes" : "No",
                    visits, edgeTraversals, timeMS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logDAGSPResults(String dataset, int nodes, int edges,
                                String pathType, int source,
                                long visits, long edgeTraversals,
                                long relaxations, double timeMS) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DAGSP_RESULTS, true))) {
            writer.printf("%s,%d,%d,%s,%d,%d,%d,%d,%.3f%n",
                    dataset, nodes, edges, pathType, source,
                    visits, edgeTraversals, relaxations, timeMS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printSummary() {
        System.out.println("\n=== RESULTS SUMMARY ===");
        System.out.println("Results saved to:");
        System.out.println("  - " + SCC_RESULTS);
        System.out.println("  - " + TOPO_RESULTS);
        System.out.println("  - " + DAGSP_RESULTS);
        System.out.println("\nYou can analyze these CSV files in Excel or any data analysis tool.");
    }
}