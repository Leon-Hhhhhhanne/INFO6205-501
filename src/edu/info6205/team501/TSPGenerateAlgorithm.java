
package edu.info6205.team501;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Generated;

/**
 * @author Ang Li
 *
 */
public class TSPGenerateAlgorithm {

	private final static double P_DEFAULT_CROSS = 0.95;
	private final static double P_DEFALUT_MUTATION = 0.0;
	private final static int GENERATION_NUM = 100;

	public int cityNum;
	public int[][] distanceMap;

	private ArrayList<Integer> xAxisList;
	private ArrayList<Integer> yAxisList;

	private TSPChromosome[] parentChromosomeList;
	private TSPChromosome[] childChromosomeList;
	private Random random;

	private int[] distanceList;
	private double[] fitnessList;
	private int populationNum;

	public TSPGenerateAlgorithm(int populationNum) {
		this.populationNum = populationNum;
	}

	@SuppressWarnings("unchecked")
	public void initDataFromTxtFile(String filename) throws Exception {
		xAxisList = new ArrayList<>();
		yAxisList = new ArrayList<>();
		random = new Random();

		// Read data from file line by line, the format is 1 6734 1453
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		while ((line = bufferedReader.readLine()) != null) {
			String[] strElement = line.split(" ");
			xAxisList.add(Integer.valueOf(strElement[1]));
			yAxisList.add(Integer.valueOf(strElement[2]));
		}
		bufferedReader.close();

		// Initialize the parameters
		cityNum = xAxisList.size();
		distanceMap = new int[cityNum][cityNum];
		parentChromosomeList = new TSPChromosome[populationNum];
		childChromosomeList = new TSPChromosome[populationNum];
		distanceList = new int[populationNum];
		fitnessList = new double[populationNum];

		// initialize the distance between every two nodes
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++)
				distanceMap[i][j] = distance(i, j);
		}

		// initialize the first generation of paranet chromosome list
		TSPChromosome tspChromosome;
		for (int i = 0; i < populationNum; i++) {
			tspChromosome = new TSPChromosome(cityNum, new Random(), distanceMap);
			parentChromosomeList[i] = tspChromosome;
		}

		// Calculate distance of the each individual
		calDistanceList();

		// Calculate the fitness list
		calFitnessList();
	}

	// Pseudo-Euclidean distance
	private int distance(int from, int to) {
		if (from == to)
			return 0;
		int dij;
		int xd = xAxisList.get(from) - xAxisList.get(to);
		int yd = yAxisList.get(from) - yAxisList.get(to);
		double rij = Math.sqrt((xd * xd + yd * yd) / 10.0);
		int tij = (int) Math.round(rij);
		if (tij < rij)
			dij = tij + 1;
		else
			dij = tij;
		return dij;
	}

	// Calculate the distance list using the current generation
	private void calDistanceList() {
		for (int i = 0; i < populationNum; i++) {
			distanceList[i] = parentChromosomeList[i].getWeight();
		}
	}

	// Calculate the fitness list using the current generation
	private void calFitnessList() {
		double sumFitness = 0;
		for (int i = 0; i < populationNum; i++) {
			fitnessList[i] = (double) 1 / distanceList[i];
			sumFitness += fitnessList[i];
		}
		fitnessList[0] = fitnessList[0] / sumFitness;
		for (int i = 1; i < populationNum; i++)
			fitnessList[i] = fitnessList[i - 1] + fitnessList[i] / sumFitness;
	}

	// select and generate the remaining descendents
	private void select() {
		Arrays.sort(parentChromosomeList);
		int flag;
		for (int k = 0; k < populationNum; k++) {
			// Copy the best ten percentage of entities from parents to children
			flag = random.nextInt(populationNum / 10);
			childChromosomeList[k] = parentChromosomeList[flag];
			// for (int i = 0; i < populationNum / 10; i++)
			// childChromosomeList[k] = parentChromosomeList[i];

			// Generate other entities
			// int j = 0;
			// for (int i = populationNum / 10; i < populationNum; i++)
			// childChromosomeList[i] = parentChromosomeList[j++];
			// childChromosomeList[i] = parentChromosomeList[wheelOut(random.nextDouble())];
		}
	}

	// Wheel out algorithm
	// private int wheelOut(double ran) {
	// for (int i = 0; i < populationNum; i++)
	// if (ran <= fitnessList[i])
	// return i;
	// return 0;
	// }

	// Mutation and crossover
	private void evolution() {
		for (int i = 1; i < populationNum; i++) {
			double ranDouble = random.nextDouble();
			if (ranDouble < P_DEFAULT_CROSS)
				childChromosomeList[i] = childChromosomeList[i].crossOver(childChromosomeList[i - 1]);
			else {
				ranDouble = random.nextDouble();
				if (ranDouble < P_DEFALUT_MUTATION)
					childChromosomeList[i].mutation();
			}
		}
	}

	// Calculate the best entity of the current generation
	private int bestEntity() {
		int shortestDistance = distanceList[0];
		for (int i = 1; i < populationNum; i++)
			if (distanceList[i] < shortestDistance)
				shortestDistance = distanceList[i];
		return shortestDistance;
	}

	// Generation
	public void generate() {
		for (int i = 0; i < GENERATION_NUM; i++) {
			select();
			evolution();
			parentChromosomeList = childChromosomeList.clone();
			calDistanceList();
			calFitnessList();
			System.out.println("Generation: " + i + " is " + bestEntity());
		}
	}

	public static void main(String[] args) throws Exception {
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(30);
		tspGenerateAlgorithm.initDataFromTxtFile("E:\\EclipseJavaWorkSpace\\TSP\\data\\data.txt");

		// Test initDataFromTxtFile() and distance()
		// System.out.println(tspGenerateAlgorithm.distanceList);
		// System.out.println(tspGenerateAlgorithm.xAxisList);
		// System.out.println(tspGenerateAlgorithm.yAxisList);

		// Test randomChromosome()
		// System.out.println(tspGenerateAlgorithm.randomChromosome());
		// System.out.println(tspGenerateAlgorithm.randomChromosome().size());
		// System.out.println(tspGenerateAlgorithm.cityNum);

		// Test the first generation of chromosome
		// System.out.println(tspGenerateAlgorithm.parentChromosomeList);

		// Test evaluate()
		// System.out.println(tspGenerateAlgorithm.evaluateDistance(tspGenerateAlgorithm.parentChromosomeList.get(0)));

		// Test the fitness list
		// System.out.println(tspGenerateAlgorithm.fitnessList);

		// tspGenerateAlgorithm.generate();
		// System.out.println(tspGenerateAlgorithm.bestEntity);
		// System.out.println(tspGenerateAlgorithm.shortestDistance);
		// System.out.println(tspGenerateAlgorithm.childChromosomeList);

		// Test the final situation
		// System.out.println(tspGenerateAlgorithm.shortestPathList);
		// System.out.println(tspGenerateAlgorithm.shortestDistance);

		/* Test the map */
		// System.out.println(Arrays.toString(tspGenerateAlgorithm.distanceMap[1]));

		/* Test the select function */
		// tspGenerateAlgorithm.select();
		// for (int i = 0; i < tspGenerateAlgorithm.populationNum; i++)
		// System.out
		// .println("Parent: " +
		// Arrays.toString(tspGenerateAlgorithm.parentChromosomeList[i].chromosomeList));
		// for (int i = 0; i < tspGenerateAlgorithm.populationNum; i++)
		// System.out.println("Child: " +
		// Arrays.toString(tspGenerateAlgorithm.childChromosomeList[i].chromosomeList));

		/* Test the evolution function */
		// tspGenerateAlgorithm.evolution();
		// System.out.println("------- After evolution -------");
		// for (int i = 0; i < tspGenerateAlgorithm.populationNum; i++)
		// System.out.println("Child: " +
		// Arrays.toString(tspGenerateAlgorithm.childChromosomeList[i].chromosomeList));

		/* Test the generate function */
		tspGenerateAlgorithm.generate();
	}
}
