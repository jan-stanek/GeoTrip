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
    
    public void plan(int nodes, double[][] distanceMatrix);
    public double getLength();
    public List<Integer> getRoute();
}
