
package edu.info6205.team501;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Ang Li
 *
 */
public class TSPGenerateAlgorithm {

	private ArrayList<ArrayList<Integer>> distanceList = new ArrayList<>();
	private ArrayList<Integer> xAxisList;
	private ArrayList<Integer> yAxisList;
	private int nodeNum;

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
		nodeNum = xAxisList.size();
		// initialize the distance between every two nodes
		for (int i = 0; i < nodeNum; i++) {
			ArrayList<Integer> childDistanceList = new ArrayList<>();
			for (int j = 0; j < nodeNum; j++)
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

	public static void main(String[] args) throws Exception {
		TSPGenerateAlgorithm tspGenerateAlgorithm = new TSPGenerateAlgorithm();
		tspGenerateAlgorithm.initDataFromTxtFile("E:\\EclipseJavaWorkSpace\\TSP\\data\\data.txt");
		System.out.println(tspGenerateAlgorithm.distanceList);
		System.out.println(tspGenerateAlgorithm.xAxisList);
		System.out.println(tspGenerateAlgorithm.yAxisList);
	}
}
