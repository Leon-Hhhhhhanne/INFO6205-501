
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

	private final static double P_DEFAULT_CROSS = 0.1;
	private final static double P_DEFALUT_MUTATION = 0;

	public int cityNum;
	
	private ArrayList<Integer> xAxisList;
	private ArrayList<Integer> yAxisList;

	private TSPChromosome[] parentChromosomeList;
	private TSPChromosome[] childChromosomeList;

	public int[][] distanceMap;
	private double[] rouletteWheelList;
	private Random random;

	public TSPGenerateAlgorithm(int populationNum) {
		this.cityNum = populationNum;
	}

	@SuppressWarnings("unchecked")
	public void initDataFromTxtFile(String filename) throws Exception {
		xAxisList = new ArrayList<>();
		yAxisList = new ArrayList<>();
		new ArrayList<>();
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
		cityNum = xAxisList.size();
		distanceMap = new int[cityNum][cityNum];
		parentChromosomeList = new TSPChromosome[cityNum];
		childChromosomeList = new TSPChromosome[cityNum];

		// initialize the distance between every two nodes
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++)
				distanceMap[i][j] = distance(i, j);
		}

		// initialize the chromosome of the first generation and the fitness list
		TSPChromosome tspChromosome;
		for (int i = 0; i < cityNum; i++) {
			tspChromosome = new TSPChromosome(cityNum, new Random(), distanceMap);
			parentChromosomeList[i] = tspChromosome;
		}
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

	// choose the best entity and put the best entity to children chromosome list
	// private void bestEntity() {
	// rouletteWheelList = new double[cityNum];
	// int shortestPath = fitnessList.get(0);
	// int shortestPosition = 0;
	// for (int i = 1; i < cityNum; i++)
	// if (fitnessList.get(i) < shortestPath) {
	// shortestPath = fitnessList.get(i);
	// shortestPosition = i;
	// }
	// bestEntity = shortestPosition;
	// childChromosomeList.set(0, parentChromosomeList.get(shortestPosition));
	//
	// // set the roulette
	// double sum = 0;
	// for (int i = 0; i < cityNum; i++) {
	// sum += (double) 10 / fitnessList.get(i);
	// }
	// for (int i = 0; i < cityNum; i++) {
	// rouletteWheelList[i] = (double) 10 / fitnessList.get(i) / sum;
	// }
	// for (int i = 1; i < cityNum; i++) {
	// rouletteWheelList[i] += rouletteWheelList[i - 1];
	// }
	// }

	// select and generate the remaining descendents
	private void select() {
		for (int i = 1; i < cityNum; i++)
			childChromosomeList.set(i, parentChromosomeList.get(wheelOut(random.nextDouble())));
	}

	private int wheelOut(double ran) {
		for (int i = 0; i < cityNum; i++) {
			if (ran <= rouletteWheelList[i]) {
				return i;
			}
		}
		return 0;
	}

	// mutation and crossover
	private void evolution() {
		for (int i = 0; i < cityNum; i += 2) {
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

	// private void generate() {
	// bestEntity();
	// select();
	// // System.out.println(childChromosomeList);
	// for (int i = 0; i < 1000; i++) {
	// evolution();
	// parentChromosomeList.get(bestEntity);
	// parentChromosomeList = (ArrayList<ArrayList<Integer>>)
	// childChromosomeList.clone();
	// // System.out.println(shortestDistance);
	// }
	// // System.out.println(childChromosomeList);
	// }

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

		tspGenerateAlgorithm.generate();
		// System.out.println(tspGenerateAlgorithm.bestEntity);
		// System.out.println(tspGenerateAlgorithm.shortestDistance);
		// System.out.println(tspGenerateAlgorithm.childChromosomeList);

		// Test the final situation
		// System.out.println(tspGenerateAlgorithm.shortestPathList);
		// System.out.println(tspGenerateAlgorithm.shortestDistance);

		// Test the map
		System.out.println(Arrays.toString(tspGenerateAlgorithm.distanceMap[1]));
	}
}
