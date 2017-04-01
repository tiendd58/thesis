package com.tiendd.clustering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import model.CloudSimilar;
import model.Constant;
import model.KMeansCustom;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

public class Clustering {

	private static HashMap<Integer, ArrayList<Integer>> mapClusters;

	public static void clustering(int k, int numLoop) throws IOException {
		System.out.println("Clustering...");
		Dataset dataset = FileHandler.loadDataset(new File(Constant.URL_FILE_RATING_UPDATED), Constant.COMMA_DELIMITER);
		Clusterer km = new KMeansCustom(k, numLoop, new CloudSimilar());
		Dataset[] clusters = km.cluster(dataset);
		mapClusters = new HashMap<>();
		for (int i = 0; i < clusters.length; i++) {
			ArrayList<Integer> listID = new ArrayList<>();
			for (Instance ins : clusters[i]) {
				listID.add(ins.getID());
			}
			mapClusters.put(i, listID);
		}
	}

	public static HashMap<Integer, ArrayList<Integer>> getMapClusters() {
		return mapClusters;
	}
}