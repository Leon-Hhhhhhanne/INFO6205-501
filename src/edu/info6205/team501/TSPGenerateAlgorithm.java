
package edu.info6205.team501;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.junit.experimental.theories.Theories;

/**
 * @author Ang Li, Xiaohan Zhao
 *
 */
public class TSPGenerateAlgorithm {

	private final static double P_DEFAULT_CROSS = 1;
	private final static double P_DEFALUT_MUTATION = 0;
	private final static int GENERATION_NUM = 100;
	private final static int DEFAULT_POPULATION_NUM = 30000;

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
	private int[] bestPhenotypeList;
	private String[] bestGenotypeList;

	public TSPGenerateAlgorithm(String filename) throws Exception {
		this.populationNum = DEFAULT_POPULATION_NUM;
		initDataFromTxtFile(filename);
	}

	public TSPGenerateAlgorithm(int populationNum, String filename) throws Exception {
		this.populationNum = populationNum;
		initDataFromTxtFile(filename);
	}

	public double[] getFitnessList() {
		return fitnessList;
	}

	public TSPChromosome[] getChildChromosomeList() {
		return childChromosomeList;
	}

	public int[] getDistanceList() {
		return distanceList;
	}

	public String[] getBestGenotypeList() {
		return bestGenotypeList;
	}

	public ArrayList<Integer> getxAxisList() {
		return xAxisList;
	}

	public ArrayList<Integer> getyAxisList() {
		return yAxisList;
	}

	public int[] getBestPhenotypeList() {
		return bestPhenotypeList;
	}

	@SuppressWarnings("unchecked")
	private void initDataFromTxtFile(String filename) throws Exception {
		xAxisList = new ArrayList<>();
		yAxisList = new ArrayList<>();
		random = new Random();

		// Read data from file line by line, the format is 1 6734 1453
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		while ((line = bufferedReader.readLine()) != null) {
			String[] strElement = line.split(" ");
			xAxisList.add(Integer.valueOf(strElement[0]));
			yAxisList.add(Integer.valueOf(strElement[1]));
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
	public int distance(int from, int to) {
		if (from == to)
			return 0;
		int dij;
		int xd = xAxisList.get(from) - xAxisList.get(to);
		int yd = yAxisList.get(from) - yAxisList.get(to);
		double rij = Math.sqrt(xd * xd + yd * yd);
		int tij = (int) Math.round(rij);
		if (tij < rij)
			dij = tij + 1;
		else
			dij = tij;
		return dij;
	}

	// Calculate the distance list using the current generation
	public void calDistanceList() {
		for (int i = 0; i < populationNum; i++) {
			distanceList[i] = parentChromosomeList[i].getWeight();
		}
	}

	// Calculate the fitness list using the current generation
	public void calFitnessList() {
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
	public void select() {
		Arrays.sort(parentChromosomeList);
		int flag;
		childChromosomeList[0]=parentChromosomeList[0];
		for (int k = 1; k < populationNum; k++) {
			// Copy the best ten percentage of entities from parents to children
			flag = random.nextInt(populationNum / 10);
			childChromosomeList[k] = parentChromosomeList[flag];
		}
	}

	// Mutation and crossover
	public void evolution() {
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
	public int bestEntity() {
		int location = 0;
		int shortestDistance = distanceList[0];
		bestGenotypeList = new String[cityNum];
		bestPhenotypeList = new int[cityNum];
		for (int i = 1; i < populationNum; i++)
			if (distanceList[i] < shortestDistance) {
				shortestDistance = distanceList[i];
				location = i;
			}
		bestGenotypeList = parentChromosomeList[location].getGenoTypeList();
		bestPhenotypeList = parentChromosomeList[location].getPhenotypeList();
		return shortestDistance;
	}

	public void generate(TSPChromosome[] allChromosomeList) {
		int length = allChromosomeList.length;
		int mid = length / 2;
		CompletableFuture<TSPChromosome[]> parGenerate1 = parGenerate(allChromosomeList, 0, mid - 1);
		CompletableFuture<TSPChromosome[]> parGenerate2 = parGenerate(allChromosomeList, mid, length - 1);
		CompletableFuture<TSPChromosome[]> parGenerate = parGenerate1.thenCombine(parGenerate2, (xs1, xs2) -> {
			TSPChromosome[] result = new TSPChromosome[length];
			for (int i = 0; i < mid; i++)
				result[i] = xs1[i];
			for (int i = mid; i < length; i++)
				result[i] = xs2[i - mid];
			return result;
		});

		parGenerate.whenComplete((result, throwable) -> {
			if (throwable != null) {
				parGenerate.completeExceptionally(throwable);
			} else {
				for (int i = 0; i < result.length; i++) {
					childChromosomeList[i] = result[i];
				}
			}
		});
		parGenerate.join();
	}

	private CompletableFuture<TSPChromosome[]> parGenerate(TSPChromosome[] generatingList, int from, int to) {
		return CompletableFuture.supplyAsync(() -> {
			TSPChromosome[] tempList = new TSPChromosome[to - from + 1];
			TSPChromosome[] generatedList = new TSPChromosome[to - from + 1];

			for (int i = from; i <= to; i++)
				tempList[i - from] = generatingList[i];

			Arrays.sort(tempList);
			int flag;
			generatedList[0]=tempList[0];
			for (int k = 1; k < tempList.length; k++) {
				// Copy the best ten percentage of entities from parents to children
				flag = random.nextInt(tempList.length / 10);
				generatedList[k] = tempList[flag];
			}

			// Mutation and crossover
			for (int i = 1; i < tempList.length; i++) {
				double ranDouble = random.nextDouble();
				if (ranDouble < P_DEFAULT_CROSS)
					generatedList[i] = generatedList[i].crossOver(generatedList[i - 1]);
				else {
					ranDouble = random.nextDouble();
					if (ranDouble < P_DEFALUT_MUTATION)
						generatedList[i].mutation();
				}
			}

			return generatedList;
		});
	}

	// Generation
	public void generate() {
		for (int i = 0; i < GENERATION_NUM; i++) {
			generate(parentChromosomeList);
			// select();
			// evolution();
			parentChromosomeList = childChromosomeList.clone();
			calDistanceList();
			calFitnessList();
			System.out.println("The Shortest Distance of Generation: " + i + " is " + bestEntity());
		}
		System.out.println("The best phenotype is " + Arrays.toString(bestPhenotypeList));
		System.out.println("The best genotype is " + Arrays.toString(bestGenotypeList));
	}

	public static void main(String[] args) throws Exception {
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm(30000, "data.txt");

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
