package cz.cvut.fit.geotrip.business.planner.impl;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;


public class ExactPlannerTest {
    
    private double[][] distanceMatrix1 = {
        {0},
    };
    
    private double[][] distanceMatrix2 = {
        {0, 1, 1, 1, 2},
        {1, 0, 1, 2, 1},
        {1, 1, 0, 1, 1},
        {1, 2, 1, 0, 1},
        {2, 1, 1, 1, 0},
    };
    
    private double[][] distanceMatrix3 = {
        { 0, 20, 42, 35},
        {20,  0, 30, 34},
        {42, 30,  0, 12},
        {35, 34, 12,  0},
    };

    private double[][] distanceMatrix4 = {
        {0, 1},
        {1, 0},
    };       
   
    private ExactPlanner instance1;
    private ExactPlanner instance2;
    private ExactPlanner instance3;
    private ExactPlanner instance4;
   
    public ExactPlannerTest() {
        instance1 = new ExactPlanner();
        instance1.plan(distanceMatrix1.length, distanceMatrix1);

        instance2 = new ExactPlanner();
        instance2.plan(distanceMatrix2.length, distanceMatrix2);
        
        instance3 = new ExactPlanner();
        instance3.plan(distanceMatrix3.length, distanceMatrix3);
        
        instance4 = new ExactPlanner();
        instance4.plan(distanceMatrix4.length, distanceMatrix4);
    }

    @Test
    public void testGetLength() {
        System.out.println("getLength");
        double expResult;
        double result;
        
        expResult = 0;
        result = instance1.getLength();
        assertTrue("expected=" + expResult + " result=" + result, Math.abs(result - expResult) < 0.000001); 
        
        expResult = 5;
        result = instance2.getLength();
        assertTrue("expected=" + expResult + " result=" + result, Math.abs(result - expResult) < 0.000001); 
        
        expResult = 97;
        result = instance3.getLength();
        assertTrue("expected=" + expResult + " result=" + result, Math.abs(result - expResult) < 0.000001); 
        
        expResult = 2;
        result = instance4.getLength();
        assertTrue("expected=" + expResult + " result=" + result, Math.abs(result - expResult) < 0.000001);
    }

    @Test
    public void testGetRoute() {
        System.out.println("getRoute");
        int expResult;
        List<Integer> result;
        
        expResult = distanceMatrix1.length;
        result = instance1.getRoute();
        assertEquals("expected nodes=" + expResult + " result nodes=" + result.size(), expResult, result.size());
        
        expResult = distanceMatrix2.length + 1;
        result = instance2.getRoute();
        assertEquals("expected nodes=" + expResult + " result nodes=" + result.size(), expResult, result.size());
        
        expResult = distanceMatrix3.length + 1;
        result = instance3.getRoute();
        assertEquals("expected nodes=" + expResult + " result nodes=" + result.size(), expResult, result.size()); 
        
        expResult = distanceMatrix4.length + 1;
        result = instance4.getRoute();
        assertEquals("expected nodes=" + expResult + " result nodes=" + result.size(), expResult, result.size()); 
    }
    
}
