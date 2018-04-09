
package edu.info6205.team501;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Ang Li, Xiaohan Zhao
 *
 */
public class TSPGenerateAlgorithm {

	private ArrayList<ArrayList<Integer>> distanceList = new ArrayList<>();
	private ArrayList<Integer> xAxisList;
	private ArrayList<Integer> yAxisList;
	private ArrayList<Integer> parentChromosomeList;
	private ArrayList<Integer> childChromosomeList;
	private int cityNum;

	private void initDataFromTxtFile(String filename) throws Exception {
		xAxisList = new ArrayList<>();
		yAxisList = new ArrayList<>();

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
		for (int i = 0; i < cityNum; i++) {
			ArrayList<Integer> childDistanceList = new ArrayList<>();
			for (int j = 0; j < cityNum; j++)
				childDistanceList.add(distance(i, j));
			distanceList.add(childDistanceList);
		}
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
		Random random = new Random();
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

	public static void main(String[] args) throws Exception {
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm();
		tspGenerateAlgorithm.initDataFromTxtFile("E:\\EclipseJavaWorkSpace\\TSP\\data\\data.txt");
		
		//Test initDataFromTxtFile() and distance()
		// System.out.println(tspGenerateAlgorithm.distanceList);
		// System.out.println(tspGenerateAlgorithm.xAxisList);
		// System.out.println(tspGenerateAlgorithm.yAxisList);
		
		//Test randomChromosome()
		// System.out.println(tspGenerateAlgorithm.randomChromosome());
		// System.out.println(tspGenerateAlgorithm.randomChromosome().size());
		// System.out.println(tspGenerateAlgorithm.cityNum);
	}
}
