package edu.info6205.team501;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Ang Li, Xiaohan Zhao
 *
 */
public class TSPChromosome implements Comparable<TSPChromosome> {

	private Random random = new Random();
	public int[] chromosomeList;
	private int chromosomeLength;
	private int weight;
	private int[][] map;

	// Generate a random chromosome list randomly
	public TSPChromosome(int chromosomeLength, Random random, int[][] givenMap) {
		this.chromosomeLength = chromosomeLength;
		this.chromosomeList = new int[chromosomeLength];
		this.map = givenMap.clone();

		// Generate the random gene list using arraylist
		ArrayList<Integer> tempList = new ArrayList<>();
		int counter = 0;
		while (counter < chromosomeLength) {
			int randomCity = Math.abs(random.nextInt() % chromosomeLength);
			if (!tempList.contains(randomCity)) {
				tempList.add(randomCity);
				counter++;
			}
		}

		// Copy the temp list to the chromosome list
		for (int i = 0; i < chromosomeLength; i++)
			chromosomeList[i] = tempList.get(i);

		// Initialize
		initialize();
	}

	// Get the weight
	public int getWeight() {
		return this.weight;
	}

	// Set the chromosome list to the given list
	public TSPChromosome(int[] givenChromosomeList, int[][] givenMap) {
		this.chromosomeLength = givenChromosomeList.length;
		this.chromosomeList = givenChromosomeList.clone();
		this.map = givenMap.clone();

		// Initialize
		initialize();
	}

	// Initialize the parameters:weight
	private void initialize() {
		this.weight = calWeight(map);
	}

	// Calculate the whole weight of this chromosome list
	private int calWeight(int[][] map) {
		int totalWeight = 0;
		for (int i = 0; i < chromosomeLength - 1; i++)
			totalWeight += map[chromosomeList[i]][chromosomeList[i + 1]];
		return totalWeight;
	}

	// Cross over with another chromosome list
	public TSPChromosome crossOver(TSPChromosome that) {
		int[] newChromosomeList = new int[chromosomeLength];
		// Random location from 1 to chromosome length - 1
		int randomLocation = Math.abs(random.nextInt() % (chromosomeLength - 1)) + 1;

		// The start (from random location to end) gene list are from this
		for (int i = 0, j = randomLocation; j < chromosomeLength; i++, j++)
			newChromosomeList[i] = chromosomeList[j];

		// The other gene list are from that and the repeated genes are removed
		for (int i = chromosomeLength - randomLocation, j = 0; i < chromosomeLength;) {
			newChromosomeList[i] = that.chromosomeList[j++];
			int k;
			for (k = 0; k < chromosomeLength - randomLocation; k++)
				if (newChromosomeList[k] == newChromosomeList[i])
					break;
			if (k == chromosomeLength - randomLocation)
				i++;

		}
		return new TSPChromosome(newChromosomeList, this.map);
	}

	// Mutate itself randomly
	public void mutation() {
		int randomLocation1, randomLocataion2, temp, count;

		count = Math.abs(random.nextInt() % chromosomeLength);

		// Generate two different gene
		randomLocation1 = Math.abs(random.nextInt() % chromosomeLength);
		randomLocataion2 = Math.abs(random.nextInt() % chromosomeLength);
		while (randomLocation1 == randomLocataion2)
			randomLocataion2 = Math.abs(random.nextInt() % chromosomeLength);

		// Exchange the gene located in the random locations
		temp = chromosomeList[randomLocation1];
		chromosomeList[randomLocation1] = chromosomeList[randomLocataion2];
		chromosomeList[randomLocataion2] = temp;
	}

	@Override
	public int compareTo(TSPChromosome that) {
		if (weight < that.weight)
			return -1;
		if (weight > that.weight)
			return 1;
		return 0;
	}

	// Main method is for testing
	public static void main(String[] args) throws Exception {

		/* Test the constructor using Random object */
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(10);
		tspGenerateAlgorithm.initDataFromTxtFile("E:\\EclipseJavaWorkSpace\\TSP\\data\\data.txt");
		TSPChromosome tspChromosome = new TSPChromosome(tspGenerateAlgorithm.cityNum, new Random(),
				tspGenerateAlgorithm.distanceMap);
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));
		// System.out.println(tspChromosome.chromosomeLength);
		// TODO:Continue test if there are any repeated values in the list

		/* Test the constructor using given list */
		// int[] testlist = { 1, 2, 3, 4, 5 };
		// tspChromosome = new TSPChromosome(testlist);
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));
		// System.out.println(tspChromosome.chromosomeLength);

		/* Test the map obtained from the TSPGenerateAlgorithm */
		// System.out.println(Arrays.toString(tspChromosome.map[4]));

		/* Test the weight */
		tspChromosome.initialize();
		// System.out.println(tspChromosome.calWeight(tspChromosome.map));

		/* Test the mutation function */
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));
		tspChromosome.mutation();
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));

		/* Test the crossover function */
		// TSPChromosome newtspChromosome = new
		// TSPChromosome(tspGenerateAlgorithm.cityNum, new Random(),
		// tspGenerateAlgorithm.distanceMap);
		// System.out.println("Father: " +
		// Arrays.toString(tspChromosome.chromosomeList));
		// System.out.println("Mother: " +
		// Arrays.toString(newtspChromosome.chromosomeList));
		// System.out.println("Child: " +
		// Arrays.toString(tspChromosome.crossOver(newtspChromosome).chromosomeList));
	}

}
