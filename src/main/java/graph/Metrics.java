package graph;

public interface Metrics {
    void incrementVisits();
    void incrementEdgeTraversals();
    void incrementRelaxations();
    long getVisits();
    long getEdgeTraversals();
    long getRelaxations();
    long getExecutionTimeNanos();
    void startTimer();
    void stopTimer();
    void reset();
}