package com.tiendd.modeling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import com.tiendd.clustering.Clustering;
import com.tiendd.uet.predicting.Predicting;
import com.tiendd.uet.preprocessing.CFUserBased;
import model.Constant;
import net.librec.common.LibrecException;

public class GroupModel {
	private static int K_CLUSTER = 20;
	private static int MAX_ITERATION = 500;

	public static double[][] modelingAvgGroup(double[][] matrix, HashMap<Integer, ArrayList<Integer>> mapCluster) {
		double avgRatingCluster[][] = new double[K_CLUSTER][Constant.SIZE_ITEM];
		for (int i = 0; i < K_CLUSTER; i++) {
			for (int item = 0; item < Constant.SIZE_ITEM; item++) {
				double sumRating = 0;
				int count = 0;
				boolean flag = false;
				for (int j = 0; j < mapCluster.get(i).size(); j++) {
					System.out.println(mapCluster.get(i).get(j));
					if (matrix[mapCluster.get(i).get(j)][item] > 0) {
						sumRating += matrix[mapCluster.get(i).get(j)][item];
						count++;
						flag = true;
					}
				}
				if (flag) {
					avgRatingCluster[i][item] = sumRating / count;
				}
			}
		}
		return avgRatingCluster;
	}

	public static double[][] modelingMinGroup(double[][] matrix, HashMap<Integer, ArrayList<Integer>> mapCluster) {
		double minRatingCluster[][] = new double[K_CLUSTER][Constant.SIZE_ITEM];
		for (int i = 0; i < K_CLUSTER; i++) {
			for (int item = 0; item < Constant.SIZE_ITEM; item++) {
				double Min = 5.0;
				boolean flag = false;
				for (int j = 0; j < mapCluster.get(i).size(); j++) {
					System.out.println(mapCluster.get(i).get(j));
					if (matrix[mapCluster.get(i).get(j)][item] > 0 && matrix[mapCluster.get(i).get(j)][item] < Min) {
						Min = matrix[mapCluster.get(i).get(j)][item];
						flag = true;
					}
				}
				if (flag) {
					minRatingCluster[i][item] = Min;
				}
			}
		}
		return minRatingCluster;
	}

	public static double[][] modelingMaxGroup(double[][] matrix, HashMap<Integer, ArrayList<Integer>> mapCluster) {
		double maxRatingCluster[][] = new double[K_CLUSTER][Constant.SIZE_ITEM];
		for (int i = 0; i < K_CLUSTER; i++) {
			for (int item = 0; item < Constant.SIZE_ITEM; item++) {
				double Max = 0.0;
				boolean flag = false;
				for (int j = 0; j < mapCluster.get(i).size(); j++) {
					System.out.println(mapCluster.get(i).get(j));
					if (matrix[mapCluster.get(i).get(j)][item] > 0 && matrix[mapCluster.get(i).get(j)][item] > Max) {
						Max = matrix[mapCluster.get(i).get(j)][item];
						flag = true;
					}
				}
				if (flag) {
					maxRatingCluster[i][item] = Max;
				}
			}
		}
		return maxRatingCluster;
	}

	public static void testRMSE(double[][] matrixGroup, double[][] rateMinGroups, double[][] rateMaxGroups)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(Constant.URL_FILE_RATING_TESTSET));
		StringTokenizer st = null;
		String row;
		double sumAvgTop = 0.0;
		double sumMinTop = 0.0;
		double sumMaxTop = 0.0;
		while ((row = br.readLine()) != null) {
			st = new StringTokenizer(row, Constant.COMMA_DELIMITER);
			while (st.hasMoreTokens()) {
				int user = Integer.parseInt(st.nextToken());
				int movie = Integer.parseInt(st.nextToken());
				double rating = Double.parseDouble(st.nextToken());
				for (int i = 0; i < Clustering.getMapClusters().keySet().size(); i++) {
					if (Clustering.getMapClusters().get(i).contains(user)) {
						double avgPg = matrixGroup[i][movie];
						double minPg = rateMinGroups[i][movie];
						double maxPg = rateMaxGroups[i][movie];
						sumAvgTop += (rating - avgPg) * (rating - avgPg);
						sumMinTop += (rating - minPg) * (rating - minPg);
						sumMaxTop += (rating - maxPg) * (rating - maxPg);
					}
				}
			}
		}
		double RMSEavg = Math.sqrt(sumAvgTop / Constant.SIZE_RECORD_TEST);
		double RMSEmin = Math.sqrt(sumMinTop / Constant.SIZE_RECORD_TEST);
		double RMSEmax = Math.sqrt(sumMaxTop / Constant.SIZE_RECORD_TEST);
		PrintWriter writerResult = new PrintWriter("output/result.txt", "UTF-8");
		writerResult.println("AVERAGE: " + RMSEavg);
		writerResult.println("MIN: " + RMSEmin);
		writerResult.println("MAX: " + RMSEmax);
		writerResult.close();
		br.close();
	}

	public static void main(String[] args) throws NumberFormatException, IOException, LibrecException {
//		Predicting.predictUnrated();
		Clustering.clustering(K_CLUSTER, MAX_ITERATION);
		double[][] matrixUpdated = CFUserBased.createMatrixUpdated();
		HashMap<Integer, ArrayList<Integer>> clusters = Clustering.getMapClusters();
		PrintWriter writerCluster = new PrintWriter("output/group_user.txt", "UTF-8");
		for (int i = 0; i < clusters.size(); i++) {
			writerCluster.print("Group"+i+":{");
			for(int j=0;j<clusters.get(i).size();j++){
				writerCluster.print(clusters.get(i).get(j)+" ");
			}
			writerCluster.println("}");
		}
		writerCluster.close();
		double[][] rateAvgGroups = modelingAvgGroup(matrixUpdated, Clustering.getMapClusters());
		double[][] rateMinGroups = modelingMinGroup(matrixUpdated, Clustering.getMapClusters());
		double[][] rateMaxGroups = modelingMaxGroup(matrixUpdated, Clustering.getMapClusters());
		PrintWriter writer = new PrintWriter(Constant.URL_FILE_RATING_GROUP, "UTF-8");
		for (int i = 0; i < rateAvgGroups.length; i++) {
			for (int j = 0; j < rateAvgGroups[0].length; j++) {
				writer.print(rateAvgGroups[i][j]);
				if (j < Constant.SIZE_ITEM - 1) {
					writer.print(Constant.COMMA_DELIMITER);
				} else {
					writer.println();
				}
			}
		}
		writer.close();
		testRMSE(rateAvgGroups, rateMinGroups, rateMaxGroups);
	}
}