
package edu.info6205.team501;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Generated;

/**
 * @author Ang Li
 *
 */
public class TSPGenerateAlgorithm {

	private ArrayList<ArrayList<Integer>> distanceList;
	private ArrayList<Integer> xAxisList;
	private ArrayList<Integer> yAxisList;
	private ArrayList<ArrayList<Integer>> parentChromosomeList;
	private ArrayList<ArrayList<Integer>> childChromosomeList;
	private ArrayList<Integer> fitnessList;
	private ArrayList<Integer> rouletteWheelList;
	private int cityNum;
	private int populationNum;
	private Random random;
	private int generationCounter;
	private int shortestDistance;
	private int bestEntity;

	public TSPGenerateAlgorithm(int populationNum) {
		this.populationNum = populationNum;
	}

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
		int shortestPath = fitnessList.get(0);
		int shortestPosition = 0;
		for (int i = 1; i < populationNum; i++)
			if (fitnessList.get(i) < shortestPath) {
				shortestPath = fitnessList.get(i);
				shortestPosition = i;
			}
		shortestDistance = shortestPath;
		bestEntity = shortestPosition;
		childChromosomeList.set(0,parentChromosomeList.get(shortestPosition));
	}

	// select and generate the remaining descendents
	private void select() {
        for (int i = 1; i < populationNum; i++) 
        	childChromosomeList.add(parentChromosomeList.get(wheelOut((int) Math.random())));
        
	}

    private int wheelOut(int ran) {
        for (int i = 0; i < populationNum; i++) {
            if (ran <= rouletteWheelList.get(i)) {
                return i;
            }
        }
        return 0;
    }
	
	private void generate() {
		generationCounter++;
		bestEntity();
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

	}
}
