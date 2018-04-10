
package edu.info6205.team501;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.annotation.Generated;

/**
 * @author Ang Li
 *
 */
public class TSPGenerateAlgorithm {

	private final static double P_DEFAULT_CROSS = 0.9;
	private final static double P_DEFALUT_MUTATION = 0.1;

	private ArrayList<ArrayList<Integer>> distanceList;
	private ArrayList<Integer> xAxisList;
	private ArrayList<Integer> yAxisList;
	private ArrayList<ArrayList<Integer>> parentChromosomeList;
	private ArrayList<ArrayList<Integer>> childChromosomeList;
	private ArrayList<Integer> fitnessList;
	private double[] rouletteWheelList;
	private int cityNum;
	private int populationNum;
	private Random random;
	private int generationCounter;
	private int shortestDistance;
	private int bestEntity;

	public TSPGenerateAlgorithm(int populationNum) {
		this.populationNum = populationNum;
	}

	@SuppressWarnings("unchecked")
	private void initDataFromTxtFile(String filename) throws Exception {
		xAxisList = new ArrayList<>();
		yAxisList = new ArrayList<>();
		distanceList = new ArrayList<>();
		parentChromosomeList = new ArrayList<>();
		childChromosomeList = new ArrayList<>();
		fitnessList = new ArrayList<>();
		random = new Random();
		generationCounter = 0;

		// Read data from file line by line, the format is 1 6734 1453
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		while ((line = bufferedReader.readLine()) != null) {
			String[] strElement = line.split(" ");
			xAxisList.add(Integer.valueOf(strElement[1]));
			yAxisList.add(Integer.valueOf(strElement[2]));
		}
		bufferedReader.close();
		cityNum = xAxisList.size();

		// initialize the distance between every two nodes
		for (int i = 0; i < populationNum; i++) {
			ArrayList<Integer> childDistanceList = new ArrayList<>();
			for (int j = 0; j < cityNum; j++)
				childDistanceList.add(distance(i, j));
			distanceList.add(childDistanceList);
		}

		// initialize the chromosome of the first generation and the fitness list
		for (int i = 0; i < populationNum; i++) {
			parentChromosomeList.add(randomChromosome());
			fitnessList.add(evaluateDistance(parentChromosomeList.get(i)));
		}
		childChromosomeList = (ArrayList<ArrayList<Integer>>) parentChromosomeList.clone();
	}

	// Pseudo-Euclidean distance
	// TODO: optimize by using system Math method(if exist)
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

	// Generate a random chromosome list
	private ArrayList<Integer> randomChromosome() {
		ArrayList<Integer> randomChromosomeList = new ArrayList<>();
		int counter = 0;
		while (counter < cityNum) {
			int randomCity = Math.abs(random.nextInt() % cityNum);
			if (!randomChromosomeList.contains(randomCity)) {
				randomChromosomeList.add(randomCity);
				counter++;
			}
		}
		return randomChromosomeList;
	}

	// Calculate the total distance for a given chromosome list
	private int evaluateDistance(ArrayList<Integer> chromoSomeList) {
		int length = 0;
		for (int i = 0; i < cityNum - 1; i++)
			length += distance(chromoSomeList.get(i + 1), chromoSomeList.get(i));
		length += distance(chromoSomeList.get(cityNum - 1), chromoSomeList.get(0));
		return length;
	}

	// choose the best entity and put the best entity to children chromosome list
	private void bestEntity() {
		rouletteWheelList = new double[populationNum];
		int shortestPath = fitnessList.get(0);
		int shortestPosition = 0;
		for (int i = 1; i < populationNum; i++)
			if (fitnessList.get(i) < shortestPath) {
				shortestPath = fitnessList.get(i);
				shortestPosition = i;
			}
		shortestDistance = shortestPath;
		bestEntity = shortestPosition;
		childChromosomeList.set(0, parentChromosomeList.get(shortestPosition));

		// set the roulette
		double sum = 0;
		for (int i = 0; i < populationNum; i++) {
			sum += (double) 10 / fitnessList.get(i);
		}
		for (int i = 0; i < populationNum; i++) {
			rouletteWheelList[i] = (double) 10 / fitnessList.get(i) / sum;
		}
		for (int i = 1; i < populationNum; i++) {
			rouletteWheelList[i] += rouletteWheelList[i - 1];
		}
	}

