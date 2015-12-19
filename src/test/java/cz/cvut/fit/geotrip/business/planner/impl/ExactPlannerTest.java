/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.geotrip.business.planner.impl;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author jan
 */
public class ExactPlannerTest {
    
    private int nodes = 5;
    
    private double[][] distanceMatrix = {
            {0, 1, 1, 1, 2},
            {1, 0, 1, 2, 1},
            {1, 1, 0, 1, 1},
            {1, 2, 1, 0, 1},
            {2, 1, 1, 1, 0},
        };
        
    private ExactPlanner instance;

    
    public ExactPlannerTest() {
        instance = new ExactPlanner();
        instance.plan(nodes, distanceMatrix);
    }

    @Test
    public void testGetLength() {
        System.out.println("getLength");
        double expResult = 5;
        double result = instance.getLength();
        assertTrue("expected=" + expResult + " result=" + result, Math.abs(result - expResult) < 0.000001); 
    }

    @Test
    public void testGetRoute() {
        System.out.println("getRoute");
        int expResult = nodes + 1;
        List<Integer> result = instance.getRoute();
        assertEquals("expected nodes=" + expResult + " result nodes=" + result.size(), expResult, result.size());
    }
    
}
