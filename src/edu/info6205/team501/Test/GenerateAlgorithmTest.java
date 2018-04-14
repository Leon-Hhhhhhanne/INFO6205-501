package edu.info6205.team501.Test;

import edu.info6205.team501.SourceCode.TSPGenerateAlgorithm;
import org.junit.Test;

import static org.junit.Assert.*;

public class GenerateAlgorithmTest {
    TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(30,"test.txt");

    public GenerateAlgorithmTest() throws Exception {
    }

    //test initial data file and constructor
    @Test
    public void testConstructor(){
        assertNotNull(tspGenerateAlgorithm.cityNum);
        assertNotNull(tspGenerateAlgorithm.distanceMap);
    }

    //test distance
    @Test
    public void testDistance(){
        int result = (int) Math.round(tspGenerateAlgorithm.distance(0,4));
        assertEquals(566,result);
    }

    //test call fitness
    @Test
    public void testCallDistanceList(){
        tspGenerateAlgorithm.calFitnessList();
        assertNotNull(tspGenerateAlgorithm.getFitnessList());
    }

    //test select
    @Test
    public void testSelect(){
        tspGenerateAlgorithm.select();
        assertEquals(30,tspGenerateAlgorithm.getChildChromosomeList().length);
    }

    //test evolution
    @Test
    public void testEvolution(){
        tspGenerateAlgorithm.select();
        tspGenerateAlgorithm.evolution();
        assertEquals(30,tspGenerateAlgorithm.getChildChromosomeList().length);
    }

    //test best entity
    @Test
    public void testEntity(){
        double result = tspGenerateAlgorithm.bestEntity();
        for (double re : tspGenerateAlgorithm.getDistanceList()){
            if (re == result) {
                assert (true);
                return;
            }
        }
        assert false;
    }

    //test generation
    @Test
    public void testGeneration(){
        tspGenerateAlgorithm.generate();
        assertNotNull(tspGenerateAlgorithm.bestEntity());
    }
}
