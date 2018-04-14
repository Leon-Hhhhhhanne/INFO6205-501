package edu.info6205.team501;

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
        int result = tspGenerateAlgorithm.distance(0,4);
        assertEquals(2,result);
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
        int result = tspGenerateAlgorithm.bestEntity();
        for (int re : tspGenerateAlgorithm.getDistanceList()){
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
