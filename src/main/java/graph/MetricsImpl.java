package graph;

public class MetricsImpl implements Metrics {
    private long visits = 0;
    private long edgeTraversals = 0;
    private long relaxations = 0;
    private long startTime = 0;
    private long executionTime = 0;

    @Override
    public void incrementVisits() { visits++; }

    @Override
    public void incrementEdgeTraversals() { edgeTraversals++; }

    @Override
    public void incrementRelaxations() { relaxations++; }

    @Override
    public long getVisits() { return visits; }

    @Override
    public long getEdgeTraversals() { return edgeTraversals; }

    @Override
    public long getRelaxations() { return relaxations; }

    @Override
    public long getExecutionTimeNanos() { return executionTime; }

    @Override
    public void startTimer() { startTime = System.nanoTime(); }

    @Override
    public void stopTimer() {
        executionTime = System.nanoTime() - startTime;
    }

    @Override
    public void reset() {
        visits = 0;
        edgeTraversals = 0;
        relaxations = 0;
        executionTime = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Visits: %d, Edges: %d, Relaxations: %d, Time: %.3f ms",
                visits, edgeTraversals, relaxations, executionTime / 1_000_000.0
        );
    }
}