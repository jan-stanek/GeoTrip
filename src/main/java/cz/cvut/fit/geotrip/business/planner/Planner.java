package cz.cvut.fit.geotrip.business.planner;

import java.util.List;

public interface Planner {

    /**
     * Solves traveling salesman problem.
     *
     * @param nodes count of nodes
     * @param distanceMatrix matrix of route distances
     */
    public void plan(int nodes, final double[][] distanceMatrix);

    /**
     * Returns route lenght.
     *
     * @return length of route
     */
    public double getLength();

    /**
     * Returns order of caches.
     *
     * @return order of caches
     */
    public List<Integer> getRoute();
}
