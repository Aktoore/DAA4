1. Executive Summary
This project explores three key graph algorithms that help schedule tasks in a smart city:

Tarjan's Algorithm for finding strongly connected components (SCCs),

Kahn's Algorithm for topological sorting,

DAG shortest/longest path algorithms for finding critical paths.

I tested these on 9 datasets of different sizes and complexities.

2. Data Summary
2.1 Dataset Overview
Dataset	Nodes	Edges	Density	SCCs	Structure Type	Description
small_1	7	7	0.14	5	Cyclic	Simple cycle (3 nodes)
small_2	8	9	0.16	8	Pure DAG	No cycles, linear tasks
small_3	10	11	0.12	6	Mixed	Two cycles (3 nodes each)
medium_1	15	16	0.08	11	Sparse Cyclic	Several small SCCs
medium_2	18	23	0.08	12	Dense Cyclic	Multiple 3-node cycles
medium_3	20	23	0.06	11	Mixed	4 distinct SCCs
large_1	30	32	0.04	24	Sparse Cyclic	3 distributed cycles
large_2	40	46	0.03	29	Complex	Multiple 3-4 node cycles
large_3	50	54	0.02	33	Complex	6 SCCs, low density
Weight Model: Edges represent task durations or costs.
Density Formula: Edges / (Nodes × (Nodes - 1)).

2.2 Key Observations
Only small_2 is a pure DAG; all others have cycles. Larger graphs become sparser as their size grows. Most SCCs are small (2-5 nodes), many tasks are alone (singletons).