	// select and generate the remaining descendents
	private void select() {
		for (int i = 1; i < populationNum; i++)
			childChromosomeList.set(i, parentChromosomeList.get(wheelOut(random.nextDouble())));
	}

	private int wheelOut(double ran) {
		for (int i = 0; i < populationNum; i++) {
			if (ran <= rouletteWheelList[i]) {
				return i;
			}
		}
		return 0;
	}

	// mutation and crossover
	private void evolution() {
		for (int i = 0; i < populationNum; i += 2) {
			double ranDouble = random.nextDouble();
			if (ranDouble < P_DEFAULT_CROSS)
				crossOver(i, i + 1);
			else {
				ranDouble = random.nextDouble();
				if (ranDouble < P_DEFALUT_MUTATION)
					mutation(i);
				ranDouble = random.nextDouble();
				if (ranDouble < P_DEFALUT_MUTATION) {
					mutation(i + 1);
				}
			}
		}
	}

	private void crossOver(int entity1, int entity2) {

		int[] tempChromosomeList1 = new int[populationNum];
		int[] tempChromosomeList2 = new int[populationNum];
		int randomCity1 = Math.abs(random.nextInt() % populationNum);
		int randomCity2 = Math.abs(random.nextInt() % populationNum);
		while (randomCity1 == randomCity2)
			randomCity2 = Math.abs(random.nextInt() % populationNum);
		if (randomCity1 > randomCity2) {
			int temp = randomCity1;
			randomCity1 = randomCity2;
			randomCity2 = temp;
		}

		// Move some chromosome to temp chromosome list 2
		int i, j, k;
		for (i = 0, j = randomCity2; j < populationNum; i++, j++)
			tempChromosomeList2[i] = childChromosomeList.get(entity1).get(j);

		int startLocation = i;
		for (k = 0, j = startLocation; j < populationNum;) {
			tempChromosomeList2[j] = childChromosomeList.get(entity2).get(k++);
			for (i = 0; i < startLocation; i++)
				if (tempChromosomeList2[i] == tempChromosomeList2[j])
					break;
			if (i == startLocation)
				j++;
		}

		startLocation = randomCity1;
		for (k = 0, j = 0; k < populationNum;) {
			tempChromosomeList1[j] = childChromosomeList.get(entity1).get(k++);
			for (i = 0; i < startLocation; i++)
				if (tempChromosomeList1[j] == childChromosomeList.get(entity2).get(i))
					break;
			if (i == startLocation)
				j++;
		}

		startLocation = populationNum - randomCity1;
		for (i = 0, j = startLocation; j < populationNum; j++, i++)
			tempChromosomeList1[j] = childChromosomeList.get(entity2).get(i);

		for (i = 0; i < populationNum; i++) {
			childChromosomeList.get(entity1).set(i, tempChromosomeList1[i]);
			childChromosomeList.get(entity2).set(i, tempChromosomeList2[i]);
		}
	}

	private void mutation(int target) {

	}

	private void generate() {
		generationCounter++;
		bestEntity();
		select();
		System.out.println(childChromosomeList);
		evolution();
		System.out.println(childChromosomeList);
	}

	public static void main(String[] args) throws Exception {
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(10);
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

		tspGenerateAlgorithm.generate();
		// System.out.println(tspGenerateAlgorithm.bestEntity);
		// System.out.println(tspGenerateAlgorithm.shortestDistance);
		// System.out.println(tspGenerateAlgorithm.childChromosomeList);
	}
}
