/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.business.planner;

import java.util.List;

/**
 *
 * @author jan
 */
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
