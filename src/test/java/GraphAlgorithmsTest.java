import graph.*;
import scc.*;
import topo.*;
import dagsp.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class GraphAlgorithmsTest {

    @Test
    public void testSCC_SimpleCycle() {
        // Graph with one cycle: 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        MetricsImpl metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals("Should have 1 SCC", 1, sccs.size());
        assertEquals("SCC should contain 3 nodes", 3, sccs.get(0).size());
    }

    @Test
    public void testSCC_PureDAG() {
        // Pure DAG: 0 -> 1 -> 2
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        MetricsImpl metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals("DAG should have n SCCs", 3, sccs.size());
    }

    @Test
    public void testSCC_MultipleSCCs() {
        // Two separate cycles
        Graph graph = new Graph(6, true);
        // Cycle 1: 0 -> 1 -> 0
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        // Cycle 2: 2 -> 3 -> 2
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        // Single nodes
        graph.addEdge(4, 5, 1);

        MetricsImpl metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals("Should have 4 SCCs", 4, sccs.size());
    }

    @Test
    public void testSCC_EmptyGraph() {
        Graph graph = new Graph(3, true);

        MetricsImpl metrics = new MetricsImpl();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals("Empty graph should have n SCCs", 3, sccs.size());
    }

    @Test
    public void testTopo_SimpleDAG() {
        // 0 -> 1 -> 2
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);

        MetricsImpl metrics = new MetricsImpl();
        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.sort();

        assertNotNull("DAG should have topological order", order);
        assertEquals("Order should contain all nodes", 3, order.size());
        assertTrue("0 should come before 1", order.indexOf(0) < order.indexOf(1));
        assertTrue("1 should come before 2", order.indexOf(1) < order.indexOf(2));
    }

    @Test
    public void testTopo_WithCycle() {
        // Graph with cycle: 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        MetricsImpl metrics = new MetricsImpl();
        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.sort();

        assertNull("Graph with cycle should return null", order);
    }

    @Test
    public void testTopo_DiamondDAG() {
        // Diamond: 0 -> 1,2 -> 3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        MetricsImpl metrics = new MetricsImpl();
        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.sort();

        assertNotNull("Diamond DAG should have topological order", order);
        assertEquals(4, order.size());
        assertTrue("0 should be first", order.indexOf(0) == 0);
        assertTrue("3 should be last", order.indexOf(3) == 3);
    }

    @Test
    public void testSP_SimpleChain() {
        // 0 -> 1 (w=3) -> 2 (w=2)
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        sp.computeShortestPaths(0);

        int[] distances = sp.getDistances();
        assertEquals("Distance to 0", 0, distances[0]);
        assertEquals("Distance to 1", 3, distances[1]);
        assertEquals("Distance to 2", 5, distances[2]);
    }

    @Test
    public void testSP_MultiPath() {
        // 0 -> 1 (w=10), 0 -> 2 (w=5), 2 -> 1 (w=2)
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 5);
        graph.addEdge(2, 1, 2);

        MetricsImpl metrics = new MetricsImpl();
        DAGShortestPath sp = new DAGShortestPath(graph, metrics);
        sp.computeShortestPaths(0);

        int[] distances = sp.getDistances();
        assertEquals("Shortest path to 1 should be via 2", 7, distances[1]);
    }

    @Test
    public void testLP_SimpleChain() {
        // 0 -> 1 (w=3) -> 2 (w=2)
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 2);

        MetricsImpl metrics = new MetricsImpl();
        DAGLongestPath lp = new DAGLongestPath(graph, metrics);
        lp.computeLongestPaths(0);

        int[] critical = lp.findCriticalPath();
        assertNotNull("Should find critical path", critical);
        assertEquals("Critical path length should be 5", 5, critical[0]);
    }

    @Test
    public void testLP_MultiPath() {

        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 2);

        MetricsImpl metrics = new MetricsImpl();
        DAGLongestPath lp = new DAGLongestPath(graph, metrics);
        lp.computeLongestPaths(0);

        int[] distances = lp.getDistances();
        assertEquals("Longest path to 3 should be 7", 7, distances[3]);
    }
}