package cz.cvut.fit.geotrip.business.planner.impl;

import cz.cvut.fit.geotrip.business.planner.Planner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FastPlanner implements Planner {

    private List<Integer> route;
    private double length;

    /**
     * {@inheritDoc}
     */
    @Override
    public void plan(int nodes, final double[][] distanceMatrix) {
        double symetricMatrix[][] = symetrize(nodes, distanceMatrix);
        int mst[] = mst(nodes, symetricMatrix);
        Set odd = findOdd(nodes, mst);
        int matches[] = match(nodes, symetricMatrix, odd);
        route = new LinkedList<>();
        List<List<Integer>> combined = combine(nodes, mst, matches);
        findHamiltonianCircuit(nodes, distanceMatrix, combined);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLength() {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getRoute() {
        return route;
    }

    private double[][] symetrize(int nodes, double[][] distanceMatrix) {
        double[][] symetricMatrix = new double[nodes][nodes];

        for (int i = 0; i < nodes; i++) {
            for (int j = i; j < nodes; j++) {
                double avg = (distanceMatrix[i][j] + distanceMatrix[j][i]) / 2;
                symetricMatrix[i][j] = avg;
                symetricMatrix[j][i] = avg;
            }
        }

        return symetricMatrix;
    }

    private int[] mst(int nodes, final double[][] distanceMatrix) {
        double distances[] = new double[nodes];
        Arrays.fill(distances, Double.MAX_VALUE);
        distances[0] = 0;

        int predecessors[] = new int[nodes];
        predecessors[0] = -1;

        boolean inTree[] = new boolean[nodes];

        int u = 0;

        while (u != -1) {
            inTree[u] = true;
            int next = -1;

            for (int v = 0; v < nodes; v++) {
                if (!inTree[v]) {
                    if (distanceMatrix[u][v] < distances[v]) {
                        distances[v] = distanceMatrix[u][v];
                        predecessors[v] = u;
                    }
                    if (next == -1 || distances[v] < distances[next]) {
                        next = v;
                    }
                }
            }

            u = next;
        }

        return predecessors;
    }

    private Set findOdd(int nodes, int[] mst) {
        int degrees[] = new int[nodes];
        Set odd = new HashSet();

        for (int i = 0; i < nodes; i++) {
            if (mst[i] != -1) {
                degrees[i]++;
                degrees[mst[i]]++;
            }
        }

        for (int i = 0; i < nodes; i++) {
            if (degrees[i] % 2 == 1) {
                odd.add(i);
            }
        }

        return odd;
    }

    private int[] match(int nodes, double[][] distanceMatrix, Set<Integer> odd) {
        boolean notMatched[] = new boolean[nodes];
        int matches[] = new int[nodes];

        Arrays.fill(matches, -1);

        for (int i : odd) {
            notMatched[i] = true;
        }

        for (int i : odd) {
            if (notMatched[i]) {
                double min = Double.MAX_VALUE;
                int minIndex = 0;

                for (int j : odd) {
                    if (i != j && notMatched[j] && distanceMatrix[i][j] < min) {
                        min = distanceMatrix[i][j];
                        minIndex = j;
                    }
                }

                matches[i] = minIndex;
                matches[minIndex] = i;
                notMatched[i] = false;
                notMatched[minIndex] = false;
            }
        }

        return matches;
    }

    private List<List<Integer>> combine(int nodes, int[] mst, int[] matches) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < nodes; i++) {
            graph.add(i, new LinkedList<Integer>());
        }

        for (int i = 0; i < mst.length; i++) {
            if (mst[i] != -1) {
                graph.get(i).add(mst[i]);
                graph.get(mst[i]).add(i);
            }
        }

        for (int i = 0; i < matches.length; i++) {
            if (matches[i] != -1) {
                graph.get(i).add(matches[i]);
            }
        }

        return graph;
    }

    private void findHamiltonianCircuit(int nodes, double[][] distanceMatrix, List<List<Integer>> graph) {
        List<Integer> euler = findEulerianCircuit(nodes, graph);

        Set<Integer> visited = new TreeSet<>();

        for (int i = 0; i < euler.size(); i++) {
            int v = euler.get(i);
            if (!visited.contains(v) || i == euler.size() - 1) {
                route.add(v);
                visited.add(v);
            }
        }

        for (int i = 0; i < route.size() - 1; i++) {
            length += distanceMatrix[route.get(i)][route.get(i + 1)];
        }
    }

    private List<Integer> findEulerianCircuit(int nodes, List<List<Integer>> graph) {
        List<Integer> euler = new LinkedList<>();
        Deque<Integer> stack = new LinkedList<>();

        int v = 0;

        while (!stack.isEmpty() || !graph.get(v).isEmpty()) {
            if (graph.get(v).isEmpty()) {
                euler.add(v);
                v = stack.pop();
            } else {
                stack.push(v);
                int u = graph.get(v).get(0);
                graph.get(v).remove(graph.get(v).indexOf(u));
                graph.get(u).remove(graph.get(u).indexOf(v));
                v = u;
            }
        }

        euler.add(v);

        return euler;
    }

}