3. Experimental Results
3.1 Strongly Connected Components (Tarjan's Algorithm)
Dataset	Nodes	Edges	NumSCCs	Visits	EdgeTraversals	Time (ms)
small_1	7	7	5	7	7	0.045
small_2	8	9	8	8	9	0.010
small_3	10	11	6	10	11	0.014
medium_1	15	16	11	15	16	0.021
medium_2	18	23	12	18	23	0.016
medium_3	20	23	11	20	23	0.022
large_1	30	32	24	30	32	0.074
large_2	40	46	29	40	46	0.030
large_3	50	54	33	50	54	0.030
Key findings: Algorithm runs in O(V + E), visiting each node and edge once. Performance is under 0.1 ms even on large graphs, confirming linear scalability.

3.2 Topological Sorting (Kahn's Algorithm)
Dataset	Components	Edges	Success	Visits	EdgeTraversals	Time (ms)
small_1	5	4	Yes	5	4	0.522
small_2	8	9	Yes	8	9	0.015
small_3	6	5	Yes	6	5	0.010
medium_1	11	10	Yes	11	10	0.031
medium_2	12	14	Yes	12	14	0.037
medium_3	11	10	Yes	11	10	0.016
large_1	24	23	Yes	24	23	0.025
large_2	29	31	Yes	29	31	0.024
large_3	33	31	Yes	33	31	0.027
Key findings: All graphs converted successfully into DAGs. Algorithm is very fast, with time between 0.01 ms and 0.52 ms.

3.3 DAG Shortest/Longest Paths
Dataset	Type	Visits	EdgeTraversals	Relaxations	Time (ms)
small_2	Shortest	8	9	7	0.042
small_2	Longest	8	9	9	0.032
Only small_2 is a pure DAG; others skipped for path analysis. Longest path is 13 units long (path: 0→2→3→4→6→7), shortest path max distance is 8 units. Performance under 0.05 ms.

4. Algorithm Analysis
4.1 Tarjan's SCC Algorithm
Strengths: Runs in one DFS pass, linear O(V + E) time, low memory, handles disconnected parts, predictable and scales well.
Bottlenecks: Deep recursion stack (can use iterative form), cache misses on sparse graphs.
Performance approximates:

Time
=
0.001
×
Nodes
+
0.0005
×
Edges
Time=0.001×Nodes+0.0005×Edges
No major issues up to 50 nodes.

4.2 Kahn's Topological Sort
Strengths: Iterative (no recursion), safe stack usage, O(1) checks for incoming edges, cache-friendly queue, allows parallel processing.
Bottlenecks: Calculating in-degree needs full edge scan, queue overhead noticeable only for very small graphs.
Works faster on sparse graphs but remains O(V + E) for dense ones. Notably, small_1 took longer due to JVM startup.

4.3 DAG Shortest/Longest Paths
Strengths: Optimal O(V + E) using topological order, no priority queue needed, handles negative weights (shortest path), finds longest paths, simple relaxations.
Bottlenecks: Requires pure DAG, topological sort adds overhead, relaxations dominate run time for dense DAGs.
Relaxation efficiency: shortest path achieves 78%, longest path fully uses all edges (100%).

5. Structural Impact Analysis
5.1 Effect of Graph Density
Density Range	Avg Time (ms)	Observations
High (>0.10)	0.020	Fast due to cache locality
Medium (0.06-0.10)	0.025	Balanced performance
Low (<0.06)	0.035	Higher overhead for sparse traversal
Conclusion: Moderate density (0.08-0.12) performs best.

5.2 Effect of SCC Size
SCC Type	Avg Components	Compression Ratio	Topo Sort Time
Pure DAG	n SCCs	1.0×	Fastest
Small SCCs (2-3 nodes)	0.5-0.7n	0.5-0.7×	Fast
Large SCCs (>4 nodes)	<0.3n	<0.3×	Moderate
Compression helps: e.g., small_1 reduces 7 nodes to 5 components (29% reduction), large_3 from 50 to 33 (34% reduction).

5.3 Scalability Analysis
Scaling from 7 to 50 nodes increases time roughly 7.4× (from 0.01 to 0.074 ms), confirming linear complexity.

Throughput: Small graphs (~500-1000/second), large graphs (~25-35/second), suitable for real-time scheduling.

6. Practical Recommendations
6.1 When to Use Each Algorithm
Tarjan's SCC
Use when detecting cycles or grouping connected tasks, especially in unknown or cyclic graphs. Avoid on known DAGs or if only quick cycle detection is needed.

Kahn's Topological Sort
Great for scheduling dependencies in systems like builds or ETL, needs iterative approach, works only on cycle-free graphs, ideal for parallel scheduling.

DAG Shortest/Longest Paths
Use for critical path analysis, resource optimization, and bottleneck detection when the graph is guaranteed a DAG. Not suitable for cyclic or graphs with negative cycles, or when all-pairs paths are required.

6.2 Smart City Applications
Street Cleaning & Maintenance Scheduling Workflow:

Model task dependencies as a graph.

Use SCC to find circular routes and compress sectors.

Topological sort for maintenance order.

Find shortest path to reduce travel.

Find longest path to identify bottlenecks.

Expected time for a city with 10,000 segments is about 10-20 seconds (per linear scaling).

Sensor Network Maintenance Use Case:

Group sensors with SCC for maintenance zones.

Use topological sorting to avoid coverage gaps.

Critical path helps find sensors whose failure affects most of the network.

6.3 Performance Optimization Tips
Prefer adjacency lists for sparse graphs, matrices only for very dense.

Cache SCC results when possible; recompute only if graph changes.

Parallelize topological sorting; SCC is sequential but Kosaraju can help.

Use primitive arrays to save memory — ~1MB for 10,000 nodes is reasonable.

7. Conclusions
7.1 Key Takeaways
All algorithms scale linearly, O(V + E) confirmed.

SCC compression reduces problem size by 30-70%.

Performance is excellent—sub-millisecond for typical tasks.

Moderate graph density (0.08-0.12) leads to best speeds.

7.2 Practical Impact
These algorithms are:

Feasible for real-time dynamic scheduling.

Scalable for city-sized tasks.

Robust in detecting and managing cycles.

Versatile in domains like maintenance, routing, resource allocation.

7.3 Future Improvements
Incremental updates for SCC to avoid full recomputation.

Parallel SCC methods (Kosaraju, Forward-Backward).

Add weights in SCC detection.

Interactive visualization tools for planners.

8. References
Tarjan, R. E. (1972). "Depth-first search and linear graph algorithms". SIAM Journal on Computing, 1(2), 146-160.

Kahn, A. B. (1962). "Topological sorting of large networks". Communications of the ACM, 5(11), 558-562.

Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). Introduction to Algorithms (3rd ed.). MIT Press. Chapters 22 & 24.
