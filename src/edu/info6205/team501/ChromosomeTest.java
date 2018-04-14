package edu.info6205.team501;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

public class ChromosomeTest {
	private TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(5, "test.txt");

	public ChromosomeTest() throws Exception {
	}

	/* Test the constructor using Random object */
	@Test
	public void testConstructor() throws Exception {
		TSPChromosome tspChromosome = new TSPChromosome(tspGenerateAlgorithm.cityNum, new Random(),
				tspGenerateAlgorithm.distanceMap);
		assertEquals(5, tspChromosome.getChromosomeLength());
	}

	/* Test the constructor using given list */
	@Test
	public void testConstructor2() throws Exception {
		int[] testlist = { 0, 1, 2, 3, 4 };
		TSPChromosome tspChromosome = new TSPChromosome(testlist, tspGenerateAlgorithm.distanceMap);
		assertEquals(5, tspChromosome.getChromosomeLength());
		assertTrue(Arrays.equals(testlist, tspChromosome.getPhenotypeList()));
	}

	/* Test the weight */
	@Test
	public void testIsValidate() {
		int[] testlist1 = { 0, 3, 1 };
		TSPChromosome tspChromosome = new TSPChromosome(testlist1, tspGenerateAlgorithm.distanceMap);
		assertTrue(tspChromosome.isValidateChromosome());
		int[] testlist2 = { 0, 3, 1, 1 };
		tspChromosome = new TSPChromosome(testlist2, tspGenerateAlgorithm.distanceMap);
		assertFalse(tspChromosome.isValidateChromosome());
	}

	/* Test the weight */
	@Test
	public void testCallWeight1() {
		int[] testlist = { 0, 1 };
		TSPChromosome tspChromosome = new TSPChromosome(testlist, tspGenerateAlgorithm.distanceMap);
		assertEquals(1, tspChromosome.calWeight());
	}

	@Test
	public void testCallWeight2() {
		int[] testlist = { 0, 3 };
		TSPChromosome tspChromosome = new TSPChromosome(testlist, tspGenerateAlgorithm.distanceMap);
		assertEquals(2, tspChromosome.calWeight());
	}

	/* Test the crossOver */
	@Test
	public void testCrossOver() {
		TSPChromosome tspChromosome1 = new TSPChromosome(tspGenerateAlgorithm.cityNum, new Random(),
				tspGenerateAlgorithm.distanceMap);
		TSPChromosome tspChromosome2 = new TSPChromosome(tspGenerateAlgorithm.cityNum, new Random(),
				tspGenerateAlgorithm.distanceMap);
		TSPChromosome tspChromosome3 = tspChromosome1.crossOver(tspChromosome2);
		assertTrue(tspChromosome1.isValidateChromosome());
		assertTrue(tspChromosome2.isValidateChromosome());
		assertTrue(tspChromosome3.isValidateChromosome());
		assertNotEquals(tspChromosome1, tspChromosome3);
		assertNotEquals(tspChromosome2, tspChromosome3);
	}

	/* Test the mutation */
	@Test
	public void testMutation() {
		int[] testlist = { 0, 1 };
		TSPChromosome tspChromosome = new TSPChromosome(testlist, tspGenerateAlgorithm.distanceMap);
		tspChromosome.mutation();
		assertTrue(tspChromosome.isValidateChromosome());
	}

	/* Test the compareTo method */
	@Test
	public void testCompareTo() {
		int[] testlist1 = { 0, 1 };
		TSPChromosome tspChromosome1 = new TSPChromosome(testlist1, tspGenerateAlgorithm.distanceMap);
		int[] testlist2 = { 0, 1, 2 };
		TSPChromosome tspChromosome2 = new TSPChromosome(testlist2, tspGenerateAlgorithm.distanceMap);
		assertEquals(-1, tspChromosome1.compareTo(tspChromosome2));
	}
}
