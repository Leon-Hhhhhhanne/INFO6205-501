package edu.info6205.team501;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Ang Li, Xiaohan Zhao
 */
public class TSPChromosome implements Comparable<TSPChromosome> {

	private Random random = new Random();
	private int[] phenotypeList;
	private String[] genotypeList;
	private int chromosomeLength;
	private int weight;
	private int[][] map;

	// Generate a random chromosome list randomly
	public TSPChromosome(int chromosomeLength, Random random, int[][] givenMap) {
		this.chromosomeLength = chromosomeLength;
		this.phenotypeList = new int[chromosomeLength];
		this.genotypeList = new String[chromosomeLength];
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
		for (int i = 0; i < chromosomeLength; i++) {
			phenotypeList[i] = tempList.get(i);
			genotypeList[i] = phenotypeToGenotype(phenotypeList[i]);
		}

		// Initialize
		initialize();
	}

	// Set the chromosome list to the given list
	public TSPChromosome(int[] givenChromosomeList, int[][] givenMap) {
		this.chromosomeLength = givenChromosomeList.length;
		this.phenotypeList = givenChromosomeList.clone();
		this.genotypeList = new String[chromosomeLength];
		this.map = givenMap.clone();
		for (int i = 0; i < chromosomeLength; i++)
			genotypeList[i] = phenotypeToGenotype(phenotypeList[i]);

		// Initialize
		initialize();
	}

	public int getChromosomeLength() {
		return chromosomeLength;
	}

	public int[] getPhenotypeList() {
		return phenotypeList;
	}

	public String[] getGenoTypeList() {
		return genotypeList;
	}

	public int[][] getMap() {
		return map;
	}

	// Phenotype List to Genotype List
	private String phenotypeToGenotype(int phenotype) {
		if (phenotype >= 256)
			throw new IllegalArgumentException();
		int num1 = phenotype / 64;
		int num2 = (phenotype - num1 * 64) / 16;
		int num3 = (phenotype - num1 * 64 - num2 * 16) / 4;
		int num4 = phenotype - num1 * 64 - num2 * 16 - num3 * 4;
		return toGene(num1) + toGene(num2) + toGene(num3) + toGene(num4);
	}

	private String toGene(int pheno) {
		switch (pheno) {
		case 0:
			return "A";
		case 1:
			return "G";
		case 2:
			return "C";
		case 3:
			return "T";
		default:
			throw new IllegalArgumentException();
		}
	}

	// Genotype List to Phenotype List
	private int genotypeToPhenotype(String phenotype) {
		char[] phenotypeCharList = phenotype.toCharArray();
		return toPheno(phenotypeCharList[0]) * 64 + toPheno(phenotypeCharList[1]) * 16
				+ toPheno(phenotypeCharList[2]) * 4 + toPheno(phenotypeCharList[3]);
	}

	private int toPheno(char gene) {
		switch (gene) {
		case 'A':
			return 0;
		case 'G':
			return 1;
		case 'C':
			return 2;
		case 'T':
			return 3;
		default:
			throw new IllegalArgumentException();
		}
	}

	// Get the weight
	public int getWeight() {
		return this.weight;
	}

	// Initialize the parameters:weight
	public void initialize() {
		this.weight = calWeight();
	}

	// Calculate the whole weight of this chromosome list
	public int calWeight() {
		int totalWeight = 0;
		for (int i = 0; i < chromosomeLength - 1; i++)
			totalWeight += map[phenotypeList[i]][phenotypeList[i + 1]];
		totalWeight += map[phenotypeList[chromosomeLength - 1]][0];
		return totalWeight;
	}

	// Cross over with another chromosome list
	public TSPChromosome crossOver(TSPChromosome that) {
		int[] newPhenotypeList = new int[chromosomeLength];
		String[] newGenotypeList = new String[chromosomeLength];
		// Random location from 1 to chromosome length - 1
		int randomLocation = Math.abs(random.nextInt() % (chromosomeLength - 1)) + 1;

		// The start (from random location to end) gene list are from this
		for (int i = 0, j = randomLocation; j < chromosomeLength; i++, j++)
			newGenotypeList[i] = genotypeList[j];

		// The other gene list are from that and the repeated genes are removed
		for (int i = chromosomeLength - randomLocation, j = 0; i < chromosomeLength;) {
			newGenotypeList[i] = that.genotypeList[j++];
			int k;
			for (k = 0; k < chromosomeLength - randomLocation; k++)
				if (newGenotypeList[k].equals(newGenotypeList[i]))
					break;
			if (k == chromosomeLength - randomLocation)
				i++;
		}

		for (int i = 0; i < chromosomeLength; i++)
			newPhenotypeList[i] = genotypeToPhenotype(newGenotypeList[i]);
		return new TSPChromosome(newPhenotypeList, this.map);
	}

	// Mutate itself randomly
	public void mutation() {
		int randomLocation1, randomLocataion2;
		String temp;

		// Generate two different gene
		randomLocation1 = Math.abs(random.nextInt() % chromosomeLength);
		randomLocataion2 = Math.abs(random.nextInt() % chromosomeLength);
		while (randomLocation1 == randomLocataion2)
			randomLocataion2 = Math.abs(random.nextInt() % chromosomeLength);

		// Exchange the gene located in the random locations
		temp = genotypeList[randomLocation1];
		genotypeList[randomLocation1] = genotypeList[randomLocataion2];
		genotypeList[randomLocataion2] = temp;
		for (int i = 0; i < chromosomeLength; i++)
			phenotypeList[i] = genotypeToPhenotype(genotypeList[i]);
	}

	public boolean isValidateChromosome() {
		Map<Integer, Integer> validateMap = new HashMap<>();
		for (int i = 0; i < chromosomeLength; i++) {
			if (null == validateMap.get(phenotypeList[i]))
				validateMap.put(phenotypeList[i], 0);
			else
				return false;
		}
		return true;
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
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(10, "data.txt");
		// tspGenerateAlgorithm.initDataFromTxtFile("data.txt");
		TSPChromosome tspChromosome = new TSPChromosome(tspGenerateAlgorithm.cityNum, new Random(),
				tspGenerateAlgorithm.distanceMap);
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));
		// System.out.println(tspChromosome.chromosomeLength);
		// TODO:Continue test if there are any repeated values in the list

		/* Test the constructor using given list */
		// int[] testlist = { 1, 2, 3, 4, 5 };
		// tspChromosome = new TSPChromosome(testlist,tspGenerateAlgorithm.distanceMap);
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));
		// System.out.println(tspChromosome.chromosomeLength);

		/* Test the map obtained from the TSPGenerateAlgorithm */
		// System.out.println(Arrays.toString(tspChromosome.map[4]));

		/* Test the weight */
		// tspChromosome.initialize();
		// System.out.println(tspChromosome.calWeight(tspChromosome.map));

		/* Test the mutation function */
		// System.out.println(Arrays.toString(tspChromosome.chromosomeList));
		// tspChromosome.mutation();
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

		/* Test the phenotype to genotype function */
		// System.out.println(tspChromosome.phenotypeToGenotype(25));
	}

}
