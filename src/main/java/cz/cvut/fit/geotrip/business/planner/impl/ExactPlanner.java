package cz.cvut.fit.geotrip.business.planner.impl;

import cz.cvut.fit.geotrip.business.planner.Planner;
import java.util.LinkedList;
import java.util.List;

public class ExactPlanner implements Planner {

    private List<Integer> route;
    private double length;

    /**
     * {@inheritDoc}
     */
    @Override
    public void plan(int nodes, final double[][] distanceMatrix) {
        route = new LinkedList<>();
        double res[][] = new double[1 << nodes][nodes];

        int subset, kPow;

        for (int k = 1; k < nodes; k++) {
            res[(1 + (1 << k))][k] = distanceMatrix[0][k];
        }

        for (int s = 3; s <= nodes; s++) {
            subset = initSubset(s);
            do {
                for (int k = 0; k < nodes; k++) {
                    kPow = 1 << k;
                    if ((subset & kPow) != 0) {
                        int kRow = subset - kPow;
                        double min = Double.POSITIVE_INFINITY;

                        for (int m = 1; m < nodes; m++) {
                            if ((subset & (1 << m)) != 0 && k != m) {
                                double tmpMin = res[kRow][m] + distanceMatrix[m][k];
                                min = tmpMin < min ? tmpMin : min;
                            }
                        }

                        res[subset][k] = min;
                    }
                }
                subset = nextSubset(subset, nodes);
            } while (subset != 0);
        }

        int resSubset = initSubset(nodes);
        int prevI = 0, minI = 0;

        route.add(0);
        for (int j = 1; j < nodes; j++) {
            double min = Double.POSITIVE_INFINITY;

            for (int i = 1; i < nodes; i++) {
                if ((resSubset & (1 << i)) == 0) {
                    continue;
                }
                double tmpMin = res[resSubset][i] + distanceMatrix[prevI][i];
                if (tmpMin < min) {
                    min = tmpMin;
                    minI = i;
                }
            }

            if (j == 1) {
                length = min;
            }

            resSubset -= 1 << minI;
            prevI = minI;
            route.add(prevI);
        }
        route.add(0);
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

    private int initSubset(int s) {
        int ret = 0;
        for (int i = 0; i < s; i++) {
            ret <<= 1;
            ret |= 1;
        }
        return ret;
    }

    private int nextSubset(int s, int nodes) {
        int t = (s | (s - 1)) + 1;
        int ret = t | ((((t & -t) / (s & -s)) >> 1) - 1);
        if ((ret & (1 << nodes)) != 0) {
            return 0;
        }
        return ret;
    }
}
